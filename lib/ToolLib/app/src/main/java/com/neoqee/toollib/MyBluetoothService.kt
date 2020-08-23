package com.neoqee.toollib

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothServerSocket
import android.bluetooth.BluetoothSocket
import android.os.Bundle
import android.os.Handler
import android.os.Message
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.*

const val MESSAGE_READ: Int = 0
const val MESSAGE_WRITE: Int = 1
const val MESSAGE_TOAST: Int = 2

class MyBluetoothService(private val handler: Handler) {

    // Name for the SDP record when creating server socket
    private val NAME_SECURE = "BluetoothZnSecure"
    private val NAME_INSECURE = "BluetoothZnInsecure"

    // Unique UUID for this application
    // 需要与连接的设备使用的uuid一致
    private val MY_UUID_SECURE =
        UUID.fromString("fa87c0d0-afac-11de-8a39-0800200c9a66")
    private val MY_UUID_INSECURE =
        UUID.fromString("8ce255c0-200a-11e0-ac64-0800200c9a66")

    private val bluetoothAdapter: BluetoothAdapter?

    // 状态
    private var mState: Int
    private var mOldState: Int

    // 线程
    private var mSecureAcceptThread: AcceptThread? = null
    private var mInsecureAcceptThread: AcceptThread? = null
    private var mConnectThread: ConnectThread? = null
    private var mConnectedThread: ConnectedThread? = null

    external fun getByteStringByNative(bytes: ByteArray, len: Int) : String?

    companion object {
        // Constants that indicate the current connection state
        const val STATE_NONE = 0            // 不做任何事
        const val STATE_LISTEN = 1          // now listening for incoming connections 作为服务端的连接中
        const val STATE_CONNECTING = 2      // now initiating an outgoing connection  作为客户端的连接中
        const val STATE_CONNECTED = 3       // 已连接一个设备

        init {
            System.loadLibrary("native-lib")
        }

    }

    init {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        mState = STATE_NONE
        mOldState = mState
    }

    @Synchronized
    fun start() {
        log("start")
        mConnectThread?.cancel()
        mConnectedThread?.cancel()
        // 开启作为服务器的连接监听
        if (mSecureAcceptThread == null) {
            mSecureAcceptThread = AcceptThread(true)
        }
        if (mInsecureAcceptThread == null) {
            mInsecureAcceptThread = AcceptThread(false)
        }
        updateState()
    }

    @Synchronized
    fun connect(device: BluetoothDevice, secure: Boolean) {
        log("连接到->$device")

        if (mState == STATE_CONNECTING) {
            mConnectThread = mConnectThread?.let {
                it.cancel()
                null
            }
        }
        mConnectThread = mConnectThread?.let {
            it.cancel()
            null
        }
        mConnectThread = ConnectThread(device, secure).apply {
            start()
            updateState()
        }

    }

    @Synchronized
    fun connected(socket: BluetoothSocket, device: BluetoothDevice) {

        mConnectThread = mConnectThread?.run {
            cancel()
            null
        }
        mConnectedThread = mConnectedThread?.run {
            cancel()
            null
        }
        mSecureAcceptThread = mSecureAcceptThread?.run {
            cancel()
            null
        }
        mInsecureAcceptThread = mInsecureAcceptThread?.run {
            cancel()
            null
        }

        mConnectedThread = ConnectedThread(socket).apply {
            start()
        }

        updateState()
    }

    @Synchronized
    fun stop(){
        log("stop")
        mConnectThread = mConnectThread?.run {
            cancel()
            null
        }
        mConnectedThread = mConnectedThread?.run {
            cancel()
            null
        }
        mSecureAcceptThread = mSecureAcceptThread?.run {
            cancel()
            null
        }
        mInsecureAcceptThread = mInsecureAcceptThread?.run {
            cancel()
            null
        }
        mState = STATE_NONE
        updateState()
    }

    @Synchronized
    fun updateState() {
        mState = getState()
        log("状态变化:$mOldState -> $mState")
        mOldState = mState
    }

    @Synchronized
    fun getState() = mState

    fun write(bytes: ByteArray){
        var c: ConnectedThread?
        synchronized(this){
            if (mState != STATE_CONNECTED){
                return
            }
            c = mConnectedThread
        }
        c?.write(bytes)
    }

    /**
     * Indicate that the connection attempt failed and notify the UI Activity.
     * 表示连接尝试失败，并通知UI活动。
     */
    private fun connectionFailed() {
        // Send a failure message back to the Activity
        val msg: Message = handler.obtainMessage(MESSAGE_TOAST)
        val bundle = Bundle()
        bundle.putString("toast", "Unable to connect device")
        msg.data = bundle
        handler.sendMessage(msg)
        mState = STATE_NONE
        // Update UI title
        updateState()

        // Start the service over to restart listening mode
        this.start()
    }

    /**
     * Indicate that the connection was lost and notify the UI Activity.
     * 表示连接丢失，并通知UI活动。
     */
    private fun connectionLost() {
        // Send a failure message back to the Activity
        val msg: Message = handler.obtainMessage(MESSAGE_TOAST)
        val bundle = Bundle()
        bundle.putString("toast", "Device connection was lost")
        msg.data = bundle
        handler.sendMessage(msg)
        mState = STATE_NONE
        // Update UI title
        updateState()

        // Start the service over to restart listening mode
        this.start()
    }

    fun manageMyConnectedSocket(socket: BluetoothSocket) {
        synchronized(this){
            when(mState){
                STATE_LISTEN or STATE_CONNECTING -> {
                    connected(socket,socket.remoteDevice)
                }
                else -> {
                    try {
                        socket.close()
                    }catch (e: IOException){
                        log("无法关闭socket",e)
                    }
                }
            }
        }
    }

    // 作为服务端连接
    private inner class AcceptThread(val secure: Boolean) : Thread() {
        //        private val mmServerSocket: BluetoothServerSocket? by lazy(LazyThreadSafetyMode.NONE) {
//            bluetoothAdapter.listenUsingInsecureRfcommWithServiceRecord(NAME_INSECURE, MY_UUID_INSECURE)
//        }
        private val mmServerSocket: BluetoothServerSocket?
        private val mSocketType: String

        init {
            var temp: BluetoothServerSocket? = null
            mSocketType = if (secure) "Secure" else "InSecure"
            try {
                temp = if (secure) {
                    bluetoothAdapter?.listenUsingRfcommWithServiceRecord(
                        NAME_SECURE,
                        MY_UUID_SECURE
                    )
                } else {
                    bluetoothAdapter?.listenUsingInsecureRfcommWithServiceRecord(
                        NAME_INSECURE,
                        MY_UUID_INSECURE
                    )
                }
            } catch (e: IOException) {
                log("Socket Type: $mSocketType listen() failed", e)
            }
            mmServerSocket = temp
            mState = STATE_LISTEN
        }

        override fun run() {

            name = "AcceptThread$mSocketType"

            var shouldLoop = true
            while (shouldLoop) {
                val socket: BluetoothSocket? = try {
                    mmServerSocket?.accept()
                } catch (e: IOException) {
                    log("socket的accept()方法失败了", e)
                    shouldLoop = false
                    null
                }
                socket?.also {
                    manageMyConnectedSocket(it)
                    cancel()
                    shouldLoop = false
                }
            }
        }

        fun cancel() {
            try {
                mmServerSocket?.close()
            } catch (e: IOException) {
                log("无法关闭socket连接", e)
            }
        }
    }

    // 作为客户端连接
    private inner class ConnectThread(val device: BluetoothDevice, secure: Boolean) : Thread() {

        //        private val mmSocket: BluetoothSocket? by lazy(LazyThreadSafetyMode.NONE){
//            device.createInsecureRfcommSocketToServiceRecord(MY_UUID_INSECURE)
//        }
        private var mmSocket: BluetoothSocket? = null
        private val mSocketType: String

        init {
            var temp: BluetoothSocket? = null
            mSocketType = if (secure) "Secure" else "InSecure"
            temp = try {
                if (secure) {
                    device.createRfcommSocketToServiceRecord(MY_UUID_SECURE)
                } else {
                    device.createInsecureRfcommSocketToServiceRecord(MY_UUID_INSECURE)
                }
            } catch (e: IOException) {
                log("Socket Type: $mSocketType create() failed ", e)
                log("使用createRfcommSocket方法创建BluetoothSocket")
                device.javaClass.getMethod("createRfcommSocket", Int::class.java)
                    .invoke(device, 1) as BluetoothSocket?
            }
            mmSocket = temp
            mState = STATE_CONNECTING
        }

        override fun run() {
            bluetoothAdapter?.cancelDiscovery()
            mmSocket?.use { socket ->
                try {
                    socket.connect()
                } catch (e: IOException) {
                    try {
                        mmSocket = device.javaClass.getMethod("createRfcommSocket", Int::class.java)
                            .invoke(device, 1) as BluetoothSocket?
                        mmSocket?.connect()
                    } catch (e: IOException) {
                        log("连接蓝牙发生异常", e)
                        try {
                            mmSocket?.close()
                        } catch (e: IOException) {
                            log("unable to close() $mSocketType socket during connection failure", e)
                        }
                        connectionFailed()
                        return
                    }
                }
                synchronized(this@MyBluetoothService){
                    mConnectThread = null
                }
                connected(mmSocket!!,device)
            }
        }

        fun cancel() {
            try {
                mmSocket?.close()
            } catch (e: IOException) {
                log("无法关闭socket连接", e)
            }
        }
    }

    // 已连接
    private inner class ConnectedThread(private val mmSocket: BluetoothSocket) : Thread() {
        private val mmInputStream: InputStream = mmSocket.inputStream       // 输入流
        private val mmOutputStream: OutputStream = mmSocket.outputStream    // 输出流
        private val mmBuffer: ByteArray = ByteArray(1024)              // 保存流的数据

        init {
            mState = STATE_CONNECTED
        }

        override fun run() {
            var numBytes: Int   // 读取的字节

            // 保持监听InputStream直到有异常发生
            while (true) {
                 try {
                    numBytes = mmInputStream.read(mmBuffer)
                     if (numBytes > 0){
                         val read8Bit = read8Bit(mmBuffer, numBytes)
                         read8Bit?.run {
                             // 发送消息数据
                             val readMsg = handler.obtainMessage(MESSAGE_READ,this)
                             readMsg.sendToTarget()
                         }
                     }
                } catch (e: IOException) {
                    log("输入流已断开连接", e)
                    connectionLost()
                    break
                }

            }
        }

        fun write(bytes: ByteArray) {
            try {
                mmOutputStream.write(bytes)
                // 将发送的数据通知出去
                val writeMsg = handler.obtainMessage(MESSAGE_WRITE, -1, -1, bytes)
                writeMsg.sendToTarget()
            } catch (e: IOException) {
                log("发送数据时发生了一个异常", e)

                // 将这个发送失败的消息通知出去
                val writeErrorMsg = handler.obtainMessage(MESSAGE_TOAST)
                val bundle = Bundle().apply {
                    putString("toast", "不能发送数据到其他设备")
                }
                writeErrorMsg.data = bundle
                handler.sendMessage(writeErrorMsg)
                return
            }

        }

        fun cancel() {
            try {
                mmSocket.close()
            } catch (e: IOException) {
                log("无法关闭socket连接", e)
            }
        }
    }

    private var cacheLen = 0
    private var cacheString = ""
    private fun read8Bit(buffer: ByteArray, len: Int):String?{
        // buffer的总解析字符串

        // buffer的总解析字符串
        val buffers = String(buffer, 0, len)
        // 当前长度 = 缓存长度 + 当次的字节长度
        // 当前长度 = 缓存长度 + 当次的字节长度
        val currentLen = cacheLen + len
        return when {
            currentLen < 8 -> {           // 不足一次，全部缓存起来
                val byteString = String(buffer, 0, len).trim()
                cacheString += byteString
                cacheLen = currentLen
                null
            }
            currentLen > 8 -> {    // 超过一次了，返回与上次缓存相关的数据，并缓存最后一次不足8字节的数据
                val byteString = String(buffer, 0, 8 - cacheLen).trim()
                val result = cacheString + byteString
                val end =
                    currentLen / 8 // 获得对应的结束节点，因为字符串中不会体现出来，所以当次长度减去结束节点个数，即字符串的长度，或者直接用buffers.length
                cacheLen = currentLen % 8 // 最后不足一次的数据长度，即需要缓存的数据
                cacheString = if (cacheLen == 0) {
                    ""
                } else {
                    buffers.substring(len - end - cacheLen, len - end).trim()
                }
                result
            }
            else -> {                        // 刚好一次，返回数据，并重置缓存
                val byteString = String(buffer, 0, len).trim()
                val result = cacheString + byteString
                cacheString = ""
                cacheLen = currentLen - 8
                result
            }
        }
    }

}
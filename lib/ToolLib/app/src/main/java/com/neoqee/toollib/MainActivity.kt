package com.neoqee.toollib

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 获取蓝牙
        val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        // 检测是否支持蓝牙
        if (bluetoothAdapter == null){
            showToast(this,"该设备不支持蓝牙")
            return
        }
        // 检测是否打开蓝牙
        if (!bluetoothAdapter.isEnabled){
            // 请求打开蓝牙
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBtIntent,0x01)
            // 启用可检测性，可以直接在app内设置请求设备可已被检测及时间
            // 启用可检测性，即使设备未启动蓝牙，也会自动启动蓝牙
            val discoverableIntent = Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE).apply {
                // value值为可检测的时间，单位为秒，这里即设置180秒可检测
                putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION,180)
            }
        }

        // 已匹配设备
        val bondedDevices = bluetoothAdapter.bondedDevices
        for (bondedDevice in bondedDevices) {
            val deviceName = bondedDevice.name
            val deviceHardwareAddress = bondedDevice.address
        }

        // 注册广播
        val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        registerReceiver(receiver,filter)

    }

    override fun onDestroy() {
        super.onDestroy()
        // 解注册广播
        unregisterReceiver(receiver)
    }

    // 广播，监听发现设备的广播
    private val receiver = object : BroadcastReceiver(){
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            when(action){
                BluetoothDevice.ACTION_FOUND -> {
                    // 扫描发现了一个设备，从Intent中获取BluetoothDevice对象
                    val device : BluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                    val deviceName = device.name
                    val deviceHardwareAddress = device.address
                }
                // 可检测到模式发生变化的广播通知字段
                BluetoothAdapter.ACTION_SCAN_MODE_CHANGED -> {
                    // 扫描模式
                    """
                        SCAN_MODE_CONNECTABLE_DISCOVERABLE:设备处于可检测到模式
                        SCAN_MODE_CONNECTABLE:设备未处于可检测到模式，但仍能收到连接
                        SCAN_MODE_NONE:设备未处于可检测到模式，且无法收到连接
                    """.trimIndent()
                    val scanMode = intent.extras?.getInt(BluetoothAdapter.EXTRA_SCAN_MODE)

                }
            }
        }
    }

}

package com.neoqee.toollib

import android.content.Context
import android.util.Log
import android.widget.Toast

fun showToast(context: Context,msg:String){
    Toast.makeText(context,msg,Toast.LENGTH_SHORT).show()
}
fun log(msg: String?,throwable: Throwable? = null){
    if (throwable == null){
        Log.i("MyBluetoothService",if (msg.isNullOrBlank()) "消息为空" else msg )
    }else{
        Log.e("MyBluetoothService",msg,throwable)
    }
}

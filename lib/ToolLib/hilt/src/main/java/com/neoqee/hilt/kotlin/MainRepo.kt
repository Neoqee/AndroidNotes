package com.neoqee.hilt.kotlin

import javax.inject.Inject

class MainRepo @Inject constructor(){

    fun getOutput() = "Hello Neoqee!"

}
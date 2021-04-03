package com.neoqee.hilt.kotlin

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import com.neoqee.hilt.kotlin.MainRepo

class MainViewModel @ViewModelInject constructor(
    mainRepo: MainRepo
) : ViewModel() {

    val output = mainRepo.getOutput()

}
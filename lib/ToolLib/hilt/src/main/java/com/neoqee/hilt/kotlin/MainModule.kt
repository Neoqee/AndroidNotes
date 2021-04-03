package com.neoqee.hilt.kotlin

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import javax.inject.Inject

@Module
@InstallIn(ActivityComponent::class)
abstract class MainModule {

    @Binds
    abstract fun bindMainService(mainServiceImpl: MainServiceImpl):MainService

}

interface MainService{
    fun doAction():String
}
class MainServiceImpl @Inject constructor() : MainService{
    override fun doAction():String {
        return "action from interface"
    }

}
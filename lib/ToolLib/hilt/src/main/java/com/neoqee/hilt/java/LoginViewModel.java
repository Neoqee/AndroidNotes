package com.neoqee.hilt.java;

import androidx.hilt.lifecycle.ViewModelInject;
import androidx.lifecycle.ViewModel;

public class LoginViewModel extends ViewModel {

    private final LoginRepo loginRepo;

    @ViewModelInject
    public LoginViewModel(LoginRepo loginRepo){
        this.loginRepo = loginRepo;
    }

    public String getOutputString(){
        return loginRepo.login();
    }

}

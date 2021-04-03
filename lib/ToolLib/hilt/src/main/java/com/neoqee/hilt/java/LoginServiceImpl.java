package com.neoqee.hilt.java;

import javax.inject.Inject;

class LoginServiceImpl implements LoginService {

    @Inject
    LoginServiceImpl(){}

    @Override
    public String getOutput() {
        return "string from LoginService";
    }
}

package com.neoqee.hilt.java;

import javax.inject.Inject;

public class LoginRepo {

    @Inject
    public LoginRepo(){}

    public String login(){
        return "Hello java hilt";
    }

}

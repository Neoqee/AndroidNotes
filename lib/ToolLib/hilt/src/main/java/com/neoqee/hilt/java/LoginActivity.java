package com.neoqee.hilt.java;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import com.neoqee.hilt.R;
import com.neoqee.hilt.databinding.ActivityLoginBinding;
import com.neoqee.hilt.kotlin.MainModule;
import com.neoqee.hilt.kotlin.MainService;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private LoginViewModel loginViewModel;
    @Inject LoginRepo loginRepo;
    @Inject MainService mainService;
    @Inject LoginService loginService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);
        setContentView(binding.getRoot());

//        binding.outputTv.setText(loginViewModel.getOutputString());
//        binding.outputTv.setText(loginRepo.login());
//        binding.outputTv.setText(mainService.doAction());
        binding.outputTv.setText(loginService.getOutput());



    }
}

package com.neoqee.javalib;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.neoqee.commonlib.PreviewImgHelper;
import com.neoqee.javalib.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        List<String> list = new ArrayList<>();
        list.add("https://cdn.pixabay.com/photo/2020/06/17/12/02/books-5309242_960_720.jpg");
        list.add("https://cdn.pixabay.com/photo/2020/06/15/19/49/fuchs-5303221_960_720.jpg");
        list.add("https://cdn.pixabay.com/photo/2020/06/18/21/02/teddy-5314912__340.jpg");

        binding.button.setOnClickListener(view -> {
            PreviewImgHelper.getInstance().preview(this,list,0);
        });

        binding.button2.setOnClickListener(view -> {
            PreviewImgHelper.getInstance().preview(this,list,1);
        });

        binding.button3.setOnClickListener(view -> {
            PreviewImgHelper.getInstance().preview(this,list,2);
        });


    }
}

package com.neoqee.javalib;

import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.billy.android.loading.Gloading;
import com.neoqee.javalib.databinding.ActivityLoadingBinding;

public class LoadingActivity extends AppCompatActivity {

    private ActivityLoadingBinding binding;
    private Gloading.Holder holder;

    private static Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoadingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        holder = Gloading.getDefault().wrap(binding.container);

        holder.showLoading();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                holder.showLoadSuccess();
            }
        },2000);

    }
}

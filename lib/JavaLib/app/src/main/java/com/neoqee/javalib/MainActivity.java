package com.neoqee.javalib;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.neoqee.commonlib.BgaActivity;
import com.neoqee.commonlib.PermissionCallback;
import com.neoqee.commonlib.PermissionHelper;
import com.neoqee.commonlib.PreviewImgHelper;
import com.neoqee.javalib.databinding.ActivityMainBinding;
import com.neoqee.lib_annotations.Anno;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Anno("")
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

        DataBean dataBean = new DataBean();
        binding.button.setOnClickListener(view -> {
//            PreviewImgHelper.getInstance().preview(this,list,0);
            Log.i("Neoqee","length:" + dataBean.getData().length());
        });

        binding.button2.setOnClickListener(view -> {
            PreviewImgHelper.getInstance().preview(this,list,1);
        });

        binding.button3.setOnClickListener(view -> {
            PreviewImgHelper.getInstance().preview(this,list,2);
        });

        binding.toFlowBtn.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, FlowLayoutActivity.class);
            startActivity(intent);
        });

        binding.toLoadingBtn.setOnClickListener(v -> {
//            Intent intent = new Intent(MainActivity.this, LoadingActivity.class);
//            startActivity(intent);
            new PermissionHelper().request(this, Arrays.asList(Manifest.permission.WRITE_EXTERNAL_STORAGE), new PermissionCallback() {
                @Override
                public void callback(boolean allGranted, List<String> deniedPermissions) {
                    if (allGranted){
                        Intent intent = new Intent(MainActivity.this, PickPhotoActivity.class);
                        startActivity(intent);
                    }
                }
            });
        });
        binding.toSearch.setOnClickListener(v -> {
            Intent intent = new Intent(this, SearchActivity.class);
            startActivity(intent);
        });
        binding.toCardView.setOnClickListener(v -> {
//            Intent intent = new Intent(this, CardViewActivity.class);
//            startActivity(intent);
            Intent intent = new Intent(this, BgaActivity.class);
            startActivity(intent);
        });


    }
}

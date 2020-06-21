package com.neoqee.commonlib;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.neoqee.commonlib.databinding.ActivityGalaryBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class GalleryActivity extends AppCompatActivity {

    private ActivityGalaryBinding binding;
    private List<String> urls = new ArrayList<>();
    private int currentItem = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGalaryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getArguments();

        PagerPhotoAdapter pagerPhotoAdapter = new PagerPhotoAdapter();
        binding.pager.setAdapter(pagerPhotoAdapter);
        pagerPhotoAdapter.submitList(urls);
        binding.pager.setCurrentItem(currentItem);

        binding.pager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                Log.i("Preview","当前查看的位置：" + position);
            }
        });



    }

    private void getArguments() {
        Intent intent = getIntent();
        ArrayList<String> urls = intent.getStringArrayListExtra("urls");
        if (null != urls){
            this.urls = urls;
        }
        this.currentItem = intent.getIntExtra("currentItem", 0);
    }

    public static void startAction(Context context, ArrayList<String> urls, int currentItem){
        Intent intent = new Intent(context, GalleryActivity.class);
        intent.putStringArrayListExtra("urls", urls);
        intent.putExtra("currentItem",currentItem);
        context.startActivity(intent);
    }

}

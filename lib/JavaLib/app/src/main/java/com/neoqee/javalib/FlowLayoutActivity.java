package com.neoqee.javalib;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.neoqee.javalib.databinding.ActivityFlowLayoutBinding;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class FlowLayoutActivity extends AppCompatActivity {

    private ActivityFlowLayoutBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFlowLayoutBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        List<String> list = Arrays.asList("asfadfdsg","asf","fgdsg","gun","我就发哦里就发来的","风很大恢复了");

        for (int i = 0; i < 20; i++){
            TextView textView = new TextView(this);
            textView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            textView.setPadding(10,0,10,0);
            Random random = new Random();
            int index = random.nextInt(6);
            textView.setText(list.get(index));
            switch (index % 3){
                case 0:
                    textView.setTextColor(Color.RED);
                    break;
                case 1:
                    textView.setTextColor(Color.BLUE);
                    break;
                case 2:
                    textView.setTextColor(Color.GREEN);
                    break;
            }
            binding.flowLayout.addView(textView);
        }

    }
}

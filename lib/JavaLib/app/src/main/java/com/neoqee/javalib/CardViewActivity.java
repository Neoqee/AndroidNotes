package com.neoqee.javalib;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.google.android.material.card.MaterialCardView;
import com.neoqee.javalib.databinding.ActivityCardViewBinding;

public class CardViewActivity extends AppCompatActivity {

    private ActivityCardViewBinding binding;
    private final static String tag = "CardViewActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCardViewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

//        binding.cardView.setCheckable(true);
        binding.cardView.setOnClickListener(v -> {
            Log.i(tag,"isChecked->" + binding.cardView.isChecked());
            binding.cardView.setChecked(!binding.cardView.isChecked());
        });
        binding.cardView.setOnCheckedChangeListener(new MaterialCardView.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(MaterialCardView card, boolean isChecked) {
                Log.i(tag,"isChecked->" + isChecked);
            }
        });

    }
}

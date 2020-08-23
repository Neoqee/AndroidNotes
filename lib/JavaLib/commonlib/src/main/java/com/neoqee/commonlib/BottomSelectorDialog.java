package com.neoqee.commonlib;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.neoqee.commonlib.databinding.DialogBottomSelectorBinding;

public class BottomSelectorDialog extends BottomSheetDialog {

    private DialogBottomSelectorBinding binding;

    public BottomSelectorDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DialogBottomSelectorBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.cancelAction.setOnClickListener(v -> dismiss());

    }
}

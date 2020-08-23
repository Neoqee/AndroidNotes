package com.neoqee.commonlib;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.neoqee.commonlib.databinding.ActivityBgaBinding;
import com.neoqee.commonlib.pickphoto.PickPhotoHelper;
import com.neoqee.commonlib.pickphoto.PickResultCallback;

import java.util.ArrayList;

import cn.bingoogolapple.photopicker.widget.BGASortableNinePhotoLayout;

public class BgaActivity extends AppCompatActivity {

    private ActivityBgaBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBgaBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.bgaLayout.setItemSpanCount(4);

        binding.bgaLayout.setDelegate(new BGASortableNinePhotoLayout.Delegate() {
            @Override
            public void onClickAddNinePhotoItem(BGASortableNinePhotoLayout sortableNinePhotoLayout, View view, int position, ArrayList<String> models) {
                PickPhotoHelper.getInstance().pick(BgaActivity.this, 0, new PickResultCallback() {
                    @Override
                    public void onResult(boolean isSuccess, String result) {
                        binding.bgaLayout.addLastItem(result);
                    }
                });
            }

            @Override
            public void onClickDeleteNinePhotoItem(BGASortableNinePhotoLayout sortableNinePhotoLayout, View view, int position, String model, ArrayList<String> models) {
                binding.bgaLayout.removeItem(position);
            }

            @Override
            public void onClickNinePhotoItem(BGASortableNinePhotoLayout sortableNinePhotoLayout, View view, int position, String model, ArrayList<String> models) {
                PreviewImgHelper.getInstance().preview(BgaActivity.this,binding.bgaLayout.getData(),position);
            }

            @Override
            public void onNinePhotoItemExchanged(BGASortableNinePhotoLayout sortableNinePhotoLayout, int fromPosition, int toPosition, ArrayList<String> models) {

            }
        });

    }
}

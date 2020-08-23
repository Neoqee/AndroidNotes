package com.neoqee.commonlib;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class GlideUtil {

    public static void display(Context context, String path, ImageView imageView){
        Glide.with(context).load(path).into(imageView);
    }

}

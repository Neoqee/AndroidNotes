package com.neoqee.javalib;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.gesture.GestureLibraries;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.webkit.MimeTypeMap;

import com.neoqee.commonlib.GlideUtil;
import com.neoqee.commonlib.pickphoto.PickPhotoHelper;
import com.neoqee.commonlib.pickphoto.PickResultCallback;
import com.neoqee.javalib.databinding.ActivityPickPhotoBinding;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

public class PickPhotoActivity extends AppCompatActivity {

    private ActivityPickPhotoBinding binding;
    private static final String TAG = "PickPhotoActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPickPhotoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.pick.setOnClickListener(v -> {
            PickPhotoHelper.getInstance().pick(PickPhotoActivity.this, 0, new PickResultCallback() {
                @Override
                public void onResult(boolean isSuccess, String result) {
                    if (isSuccess){
                        GlideUtil.display(PickPhotoActivity.this,result,binding.image);
                        save2Album(result);
                    }
                }
            });
        });

    }

    private void save2Album(String path){
        File file = new File(path);
        String name = file.getName();
        Log.i(TAG,"name="+name);
//        Uri fileUri = FileProvider.getUriForFile(this,
//                BuildConfig.APPLICATION_ID + ".FileProvider",
//                file);
        String extension = MimeTypeMap.getFileExtensionFromUrl(path);
        Log.i(TAG,"extension="+extension);
        if (extension != null){
            String type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
            Log.i(TAG,"type="+type);
            if (type != null){
                Log.i(TAG,"开始保存");
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DESCRIPTION, "This is an image");
                values.put(MediaStore.Images.Media.DISPLAY_NAME, name);
                values.put(MediaStore.Images.Media.MIME_TYPE, type);
                values.put(MediaStore.Images.Media.TITLE, name);
//                values.put(MediaStore.MediaColumns.RELATIVE_PATH,Environment.DIRECTORY_DCIM+"/Camera");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DCIM+"/Camera");//保存路径
                    values.put(MediaStore.MediaColumns.IS_PENDING, true);
                }
//                values.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES);

                Uri external = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
//                Uri external = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL);
                Log.i(TAG,"external="+external);
                Log.i(TAG,"getScheme="+external.getScheme());
                Log.i(TAG,"getAuthority="+external.getAuthority());
                ContentResolver resolver = getContentResolver();

                Uri insertUri = resolver.insert(external, values);
                Log.i(TAG,"insertUri="+insertUri);

                OutputStream os = null;
                try {
                    if (insertUri != null) {
                        os = resolver.openOutputStream(insertUri);
                    }
                    if (os != null) {
//                        final Bitmap bitmap = Bitmap.createBitmap(32, 32, Bitmap.Config.ARGB_8888);
                        final Bitmap bitmap = BitmapFactory.decodeFile(path);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 10, os);
                        Log.i(TAG,"保存成功");
                        binding.image.setImageBitmap(bitmap);
                        // write what you want
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
                            values.clear();
                            values.put(MediaStore.MediaColumns.IS_PENDING,false);
                            resolver.update(insertUri,values,null,null);
                        }
                    }else {
                        Log.i(TAG,"os==null");
                    }
                } catch (IOException e) {
                    Log.i("PickPhotoActivity","fail: " + e.getCause());
                } finally {
                    try {
                        if (os != null) {
                            os.close();
                        }
                    } catch (IOException e) {
                        Log.i("PickPhotoActivity","fail in close: " + e.getCause());
                    }
                }

            }
        }
    }

}

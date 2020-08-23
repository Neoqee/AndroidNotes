package com.neoqee.commonlib;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.neoqee.commonlib.pickphoto.PickResultCallback;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * ProjectName : JavaLib
 * PackageName : com.neoqee.commonlib
 * Create by 小孩 on 2020/6/21
 */
public class InvisibleFragment extends Fragment {

    private PermissionCallback callback;
    private PickResultCallback pickResultCallback;

    public void pickPhoto(int type, PickResultCallback callback){
        pickResultCallback = callback;
        if (type == 0){ //拍照
            requestPermission(Manifest.permission.CAMERA,101);
        }
//        else {     //相册
//            requestPermission(Manifest.permission.READ_EXTERNAL_STORAGE,102);
//        }
    }

    private void requestPermission(String permission, int requestCode){
        if (Build.VERSION.SDK_INT >= 23) {
            if (requireContext().checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{permission}, requestCode);
            } else {
                if (requestCode == 101) {
                    jumpToCamera();
                }
//                else if (requestCode == 102) {
//                    jumpToAlbum();
//                }
            }
        }else{
            if (requestCode == 101) {
                jumpToCamera();
            }
//            else if (requestCode == 102) {
//                jumpToAlbum();
//            }
        }
    }

    private void jumpToCamera(){
        dispatchTakePictureIntent();
//        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        if (intent.resolveActivity(requireContext().getPackageManager()) != null) {
//            startActivityForResult(intent,1);
//        }
    }

    public void requestNow(List<String> permissions,PermissionCallback callback){
        this.callback = callback;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            String[] permissions = new String[]{
//                    Manifest.permission.READ_EXTERNAL_STORAGE,
//                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
//                    Manifest.permission.CAMERA,
//                    Manifest.permission.READ_PHONE_STATE,
//                    Manifest.permission.ACCESS_FINE_LOCATION,
//                    Manifest.permission.ACCESS_COARSE_LOCATION
//            };
            int count = 0;
            for (String permission : permissions) {
                if (ContextCompat.checkSelfPermission(requireContext(),permission) != PackageManager.PERMISSION_GRANTED){
                    count += 1;
                }
            }
            if (count != 0){
                requireActivity().requestPermissions(permissions.toArray(new String[permissions.size()]),911);
            }else {
                this.callback.callback(true,null);
            }
        }else {
            this.callback.callback(true,null);
        }
//        requestPermissions(permissions.toArray(new String[permissions.size()]),998);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 911){
            List<String> deniedList = new ArrayList<>();
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED){
                    deniedList.add(permissions[i]);
                }
            }
            boolean allGranted = deniedList.isEmpty();
            callback.callback(allGranted,deniedList);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == -1){
            if (requestCode == 1){
//                if (data!=null){
//                    Log.i("InvisibleFragment","data:" + data);
//                    Uri uri = data.getData();
//                    if (uri != null){
//                        Log.i("InvisibleFragment","uri:" + uri);
//                    }else {
//                        Log.i("InvisibleFragment","uri:null");
//                    }
//                }else {
//                    Log.i("InvisibleFragment","data:null");
//                }
                pickResultCallback.onResult(true,currentPhotoPath);
            }
        }else if (resultCode == 0){
            pickResultCallback.onResult(false,null);
        }
    }


    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(requireContext().getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(
                        requireContext(),
                        BuildConfig.LIBRARY_PACKAGE_NAME+".FileProvider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
//                takePictureIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//                takePictureIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                startActivityForResult(takePictureIntent, 1);
            }
        }
    }

    private String currentPhotoPath;

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        Log.i("InvisibleFragment","currentPhotoPath:" + currentPhotoPath);
        return image;
    }

}

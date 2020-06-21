package com.neoqee.commonlib;

import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import uk.co.senab.photoview.PhotoView;

/**
 * ProjectName : JavaLib
 * PackageName : com.neoqee.commonlib
 * Create by 小孩 on 2020/6/21
 */
public class PagerPhotoAdapter extends ListAdapter<String, PagerPhotoAdapter.PagerPhotoViewHolder> {


    protected PagerPhotoAdapter() {
        super(new DiffUtil.ItemCallback<String>() {
            @Override
            public boolean areItemsTheSame(@NonNull String oldItem, @NonNull String newItem) {
                return oldItem.hashCode() == newItem.hashCode();
            }

            @Override
            public boolean areContentsTheSame(@NonNull String oldItem, @NonNull String newItem) {
                return oldItem.equals(newItem);
            }
        });
    }

    @NonNull
    @Override
    public PagerPhotoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.pager_photo_view, parent, false);
        return new PagerPhotoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PagerPhotoViewHolder holder, int position) {
        String item = getItem(position);
        Glide.with(holder.itemView).load(item).into(holder.photoView);
    }

    static class PagerPhotoViewHolder extends RecyclerView.ViewHolder{

        private PhotoView photoView;

        public PagerPhotoViewHolder(@NonNull View itemView) {
            super(itemView);

            photoView = itemView.findViewById(R.id.photoView);

        }
    }

}

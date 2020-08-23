package com.neoqee.commonlib;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

public class PhotoAdapter extends ListAdapter<String, RecyclerView.ViewHolder> {

    private boolean hasFooter = false;
    private OnDeleteClickListener onDeleteClickListener;
    private OnAddClickListener onAddClickListener;

    public PhotoAdapter() {
        super(new DiffUtil.ItemCallback<String>() {
            @Override
            public boolean areItemsTheSame(@NonNull String oldItem, @NonNull String newItem) {
                return oldItem.equals(newItem);
            }

            @Override
            public boolean areContentsTheSame(@NonNull String oldItem, @NonNull String newItem) {
                return oldItem.equals(newItem);
            }
        });
    }

    private void hideFooter(){
        if (hasFooter){
            notifyItemRemoved(getItemCount() - 1);
        }
        hasFooter = false;
    }
    private void showFooter(){
        if (hasFooter){
            notifyItemChanged(getItemCount() - 1);
        }else {
            hasFooter = true;
            notifyItemChanged(getItemCount() - 1);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == R.layout.adapter_photo){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_photo,parent,false);
            PhotoViewHolder holder = new PhotoViewHolder(view);
            holder.itemImg.setOnClickListener(v -> {
                PreviewImgHelper.getInstance().preview(parent.getContext(),getCurrentList(),holder.getAdapterPosition());
            });
            holder.itemDelete.setOnClickListener(v -> {
                if (null == onDeleteClickListener){
                    return;
                }
                onDeleteClickListener.deleteClick(holder.getAdapterPosition());
            });
            return holder;
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_photo_footer,parent,false);
            FootViewHolder holder = new FootViewHolder(view);
            holder.addItem.setOnClickListener(v -> {
                if (null == onAddClickListener){
                    return;
                }
                onAddClickListener.addClick();
            });
            return holder;
        }
    }

    public void addDeleteClickListener(OnDeleteClickListener listener){
        onDeleteClickListener = listener;
    }

    public void addAddClickListener(OnAddClickListener listener){
        onAddClickListener = listener;
    }

    @Override
    public int getItemViewType(int position) {
        return (hasFooter && position == getItemCount() - 1) ? R.layout.adapter_photo_footer : R.layout.adapter_photo;
    }

    @Override
    public int getItemCount() {
        return super.getItemCount() + (hasFooter ? 1 : 0);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

    }

    static class PhotoViewHolder extends RecyclerView.ViewHolder{

        private ImageView itemImg,itemDelete;

        public PhotoViewHolder(@NonNull View itemView) {
            super(itemView);
            itemImg = itemView.findViewById(R.id.itemImg);
            itemDelete = itemView.findViewById(R.id.itemDelete);
        }
    }

    static class FootViewHolder extends RecyclerView.ViewHolder{

        private ImageView addItem;
        public FootViewHolder(@NonNull View itemView) {
            super(itemView);
            addItem = itemView.findViewById(R.id.addItem);
        }
    }

}

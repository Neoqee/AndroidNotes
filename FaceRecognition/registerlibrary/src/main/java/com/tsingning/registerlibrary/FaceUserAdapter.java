package com.tsingning.registerlibrary;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.recyclerview.extensions.ListAdapter;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.tsingning.registerlibrary.model.User;
import com.tsingning.registerlibrary.utils.BitmapUtils;
import com.tsingning.registerlibrary.utils.FileUtils;

public class FaceUserAdapter extends ListAdapter<User, FaceUserAdapter.FaceUserViewHolder> {

    public FaceUserAdapter() {
        super(new DiffUtil.ItemCallback<User>() {
            @Override
            public boolean areItemsTheSame(@NonNull User user, @NonNull User t1) {
                return user.getId() == user.getId();
            }

            @Override
            public boolean areContentsTheSame(@NonNull User user, @NonNull User t1) {
                return false;
            }
        });
    }

    @NonNull
    @Override
    public FaceUserViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_register_user_list, viewGroup, false);
        FaceUserViewHolder viewHolder = new FaceUserViewHolder(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull FaceUserViewHolder holder, int position) {
        holder.nameTextView.setText(getItem(position).getUserName());
        Bitmap bitmap = BitmapFactory.decodeFile(FileUtils.getBatchImportSuccessDirectory()
                + "/" + getItem(position).getImageName());
        Bitmap descBmp = BitmapUtils.calculateInSampleSize(bitmap, 100, 100);
        if (descBmp != null) {
            holder.imageView.setImageBitmap(descBmp);
        }
    }

    static class FaceUserViewHolder extends RecyclerView.ViewHolder {

        private View itemView;
        private ImageView imageView;
        private TextView nameTextView;
        FaceUserViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
            imageView = itemView.findViewById(R.id.circle_user);
            nameTextView = itemView.findViewById(R.id.text_user_name);
        }
    }

}

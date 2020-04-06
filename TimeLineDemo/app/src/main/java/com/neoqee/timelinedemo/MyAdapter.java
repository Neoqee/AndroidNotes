package com.neoqee.timelinedemo;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

public class MyAdapter extends ListAdapter<DataBean, MyAdapter.MyViewHolder> {

    protected MyAdapter() {
        super(new DiffUtil.ItemCallback<DataBean>() {
            @Override
            public boolean areItemsTheSame(@NonNull DataBean oldItem, @NonNull DataBean newItem) {
                return false;
            }

            @Override
            public boolean areContentsTheSame(@NonNull DataBean oldItem, @NonNull DataBean newItem) {
                return false;
            }
        });
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recycler, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.textView.setText(getItem(position).getTime());
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        private TextView textView;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.textView);
        }
    }

}

package com.projects.automatedattendancesystem;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.projects.automatedattendancesystem.Pojo.TagsStatePojo;

import java.util.ArrayList;
import java.util.List;


public class MultiTagsSelectAdapter extends RecyclerView.Adapter<MultiTagsSelectAdapter.ViewHolder> {

    private List<TagsStatePojo> tagsList;

    private OnMultiTagsSelectedListener listener;


    public MultiTagsSelectAdapter(List<TagsStatePojo> tagsList, OnMultiTagsSelectedListener listener) {
        this.tagsList = tagsList;
        this.listener = listener;
    }

    public void refreshList(List<TagsStatePojo> tagsList)
    {
        this.tagsList = tagsList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MultiTagsSelectAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_item_chip, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MultiTagsSelectAdapter.ViewHolder holder, int position) {
        holder.updateUi(position);
    }

    @Override
    public int getItemCount() {
        return tagsList.size();
    }

    public interface OnMultiTagsSelectedListener {

        void onTagSelected(TagsStatePojo tagsStatePojo);

        void onTagRemoved(TagsStatePojo tagsStatePojo);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView tvStudId;

        TagsStatePojo tagsStatePojo;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvStudId = itemView.findViewById(R.id.tvStudId);
            tvStudId.setOnClickListener(this);
        }

        public void updateUi(int position) {
            tagsStatePojo = tagsList.get(position);
            tvStudId.setText(tagsList.get(position).getStud_Id());
            if (tagsList.get(position).isSelected()) {
                tvStudId.setTextColor(itemView.getResources().getColor(R.color.White));
                tvStudId.setBackgroundResource(R.drawable.red_background);
            } else if (!tagsList.get(position).isSelected()) {
                tvStudId.setTextColor(itemView.getResources().getColor(R.color.black));
                tvStudId.setBackgroundResource(R.drawable.gray_background);
            }
        }

        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.tvStudId) {
                if (tagsStatePojo.isSelected()) {
                    tagsStatePojo.setSelected(false);
                    tvStudId.setTextColor(itemView.getResources().getColor(R.color.black));
                    tvStudId.setBackgroundResource(R.drawable.gray_background);
                    listener.onTagRemoved(tagsStatePojo);
                } else if (!tagsStatePojo.isSelected()) {
                        tagsStatePojo.setSelected(true);
                        tvStudId.setTextColor(itemView.getResources().getColor(R.color.White));
                        tvStudId.setBackgroundResource(R.drawable.red_background);
                        listener.onTagSelected(tagsStatePojo);
                    }
                }
            }
        }
    }


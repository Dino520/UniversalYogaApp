package com.example.universalyogaapp.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.universalyogaapp.R;
import com.example.universalyogaapp.model.YogaCourse;

import java.util.List;

public class YogaClassAdapter extends RecyclerView.Adapter<YogaClassAdapter.ViewHolder> {

    private List<YogaCourse> yogaClasses;

    public YogaClassAdapter(List<YogaCourse> yogaClasses) {
        this.yogaClasses = yogaClasses;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_yoga_class, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        YogaCourse yogaClass = yogaClasses.get(position);
        holder.className.setText(yogaClass.getType());
        holder.classDetails.setText(yogaClass.getDay() + ", " + yogaClass.getTime());
    }

    @Override
    public int getItemCount() {
        return yogaClasses.size();
    }

    public void updateData(List<YogaCourse> newClasses) {
        yogaClasses = newClasses;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView className;
        TextView classDetails;
        TextView teacherName;
        TextView classDate;

        ViewHolder(View itemView) {
            super(itemView);
            className = itemView.findViewById(R.id.className);
            classDetails = itemView.findViewById(R.id.classDetails);
        }
    }
}

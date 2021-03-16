package com.example.project;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.MyViewHolder> {
    Context context;
    ArrayList<String> records_id, records_date, records_time, records_lowest, records_highest, records_calories;
    private OnItemClickListener m_listener;

    public interface OnItemClickListener {
        void onDeleteClick(String id, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        m_listener = listener;
    }

    CustomAdapter(Context context,
                  ArrayList<String> records_id,
                  ArrayList<String> records_date,
                  ArrayList<String> records_time,
                  ArrayList<String> records_lowest,
                  ArrayList<String> records_highest,
                  ArrayList<String> records_calories) {
        this.context = context;
        this.records_id = records_id;
        this.records_date = records_date;
        this.records_time = records_time;
        this.records_lowest = records_lowest;
        this.records_highest = records_highest;
        this.records_calories = records_calories;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.records_row, parent, false);

        return new MyViewHolder(view, m_listener);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomAdapter.MyViewHolder holder, int position) {
        holder.id = String.valueOf(records_id.get(position));
        holder.record_date.setText(String.valueOf(records_date.get(position)));
        holder.record_time.setText(String.valueOf(records_time.get(position)));
        holder.record_lowest.setText(String.valueOf(records_lowest.get(position)));
        holder.record_highest.setText(String.valueOf(records_highest.get(position)));
        holder.record_calories.setText(String.valueOf(records_calories.get(position)));
    }

    @Override
    public int getItemCount() {
        return records_date.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        public ImageView delete_image;
        TextView record_date, record_time, record_lowest, record_highest, record_calories;
        String id;

        public MyViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            delete_image = itemView.findViewById(R.id.image_delete);
            record_date = itemView.findViewById(R.id.records_date_text);
            record_time = itemView.findViewById(R.id.records_time_text);
            record_lowest = itemView.findViewById(R.id.records_lowest_text);
            record_highest = itemView.findViewById(R.id.records_highest_text);
            record_calories = itemView.findViewById(R.id.records_calories_text);

            delete_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION)
                            listener.onDeleteClick(id, position);
                    }
                }
            });
        }
    }
}

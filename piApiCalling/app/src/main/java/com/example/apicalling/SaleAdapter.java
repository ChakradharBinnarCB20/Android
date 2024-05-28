package com.example.apicalling;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class SaleAdapter extends RecyclerView.Adapter<SaleAdapter.ViewHolder> {

    private List<SaleReport> data;

    public SaleAdapter(List<SaleReport> data) {
        this.data = data;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewDate;
        TextView textViewAmount;

        ViewHolder(View itemView) {
            super(itemView);
            textViewDate = itemView.findViewById(R.id.textViewDate);
            textViewAmount = itemView.findViewById(R.id.textViewAmount);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sale_report, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SaleReport item = data.get(position);
        holder.textViewDate.setText(item.getDOCDT());
        holder.textViewAmount.setText("Amount: " + item.getAMOUNT());
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void setData(List<SaleReport> newData) {
        data.clear();
        data.addAll(newData);
        notifyDataSetChanged();
    }
}
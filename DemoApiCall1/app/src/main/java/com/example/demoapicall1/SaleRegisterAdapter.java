package com.example.demoapicall1;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

//public class SaleRegisterAdapter extends RecyclerView.Adapter<SaleRegisterAdapter.ViewHolder> {
//
//    private List<SaleRegisterItem> itemList;
//
//    public SaleRegisterAdapter(List<SaleRegisterItem> itemList) {
//        this.itemList = itemList;
//    }
//
//    @NonNull
//    @Override
//    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout, parent, false);
//        return new ViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
//        SaleRegisterItem item = itemList.get(position);
//        holder.docDate.setText("Document Date: " + item.getDocDate());
//        holder.amt1.setText("Amount 1: " + item.getAmount1());
//        holder.amt2.setText("Amount 2: " + item.getAmount2());
//        holder.amt3.setText("Amount 3: " + item.getAmount3());
//        holder.amt4.setText("Amount 4: " + item.getAmount4());
//        holder.amt5.setText("Amount 5: " + item.getAmount5());
//        holder.amt6.setText("Amount 6: " + item.getAmount6());
//        holder.disAmount.setText("Dis amount: " + item.getDiscountAmount());
//        holder.amt.setText("Amount: " + item.getTotalAmount());
//        // Bind other data fields here
//    }
//
//    @Override
//    public int getItemCount() {
//        return itemList.size();
//    }
//
//    public static class ViewHolder extends RecyclerView.ViewHolder {
//        TextView docDate, amt1, amt2, amt3, amt4, amt5, amt6, disAmount, amt, docDate2;
//
//        public ViewHolder(@NonNull View itemView) {
//            super(itemView);
//            docDate = itemView.findViewById(R.id.doc_date);
//            amt1 = itemView.findViewById(R.id.amount_1);
//            amt2 = itemView.findViewById(R.id.amount_2);
//            amt3 = itemView.findViewById(R.id.amount_3);
//            amt4 = itemView.findViewById(R.id.amount_4);
//            amt5 = itemView.findViewById(R.id.amount_5);
//            amt6 = itemView.findViewById(R.id.amount_6);
//            disAmount = itemView.findViewById(R.id.dis_amount);
//            amt = itemView.findViewById(R.id.amount);
//            //docDate2 = itemView.findViewById(R.id.doc_date_2);
//            // Initialize other TextViews here
//        }
//    }
//}

public class SaleRegisterAdapter extends RecyclerView.Adapter<SaleRegisterAdapter.ViewHolder> {

    private List<SaleRegisterItem> itemList;

    public SaleRegisterAdapter() {
        this.itemList = new ArrayList<>();
    }

    public void addItem(SaleRegisterItem item) {
        itemList.add(item);
        notifyItemInserted(itemList.size() - 1);
    }

    public void setItems(List<SaleRegisterItem> items) {
        itemList.clear();
        itemList.addAll(items);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SaleRegisterItem item = itemList.get(position);
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView docDate, amt1, amt2, amt3, amt4, amt5, amt6, disAmount, amt, docDate2;
        // Add more TextViews for other data fields

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            docDate = itemView.findViewById(R.id.doc_date);
            amt1 = itemView.findViewById(R.id.amount_1);
            amt2 = itemView.findViewById(R.id.amount_2);
            amt3 = itemView.findViewById(R.id.amount_3);
            amt4 = itemView.findViewById(R.id.amount_4);
            amt5 = itemView.findViewById(R.id.amount_5);
            amt6 = itemView.findViewById(R.id.amount_6);
            disAmount = itemView.findViewById(R.id.dis_amount);
            amt = itemView.findViewById(R.id.amount);
            // Initialize other TextViews here
        }

        public void bind(SaleRegisterItem item) {
           docDate.setText("Document Date: " + item.getDocDate());
            amt1.setText("Amount 1: " + item.getAmount1());
           amt2.setText("Amount 2: " + item.getAmount2());
            amt3.setText("Amount 3: " + item.getAmount3());
           amt4.setText("Amount 4: " + item.getAmount4());
            amt5.setText("Amount 5: " + item.getAmount5());
           amt6.setText("Amount 6: " + item.getAmount6());
           disAmount.setText("Dis amount: " + item.getDiscountAmount());
           amt.setText("Amount: " + item.getTotalAmount());
            // Bind other data fields here
        }
    }
}

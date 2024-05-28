package com.example.demoapicall;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class SaleRegisterAdapter extends RecyclerView.Adapter<SaleRegisterAdapter.ViewHolder> {

    private List<SaleRegister> saleRegisters;

    public void setData(List<SaleRegister> saleRegisters) {
        this.saleRegisters = saleRegisters;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sale_register, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SaleRegister saleRegister = saleRegisters.get(position);
        holder.bind(saleRegister);
    }

    @Override
    public int getItemCount() {
        return saleRegisters != null ? saleRegisters.size() : 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView docDtTextView;
        private TextView amountTextView;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            docDtTextView = itemView.findViewById(R.id.docDtTextView);
            amountTextView = itemView.findViewById(R.id.amountTextView);
        }

        void bind(SaleRegister saleRegister) {
            docDtTextView.setText(saleRegister.getDocDt());
            amountTextView.setText(saleRegister.getAmount());
        }
    }
}

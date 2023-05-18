package com.marcosviniciusferreira.casaflow.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.marcosviniciusferreira.casaflow.R;
import com.marcosviniciusferreira.casaflow.model.Transaction;

import java.util.List;

public class AdapterTransactions extends RecyclerView.Adapter<AdapterTransactions.MyViewHolder> {

    List<Transaction> transactions;
    Context context;

    public AdapterTransactions(List<Transaction> transactions, Context context) {
        this.transactions = transactions;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View listItem = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_transaction, parent, false);
        return new MyViewHolder(listItem);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        Transaction transaction = transactions.get(position);

        holder.title.setText(transaction.getTitle());
        holder.value.setText(String.valueOf(transaction.getValue()));
        holder.category.setText(transaction.getCategory());

        textColorSettings(holder, transaction);

    }

    private void textColorSettings(MyViewHolder holder, Transaction transaction) {

        holder.value.setTextColor(context.getResources().getColor(R.color.green_check));
        holder.value.setText(String.valueOf(transaction.getValue()).replace(".", ","));

        if (transaction.getType() == "EXPENSE" || transaction.getType().equals("EXPENSE")) {
            holder.value.setTextColor(context.getResources().getColor(R.color.red_uncheck));
            holder.value.setText("-" + String.valueOf(transaction.getValue()).replace(".", ","));
        }
    }

    @Override
    public int getItemCount() {
        return transactions.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView title, value, category;

        public MyViewHolder(View view) {
            super(view);

            title = view.findViewById(R.id.textAdapterTitle);
            category = view.findViewById(R.id.textAdapterCategory);
            value = view.findViewById(R.id.textAdapterValue);


        }

    }
}

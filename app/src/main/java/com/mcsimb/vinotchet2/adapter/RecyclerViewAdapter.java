package com.mcsimb.vinotchet2.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.mcsimb.vinotchet2.adapter.RecyclerViewAdapter.ViewHolder;
import com.mcsimb.vinotchet2.common.Item;
import java.util.ArrayList;
import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<ViewHolder> {

  private final List<Item> itemList;
  private final int layoutId;
  private final OnItemClickListener listener;

  public RecyclerViewAdapter(int layoutId, OnItemClickListener listener) {
    this.layoutId = layoutId;
    this.listener = listener;
    this.itemList = new ArrayList<>();
  }

  public void setItems(List<? extends Item> items) {
    int size = itemList.size();
    itemList.clear();
    itemList.addAll(items);
    notifyItemRangeChanged(0, size);
  }

  public void remove(int position) {
    itemList.remove(position);
    notifyItemRemoved(position);
  }

  @Override
  @NonNull
  public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false);
    return new ViewHolder(view);
  }

  @Override
  public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
    itemList.get(position).bindView(holder.view);
    holder.view.setOnClickListener(v -> listener.onItemClick(holder.getAdapterPosition()));
    holder.view.setOnLongClickListener(
        v -> {
          listener.onItemLongClick(v, holder.getAdapterPosition());
          return true;
        });
  }

  @Override
  public int getItemCount() {
    return itemList.size();
  }

  // TODO: 14.02.2023 remove method
  public Item getItem(int position) {
    return itemList.get(position);
  }

  public interface OnItemClickListener {

    void onItemClick(int position);

    void onItemLongClick(View view, int position);
  }

  public static class ViewHolder extends RecyclerView.ViewHolder {

    final View view;

    public ViewHolder(@NonNull View view) {
      super(view);
      this.view = view;
    }
  }
}

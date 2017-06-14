package com.mcsimb.vinotchet2;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

class MainRecyclerAdapter extends RecyclerView.Adapter<MainRecyclerAdapter.ViewHolder> {

	private ArrayList<MainRecyclerItem> recyclerItems;

	MainRecyclerAdapter(CharSequence day) {
		recyclerItems = MainRecyclerItem.getDayItems(day);
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
		View v = LayoutInflater.from(viewGroup.getContext())
				.inflate(R.layout.list_item_main, viewGroup, false);
		return new ViewHolder(v);
	}

	@Override
	public void onBindViewHolder(ViewHolder viewHolder, final int position) {
		viewHolder.getTextViewWine().setText(recyclerItems.get(position).wine);
		viewHolder.getTextViewCounter1().setText(recyclerItems.get(position).counter1);
		viewHolder.getTextViewCounter2().setText(recyclerItems.get(position).counter2);
		viewHolder.getImageViewLabel().setImageBitmap(recyclerItems.get(position).label);
	}

	@Override
	public int getItemCount() {
		return recyclerItems.size();
	}


	static class ViewHolder extends RecyclerView.ViewHolder {

		private final TextView textViewWine;
		private final TextView textViewCounter1;
		private final TextView textViewCounter2;
		private final ImageView imageViewLabel;

		ViewHolder(View view) {
			super(view);
			view.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Toast.makeText(v.getContext(), "click", Toast.LENGTH_SHORT).show();
				}
			});
			textViewWine = (TextView) view.findViewById(R.id.text_wine_main);
			textViewCounter1 = (TextView) view.findViewById(R.id.text_counter1_main);
			textViewCounter2 = (TextView) view.findViewById(R.id.text_counter2_main);
			imageViewLabel = (ImageView) view.findViewById(R.id.ic_main);
		}

		TextView getTextViewWine() {
			return textViewWine;
		}

		TextView getTextViewCounter1() {
			return textViewCounter1;
		}

		TextView getTextViewCounter2() {
			return textViewCounter2;
		}

		ImageView getImageViewLabel() {
			return imageViewLabel;
		}
	}

}

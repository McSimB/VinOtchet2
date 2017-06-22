package com.mcsimb.vinotchet2;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mcsimb.vinotchet2.MainFragment.OnFragmentInteractionListener;

import java.util.List;
import android.view.GestureDetector.OnContextClickListener;
import android.view.MotionEvent;
import android.view.View.OnLongClickListener;

public class MainRecyclerAdapter extends RecyclerView.Adapter<MainRecyclerAdapter.ViewHolder> {

	private List<RecyclerContent.Item> mItems;
	private final OnFragmentInteractionListener mListener;

	public MainRecyclerAdapter(List<RecyclerContent.Item> items, OnFragmentInteractionListener listener) {
		mItems = items;
		mListener = listener;
	}

	public void setItems(List<RecyclerContent.Item> items) {
		mItems = items;
		notifyDataSetChanged();
	}
	
	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext())
				.inflate(R.layout.recycler_item_main, parent, false);
		return new ViewHolder(view);
	}

	@Override
	public void onBindViewHolder(final ViewHolder holder, int position) {
		holder.mItem = mItems.get(position);
		holder.mWineView.setText(mItems.get(position).wine);
		holder.mCounter1View.setText(mItems.get(position).counter1);
		holder.mCounter2View.setText(mItems.get(position).counter2);
		holder.mIconView.setImageResource(mItems.get(position).iconId);

		holder.mView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (null != mListener) {
					// Notify the active callbacks interface (the activity, if the
					// fragment is attached to one) that an item has been selected.
					mListener.onFragmentInteraction(holder.mItem);
				}
			}
		});
		
		
		holder.mView.setOnLongClickListener(new OnLongClickListener() {
				@Override
				public boolean onLongClick(View v) {
					
					return false;
				}
			});
	}

	@Override
	public int getItemCount() {
		return mItems.size();
	}

	class ViewHolder extends RecyclerView.ViewHolder {

		final View mView;
		final TextView mWineView;
		final TextView mCounter1View;
		final TextView mCounter2View;
		final ImageView mIconView;
		RecyclerContent.Item mItem;

		ViewHolder(View view) {
			super(view);
			mView = view;
			mWineView = (TextView) view.findViewById(R.id.text_wine_main);
			mCounter1View = (TextView) view.findViewById(R.id.text_counter1_main);
			mCounter2View = (TextView) view.findViewById(R.id.text_counter2_main);
			mIconView = (ImageView) view.findViewById(R.id.ic_main);
		}
	}

}

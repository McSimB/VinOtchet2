package com.mcsimb.vinotchet2;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.mcsimb.vinotchet2.MainFragment.OnFragmentInteractionListener;
import java.util.List;
import android.graphics.drawable.Drawable;

public class MainRecyclerAdapter extends RecyclerView.Adapter<MainRecyclerAdapter.ViewHolder> {

	private final List<RecyclerContent.Item> mValues;
	private final OnFragmentInteractionListener mListener;

	public MainRecyclerAdapter(List<RecyclerContent.Item> items, OnFragmentInteractionListener listener) {
		mValues = items;
		mListener = listener;
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext())
			.inflate(R.layout.recycler_item_main, parent, false);
		return new ViewHolder(view);
	}

	@Override
	public void onBindViewHolder(final ViewHolder holder, int position) {
		holder.mItem = mValues.get(position);
		holder.mWineView.setText(mValues.get(position).wine);
		holder.mCounter1View.setText(mValues.get(position).counter1);
		holder.mCounter2View.setText(mValues.get(position).counter2);

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
	}

	@Override
	public int getItemCount() {
		return mValues.size();
	}

	public class ViewHolder extends RecyclerView.ViewHolder {

		public final View mView;
		public final TextView mWineView;
		public final TextView mCounter1View;
		public final TextView mCounter2View;
		//public final Drawable mIcon;
		public RecyclerContent.Item mItem;

		public ViewHolder(View view) {
			super(view);
			mView = view;
			mWineView = (TextView) view.findViewById(R.id.text_wine_main);
			mCounter1View = (TextView) view.findViewById(R.id.text_counter1_main);
			mCounter2View = (TextView) view.findViewById(R.id.text_counter2_main);
			//mIcon = = (Drawable) view.findViewById(R.id.ic_main);
		}
	}

}

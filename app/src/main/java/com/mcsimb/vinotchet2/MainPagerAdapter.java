package com.mcsimb.vinotchet2;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

class MainPagerAdapter extends PagerAdapter {

	private ArrayList<String> days = new ArrayList<>();
	private Context context;

	MainPagerAdapter(Context context) {
		this.context = context;
	}

	void addPage(String day) {
		days.add(day);
		notifyDataSetChanged();
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rootView = inflater.inflate(R.layout.page_main, container, false);

		RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_main);
		RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);
		recyclerView.setLayoutManager(layoutManager);

		MainRecyclerAdapter recyclerAdapter = new MainRecyclerAdapter(getPageTitle(position));
		recyclerView.setAdapter(recyclerAdapter);

		container.addView(rootView);

		return rootView;
	}

	@Override
	public int getCount() {
		return days.size();
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view == object;
	}

	@Override
	public CharSequence getPageTitle(int position) {
		return days.get(position);
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView((View) object);
	}

}

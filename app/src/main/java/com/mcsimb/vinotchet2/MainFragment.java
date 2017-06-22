package com.mcsimb.vinotchet2;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;
import android.widget.Toast;
import android.view.View.OnTouchListener;
import android.view.MotionEvent;

public class MainFragment extends Fragment {

	private static final String ARG_POSITION = "position";
	private OnFragmentInteractionListener mListener;

	public MainFragment() {
	}

	public static MainFragment newInstance(int position) {
		MainFragment fragment = new MainFragment();
		Bundle arguments = new Bundle();
		arguments.putInt(ARG_POSITION, position);
		fragment.setArguments(arguments);
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		Bundle arguments = getArguments();
		final int position = arguments.getInt(ARG_POSITION);
		View view = inflater.inflate(R.layout.fragment_main, container, false);
		if (view instanceof RecyclerView) {
			final Context context = view.getContext();
			final RecyclerView recyclerView = (RecyclerView) view;
			recyclerView.setLayoutManager(new LinearLayoutManager(context));
			recyclerView.setAdapter(
				new MainRecyclerAdapter(RecyclerContent.getItems(context, position), mListener));
		}
		return view;
	}
	
	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		if (context instanceof OnFragmentInteractionListener) {
			mListener = (OnFragmentInteractionListener) context;
		} else {
			throw new RuntimeException(context.toString()
									   + " must implement OnFragmentInteractionListener");
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mListener = null;
	}

	/**
	 * This interface must be implemented by activities that contain this
	 * fragment to allow an interaction in this fragment to be communicated
	 * to the activity and potentially other fragments contained in that
	 * activity.
	 * <p/>
	 * See the Android Training lesson <a href=
	 * "http://developer.android.com/training/basics/fragments/communicating.html"
	 * >Communicating with Other Fragments</a> for more information.
	 */
	public interface OnFragmentInteractionListener {
		void onFragmentInteraction(RecyclerContent.Item item);
	}

	public static class MainFragmentStatePagerAdapter extends FragmentStatePagerAdapter {

		private List<String> mPages;

		public MainFragmentStatePagerAdapter(FragmentManager fm) {
			super(fm);
		}

		public void setPages(List<String> pages) {
			mPages = pages;
			notifyDataSetChanged();
		}
		
		@Override
		public Fragment getItem(int position) {
			return MainFragment.newInstance(position);
		}

		@Override
		public int getCount() {
			return mPages.size();
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return mPages.get(position) + "." + MainActivity.MONTH +
				"." + MainActivity.YEAR;
		}
	}

}

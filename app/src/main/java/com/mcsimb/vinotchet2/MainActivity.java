package com.mcsimb.vinotchet2;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import java.util.Calendar;
import java.util.List;
import android.widget.PopupMenu;
import android.view.MenuInflater;

public class MainActivity extends AppCompatActivity
implements MainFragment.OnFragmentInteractionListener {

	private final static int REQUEST_CODE = 1;

	private MainFragment.MainFragmentStatePagerAdapter mFragmentStatePagerAdapter;
	private TabLayout mTabLayout;
	private ViewPager mPager;
	private DBHelper mDBHelper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_main);
		setSupportActionBar(toolbar);

		mDBHelper = new DBHelper(this);

		mFragmentStatePagerAdapter = new MainFragment
			.MainFragmentStatePagerAdapter(getSupportFragmentManager());
		mFragmentStatePagerAdapter.setPages(mDBHelper.getDays());
		mPager = (ViewPager) findViewById(R.id.pager_main);
		mPager.setAdapter(mFragmentStatePagerAdapter);
		mTabLayout = (TabLayout) findViewById(R.id.tabs_main);
		mTabLayout.setupWithViewPager(mPager);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.menu_add_main) {
			Intent intent = new Intent(this, AddActivity.class);
			startActivityForResult(intent, REQUEST_CODE);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onFragmentInteraction(final MainRecyclerAdapter.ViewHolder holder, final int position) {
		PopupMenu popup = new PopupMenu(this, holder.mView);
		MenuInflater inflater = popup.getMenuInflater();
		inflater.inflate(R.menu.popup_menu_main, popup.getMenu());
		popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
				@Override
				public boolean onMenuItemClick(MenuItem item) {
					switch (item.getItemId()) {
						case R.id.popup_menu_remove_main:
							RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler);
							MainRecyclerAdapter adapter = (MainRecyclerAdapter) recyclerView.getAdapter();
							adapter.removeItem(position);
							mDBHelper.deleteEntry(holder.item.id);
							break;
					}
					return false;
				}
			});
		popup.show();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			List<String> days = mDBHelper.getDays();
			mFragmentStatePagerAdapter.setPages(days);
			//mPager.setCurrentItem(days.size() - 1);
			RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler);
			MainRecyclerAdapter adapter = (MainRecyclerAdapter) recyclerView.getAdapter();
			adapter.setItems(mDBHelper.getItems(days.size() - 1));
			
		}
	}

	@Override
	protected void onDestroy() {
		mDBHelper.close();
		super.onDestroy();
	}

	@Override
	public void onLowMemory() {
		mDBHelper.close();
		super.onLowMemory();
	}

}

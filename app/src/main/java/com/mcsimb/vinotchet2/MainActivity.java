package com.mcsimb.vinotchet2;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import android.util.Log;
import java.util.List;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.CardView;

public class MainActivity extends AppCompatActivity
		implements MainFragment.OnFragmentInteractionListener {

	public static String MONTH = "06";
	public static String YEAR = "17";
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
		//dbHelper.newMonth(MONTH);

		mFragmentStatePagerAdapter = new MainFragment
				.MainFragmentStatePagerAdapter(getSupportFragmentManager());
		mFragmentStatePagerAdapter.setPages(mDBHelper.getDays());
		mPager = (ViewPager) findViewById(R.id.pager_main);
		mPager.setAdapter(mFragmentStatePagerAdapter);
		mTabLayout = (TabLayout) findViewById(R.id.tabs_main);
		mTabLayout.setupWithViewPager(mPager);
		
		//CardView card = (CardView) findViewById(R.id.card_main);
		//registerForContextMenu(card);
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
	public void onFragmentInteraction(RecyclerContent.Item item) {
		Toast.makeText(this, "dummy", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			List<String> days = mDBHelper.getDays();
			mFragmentStatePagerAdapter.setPages(days);
			RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler);
			MainRecyclerAdapter adapter = (MainRecyclerAdapter) recyclerView.getAdapter();
			adapter.setItems(RecyclerContent.getItems(this, days.size() - 1));
			mPager.setCurrentItem(days.size() - 1);
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

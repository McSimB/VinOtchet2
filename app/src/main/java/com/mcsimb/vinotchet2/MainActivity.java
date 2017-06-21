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

public class MainActivity extends AppCompatActivity
		implements MainFragment.OnFragmentInteractionListener {

	public static String MONTH = "06";
	public static String YEAR = "2017";
	private final static int REQUEST_CODE = 1;
			
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_main);
		setSupportActionBar(toolbar);

		//FileUtils.verifyStoragePermissions(this);
		//FileUtils.pathExists();
		//MainControl mControl = new MainControl();
		//mControl.initControl("05");

		DBHelper dbHelper = new DBHelper(this);
		//dbHelper.newMonth(MONTH);

		MainFragment.MainFragmentStatePagerAdapter mFragmentStatePagerAdapter = new MainFragment
				.MainFragmentStatePagerAdapter(getSupportFragmentManager(), dbHelper.getDays());
		ViewPager mPager = (ViewPager) findViewById(R.id.pager_main);
		mPager.setAdapter(mFragmentStatePagerAdapter);

		TabLayout mTabLayout = (TabLayout) findViewById(R.id.tabs_main);
		mTabLayout.setupWithViewPager(mPager);

		dbHelper.close();
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

}

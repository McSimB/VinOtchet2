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

	private MainFragment.MainFragmentStatePagerAdapter mFragmentStatePagerAdapter;
	private ViewPager mPager;
	private TabLayout mTabLayout;
	private MainControl mControl;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_main);
		setSupportActionBar(toolbar);

		FileUtils.verifyStoragePermissions(this);
		FileUtils.pathExists();
		mControl = new MainControl();
		mControl.initControl("05");
		//DBHelper dbHelper = new DBHelper(this);
		//SQLiteDatabase db = openOrCreateDatabase(DB_NAME, Context.MODE_PRIVATE, null);

		mFragmentStatePagerAdapter = new MainFragment
				.MainFragmentStatePagerAdapter(getSupportFragmentManager());
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
			if (!FileUtils.dataBase.isEmpty()) {
				intent.putExtra("day", FileUtils.dataBase.get(FileUtils.dataBase.size() - 1)[0]);
			} else
				intent.putExtra("day", "1");
			intent.putExtra("month", FileUtils.month);
			intent.putExtra("year", "17");
			intent.putExtra("sort_list",
					FileUtils.winesList.keySet()
							.toArray(new String[FileUtils.winesList.size()]));
			startActivityForResult(intent, 1);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onFragmentInteraction(RecyclerContent.Item item) {
		Toast.makeText(this, "dummy", Toast.LENGTH_SHORT).show();
	}

}

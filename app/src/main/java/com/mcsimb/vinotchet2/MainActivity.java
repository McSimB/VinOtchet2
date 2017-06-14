package com.mcsimb.vinotchet2;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.mcsimb.vinotchet2.dummy.DummyContent;

public class MainActivity extends AppCompatActivity
		implements MainFragment.OnFragmentInteractionListener {

	private MainFragmentStatePagerAdapter mFragmentStatePagerAdapter;
	private ViewPager mPager;
	private TabLayout mTabLayout;
	private MainControl mControl;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_main);
		setSupportActionBar(toolbar);

		//FileUtils.verifyStoragePermissions(this);
		//FileUtils.pathExists();
		//mControl = new MainControl();
		//mControl.initControl("05");

		mFragmentStatePagerAdapter = new MainFragmentStatePagerAdapter(getSupportFragmentManager());
		mPager = (ViewPager) findViewById(R.id.pager_main);
		mPager.setAdapter(mFragmentStatePagerAdapter);

		mTabLayout = (TabLayout) findViewById(R.id.tabs_main);
		mTabLayout.setupWithViewPager(mPager);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		//getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		//noinspection SimplifiableIfStatement
		//if (id == R.id.action_settings) {
		//	return true;
		//}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onFragmentInteraction(DummyContent.DummyItem item) {
		Toast.makeText(this, "dummy", Toast.LENGTH_SHORT).show();
	}

	public class MainFragmentStatePagerAdapter extends FragmentStatePagerAdapter {

		MainFragmentStatePagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			return MainFragment.newInstance(position);
		}

		@Override
		public int getCount() {
			return 25;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return position + "";
		}
	}

}

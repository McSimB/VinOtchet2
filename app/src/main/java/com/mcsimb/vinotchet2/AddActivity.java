package com.mcsimb.vinotchet2;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class AddActivity extends AppCompatActivity implements View.OnClickListener {

	private EditText mSetDate;
	private String mCurrentDate;
	private String mWine;
	private String mVol;
	private EditText mCounter1;
	private EditText mCounter2;

	@Override
	protected void onCreate(Bundle savedInstantState) {
		super.onCreate(savedInstantState);
		setContentView(R.layout.activity_add);

		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_add);
		setSupportActionBar(toolbar);

		Intent intent = getIntent();
		final String[] wineList = intent.getStringArrayExtra("wine_list");
		final String[] volList = {"0,5", "0,7"};
		mCounter1 = (EditText) findViewById(R.id.text_counter1_add);
		mCounter2 = (EditText) findViewById(R.id.text_counter2_add);
		mSetDate = (EditText) findViewById(R.id.text_set_date_add);
		mCurrentDate = intent.getStringExtra("day");
		TextView monthYear = (TextView) findViewById(R.id.text_month_year_add);
		mSetDate.setText(mCurrentDate);
		monthYear.setText("." + intent.getStringExtra("month") + "." + intent.getStringExtra("year"));
		ArrayAdapter<String> adapterSort = new ArrayAdapter<>(this,
				android.R.layout.simple_dropdown_item_1line, wineList);
		final Spinner spinnerWine = (Spinner) findViewById(R.id.spinner_set_wine_add);
		spinnerWine.setAdapter(adapterSort);

		spinnerWine.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				mWine = wineList[position];
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});

		spinnerWine.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				InputMethodManager imm = (InputMethodManager)
						getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
				return false;
			}
		});

		ArrayAdapter<String> adapterVol = new ArrayAdapter<>(this,
				android.R.layout.simple_dropdown_item_1line, volList);
		Spinner spinnerVol = (Spinner) findViewById(R.id.spinner_set_vol_add);
		spinnerVol.setAdapter(adapterVol);
		spinnerVol.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				mVol = volList[position].replace(",", ".");
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});

		spinnerVol.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				InputMethodManager imm = (InputMethodManager)
						getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
				return false;
			}
		});
	}

	@Override
	public void onClick(View v) {
		Intent intent = new Intent();
		switch (v.getId()) {
			case R.id.button_ok_add:
				if (Integer.decode(mSetDate.getText().toString()) < Integer.decode(mCurrentDate)) {
					Toast.makeText(AddActivity.this, R.string.bad_day, Toast.LENGTH_SHORT).show();
					break;
				}
				if ((mCounter1.getText().toString().equals("")) ||
						(mCounter2.getText().toString().equals(""))) {
					Toast.makeText(AddActivity.this, R.string.bad_counter, Toast.LENGTH_SHORT).show();
					break;
				}
				intent.putExtra("day", mSetDate.getText().toString());
				intent.putExtra("wine", mWine);
				intent.putExtra("vol", mVol);
				intent.putExtra("counter1", mCounter1.getText().toString());
				intent.putExtra("counter2", mCounter2.getText().toString());
				setResult(RESULT_OK, intent);
				finish();
			case R.id.button_cancel_add:
				finish();
		}
	}

}

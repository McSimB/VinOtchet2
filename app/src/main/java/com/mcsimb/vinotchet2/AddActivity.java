package com.mcsimb.vinotchet2;

import android.content.ContentValues;
import android.content.Context;
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

import static com.mcsimb.vinotchet2.MainActivity.MONTH;
import static com.mcsimb.vinotchet2.MainActivity.YEAR;

public class AddActivity extends AppCompatActivity implements View.OnClickListener {

	private EditText mSetDate;
	private String mCurrentDate;
	private int mWineId;
	private String mVolume;
	private EditText mCounter1;
	private EditText mCounter2;
	private DBHelper mDBHelper;

	@Override
	protected void onCreate(Bundle savedInstantState) {
		super.onCreate(savedInstantState);
		setContentView(R.layout.activity_add);

		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_add);
		setSupportActionBar(toolbar);

		mDBHelper = new DBHelper(this);
		final String[] sorts = mDBHelper.getSorts();
		final String[] volumes = {"0,5", "0,7"};
		mCounter1 = (EditText) findViewById(R.id.text_counter1_add);
		mCounter2 = (EditText) findViewById(R.id.text_counter2_add);
		mSetDate = (EditText) findViewById(R.id.text_set_date_add);
		mCurrentDate = "1";
		TextView monthYear = (TextView) findViewById(R.id.text_month_year_add);
		mSetDate.setText(mCurrentDate);
		monthYear.setText("." + MONTH + "." + YEAR);
		ArrayAdapter<String> adapterSort = new ArrayAdapter<>(this,
				android.R.layout.simple_dropdown_item_1line, sorts);
		final Spinner spinnerWine = (Spinner) findViewById(R.id.spinner_set_wine_add);
		spinnerWine.setAdapter(adapterSort);

		spinnerWine.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				mWineId = position + 1;
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
				android.R.layout.simple_dropdown_item_1line, volumes);
		Spinner spinnerVol = (Spinner) findViewById(R.id.spinner_set_vol_add);
		spinnerVol.setAdapter(adapterVol);
		spinnerVol.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				mVolume = volumes[position].replace(",", ".");
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});

		/*spinnerVol.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				InputMethodManager imm = (InputMethodManager)
						getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
				return false;
			}
		});*/
	}

	@Override
	public void onClick(View v) {
		ContentValues values = new ContentValues();
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
				values.put(DBHelper.DAY, mSetDate.getText().toString());
				values.put(DBHelper.WINE_ID, mWineId);
				values.put(DBHelper.VOLUME, mVolume);
				values.put(DBHelper.COUNTER_1, mCounter1.getText().toString());
				values.put(DBHelper.COUNTER_2, mCounter2.getText().toString());
				mDBHelper.addEntry(values);
				exit();
			case R.id.button_cancel_add:
				exit();
		}
	}

	private void exit() {
		mDBHelper.close();
		finish();
	}

}

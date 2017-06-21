package com.mcsimb.vinotchet2;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

class RecyclerContent {

	static List<Item> getItems(Context context, int position) {
		List<Item> items = new ArrayList<>();
		DBHelper dbHelper = new DBHelper(context);
		List<String[]> entries = dbHelper.getDayEntries(position);
		dbHelper.close();
		for (String[] str : entries) {
			items.add(new Item(str[0], str[1], str[2], Integer.decode(str[3])));
		}
		return items;
	}

	static class Item {

		final String wine;
		final String counter1;
		final String counter2;
		final int iconId;

		Item(String wine, String counter1, String counter2, int iconId) {
			this.wine = wine;
			this.counter1 = counter1;
			this.counter2 = counter2;
			this.iconId = iconId;
		}
	}

}

package com.mcsimb.vinotchet2;

import java.util.ArrayList;
import java.util.List;

class RecyclerContent {

	private static final int COUNT = 2;

	static List<Item> getItems(int position) {
		List<Item> items = new ArrayList<>();
		for (int i = 1; i <= COUNT; i++) {
			items.add(new Item("Мелодии лета 0.5", String.valueOf(position),
					String.valueOf(position), R.drawable.ic_0));
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

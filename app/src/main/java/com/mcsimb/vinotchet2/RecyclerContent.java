package com.mcsimb.vinotchet2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecyclerContent {

	public static final List<Item> ITEMS = new ArrayList<Item>();
	private static final int COUNT = 25;

	static {
		for (int i = 1; i <= COUNT; i++) {
			ITEMS.add(new Item("Мелодии лета 0.5", "2345", "2340", R.drawable.ic_0));
		}
	}

	public static class Item {

		public final String wine;
		public final String counter1;
		public final String counter2;
		public final int iconId;

		public Item(String wine, String counter1, String counter2, int iconId) {
			this.wine = wine;
			this.counter1 = counter1;
			this.counter2 = counter2;
			this.iconId = iconId;
		}
	}

}

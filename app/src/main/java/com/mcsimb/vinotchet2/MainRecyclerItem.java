package com.mcsimb.vinotchet2;

import android.graphics.Bitmap;

import java.util.ArrayList;

class MainRecyclerItem {

    final String wine;
    final String counter1;
    final String counter2;
    final Bitmap label;

    MainRecyclerItem(String wine, String counter1, String counter2, Bitmap label) {
        this.wine = wine;
        this.counter1 = counter1;
        this.counter2 = counter2;
        this.label = label;
    }

    static ArrayList<MainRecyclerItem> getDayItems(CharSequence day) {
        ArrayList<MainRecyclerItem> mainRecyclerItems = new ArrayList<>();
        for (String[] data : FileUtils.dataBase) {
            if (data[0].equals(day)) {
                mainRecyclerItems.add(new MainRecyclerItem(data[1] + "  " + data[2], data[3], data[4],
                        FileUtils.readIcon(data[1] + ".png")));
            }
        }
        return mainRecyclerItems;
    }

}

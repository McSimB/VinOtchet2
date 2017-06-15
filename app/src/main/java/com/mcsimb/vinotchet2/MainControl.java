package com.mcsimb.vinotchet2;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;
import java.util.*;

class MainControl {

	ArrayList<String> days = new ArrayList<>();
	private Map<String, Bitmap> sortsIcons = new TreeMap<>();

	void initControl(String m) {
		FileUtils.month = m;
		days.clear();
		sortsIcons.clear();
		ArrayList<String[]> sList = FileUtils.readFile("sorts" + FileUtils.month);
		for (String[] s : sList) {
			sortsIcons.put(s[0], FileUtils.readIcon(s[0] + ".png"));
			ArrayList<String> rest = new ArrayList<>();
			rest.addAll(Arrays.asList(s).subList(1, s.length));
			FileUtils.winesList.put(s[0], rest);
		}
		FileUtils.dataBase = FileUtils.readFile("data" + FileUtils.month);
		for (String[] data : FileUtils.dataBase) {
			if (!days.contains(data[0])) {
				days.add(data[0]);
			}
		}
		ArrayList<String[]> counters = FileUtils.readFile("counters" + FileUtils.month);
		for (String[] s : counters) {
			String[] c = Arrays.asList(s).subList(1, 5).toArray(new String[4]);
			FileUtils.counters.put(s[0], c);
		}
	}

	/*public ArrayList<MainRecyclerItem> getDayItems(CharSequence day) {
		ArrayList<MainRecyclerItem> mainRecyclerItems = new ArrayList<>();
		for (String[] data : FileUtils.dataBase) {
			if (data[0].equals(day)) {
				mainRecyclerItems.add(new MainRecyclerItem(data[1] + "  " + data[2], data[3], data[4],
										 sortsIcons.get(data[1])));
			}
		}
		return mainRecyclerItems;
	}*/

	public void removeData(String day, int pos) {
		int index = -1;
		for (int i = 0; i < FileUtils.dataBase.size(); i++) {
			if (FileUtils.dataBase.get(i)[0].equals(day)) {
				index = i + pos;
				break;
			}
		}
		try {
			FileUtils.dataBase.remove(index);
		} catch (Exception e) {
			e.printStackTrace();
		}
		FileUtils.writeFile("data" + FileUtils.month, FileUtils.dataBase);
	}

	public void addBlend(String sort, String newBlend) {
		ArrayList<String> blends = FileUtils.winesList.get(sort);
		if (Double.parseDouble(blends.get(blends.size() - 1)) == 0) {
			blends.set(blends.size() - 1, newBlend);
		} else {
			blends.add(newBlend);
		}
		FileUtils.winesList.put(sort, blends);
		ArrayList<String[]> sorts = new ArrayList<>();
		for (String s : FileUtils.winesList.keySet()) {
			ArrayList<String> b = new ArrayList<>();
			b.add(s);
			b.addAll(FileUtils.winesList.get(s));
			sorts.add(b.toArray(new String[b.size()]));
		}
		FileUtils.writeFile("sorts" + FileUtils.month, sorts);
	}


	public String[] material(int start, int end) {
		String[] aList = new String[] {"0", "0", "0", "0", "0", "0", "0", "", "0", "0", ""};
		String v05 = " 0,5";
		String v07 = " 0,7";
		int c05 = 0;
		int c07 = 0;
		Map<String, Integer> labels = new LinkedHashMap<>();
		for (String s: FileUtils.winesList.keySet()) {
			labels.put(s + v05, 0);
			labels.put(s + v07, 0);
		}
		ArrayList<String[]> db = FileUtils.dataBase;
		for (String[] s: db) {
			if (Integer.decode(s[0]) >= start && Integer.decode(s[0]) <= end) {
				if (s[2].equals("0.5")) {
					int c = Integer.decode(s[4]);
					c05 += c;
					labels.put(s[1] + v05, labels.get(s[1] + v05) + c);
				} else {
					int c = Integer.decode(s[4]);
					c07 += c;
					labels.put(s[1] + v07, labels.get(s[1] + v07) + c);
				}
			}
		}
		aList[0] = toStr((c05 * 0.05 + c07 * 0.07) * 0.021);
		aList[1] = toStr(c05 * 0.05 * 0.0081 + c07 * 0.07 * 0.0054 +
			c05 * 0.05 * 0.004 + c07 * 0.07 * 0.003);
		aList[2] = toStr(c05 * 1.025);
		aList[3] = toStr(c07 * 1.015);
		aList[4] = toStr((c05 + c07) * 1.012);
		aList[5] = toStr((c05 + c07) * 0.003);
		aList[6] = toStr((c05 + c07) * 0.0007);
		aList[8] = toStr(c05);
		aList[9] = toStr(c07);
		String[] bList = new String[labels.size()];
		int i = 0;
		for (String s: labels.keySet()) {
			bList[i] = toStr(labels.get(s) * 1.008);
			++i;
		}
		int aLen = aList.length;
		int bLen = bList.length;
		String[] dataList = new String[aLen+bLen];
		System.arraycopy(aList, 0, dataList, 0, aLen);
		System.arraycopy(bList, 0, dataList, aLen, bLen);
		return dataList;
	}

	private String toStr(double d) {
		return String.valueOf(Math.round(d));
	}

}

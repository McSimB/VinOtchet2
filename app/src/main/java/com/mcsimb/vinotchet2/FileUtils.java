package com.mcsimb.vinotchet2;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;

class FileUtils {

	static final Map<String, ArrayList<String>> wineList = new TreeMap<>();
	static final Map<String, String[]> counters = new TreeMap<>();
	static ArrayList<String[]> dataBase;
	static String MONTH = "00";
	
	private static final int REQUEST_EXTERNAL_STORAGE = 1;
	private static String[] PERMISSIONS_STORAGE = {
		Manifest.permission.READ_EXTERNAL_STORAGE,
		Manifest.permission.WRITE_EXTERNAL_STORAGE
	};
	private static final String DIR_SD = "VinOtchet";
	private static final String EXT = ".txt";
	private static BufferedWriter bw;
	private static BufferedReader br;
	private static FileWriter fw;
	private static FileReader fr;
	private static FileInputStream fis;
	private static BufferedInputStream bis;
	private static File sdPath;

	static void verifyStoragePermissions(Activity activity) {
		// Check if we have write permission
		int permission = ActivityCompat.checkSelfPermission(
				activity,
				Manifest.permission.WRITE_EXTERNAL_STORAGE);

		if (permission != PackageManager.PERMISSION_GRANTED) {
			// We don't have permission so prompt the user
			ActivityCompat.requestPermissions(
					activity,
					PERMISSIONS_STORAGE,
					REQUEST_EXTERNAL_STORAGE
			);
		}
	}

	public static boolean mediaMounted() {
		return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
	}

	static boolean pathExists() {
		boolean flag = false;
		sdPath = Environment.getExternalStorageDirectory();
		try {
			sdPath = new File(sdPath.getAbsolutePath() + "/" + DIR_SD);
			flag = sdPath.exists();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return flag;
	}

	public static boolean fileExists(String fName) {
		Boolean flag = false;
		try {
			flag = new File(sdPath, fName + EXT).exists();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return flag;
	}

	public static String[] existingMonths() {
		ArrayList<String> months = new ArrayList<>();
		String[] files = sdPath.list();
		Arrays.sort(files);
		for (String s : files) {
			if (s.startsWith("data")) {
				if (s.startsWith("data0")) {
					months.add(s.substring(5, 6));
				} else
					months.add(s.substring(4, 6));
			}
		}
		return months.toArray(new String[months.size()]);
	}

	static void writeFile(String fName, ArrayList<String[]> fData) {
		try {
			File sdFile = new File(sdPath, fName + EXT);
			fw = new FileWriter(sdFile);
			bw = new BufferedWriter(fw);
			String data = "";
			for (String[] d : fData) {
				for (int i = 0; i < d.length; i++) {
					if (i != d.length - 1) {
						data += d[i] + "; ";
					} else
						data += d[i] + "\n";
				}
			}
			bw.write(data);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				bw.flush();
				bw.close();
				fw.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	static ArrayList<String[]> readFile(String fName) {
		ArrayList<String[]> data = new ArrayList<>();
		try {
			File sdFile = new File(sdPath, fName + EXT);
			fr = new FileReader(sdFile);
			br = new BufferedReader(fr);
			String str;
			while ((str = br.readLine()) != null) {
				data.add(str.split("; "));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				fr.close();
				br.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return data;
	}

	static Bitmap readIcon(String fName) {
		Bitmap icon = null;
		try {
			File sdFile = new File(sdPath, fName);
			fis = new FileInputStream(sdFile);
			bis = new BufferedInputStream(fis);
			icon = BitmapFactory.decodeStream(bis);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				fis.close();
				bis.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return icon;
	}
}

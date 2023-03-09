package com.mcsimb.vinotchet2;

import static com.mcsimb.vinotchet2.util.Const.TRUE;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

public class Repository {

  private static final String DATA_DIR = "VinOtchet2";
  private static final String EXT_PNG = ".png";

  private static volatile Repository instance;
  private final String EXT_TXT = ".txt";
  public Map<String, ArrayList<String>> sorts = new TreeMap<>();
  public Map<String, String[]> counters = new TreeMap<>();
  public Map<String, Bitmap> sortsIcons = new TreeMap<>();
  public String[] resetCounters;
  public ArrayList<String> stamps05 = new ArrayList<>();
  public ArrayList<String> stamps07 = new ArrayList<>();
  public ArrayList<String[]> dataBase;
  public String month = "00";
  public String year;

  private CharSequence usedSortsFlags;
  private BufferedWriter bw;
  private BufferedReader br;
  private FileWriter fw;
  private FileReader fr;
  private FileInputStream fis;
  private BufferedInputStream bis;
  private File path;

  private Repository() {}

  public static Repository getInstance() {
    if (instance == null) {
      synchronized (Repository.class) {
        if (instance == null) {
          instance = new Repository();
        }
      }
    }
    return instance;
  }

  public boolean pathExists(Context context) {
    boolean flag = false;
    path = context.getExternalFilesDir("");
    try {
      path = new File(path.getAbsolutePath() + "/" + DATA_DIR);
      flag = path.exists();
    } catch (Exception e) {
      e.printStackTrace();
    }
    return flag;
  }

  public boolean fileExists(String fName) {
    boolean flag = false;
    try {
      flag = new File(path, fName + EXT_TXT).exists();
    } catch (Exception e) {
      e.printStackTrace();
    }
    return flag;
  }

  public void setUsedSortsFlags(CharSequence sorts) {
    usedSortsFlags = sorts;
  }

  public String[] getSorts(boolean allSorts) {
    String[] all = sorts.keySet().toArray(new String[0]);
    if (allSorts) {
      return all;
    } else {
      ArrayList<String> used = new ArrayList<>();
      for (int i = 0; i < usedSortsFlags.length(); i++) {
        if (usedSortsFlags.charAt(i) == TRUE) {
          used.add(all[i]);
        }
      }
      return used.toArray(new String[0]);
    }
  }

  public Integer[] existingMonths() {
    ArrayList<Integer> existMonths = new ArrayList<>();
    String[] files = path.list();
    if (files != null) {
      Arrays.sort(files);
      for (String file : files) {
        if (file.startsWith("data")) {
          existMonths.add(Integer.parseInt(file.substring(4, 6)));
        }
      }
    }
    return existMonths.toArray(new Integer[0]);
  }

  public void writeFile(String fName, ArrayList<String[]> fData) {
    try {
      File sdFile = new File(path, fName + EXT_TXT);
      fw = new FileWriter(sdFile);
      bw = new BufferedWriter(fw);
      StringBuilder data = new StringBuilder();
      for (String[] d : fData) {
        for (int i = 0; i < d.length; i++) {
          if (i != d.length - 1) {
            data.append(d[i]).append("; ");
          } else data.append(d[i]).append("\n");
        }
      }
      bw.write(data.toString());
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

  public ArrayList<String[]> readFile(String fName) {
    ArrayList<String[]> data = new ArrayList<>();
    try {
      File sdFile = new File(path, fName + EXT_TXT);
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

  public void readSorts() {
    ArrayList<String[]> sortsList = readFile("sorts" + month);
    this.sorts.clear();
    for (String[] sort : sortsList) {
      ArrayList<String> rest = new ArrayList<>(Arrays.asList(sort).subList(1, sort.length));
      this.sorts.put(sort[0], rest);
    }
  }

  public void writeSorts(ArrayList<String[]> sorts) {
    writeFile("sorts" + month, sorts);
  }

  public void readIcons() {
    sortsIcons.clear();
    for (String sort : sorts.keySet()) {
      Bitmap bitmap = readIcon(sort + EXT_PNG);
      sortsIcons.put(sort, bitmap);
    }
  }

  public void readData() {
    dataBase = readFile("data" + month);
  }

  public void writeData() {
    writeFile("data" + month, dataBase);
  }

  public void removeProduct(String day, int positionInDay) {
    int index = -1;
    for (int i = 0; i < dataBase.size(); i++) {
      if (dataBase.get(i)[0].equals(day)) {
        index = i + positionInDay;
        break;
      }
    }
    dataBase.remove(index);
    writeData();
  }

  public void readCounters() {
    ArrayList<String[]> sortsCounters = readFile("counters" + month);
    counters.clear();
    for (String[] sort : sortsCounters) {
      String[] cntrs = Arrays.copyOfRange(sort, 1, 5);
      counters.put(sort[0], cntrs);
    }

    resetCounters = readFile("resetcounters").get(0);
  }

  public void readStamps() {
    ArrayList<String[]> allStamps = readFile("stamps" + month);
    stamps05.clear();
    stamps07.clear();
    for (String[] stamps : allStamps) {
      if (stamps[0].equals("0")) {
        stamps05.add(stamps[1]);
      } else {
        stamps07.add(stamps[1]);
      }
    }
  }

  public void saveStampsRange(String range, String number, String tare) {
    ArrayList<String[]> stamps = new ArrayList<>();
    for (String s : stamps05) {
      stamps.add(new String[] {"0", s});
    }
    for (String s : stamps07) {
      stamps.add(new String[] {"1", s});
    }
    if (tare.equals("0.5")) {
      stamps05.add(range + number);
      stamps.add(new String[] {"0", range + number});
    } else {
      stamps07.add(range + number);
      stamps.add(new String[] {"1", range + number});
    }

    writeStamps(stamps);
  }

  public void writeStamps(ArrayList<String[]> stamps) {
    writeFile("stamps" + month, stamps);
  }

  private Bitmap readIcon(String fName) {
    Bitmap icon = null;
    try {
      File sdFile = new File(path, fName);
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

  public String readUtils(String fileName) {
    StringBuilder data = new StringBuilder();
    try {
      File sdFile = new File(path + "/utils", fileName);
      fr = new FileReader(sdFile);
      br = new BufferedReader(fr);
      String str;
      while ((str = br.readLine()) != null) {
        data.append(str).append("\n");
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
    return data.toString();
  }

  public void writeExport(String data, String fileName) {
    try {
      File sdFile = new File(path + "/export", fileName + ".html");
      fw = new FileWriter(sdFile);
      bw = new BufferedWriter(fw);
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
}

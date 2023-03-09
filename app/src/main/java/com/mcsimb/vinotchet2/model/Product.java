package com.mcsimb.vinotchet2.model;

import com.mcsimb.vinotchet2.util.MathUtils;
import java.util.Date;

public class Product {

  private final Date date;
  private final Sort sort;
  private final Tare tare;
  private final int counter1;
  private final int counter2;
  private final Filtering filtering;
  private final Bottling bottling;
  private int[] splitCounters;
  private boolean firstSplit;

  public Product(Date date, Sort sort, Tare tare, int counter1, int counter2) {
    this.date = date;
    this.sort = sort;
    this.tare = tare;
    this.counter1 = counter1;
    this.counter2 = counter2;
    filtering = new Filtering(this);
    bottling = new Bottling(this);
  }

  public Product(Product product) {
    date = product.getDate();
    sort = product.getSort();
    tare = product.getTare();
    counter1 = product.getCounter1();
    counter2 = product.getCounter2();
    splitCounters = product.getSplitCounters();
    firstSplit = product.isFirstSplit();
    filtering = new Filtering(this);
    bottling = new Bottling(this);
  }

  public Date getDate() {
    return date;
  }

  public Sort getSort() {
    return sort;
  }

  public int getCounter1() {
    return counter1;
  }

  public int getCounter2() {
    return counter2;
  }

  public Tare getTare() {
    return tare;
  }

  public Filtering getFiltering() {
    return filtering;
  }

  public Bottling getBottling() {
    return bottling;
  }

  public int[] getSplitCounters() {
    return splitCounters;
  }

  public void setSplitCounters(int[] counters) {
    splitCounters = counters;
  }

  public boolean isFirstSplit() {
    return firstSplit;
  }

  public void setFirstSplit(boolean firstSplit) {
    this.firstSplit = firstSplit;
  }

  public boolean isValidCounters() {
    return counter1 > counter2;
  }

  public float getDal1() {
    int counter;
    if (splitCounters != null) {
      if (firstSplit) {
        counter = splitCounters[0];
      } else {
        counter = counter1 - splitCounters[0];
      }
    } else {
      counter = counter1;
    }
    return MathUtils.round2(counter * tare.getDal());
  }

  public float getDal2() {
    int counter;
    if (splitCounters != null) {
      if (firstSplit) {
        counter = splitCounters[1];
      } else {
        counter = counter2 - splitCounters[1];
      }
    } else {
      counter = counter2;
    }
    return MathUtils.round2(counter * tare.getDal());
  }
}

package com.mcsimb.vinotchet2.model;

public class Sort {

  private final String name;
  private final String labelId;

  public Sort(String name, String labelId) {
    this.name = name;
    this.labelId = labelId;
  }

  public String getName() {
    return name;
  }

  public String getLabelId() {
    return labelId;
  }
}

package com.mcsimb.vinotchet2.model;

import java.util.ArrayList;
import java.util.List;

public class Production {

  public static float FILTERING_LOSS = 0.42f;
  public static float BOTTLING_LOSS = 0.34f;

  private final List<Blend> blends;

  public Production() {
    blends = new ArrayList<>();
  }

  public void addBlend(Blend blend) {
    blends.add(blend);
  }

  /**
   * @return false - необходимо добавить новый купаж, продукт не добавлен
   */
  public boolean addProduct(Product product) {
    String name = product.getSort().getName();
    Blend blend = getCurrentBlend(name);
    if (blend == null) {
      blend = getNewBlend(name);
      if (blend == null) {
        return false;
      }
    }
    boolean split = blend.addProduct(product);
    if (split) {
      Blend newBlend = getNewBlend(name);
      if (newBlend == null) {
        blend.removeProduct(product);
        return false;
      }
      Product product1 = new Product(product);
      product1.setFirstSplit(false);
      product1.getFiltering().recalculateIncome();
      newBlend.addProduct(product1);
    }
    return true;
  }

  public List<Blend> getBlendsByName(String name) {
    List<Blend> blendList = new ArrayList<>();
    for (Blend blend : blends) {
      if (blend.getName().equals(name)) {
        blendList.add(blend);
      }
    }
    return blendList;
  }

  public Blend getCurrentBlend(String name) {
    List<Blend> blendList = getBlendsByName(name);
    for (Blend blend : blendList) {
      if (!blend.isCompleted() && !blend.isNew()) {
        return blend;
      }
    }
    return null;
  }

  public Blend getNewBlend(String name) {
    List<Blend> blendList = getBlendsByName(name);
    for (Blend blend : blendList) {
      if (blend.isNew()) {
        return blend;
      }
    }
    return null;
  }
}

package com.mcsimb.vinotchet2.model;

import com.mcsimb.vinotchet2.util.MathUtils;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Blend {

  /* Максимальное допустимое количество бутылок, превышающие норму, при котором все еще происходит
  полное закрытие купажа. */
  public static int EXCESS_COUNT = 15;
  private final Sort sort;
  private final Date createDate;
  private final float accountVolume;
  private final float startMonthVolume;
  private final List<Product> products;

  public Blend(Date createDate, Sort sort, float accountVolume, float startMonthVolume) {
    this.sort = sort;
    this.createDate = createDate;
    this.accountVolume = accountVolume;
    this.startMonthVolume = startMonthVolume;
    this.products = new ArrayList<>();
  }

  /**
   * @return true - разделение купажей, false - обычное добавление продукта или полное закрытие
   *     купажа.
   */
  public boolean addProduct(Product product) {
    products.add(product);
    if (getEndRestForProduct(product) < EXCESS_COUNT * product.getTare().getDal()) {
      int[] split = split(product);
      product.setSplitCounters(split);
      product.setFirstSplit(true);
      product.getFiltering().recalculateIncome();
      product
          .getFiltering()
          .setLoss(getStartRestForProduct(product) - product.getFiltering().getIncomeDal());
      return split[1] != product.getCounter2();
    }
    return false;
  }

  public String getName() {
    return sort.getName();
  }

  public Date getCreateDate() {
    return createDate;
  }

  public List<Product> getProducts() {
    return products;
  }

  public void removeProduct(Product product) {
    products.remove(product);
  }

  public float getRestVolume() {
    return products.size() > 0
        ? getEndRestForProduct(products.get(products.size() - 1))
        : startMonthVolume;
  }

  public float getStartRestForProduct(Product product) {
    float rest = startMonthVolume;
    for (Product iterProduct : products) {
      if (iterProduct.equals(product)) {
        return MathUtils.round1(rest);
      }
      rest -= iterProduct.getFiltering().getExpense();
    }
    return Float.NaN;
  }

  public float getEndRestForProduct(Product product) {
    float rest = startMonthVolume;
    for (Product iterProduct : products) {
      rest -= iterProduct.getFiltering().getExpense();
      if (iterProduct.equals(product)) {
        return MathUtils.round1(rest);
      }
    }
    return Float.NaN;
  }

  public boolean isCompleted() {
    return getRestVolume() == 0f;
  }

  public boolean isNew() {
    return getRestVolume() == accountVolume;
  }

  public float calcRestProduction() {
    return MathUtils.round1(getRestVolume() - calcRestLosses());
  }

  public float calcRestLosses() {
    float losses = Production.FILTERING_LOSS + Production.BOTTLING_LOSS;
    return MathUtils.round1(getRestVolume() * losses / 100.0f);
  }

  private int[] split(Product product) {
    int count1;
    int count2;

    float losses = Production.FILTERING_LOSS + Production.BOTTLING_LOSS;
    float dal1 =
        (getStartRestForProduct(product) - product.getTare().getDal()) / (losses / 100.0f + 1);
    count2 = (int) (dal1 / product.getTare().getDal());

    if (product.getCounter2() - count2 < EXCESS_COUNT) {
      count1 = product.getCounter1();
      count2 = product.getCounter2();
    } else {
      count1 = (int) (count2 * product.getCounter1() / (float) product.getCounter2());
    }

    return new int[] {count1, count2};
  }
}

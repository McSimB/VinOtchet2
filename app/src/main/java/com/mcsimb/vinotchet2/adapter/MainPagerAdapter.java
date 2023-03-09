package com.mcsimb.vinotchet2.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import com.mcsimb.vinotchet2.fragment.MainFragment;
import java.util.ArrayList;
import java.util.List;

public class MainPagerAdapter extends FragmentStateAdapter {

  private final List<Long> itemList;

  public MainPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
    super(fragmentActivity);
    itemList = new ArrayList<>();
  }

  public void add(long item) {
    itemList.add(item);
    notifyItemRangeChanged(0, itemList.size());
  }

  public void remove(int position) {
    itemList.remove(itemList.get(position));
    notifyItemRemoved(position);
  }

  public void removeAllPages() {
    int count = itemList.size();
    itemList.clear();
    notifyItemRangeRemoved(0, count);
  }

  @NonNull
  @Override
  public Fragment createFragment(int position) {
    return MainFragment.newInstance(position);
  }

  @Override
  public long getItemId(int position) {
    return itemList.get(position);
  }

  @Override
  public boolean containsItem(long itemId) {
    for (long item : itemList) {
      if (item == itemId) {
        return true;
      }
    }
    return false;
  }

  @Override
  public int getItemCount() {
    return itemList.size();
  }
}

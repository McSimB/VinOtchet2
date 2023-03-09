package com.mcsimb.vinotchet2.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.mcsimb.vinotchet2.R;
import com.mcsimb.vinotchet2.viewmodel.MainViewModel;

public class MaterialsFragment extends Fragment {

  private MainViewModel viewModel;

  public MaterialsFragment() {}

  public static MaterialsFragment newInstance() {
    return new MaterialsFragment();
  }

  @Override
  public View onCreateView(
      LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
    View fragmentView = inflater.inflate(R.layout.dialog_materials, container, false);
    NumberPicker numPickStart = fragmentView.findViewById(R.id.np_dialog_materials_start);
    NumberPicker numPickEnd = fragmentView.findViewById(R.id.np_dialog_materials_end);
    if (!viewModel.getDays().isEmpty()) {
      int min = Integer.parseInt(viewModel.getDays().get(0));
      int max = Integer.parseInt(viewModel.getDays().get(viewModel.getDays().size() - 1));
      numPickStart.setMinValue(min);
      numPickStart.setMaxValue(max);
      numPickEnd.setMinValue(min);
      numPickEnd.setMaxValue(max);
      /*Tab selectedTab = tabLayout.getTabAt(tabLayout.getSelectedTabPosition());
      if (selectedTab != null) {
        //noinspection ConstantConditions
        numPickStart.setValue(Integer.parseInt((String) selectedTab.getTag()));
        numPickEnd.setValue(Integer.parseInt((String) selectedTab.getTag()));
      }*/
    }
    return fragmentView;
  }
}

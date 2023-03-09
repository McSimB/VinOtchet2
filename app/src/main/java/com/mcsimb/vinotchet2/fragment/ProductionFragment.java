package com.mcsimb.vinotchet2.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.mcsimb.vinotchet2.R;
import com.mcsimb.vinotchet2.util.MathUtils;
import com.mcsimb.vinotchet2.viewmodel.MainViewModel;

public class ProductionFragment extends Fragment {

  private MainViewModel viewModel;

  public ProductionFragment() {}

  public static ProductionFragment newInstance() {
    return new ProductionFragment();
  }

  @Override
  public View onCreateView(
      LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
    View fragmentView = inflater.inflate(R.layout.dialog_production, container, false);
    ListView prodListView = fragmentView.findViewById(R.id.lv_dialog_production);
    TextView sumTextView = fragmentView.findViewById(R.id.tv_sum_dialog_production);
    String[] list = viewModel.getProduction();
    float sum = 0f;
    for (int i = 0; i < list.length; i++) {
      sum += Float.parseFloat(list[i]);
      list[i] = viewModel.getDays().get(i) + ".    --    " + list[i];
    }
    sumTextView.setText(String.valueOf(MathUtils.round2(sum)));
    ArrayAdapter<String> adapter =
        new ArrayAdapter<>(requireActivity(), android.R.layout.simple_list_item_1, list);
    prodListView.setAdapter(adapter);
    return fragmentView;
  }
}

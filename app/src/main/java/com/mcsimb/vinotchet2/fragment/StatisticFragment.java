package com.mcsimb.vinotchet2.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.mcsimb.vinotchet2.R;
import com.mcsimb.vinotchet2.viewmodel.MainViewModel;

public class StatisticFragment extends Fragment {

  private MainViewModel viewModel;

  public StatisticFragment() {}

  public static StatisticFragment newInstance() {
    return new StatisticFragment();
  }

  @Override
  public View onCreateView(
      LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
    View fragmentView = inflater.inflate(R.layout.dialog_statistic, container, false);
    TextView made = fragmentView.findViewById(R.id.tv_dialog_made);
    TextView rest = fragmentView.findViewById(R.id.tv_dialog_rest);
    String[] stat = viewModel.getStatistic();
    made.setText(stat[0]);
    rest.setText(stat[1]);
    return fragmentView;
  }
}

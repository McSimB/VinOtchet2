package com.mcsimb.vinotchet2.fragment;

import static com.mcsimb.vinotchet2.util.Const.ARG_DAY;
import static com.mcsimb.vinotchet2.util.Const.ARG_FOR_DAY;
import static com.mcsimb.vinotchet2.util.Const.ARG_LOG_NUMBER;
import static com.mcsimb.vinotchet2.util.Const.ARG_PAGE_NUMBER;
import static com.mcsimb.vinotchet2.util.Const.ARG_POS_IN_DAY;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.divider.MaterialDividerItemDecoration;
import com.mcsimb.vinotchet2.LogActivity;
import com.mcsimb.vinotchet2.ProductViewItem;
import com.mcsimb.vinotchet2.R;
import com.mcsimb.vinotchet2.adapter.RecyclerViewAdapter;
import com.mcsimb.vinotchet2.adapter.RecyclerViewAdapter.OnItemClickListener;
import com.mcsimb.vinotchet2.dialog.LogSelectDialog;
import com.mcsimb.vinotchet2.dialog.ProductRemoveDialog;
import com.mcsimb.vinotchet2.viewmodel.MainViewModel;

public class MainFragment extends Fragment implements OnItemClickListener {

  private MainViewModel viewModel;
  private RecyclerViewAdapter adapter;
  private int pageNumber;
  private String day;

  public MainFragment() {}

  public static MainFragment newInstance(int pageNumber) {
    var fragment = new MainFragment();
    var arguments = new Bundle();
    arguments.putInt(ARG_PAGE_NUMBER, pageNumber);
    fragment.setArguments(arguments);
    return fragment;
  }

  @Override
  public View onCreateView(
      LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
    View fragmentView = inflater.inflate(R.layout.fragment_main, container, false);
    RecyclerView recyclerView = fragmentView.findViewById(R.id.recycler);
    recyclerView.addItemDecoration(
        new MaterialDividerItemDecoration(
            requireContext(), MaterialDividerItemDecoration.HORIZONTAL));
    recyclerView.addItemDecoration(
        new MaterialDividerItemDecoration(
            requireContext(), MaterialDividerItemDecoration.VERTICAL));

    var args = getArguments();
    if (args != null) {
      pageNumber = args.getInt(ARG_PAGE_NUMBER);
      day = viewModel.getDays().get(pageNumber);
      adapter = new RecyclerViewAdapter(R.layout.list_item_main, this);
      adapter.setItems(viewModel.getDayProducts(day));
      recyclerView.setAdapter(adapter);
    }

    return fragmentView;
  }

  @Override
  public void onItemClick(int position) {
    new LogSelectDialog(
        requireContext(),
        (dialog, item) -> {
          Intent intent = new Intent(requireContext(), LogActivity.class);
          intent.putExtra(ARG_LOG_NUMBER, item);
          intent.putExtra(ARG_POS_IN_DAY, position);
          intent.putExtra(ARG_DAY, day);
          intent.putExtra(ARG_FOR_DAY, false);
          startActivity(intent);
        });
  }

  @Override
  public void onItemLongClick(View view, int position) {
    var popup = new PopupMenu(requireContext(), view);
    requireActivity()
        .getMenuInflater()
        .inflate(R.menu.popup_menu_product_item_main, popup.getMenu());
    popup.setOnMenuItemClickListener(
        item -> {
          if (item.getItemId() == R.id.edit_product) {
            // TODO: 14.02.2023 edit product
            return true;
          } else if (item.getItemId() == R.id.remove_product) {
            new ProductRemoveDialog(
                requireContext(),
                ((ProductViewItem) adapter.getItem(position)).sort,
                (dialog, whichButton) -> {
                  // var activity = (MainActivity) requireActivity();
                  viewModel.removeProduct(day, position);
                  adapter.remove(position);
                  if (adapter.getItemCount() == 0) {
                    viewModel.removeDay(pageNumber);

                    /*var pagerAdapter = activity.getPagerAdapter();
                    pagerAdapter.remove(pageNumber);

                    if ((pagerAdapter.getItemCount() == 0)
                        || (pageNumber - 1 == pagerAdapter.getItemCount() - 1)) {
                      activity.getMainMenu().findItem(R.id.menu_add_main).setVisible(true);
                    }*/
                  }
                });
            return true;
          }
          return false;
        });
    popup.show();
  }
}

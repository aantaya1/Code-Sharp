package com.aantaya.codesharp.ui.dashboard;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.aantaya.codesharp.R;
import com.aantaya.codesharp.models.ProgressModel;
import com.aantaya.codesharp.models.UserStatsModel;
import com.aantaya.codesharp.ui.settings.SettingsActivity;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.ybq.android.spinkit.SpinKitView;

import java.util.ArrayList;
import java.util.List;

public class DashboardFragment extends Fragment {

    private DashboardViewModel mDashboardViewModel;
    private PieChart mTotalProgressChart;
    private TextView mTotalProgressTextView;
    private PieChart mDifficultyProgressChart;
    private TextView mEasyTotalText;
    private TextView mMediumTotalText;
    private TextView mHardTotalText;
    private ConstraintLayout mDashboardLayout;
    private SpinKitView mLoadingAnimation;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);

        mTotalProgressChart = root.findViewById(R.id.dashboard_progress_pie_chart);
        mTotalProgressTextView = root.findViewById(R.id.total_progress_textview);
        mDifficultyProgressChart = root.findViewById(R.id.dashboard_difficulty_progress_pie_chart);
        mEasyTotalText = root.findViewById(R.id.dashboard_easy_total);
        mMediumTotalText = root.findViewById(R.id.dashboard_medium_total);
        mHardTotalText = root.findViewById(R.id.dashboard_hard_total);
        mDashboardLayout = root.findViewById(R.id.dashboard_layout);
        mLoadingAnimation = root.findViewById(R.id.loading_animation);

        //This tells android that we have a options menu that we would like to render
        // if we don't set this, the menu callbacks will not be called
        setHasOptionsMenu(true);

        Toolbar toolbar = root.findViewById(R.id.my_toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mDashboardViewModel = new ViewModelProvider(this).get(DashboardViewModel.class);
        mDashboardViewModel.init();

        mDashboardViewModel.getState().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer state) {
                switch (state){
                    case DashboardViewModel.STATE_NORMAL:
                        hideLoading();
                        break;
                    case DashboardViewModel.STATE_LOADING:
                        displayLoading();
                        break;
                    case DashboardViewModel.STATE_FAILED:
                        //todo: need to implement
                        break;
                }
            }
        });

        mDashboardViewModel.getTotalProgress().observe(getViewLifecycleOwner(), new Observer<ProgressModel>() {
            @Override
            public void onChanged(ProgressModel progressModel) {

                int completed = progressModel.getNumCompleted();
                int total = progressModel.getTotal();

                //Set the values in the graph
                List<PieEntry> progressEntries = new ArrayList<>();
                progressEntries.add(new PieEntry(completed, "Completed", 0));
                if (total-completed != 0) progressEntries.add(new PieEntry(total-completed, "Todo", 1));

                PieDataSet dataSet = new PieDataSet(progressEntries, "Total Progress");

                PieData pieData = new PieData(dataSet);
                dataSet.setColors(ColorTemplate.LIBERTY_COLORS);
                dataSet.setDrawValues(false);//don't draw the actual values on the chart
                Typeface typeface = ResourcesCompat.getFont(getActivity(), R.font.montserrat);
                dataSet.setValueTypeface(typeface);

                mTotalProgressChart.setData(pieData);

                //----------- Pie Chart configurations -------------------//
                // Add an animation to make the pie chart load slowly (looks koool)
                mTotalProgressChart.animateXY(2000, 2000);

                // Make the labels text size larger than the default
                mTotalProgressChart.setEntryLabelTextSize(16f);

                // Remove the description and legend
                Description chartDescription = new Description();
                chartDescription.setText("");
                mTotalProgressChart.setDescription(chartDescription);
                mTotalProgressChart.getLegend().setEnabled(false);
                mTotalProgressChart.setHoleRadius(0.0f);
                mTotalProgressChart.setDrawHoleEnabled(false);

                mTotalProgressTextView.setText(completed + "/" + total + "\nSolved");
            }
        });

        mDashboardViewModel.getUserStats().observe(getViewLifecycleOwner(), new Observer<UserStatsModel>() {
            @Override
            public void onChanged(UserStatsModel userStatsModel) {
                mEasyTotalText.setText(userStatsModel.getNumEasyCompleted() + " Easy");
                mMediumTotalText.setText(userStatsModel.getNumMediumCompleted() + " Medium");
                mHardTotalText.setText(userStatsModel.getNumHardCompleted() + " Hard");

                
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.dashboard_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_more:
                startActivity(new Intent(getContext(), SettingsActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void displayLoading(){
        mDashboardLayout.setVisibility(View.INVISIBLE);
        mLoadingAnimation.setVisibility(View.VISIBLE);
    }

    private void hideLoading(){
        mDashboardLayout.setVisibility(View.VISIBLE);
        mLoadingAnimation.setVisibility(View.INVISIBLE);
    }
}

package com.aantaya.codesharp.ui.dashboard;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
import com.facebook.shimmer.ShimmerFrameLayout;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

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
    private ShimmerFrameLayout mShimmerLayout;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);

        mTotalProgressChart = root.findViewById(R.id.dashboard_progress_pie_chart);
        mTotalProgressTextView = root.findViewById(R.id.total_progress_textview);
        mDifficultyProgressChart = root.findViewById(R.id.dashboard_difficulty_progress_pie_chart);
        mEasyTotalText = root.findViewById(R.id.dashboard_easy_total);
        mMediumTotalText = root.findViewById(R.id.dashboard_medium_total);
        mHardTotalText = root.findViewById(R.id.dashboard_hard_total);
        mDashboardLayout = root.findViewById(R.id.dashboard_layout);
        mShimmerLayout = root.findViewById(R.id.shimmer_layout);

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
                if (total-completed != 0) progressEntries.add(new PieEntry(total-completed, "", 1));

                PieDataSet dataSet = new PieDataSet(progressEntries, "Total Progress");

                PieData pieData = new PieData(dataSet);
                dataSet.setColors(ColorTemplate.LIBERTY_COLORS);
                dataSet.setDrawValues(false);//don't draw the actual values on the chart
                //todo: for some reason this font is not being displayed properly
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

                int numEasy = userStatsModel.getNumEasyCompleted();
                int numMed = userStatsModel.getNumMediumCompleted();
                int numHard = userStatsModel.getNumHardCompleted();

                mEasyTotalText.setText(numEasy + " Easy");
                mMediumTotalText.setText(numMed + " Medium");
                mHardTotalText.setText(numHard + " Hard");

                Resources res = getResources();

                int easyColor = res.getColor(R.color.easy);
                int medColor = res.getColor(R.color.medium);
                int hardColor = res.getColor(R.color.hard);

                List<Integer> colors = new ArrayList<>();
                List<PieEntry> entries = new ArrayList<>();
                int idx = 0;

                //We only want to add entries that have > 0 completed or
                // else the colors will not match up
                if (numEasy != 0){
                    colors.add(easyColor);
                    entries.add(new PieEntry(numEasy, "", idx++));
                }
                if (numMed != 0){
                    colors.add(medColor);
                    entries.add(new PieEntry(numMed, "", idx++));
                }
                if (numHard != 0){
                    colors.add(hardColor);
                    entries.add(new PieEntry(numHard, "", idx++));
                }

                //The API can't take an array of Integer (need to convert to array int)
                int[] colorInts = new int[colors.size()];
                for (int i=0; i< colorInts.length; i++){
                    colorInts[i] = colors.get(i);
                }

                PieDataSet dataSet = new PieDataSet(entries, "");

                PieData pieData = new PieData(dataSet);
                dataSet.setColors(colorInts);
                dataSet.setDrawValues(false);//don't draw the actual values on the chart

                mDifficultyProgressChart.setData(pieData);

                //----------- Pie Chart configurations -------------------//
                // Add an animation to make the pie chart load slowly (looks koool)
                mDifficultyProgressChart.animateXY(2000, 2000);

                // Make the labels text size larger than the default
                mDifficultyProgressChart.setEntryLabelTextSize(16f);

                // Remove the description and legend
                Description chartDescription = new Description();
                chartDescription.setText("");
                mDifficultyProgressChart.setDescription(chartDescription);
                mDifficultyProgressChart.getLegend().setEnabled(false);
                mDifficultyProgressChart.setHoleRadius(0.0f);
                mDifficultyProgressChart.setDrawHoleEnabled(false);
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
        mShimmerLayout.startShimmer();
        mShimmerLayout.setVisibility(View.VISIBLE);
        mDashboardLayout.setVisibility(View.INVISIBLE);
    }

    private void hideLoading(){
        mShimmerLayout.stopShimmer();
        mShimmerLayout.setVisibility(View.GONE);
        mDashboardLayout.setVisibility(View.VISIBLE);
    }
}

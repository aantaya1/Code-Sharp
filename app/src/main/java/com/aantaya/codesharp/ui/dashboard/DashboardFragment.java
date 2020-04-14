package com.aantaya.codesharp.ui.dashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.aantaya.codesharp.R;
import com.aantaya.codesharp.models.ProgressModel;
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
    private TextView mEasyTotalText;
    private TextView mMediumTotalText;
    private TextView mHardTotalText;
    private ConstraintLayout mDashboardLayout;
    private SpinKitView mLoadingAnimation;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);

        mTotalProgressChart = root.findViewById(R.id.dashboard_progress_pie_chart);
        mEasyTotalText = root.findViewById(R.id.dashboard_easy_total);
        mMediumTotalText = root.findViewById(R.id.dashboard_medium_total);
        mHardTotalText = root.findViewById(R.id.dashboard_hard_total);
        mDashboardLayout = root.findViewById(R.id.dashboard_layout);
        mLoadingAnimation = root.findViewById(R.id.loading_animation);

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
                progressEntries.add(new PieEntry(total-completed, "Todo", 1));

                PieDataSet dataSet = new PieDataSet(progressEntries, "Total Progress");

                PieData pieData = new PieData(dataSet);
                dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
                dataSet.setDrawValues(false);//don't draw the actual values on the chart

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

                // In the center of the chart, draw the text that breaks down the user's progress
                // and again increase the font
                mTotalProgressChart.setCenterText(completed + "/" + total + "\nSolved");
                mTotalProgressChart.setCenterTextSize(16f);
            }
        });

        mDashboardViewModel.getEasyCompleted().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer easy) {
                mEasyTotalText.setText(easy + " Easy");
            }
        });

        mDashboardViewModel.getMediumCompleted().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer med) {
                mMediumTotalText.setText(med + " Medium");
            }
        });

        mDashboardViewModel.getHardCompleted().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer hard) {
                mHardTotalText.setText(hard + " Hard");
            }
        });
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

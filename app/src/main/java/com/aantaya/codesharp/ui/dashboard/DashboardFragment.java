package com.aantaya.codesharp.ui.dashboard;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.aantaya.codesharp.R;
import com.aantaya.codesharp.models.ProgressModel;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DashboardFragment extends Fragment {

    private DashboardViewModel dashboardViewModel;
    private PieChart totalProgressChart;
    private TextView easyTotalText;
    private TextView mediumTotalText;
    private TextView hardTotalText;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        dashboardViewModel = ViewModelProviders.of(this).get(DashboardViewModel.class);
        dashboardViewModel.init();

        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);

        totalProgressChart = root.findViewById(R.id.dashboard_progress_pie_chart);
        easyTotalText = root.findViewById(R.id.dashboard_easy_total);
        mediumTotalText = root.findViewById(R.id.dashboard_medium_total);
        hardTotalText = root.findViewById(R.id.dashboard_hard_total);

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();

        dashboardViewModel.getTotalProgress().observe(getViewLifecycleOwner(), new Observer<ProgressModel>() {
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

                totalProgressChart.setData(pieData);

                //----------- Pie Chart configurations -------------------//
                // Add an animation to make the pie chart load slowly (looks koool)
                totalProgressChart.animateXY(2000, 2000);

                // Make the labels text size larger than the default
                totalProgressChart.setEntryLabelTextSize(16f);

                // Remove the description and legend
                Description chartDiscription = new Description();
                chartDiscription.setText("");
                totalProgressChart.setDescription(chartDiscription);
                totalProgressChart.getLegend().setEnabled(false);

                // In the center of the chart, draw the text that breaks down the user's progress
                // and again increase the font
                totalProgressChart.setCenterText(completed + "/" + total + "\nSolved");
                totalProgressChart.setCenterTextSize(16f);
            }
        });

        dashboardViewModel.getEasyCompleted().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer easy) {
                easyTotalText.setText(easy + " Easy");
            }
        });

        dashboardViewModel.getMediumCompleted().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer med) {
                mediumTotalText.setText(med + " Medium");
            }
        });

        dashboardViewModel.getHardCompleted().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer hard) {
                hardTotalText.setText(hard + " Hard");
            }
        });
    }
}

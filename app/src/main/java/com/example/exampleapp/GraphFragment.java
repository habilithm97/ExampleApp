package com.example.exampleapp;

import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.MPPointF;

import java.util.ArrayList;

public class GraphFragment extends Fragment {

    PieChart pieChart;
    BarChart barChart;
    LineChart lineChart;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup)inflater.inflate(R.layout.fragment_graph, container, false);

        initUi(rootView);

        return rootView;
    }
    private void initUi(ViewGroup rootView) {
        pieChart = rootView.findViewById(R.id.pieChart);
        pieChart.setUsePercentValues(true); // ??
        pieChart.getDescription().setEnabled(false); // 그래프 설명 표시하지 않음
        pieChart.setCenterText("기분별 비율"); // 중앙 원 제목
        pieChart.setHoleRadius(70f); // 중앙 원 반지름
        pieChart.setDrawCenterText(true); // 중앙 원에 있는 텍스트 보이기

        pieChart.setTransparentCircleColor(Color.WHITE); // 중앙 원 테두리 색
        pieChart.setTransparentCircleAlpha(110); // ??
        pieChart.setTransparentCircleRadius(40f); // ??
        pieChart.setHighlightPerTapEnabled(false); // ??

        Legend legend1 = pieChart.getLegend();
        legend1.setEnabled(false); // 속성 표시 안함
        pieChart.setEntryLabelColor(Color.WHITE); // ??
        pieChart.setEntryLabelTextSize(12f); // ??
        setData1(); // 넣을 데이터 설정


        barChart = rootView.findViewById(R.id.barChart);
        lineChart = rootView.findViewById(R.id.lineChart);
    }

    private void setData1() {
        ArrayList<PieEntry> entries = new ArrayList<>();
        // 각각의 속성에 추가함(비율, 속성 이름, 속성 아이콘)
        entries.add(new PieEntry(20.0f, "", getResources().getDrawable(R.drawable.smile1_48)));
        entries.add(new PieEntry(20.0f, "", getResources().getDrawable(R.drawable.smile2_48)));
        entries.add(new PieEntry(20.0f, "", getResources().getDrawable(R.drawable.smile3_48)));
        entries.add(new PieEntry(20.0f, "", getResources().getDrawable(R.drawable.smile4_48)));
        entries.add(new PieEntry(20.0f, "", getResources().getDrawable(R.drawable.smile5_48)));

        PieDataSet dataSet = new PieDataSet(entries, "기분별 비율");

        dataSet.setDrawIcons(true); // 속성 아이콘 표시
        dataSet.setSliceSpace(5f); // 속성 간 간격
        dataSet.setIconsOffset(new MPPointF(0, -40)); // 속성 아이콘 위치
        dataSet.setSelectionShift(10f); // 그래프 크기?

        ArrayList<Integer> colors = new ArrayList<>();

        for(int color : ColorTemplate.JOYFUL_COLORS) {
            colors.add(color);
        }
        dataSet.setColors(colors);

        PieData data = new PieData(dataSet);
        data.setValueTextSize(22.0f); // 비율 텍스트 크기
        data.setValueTextColor(Color.BLACK); // 비율 텍스트 색

        pieChart.setData(data);
        pieChart.invalidate();
    }
}
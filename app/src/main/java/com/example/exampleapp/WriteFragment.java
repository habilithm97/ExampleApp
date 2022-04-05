package com.example.exampleapp;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.github.channguyen.rsv.RangeSliderView;
import com.google.android.material.tabs.TabLayout;

public class WriteFragment extends Fragment {

    private static final String TAG = "WriteFragment";

    Context context;
    onTabItemSelectedListener listener;

    TextView dateTv;

    OnRequestListener requestListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        this.context = context;

        if(context instanceof onTabItemSelectedListener) {
            listener = (onTabItemSelectedListener) context;
        }

        if(context instanceof OnRequestListener) {
            requestListener = (OnRequestListener) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();

        if(context != null) {
            context = null;
            listener = null;
            requestListener = null;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup)inflater.inflate(R.layout.fragment_write, container, false);

        initUi(rootView);

        if(requestListener != null)
        {
            requestListener.onRequest("getCurrentLocation"); // 현재 위치 요청하기!!!
        }
        return rootView;
    }
    private void initUi(ViewGroup rootView) { // 인플레이션 후에 xml 레이아웃 안에 들어 있는 위젯이나 레이아웃을 찾아
        // 변수에 할당하는 코드들을 넣기 위해 만들어 둔 것임

        dateTv = rootView.findViewById(R.id.dateTv);

        Button saveBtn = rootView.findViewById(R.id.saveBtn);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(listener != null) {
                    listener.onTabSelected(0); // 리스트 프래그먼트로 화면 전환
                }
            }
        });

        Button deleteBtn = rootView.findViewById(R.id.deleteBtn);
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(listener != null) {
                    listener.onTabSelected(0); // 리스트 프래그먼트로 화면 전환
                }
            }
        });

        Button cancelBtn = rootView.findViewById(R.id.cancelBtn);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(listener != null) {
                    listener.onTabSelected(0); // 리스트 프래그먼트로 화면 전환
                }
            }
        });

        RangeSliderView sliderView = rootView.findViewById(R.id.sliderView);
        sliderView.setOnSlideListener(new RangeSliderView.OnSlideListener() {
            @Override
            public void onSlide(int index) { // 값이 바뀔 때마다 호출

            }
        });
        sliderView.setInitialIndex(2); // 다섯 개의 기분 중 가운데 기분이 디폴트 값임
    }

    public void setDateString(String dateString) {
        dateTv.setText(dateString);
    }
}
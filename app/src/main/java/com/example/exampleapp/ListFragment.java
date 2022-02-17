package com.example.exampleapp;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ListFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup)inflater.inflate(R.layout.fragment_list, container, false); // rootView는 최상위 레이아웃, xml 인플레이션을 통해 참조한 객체임
        // -> 인플레이션 과정이 끝나고 나면 프래그먼트가 하나의 뷰처럼 동작할 수 있게됨
        initUi(rootView);

        return rootView;
    }
    private void initUi(ViewGroup rootView) { // 인플레이션 후에 xml 레이아웃 안에 들어 있는 위젯이나 레이아웃을 찾아
        // 변수에 할당하는 코드들을 넣기 위해 만들어 둔 것임
    }
}
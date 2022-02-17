package com.example.exampleapp;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.ContentInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;

import lib.kingja.switchbutton.SwitchMultiButton;

public class ListFragment extends Fragment {

    RecyclerView recyclerView;
    CardAdapter adapter;

    Context context;

    onTabItemSelectedListener listener;

    @Override // 액티비티에 붙을 때 호출됨 -> 액티비티를 위해 설정해야하는 정보들은 이 곳에서 처리함
    public void onAttach(@NonNull Context context) { // context 객체나 리스너 객체를 참조하여 변수에 할당
        super.onAttach(context);

        this.context = context;

        if(context instanceof onTabItemSelectedListener) { // context가
            listener = (onTabItemSelectedListener) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();

        if(context != null) {
            context = null;
            listener = null;
        }
    }

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

        Button writeBtn = rootView.findViewById(R.id.writeBtn);
        writeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(listener != null) {
                    listener.onTabSelected(1); // 작성하기 버튼을 누르면 두 번째 프래그먼트(작성화면)를 띄워줌
                }
            }
        });

        SwitchMultiButton switchBtn = rootView.findViewById(R.id.switchBtn);
        switchBtn.setOnSwitchListener(new SwitchMultiButton.OnSwitchListener() {
            @Override
            public void onSwitch(int position, String tabText) {
                adapter.switchLayout(position);
                adapter.notifyDataSetChanged();
            }
        });

        recyclerView = rootView.findViewById(R.id.recyclerView);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        adapter = new CardAdapter();

        adapter.addItem(new Card(0, "서울시 강남구 홍길동", "0", ", ", ", ", "오늘도 열심히 공부", "0", "cube.jpg", "2월 17일"));
        adapter.addItem(new Card(0, "서울시 강남구 홍길동", "0", ", ", ", ", "오늘도 열심히 공부", "0", "cube.jpg", "2월 17일"));
        adapter.addItem(new Card(0, "서울시 강남구 홍길동", "0", ", ", ", ", "오늘도 열심히 공부", "0", "cube.jpg", "2월 17일"));
        adapter.addItem(new Card(0, "서울시 강남구 홍길동", "0", ", ", ", ", "오늘도 열심히 공부", "0", "cube.jpg", "2월 17일"));
        adapter.addItem(new Card(0, "서울시 강남구 홍길동", "0", ", ", ", ", "오늘도 열심히 공부", "0", "cube.jpg", "2월 17일"));

        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new OnCardItemClickListener() {
            @Override
            public void onItemClick(CardAdapter.ViewHolder holder, View view, int position) {
                Card item = adapter.getItem(position);
                Toast.makeText(getContext(), "아이템 선택 : " + item.getContents(), Toast.LENGTH_SHORT).show();
            }
        });
        }
    }
package com.example.exampleapp;

public interface onTabItemSelectedListener { // 하단 탭에 들어 있는 각각의 버튼을 눌렀을 때
    // onNavigationItemSelected()가 자동으로 호출되므로 그 안에서 메뉴 아이템의 id값으로 버튼을 구분한 후 그에 맞는 기능을 구현함
    public void onTabSelected(int position);
}

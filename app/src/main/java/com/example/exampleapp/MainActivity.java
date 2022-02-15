package com.example.exampleapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity implements onTabItemSelectedListener {

    ListFragment listFragment;
    WriteFragment writeFragment;
    GraphFragment graphFragment;

    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listFragment = new ListFragment();
        writeFragment = new WriteFragment();
        graphFragment = new GraphFragment();

        getSupportFragmentManager().beginTransaction().replace(R.id.container, listFragment).commit();

        bottomNavigationView = findViewById(R.id.bottomNavi);
        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.tab1:
                                Toast.makeText(getApplicationContext(), "리스트", Toast.LENGTH_SHORT).show();
                                getSupportFragmentManager().beginTransaction().replace(R.id.container, listFragment).commit();

                                return true;
                            case R.id.tab2:
                                Toast.makeText(getApplicationContext(), "작성", Toast.LENGTH_SHORT).show();
                                getSupportFragmentManager().beginTransaction().replace(R.id.container, writeFragment).commit();

                                return true;

                            case R.id.tab3:
                                Toast.makeText(getApplicationContext(), "그래프", Toast.LENGTH_SHORT).show();
                                getSupportFragmentManager().beginTransaction().replace(R.id.container, graphFragment).commit();

                                return true;
                        }
                        return false;
                    }
                });
    }

    public void onTabSelected(int position) {
        if(position == 0) {
            bottomNavigationView.setSelectedItemId(R.id.tab1);
        } else if(position == 2) {
            bottomNavigationView.setSelectedItemId(R.id.tab2);
        } else if(position == 3) {
            bottomNavigationView.setSelectedItemId(R.id.tab3);
        }
    }
}
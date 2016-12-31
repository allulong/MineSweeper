package com.logn.minesweeper;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_game_view);

//        MineView view = (MineView) findViewById(R.id.mine_view);
//        view.setBackgroundColor(getResources().getColor(R.color.number_1));
//        view.initMine(-1);
    }
}

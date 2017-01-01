package com.logn.minesweeper;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.logn.minesweeper.views.GameView;


public class MainActivity extends AppCompatActivity {

    RadioGroup group;
    RadioButton modePrimary;
    RadioButton modeInter;
    RadioButton modeSenior;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        group = (RadioGroup) findViewById(R.id.game_mode_choose);

        modePrimary = (RadioButton) findViewById(R.id.game_mode_primary);
        modeInter = (RadioButton) findViewById(R.id.game_mode_inter);
        modeSenior = (RadioButton) findViewById(R.id.game_mode_senior);

        group.check(R.id.game_mode_primary);

        findViewById(R.id.game_begin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, MineGameActivity.class);
                startActivity(intent);
            }
        });

        group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkId) {
                switch (checkId) {
                    case R.id.game_mode_primary:
                        GameView.setLevel(GameView.LEVEL.PRIMARY);
                        break;
                    case R.id.game_mode_inter:
                        GameView.setLevel(GameView.LEVEL.INTERMEDIATE);
                        break;
                    case R.id.game_mode_senior:
                        GameView.setLevel(GameView.LEVEL.SENIOR);
                        break;
                    default:
                        GameView.setLevel(GameView.LEVEL.PRIMARY);

                }
            }
        });
    }
}

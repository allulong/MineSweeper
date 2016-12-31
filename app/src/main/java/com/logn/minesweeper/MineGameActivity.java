package com.logn.minesweeper;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.logn.minesweeper.views.GameView;

/**
 * Created by OurEDA on 2016/12/31.
 */

public class MineGameActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_game_view);

        final GameView gameView = (GameView) findViewById(R.id.game_mine);

        findViewById(R.id.game_refresh).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gameView.resetGame();
            }
        });
    }
}

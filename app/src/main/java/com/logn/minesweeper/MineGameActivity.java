package com.logn.minesweeper;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.TextView;

import com.logn.minesweeper.views.GameView;

/**
 * Created by logn on 2016/12/31.
 */

public class MineGameActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_view);

        final GameView gameView = (GameView) findViewById(R.id.game_mine);

        final TextView progressText = (TextView) findViewById(R.id.game_progress);

        findViewById(R.id.game_refresh).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gameView.resetGame();
            }
        });

        findViewById(R.id.game_finish).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        final TextView tv = (TextView) findViewById(R.id.game_mode);
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean mode = !gameView.isMode();
                gameView.setMode(mode);
                if (!mode) {
                    tv.setText(getResources().getText(R.string.mode_open));
                } else {
                    tv.setText(getResources().getText(R.string.mode_mack));
                }
            }
        });

        gameView.setOnProgressListener(new GameView.OnProgressListener() {
            @Override
            public void onProgress(float progress) {
                progressText.setText("进度：" + (int) (progress * 100) + "%");
                if (progress == 1) {
                    showDialogWhenWin("恭喜");
                }
            }
        });
    }

    private void showDialogWhenWin(String info) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MineGameActivity.this);
        builder.setMessage(info);
        builder.setTitle("你赢了！");
        builder.setPositiveButton("继续游戏", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.setNegativeButton("关闭", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.create().show();
    }
}

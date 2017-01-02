package com.logn.minesweeper.views;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Point;
import android.support.v7.app.AlertDialog;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.logn.minesweeper.utils.MineUtils;

import java.util.List;


/**
 * Created by logn on 2016/12/25.
 */

public class GameView extends FrameLayout implements View.OnClickListener {
    private static final String TAG = "GameView";
    private int mineArray[][];
    private Context context;

    private int rows;
    private int columns;
    private int mines;
    private static LEVEL level = LEVEL.PRIMARY;
    //用于计算非地雷的数量
    private int noMineSum = 0;
    private int count = 0;

    private boolean mode = false;

    public boolean isMode() {
        return mode;
    }

    public void setMode(boolean mode) {
        this.mode = mode;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                MineView mv = (MineView) mineFields.findViewById(getID(i, j));
                mv.setMode(mode);
            }
        }
    }

    /**
     * 标记游戏是否结束
     */
    private boolean isGameOver = false;
    private boolean hasWin = false;

    /**
     * 判断是有有第一个 MineView 翻开了。
     */
    private boolean isFirst = true;


    private OnProgressListener progressListener;

    public void setOnProgressListener(OnProgressListener progressListener) {
        this.progressListener = progressListener;
    }

    @Override
    public void onClick(View view) {
        if (isGameOver) {
            doItWhenGameOver();
        } else if (hasWin) {
            if (listener != null) {
                progressListener.onProgress(1);
            }
        }
    }


    public enum LEVEL {
        PRIMARY,
        INTERMEDIATE,
        SENIOR
    }

    private void initStatus() {
        count = 0;
        isFirst = true;
        isGameOver = false;
        hasWin = false;
    }

    public static void setLevel(LEVEL level) {
        GameView.level = level;
    }

    private LinearLayout mineFields;
    private MineView mineViews[][];

    public GameView(Context context) {
        super(context);
        this.context = context;
        initView();
    }

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initView();
    }

    private void initView() {
        LayoutParams lp;
        lp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        mineFields = new LinearLayout(context);
        mineFields.setOrientation(LinearLayout.VERTICAL);
        //通过MODE来决定行数列数
        setMineField(level);
        mineViews = new MineView[rows][columns];
        seedMines();
        //设置监听器，游戏结束时拦截触摸状态，并执行响应操作
        this.setOnClickListener(this);

        addView(mineFields, lp);
    }

    /**
     * 根据已知的行数列数，生成初始的view对象
     */
    private void seedMines() {
        for (int i = 0; i < rows; i++) {
            LinearLayout ll = new LinearLayout(context);
            ll.setOrientation(LinearLayout.HORIZONTAL);
            for (int j = 0; j < columns; j++) {
                mineViews[i][j] = new MineView(context);
                mineViews[i][j].setId(getID(i, j));
                mineViews[i][j].setPoint(i, j);
                mineViews[i][j].show(MineView.MINE_STATUS.UNOPEN_UNMACK);
                mineViews[i][j].setListener(listener);
                ll.addView(mineViews[i][j], 120, 120);
            }
            mineFields.addView(ll);
        }

    }

    private MineView.OnStatusChangeListener listener = new MineView.OnStatusChangeListener() {
        /**
         *
         * @param view  被点击的MineView
         * @param doOpen    是否将要点开这个 MineView, （第二次以后点击才生效）
         * @return 返回true时，一定点开,false则不一定
         */
        @Override
        public boolean change(View view, boolean doOpen) {
            MineView mineV = (MineView) view;
            int x = mineV.getPoint().getX();
            int y = mineV.getPoint().getY();


            if (isFirst) {  //第一次点击执行此段代码，生成地雷分布图
                mineArray = MineUtils.mineGenerator(x, y, rows, columns, mines);
                setAllMineNumber();

                //当一个打开的MineView为空时，直接打开周围的几个MineView
                openMineAround(x, y);
                isFirst = false;


                return true;
            }

            //第二次以后的点击执行后面的代码，此时doOpen起作用
            if (doOpen && mineV.getMine_status() == MineView.MINE_STATUS.UNOPEN_UNMACK) { //点开view时，判断是否是地雷
                if (mineV.getNumber() == -1) {    //踩到了地雷，游戏结束。
                    showAllRealMine();
                    isGameOver = true;
                    //mineV.setBackgroundColor(getResources().getColor(R.color.number_8));
                    //标记被踩到的地雷
                    mineV.showMineBond();
                    doItWhenGameOver();
                    return false;
                } else if (mineV.getNumber() == 0) {     //踩到了0，四周都为数字
                    openMineAround(x, y);   //位置（x,y）有可能在这里被打开，打开了则会被计数
                }
                //先判断是否打开了，不管是不是0都手动打开并计数
                if (mineV.getMine_status() == MineView.MINE_STATUS.UNOPEN_UNMACK) {
                    mineV.show(MineView.MINE_STATUS.OPEN_MINE);
                    countAdd();
                }
            }


            return false;
        }
    };

    /**
     * 恢复到最开始的状态
     */
    public void resetGame() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                MineView mv = (MineView) mineFields.findViewById(getID(i, j));
                mv.resetView();
            }
        }
        initStatus();
        if (progressListener != null) {
            progressListener.onProgress(0);
        }
    }

    /**
     * 当踩到地雷时，显示所有的地雷
     */
    private void showAllRealMine() {
        if (rows <= 0 || columns <= 0)
            return;
        MineView mv;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                mv = (MineView) mineFields.findViewById(getID(i, j));
                if (mv.getNumber() == -1) {
                    mv.showRealMine();
                }
            }
        }
    }

    private void countAdd() {
        count++;
        if (progressListener != null) {
            progressListener.onProgress(count / (float) noMineSum);
            if (count == noMineSum) {
                hasWin = true;
            }
        }
    }

    /**
     * 先判断位置（x,y）是否是0
     * 是则打开周围的MineView 并且继续判断打开的MineView
     *
     * @param x
     * @param y
     */
    private void openMineAround(int x, int y) {
        List<Point> pList = MineUtils.getMineAround(x, y, rows, columns);
        MineView mine = (MineView) mineFields.findViewById(getID(x, y));
        if (mine.getNumber() == 0) {
            for (Point p : pList) {
                MineView mv = (MineView) mineFields.findViewById(getID(p.x, p.y));
                if (mv.getMine_status() == MineView.MINE_STATUS.UNOPEN_UNMACK) {
                    mv.show(MineView.MINE_STATUS.OPEN_NUMBER);
                    countAdd();
                    openMineAround(p.x, p.y);
                }
            }
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (isGameOver || hasWin) {
            return true;
        }
        return super.onInterceptTouchEvent(ev);
    }

    private void setAllMineNumber() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                mineViews[i][j].setNumber(mineArray[i][j]);
            }
        }
    }

    private void setMineField(LEVEL level) {
        switch (level) {
            case PRIMARY:
                rows = 9;
                columns = 9;
                mines = 10;
                break;
            case INTERMEDIATE:
                rows = 16;
                columns = 16;
                mines = 40;
                break;
            case SENIOR:
                rows = 16;
                columns = 30;
                mines = 99;
                break;
        }
        noMineSum = rows * columns - mines;
        count = 0;
    }


    /**
     * 根据位置设置不同的id
     *
     * @param x
     * @param y
     * @return
     */
    private int getID(int x, int y) {
        return x * 1000 + y;
    }

    /**
     * 游戏结束后的操作
     */
    private void doItWhenGameOver() {
        showDialogWhenOver("进度：" + (int) (count / (float) noMineSum * 100) + "%");
    }

    public interface OnProgressListener {
        void onProgress(float progress);
    }

    private void showDialogWhenOver(String info) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(info);
        builder.setTitle("游戏结束");
        builder.setPositiveButton("重新开始", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                resetGame();
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

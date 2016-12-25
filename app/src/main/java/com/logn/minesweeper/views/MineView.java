package com.logn.minesweeper.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.logn.minesweeper.R;

/**
 * Created by logn on 2016/12/25.
 */

public class MineView extends FrameLayout {
    private RelativeLayout mineView;
    private LayoutInflater inflater;
    private ImageView surface;
    private TextView numberView;
    private Context context;

    private boolean isMine;
    private MINE_STATUS mine_status;
    /**
     * 地雷为-1，其他为0-8
     * <p>
     * 暂时用纯数字表示地雷状态
     * 怀疑的：-2，
     * 标记的：-3
     */
    private int number;

    public MineView(Context context) {
        super(context);
        this.context = context;
        inflater = LayoutInflater.from(context);
        initView();
    }

    public MineView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    public MineView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        inflater = LayoutInflater.from(context);
    }


    private void initView() {
        LayoutParams lp;
        lp = new LayoutParams(-1, -1);
        lp.setMargins(0, 0, 0, 0);
        //载入布局
        mineView = (RelativeLayout) inflater.inflate(R.layout.item_mine, null);
        surface = (ImageView) mineView.findViewById(R.id.item_mine_surface);
        numberView = (TextView) mineView.findViewById(R.id.item_mine_number);
        //初始化时只显示imageview
        numberView.setVisibility(GONE);

        //将加载的布局放进FrameLayout
        addView(mineView);

        setNumber(0);
        show(MINE_STATUS.UNOPEN_UNMACK);
    }

    public void initMine(int number, boolean isMine) {
        setNumber(number);
        this.isMine = isMine;
    }

    public MINE_STATUS getMine_status() {
        return mine_status;
    }

    public int getNumber() {
        return number;
    }

    /**
     * 初始化时设置Mine的状态，并根据状设置颜色
     *
     * @param number
     */
    public void setNumber(int number) {
        this.number = number;
        switch (number) {
            case 1:
                numberView.setTextColor(getResources().getColor(R.color.number_1));
                break;
            case 2:
                numberView.setTextColor(getResources().getColor(R.color.number_2));
                break;
            case 3:
                numberView.setTextColor(getResources().getColor(R.color.number_3));
                break;
            case 4:
                numberView.setTextColor(getResources().getColor(R.color.number_4));
                break;
            case 5:
                numberView.setTextColor(getResources().getColor(R.color.number_5));
                break;
            case 6:
                numberView.setTextColor(getResources().getColor(R.color.number_6));
                break;
            case -1:
                numberView.setTextColor(getResources().getColor(R.color.number_1_mime));
                break;
            case -2:
                numberView.setTextColor(getResources().getColor(R.color.number_2_doubt));
                break;
            case -3:
                numberView.setTextColor(getResources().getColor(R.color.number_3_mack));
                break;
            default:
                numberView.setTextColor(getResources().getColor(R.color.surface));
        }
    }

    /**
     * 显示view的各种状态
     *
     * @param status
     */
    public void show(MINE_STATUS status) {
        switch (status) {
            case UNOPEN_UNMACK:
                mine_status = MINE_STATUS.UNOPEN_UNMACK;
                break;
            case UNOPEN_MACK:   //此状态下不可翻开
                if (mine_status == MINE_STATUS.UNOPEN_UNMACK) {

                    mine_status = MINE_STATUS.UNOPEN_MACK;
                }
                break;
            case UNOPEN_DOUBT:   //此状态下不可翻开
                if (mine_status == MINE_STATUS.UNOPEN_MACK) {

                    mine_status = MINE_STATUS.UNOPEN_MACK;
                }
                break;
            case OPEN_MINE:     //完蛋
                if (mine_status == MINE_STATUS.UNOPEN_UNMACK) {

                    mine_status = MINE_STATUS.OPEN_MINE;
                }
                break;
            case OPEN_NUMBER:
                if (mine_status == MINE_STATUS.UNOPEN_UNMACK) {
                    surface.setVisibility(GONE);
                    numberView.setVisibility(View.VISIBLE);
                    mine_status = MINE_STATUS.OPEN_NUMBER;
                }
                break;
        }

    }

    /**
     * 状态的枚举
     */
    public static enum MINE_STATUS {
        UNOPEN_UNMACK,      // 未点开，未标记,显示imageView
        UNOPEN_MACK,        // 未点开，已标记，显示imageView，小旗子
        UNOPEN_DOUBT,            // 有疑问状态， 显示imageView， 问号
        OPEN_MINE,          //打开的地雷，显示imageView，地雷
        OPEN_NUMBER         //打开的数字，显示textView，或者 空
    }

}

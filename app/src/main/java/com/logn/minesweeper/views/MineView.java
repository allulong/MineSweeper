package com.logn.minesweeper.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.logn.minesweeper.R;

import static com.logn.minesweeper.views.MineView.MINE_STATUS.UNOPEN_MACK;
import static com.logn.minesweeper.views.MineView.MINE_STATUS.UNOPEN_UNMACK;

/**
 * Created by logn on 2016/12/25.
 */

public class MineView extends FrameLayout implements View.OnClickListener, View.OnLongClickListener {
    private RelativeLayout mineView;
    private LayoutInflater inflater;
    private ImageView surface;
    private TextView numberView;
    private Context context;

    private boolean isMine = false;
    private boolean doOpen = false;
    /**
     * 用于更改模式
     * false 为翻开模式（默认）;
     * true  为标记模式
     */
    private boolean mode = false;
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
        initView();
    }

    public MineView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        inflater = LayoutInflater.from(context);
        initView();
    }


    private void initView() {
        LayoutParams layoutParams;
        layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        //载入布局
        mineView = (RelativeLayout) inflater.inflate(R.layout.item_mine, null);
        surface = (ImageView) mineView.findViewById(R.id.item_mine_surface);
        numberView = (TextView) mineView.findViewById(R.id.item_mine_number);

        mineView.setOnClickListener(this);
        mineView.setOnLongClickListener(this);
        //初始化时
        surface.setVisibility(GONE);

        //将加载的布局放进FrameLayout
        addView(mineView, layoutParams);

        setNumber(9, true);   //表示刚开始的状态
        mine_status = MINE_STATUS.UNOPEN_UNMACK;
        show(MINE_STATUS.UNOPEN_UNMACK);
    }

    /**
     * 一定要调用此方法，用于初始化mine 的数字
     *
     * @param number -1 代表地雷（暂时的。。。）
     */
    public void initMine(int number) {
        setNumber(number);
        if (number == -1) {
            isMine = true;
        } else {
            isMine = false;
        }
    }

    /**
     * 重写{@link #onMeasure(int, int)}实现限制 view 为正方形
     * 默认值为120
     *
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = getSide(120, widthMeasureSpec);
        int height = getSide(120, heightMeasureSpec);

        if (width < height) {
            height = width;
        } else {
            width = height;
        }
        setMeasuredDimension(width, height);

    }

    private int getSide(int defaultSize, int measureSpec) {
        int mySize = defaultSize;

        int mode = MeasureSpec.getMode(measureSpec);
        int size = MeasureSpec.getSize(measureSpec);

        switch (mode) {
            case MeasureSpec.UNSPECIFIED: {//如果没有指定大小，就设置为默认大小
                mySize = defaultSize;
                break;
            }
            case MeasureSpec.AT_MOST: {//如果测量模式是最大取值为size
                //我们将大小取最大值,你也可以取其他值
                mySize = size;
                break;
            }
            case MeasureSpec.EXACTLY: {//如果是固定的大小，那就不要去改变它
                mySize = size;
                break;
            }
        }
        return mySize;
    }

    public MINE_STATUS getMine_status() {
        return mine_status;
    }

    public int getNumber() {
        return number;
    }

    public void changMode() {
        mode = !mode;
    }

    /**
     * 初始化时设置Mine的状态，并根据状设置颜色
     *
     * @param number
     */
    public void setNumber(int number) {
        setNumber(number, false);
    }

    /**
     * @param number
     * @param down   设置数字的时候是否修改界面
     */
    private void setNumber(int number, boolean down) {
        this.number = number;
        if (down)
            setTextColor(number);
    }

    private void setTextColor(int number) {
        numberView.setText(number + "");
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
                numberView.setTextColor(getResources().getColor(R.color.number_3_mack));
        }
    }

    /**
     * 显示view的各种状态
     *
     * @param status
     */
    public void show(MINE_STATUS status) {
        switch (status) {       //目前的状态，即要显示的状态
            case UNOPEN_UNMACK:
                if (mine_status == MINE_STATUS.UNOPEN_UNMACK || mine_status == MINE_STATUS.UNOPEN_DOUBT) {
                    setTextColor(9);
                    mine_status = UNOPEN_UNMACK;
                }
                break;
            case UNOPEN_MACK:   //只有当前状态为unopen_unmake 时才能显示此状态
                if (mine_status == MINE_STATUS.UNOPEN_UNMACK) {
                    setTextColor(-3);
                    mine_status = UNOPEN_MACK;
                }
                break;
            case UNOPEN_DOUBT:   //只有当前状态为unopen_make 时才能显示此状态
                if (mine_status == UNOPEN_MACK) {
                    setTextColor(-2);
                    mine_status = MINE_STATUS.UNOPEN_DOUBT;
                }
                break;
            case OPEN_MINE:     //只有当前状态为unopen_unmake 时才能显示此状态
                if (mine_status == MINE_STATUS.UNOPEN_UNMACK && isMine) {
                    setTextColor(-1);
                    mine_status = MINE_STATUS.OPEN_MINE;
                }
                break;
            case OPEN_NUMBER:   //只有当前状态为unopen_unmake 时才能显示此状态
                if (mine_status == MINE_STATUS.UNOPEN_UNMACK && !isMine) {
//                    surface.setVisibility(GONE);
//                    numberView.setVisibility(View.VISIBLE);
//                    mine_status = MINE_STATUS.OPEN_NUMBER;
                    setTextColor(number);
                    mine_status = MINE_STATUS.OPEN_NUMBER;
                }
                break;
        }

    }

    private void changeStatus() {
        switch (mine_status) {
            case UNOPEN_UNMACK:
                if (doOpen) {
                    Toast.makeText(context, "unmack", Toast.LENGTH_SHORT).show();
                    if (isMine) {
                        show(MINE_STATUS.OPEN_MINE);
                    } else {
                        show(MINE_STATUS.OPEN_NUMBER);
                    }
                } else {
                    Toast.makeText(context, "mack", Toast.LENGTH_SHORT).show();
                    show(UNOPEN_MACK);
                }
                break;
            case UNOPEN_MACK:
                show(MINE_STATUS.UNOPEN_DOUBT);
                break;
            case UNOPEN_DOUBT:
                show(MINE_STATUS.UNOPEN_UNMACK);
                break;
            case OPEN_MINE:
                show(MINE_STATUS.OPEN_MINE);
                break;
            case OPEN_NUMBER:
                show(MINE_STATUS.OPEN_NUMBER);
                break;
        }
    }

    @Override
    public void onClick(View view) {
        doOpen = !mode;
        Toast.makeText(context, "click: " + mine_status, Toast.LENGTH_SHORT).show();
        changeStatus();
    }

    @Override
    public boolean onLongClick(View view) {
        doOpen = mode;
        Toast.makeText(context, "longClick", Toast.LENGTH_SHORT).show();
        changeStatus();
        return true;
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

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

import com.logn.minesweeper.R;

import static com.logn.minesweeper.views.MineView.MINE_STATUS.UNOPEN_MACK;
import static com.logn.minesweeper.views.MineView.MINE_STATUS.UNOPEN_UNMACK;

/**
 * 目前用数字代表不同的状态
 * 0到8即正常数字
 * -1代表是地雷
 * -2代表是怀疑的状态（仅用于显示状态）
 * -3代表是标记的状态（仅用于显示状态）
 * 其他数字（9）代表最开始状态（仅用于显示状态）
 * <p>
 * Created by logn on 2016/12/25.
 */

public class MineView extends FrameLayout implements View.OnClickListener, View.OnLongClickListener {
    private RelativeLayout mineView;
    private LayoutInflater inflater;
    private ImageView surface;
    private TextView numberView;
    private Context context;
    private Point point;

    private OnStatusChangeListener listener;

    public void setListener(OnStatusChangeListener listener) {
        this.listener = listener;
    }

    public Point getPoint() {
        return point;
    }

    public void setPoint(int x, int y) {
        point.setX(x);
        point.setY(y);
    }

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
        point = new Point(0, 0);
        LayoutParams layoutParams;
        layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        //载入布局
        mineView = (RelativeLayout) inflater.inflate(R.layout.item_mine, null);
        surface = (ImageView) mineView.findViewById(R.id.item_mine_surface);
        numberView = (TextView) mineView.findViewById(R.id.item_mine_number);

        mineView.setOnClickListener(this);
        mineView.setOnLongClickListener(this);
        //初始化时,显示number
        selectNumberView();

        //将加载的布局放进FrameLayout
        addView(mineView, layoutParams);

        setNumber(0);   //初始化时默认为0
        setSurface(9);//表示刚开始的状态
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
     * 设置此地雷view的实质
     * 即是数字？还是地雷？
     *
     * @param number
     */
    public void setNumber(int number) {
        this.number = number;
        if (number == -1) {
            isMine = true;
        } else {
            isMine = false;
        }
    }


    /**
     * 用于显示不同状态的界面（现在用数字代表多种状态）
     *
     * @param number
     */
    private void setSurface(int number) {
        selectAll();
        numberView.setText(number + "");
        surface.setImageResource(R.drawable.mine_surface_disable);
        surface.setScaleType(ImageView.ScaleType.FIT_XY);
        switch (number) {
            case 0:
                numberView.setText("");
                //break;
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
                numberView.setText("*");
                numberView.setTextColor(getResources().getColor(R.color.number_1_mime));
                break;
            case -2:
                selectAll();
                numberView.setText("?");
                surface.setImageResource(R.drawable.mine_surface_normal);
                numberView.setTextColor(getResources().getColor(R.color.number_2_doubt));
                break;
            case -3:
                selectAll();
                numberView.setText("X");
                numberView.setTextColor(getResources().getColor(R.color.number_3_mack));
                surface.setImageResource(R.drawable.mine_surface_normal);
                break;
            default:
                selectSurface();
                //numberView.setTextColor(getResources().getColor(R.color.number_3_mack));
                surface.setImageResource(R.drawable.mine_surface_normal);
        }
    }

    /**
     * 重置view
     * 用于重新开始游戏
     */
    public void resetView() {
        setSurface(9);
        mine_status = UNOPEN_UNMACK;
        setNumber(0);
    }

    /**
     * 显示view的各种状态
     *
     * @param status
     */
    public void show(MINE_STATUS status) {
        switch (status) {       //将要显示的状态
            case UNOPEN_UNMACK:
                if (mine_status == MINE_STATUS.UNOPEN_UNMACK || mine_status == MINE_STATUS.UNOPEN_DOUBT) {
                    setSurface(9);
                    mine_status = UNOPEN_UNMACK;
                }
                break;
            case UNOPEN_MACK:   //只有当前状态为unopen_unmake 时才能显示此状态
                if (mine_status == MINE_STATUS.UNOPEN_UNMACK) {
                    setSurface(-3);
                    mine_status = UNOPEN_MACK;
                }
                break;
            case UNOPEN_DOUBT:   //只有当前状态为unopen_make 时才能显示此状态
                if (mine_status == UNOPEN_MACK) {
                    setSurface(-2);
                    mine_status = MINE_STATUS.UNOPEN_DOUBT;
                }
                break;
            case OPEN_MINE:     //只有当前状态为unopen_unmake 时才能显示此状态
                if (mine_status == MINE_STATUS.UNOPEN_UNMACK && isMine) {
                    setSurface(-1);
                    mine_status = MINE_STATUS.OPEN_MINE;
                }
                break;
            case OPEN_NUMBER:   //只有当前状态为unopen_unmake 时才能显示此状态
                if (mine_status == MINE_STATUS.UNOPEN_UNMACK && !isMine) {
//                    surface.setVisibility(GONE);
//                    numberView.setVisibility(View.VISIBLE);
//                    mine_status = MINE_STATUS.OPEN_NUMBER;
                    setSurface(number);
                    mine_status = MINE_STATUS.OPEN_NUMBER;
                }
                break;
        }

    }

    private void changeStatus() {
        if (listener != null && listener.change(this, doOpen)) {
            doOpen = true;
        }
        switch (mine_status) {
            case UNOPEN_UNMACK:
                if (doOpen) {
                    if (isMine) {
                        show(MINE_STATUS.OPEN_MINE);
                    } else {
                        show(MINE_STATUS.OPEN_NUMBER);
                    }
                } else {
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
        changeStatus();
    }

    @Override
    public boolean onLongClick(View view) {
        doOpen = mode;
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


    private void selectSurface() {
        numberView.setVisibility(GONE);
        surface.setVisibility(VISIBLE);
    }

    private void selectNumberView() {
        surface.setVisibility(GONE);
        numberView.setVisibility(VISIBLE);
    }

    private void selectAll() {
        surface.setVisibility(VISIBLE);
        numberView.setVisibility(VISIBLE);
    }

    public class Point {
        int x;
        int y;

        public Point(int x, int y) {
            setX(x);
            setY(y);
        }

        public int getY() {
            return y;
        }

        public void setY(int y) {
            this.y = y;
        }

        public int getX() {
            return x;
        }

        public void setX(int x) {
            this.x = x;
        }
    }

    public interface OnStatusChangeListener {
        /**
         * 此方法返回为真时
         * 长按和点击都打开mine view
         *
         * @param view
         * @return
         */
        boolean change(View view, boolean doOpen);
    }


}

package com.logn.minesweeper.utils;

import java.util.Random;

/**
 * 输入行数、列数、地雷数目，生成地雷分布图。
 */

public class MineUtils {
    public static final int MINE = -1;

    private static int[][] mine;

    /**
     * 为了避免点击的第一个方块为地雷，设置点x,y 以及四周的八个（？）为非雷区
     * 即 为先设置为（-2）
     *
     * @param i
     * @param j
     */
    private static void setFirst(int i, int j, int rows, int columns) {

        //(i-1)->(j-1,j,j+1)
        if (i - 1 >= 0) {
            if (j - 1 >= 0) {
                mine[i - 1][j - 1] = -2;
            }
            if (j + 1 < columns) {
                mine[i - 1][j] = -2;
            }
            mine[i - 1][j + 1] = -2;
        }
        //(i)->(j-1,j+1)
        if (i >= 0) {
            if (j - 1 >= 0) {
                mine[i][j - 1] = -2;
            }
            if (j + 1 < columns) {
                mine[i][j + 1] = -2;
            }
            mine[i][j] = -2;
        }
        //(i+1)->(j-1,j,j+1)
        if (i + 1 < rows) {
            if (j - 1 >= 0) {
                mine[i + 1][j - 1] = -2;
            }
            if (j + 1 < columns) {
                mine[i + 1][j + 1] = -2;
            }
            mine[i + 1][j] = -2;
        }
    }

    /**
     * 返回整形二维数组
     * 非地雷 数字： 0-8
     * 地雷：  -1 {@link #MINE}
     * 以x,y为中心的几个方块都为非雷区
     *
     * @param rows
     * @param columns
     * @param mineNum
     * @return
     */
    public static int[][] mineGenerator(int x, int y, int rows, int columns, int mineNum) {
        if (mine == null)
            mine = new int[rows][columns];
        mineNum = mineNum >= (int) (rows * columns * 0.8) ? (int) (rows * columns * 0.8) : mineNum;
        setFirst(x, y, rows, columns);
        for (int i = 0; i < mineNum; i++) {
            int r = getNum(rows);
            int c = getNum(columns);
            if (mine[r][c] == -1 || mine[r][c] == -2) { //遇到地雷或非雷区（经过定义的）则重新生成
                i--;
            } else {
                mine[r][c] = -1;
            }
        }
        //生成地雷位置后根据地雷计算其余各个位置的number
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                if (mine[i][j] != -1) {
                    int num = getAroundNum(i, j, rows, columns, mine);
                    mine[i][j] = num;
                }
            }
        }
        return mine;
    }

    /**
     * 返回（i,j）四周的地雷的数目
     *
     * @param i
     * @param j
     * @param rows
     * @param columns
     * @param mine
     * @return
     */
    private static int getAroundNum(int i, int j, int rows, int columns, int[][] mine) {
        int num = 0;
        //(i-1)->(j-1,j,j+1)
        if (i - 1 >= 0) {
            if (j - 1 >= 0) {
                if (mine[i - 1][j - 1] == -1) {
                    num++;
                }
            }
            if (j + 1 < columns) {
                if (mine[i - 1][j + 1] == -1) {
                    num++;
                }
            }
            if (mine[i - 1][j] == -1) {
                num++;
            }
        }
        //(i)->(j-1,j+1)
        if (i >= 0) {
            if (j - 1 >= 0) {
                if (mine[i][j - 1] == -1) {
                    num++;
                }
            }
            if (j + 1 < columns) {
                if (mine[i][j + 1] == -1) {
                    num++;
                }
            }
        }
        //(i+1)->(j-1,j,j+1)
        if (i + 1 < rows) {
            if (j - 1 >= 0) {
                if (mine[i + 1][j - 1] == -1) {
                    num++;
                }
            }
            if (j + 1 < columns) {
                if (mine[i + 1][j + 1] == -1) {
                    num++;
                }
            }
            if (mine[i + 1][j] == -1) {
                num++;
            }
        }
        return num;
    }

    /**
     * 返回一个在区间[0 , max) 内的随机数
     *
     * @param max
     * @return
     */
    private static int getNum(int max) {
        Random random = new Random();
        int a = Math.abs(random.nextInt()) % max;
        return a;
    }

}

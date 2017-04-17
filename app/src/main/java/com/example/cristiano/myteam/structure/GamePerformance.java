package com.example.cristiano.myteam.structure;

/**
 * Created by Cristiano on 2017/4/15.
 */

public class GamePerformance {
    private int win,draw,loss;

    public GamePerformance(int win, int draw, int loss) {
        this.win = win;
        this.draw = draw;
        this.loss = loss;
    }

    public int getWin() {
        return win;
    }

    public void setWin(int win) {
        this.win = win;
    }

    public int getDraw() {
        return draw;
    }

    public void setDraw(int draw) {
        this.draw = draw;
    }

    public int getLoss() {
        return loss;
    }

    public void setLoss(int loss) {
        this.loss = loss;
    }
}

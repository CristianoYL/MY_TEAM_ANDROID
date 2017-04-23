package com.example.cristiano.myteam.structure;

import com.google.gson.Gson;

/**
 * Created by Cristiano on 2017/4/7.
 */

public class Stats {
    private int tournamentID, clubID, playerID;
    private int win,draw,loss,goalsConceded;
    public int attendance, appearance, start, goal, penalty,freekick, penaltyShootout, penaltyTaken,
            ownGoal, header, weakFootGoal, otherGoal, assist, yellow, red, cleanSheet, penaltySaved;

    public Stats(int tournamentID, int clubID, int playerID, int attendance, int appearance,
                 int start, int goal, int penalty, int freekick,int penaltyShootout, int penaltyTaken,
                 int ownGoal, int header, int weakFootGoal, int otherGoal,int assist, int yellow,
                 int red, int cleanSheet, int penaltySaved) {
        this.tournamentID = tournamentID;
        this.clubID = clubID;
        this.playerID = playerID;
        this.attendance = attendance;
        this.appearance = appearance;
        this.start = start;
        this.goal = goal;
        this.penalty = penalty;
        this.freekick = freekick;
        this.penaltyShootout = penaltyShootout;
        this.penaltyTaken = penaltyTaken;
        this.ownGoal = ownGoal;
        this.header = header;
        this.weakFootGoal = weakFootGoal;
        this.otherGoal = otherGoal;
        this.assist = assist;
        this.yellow = yellow;
        this.red = red;
        this.cleanSheet = cleanSheet;
        this.penaltySaved = penaltySaved;
    }

    public int getTournamentID() {
        return tournamentID;
    }

    public int getClubID() {
        return clubID;
    }

    public int getPlayerID() {
        return playerID;
    }

    public String toJson(){
        Gson gson = new Gson();
        return gson.toJson(this);
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

    public int getGoalsConceded() {
        return goalsConceded;
    }

    public void setGoalsConceded(int goalsConceded) {
        this.goalsConceded = goalsConceded;
    }
}

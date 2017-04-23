package com.example.cristiano.myteam.structure;

/**
 * Created by Cristiano on 2017/4/19.
 */

public class ClubInfo {
    private Club club;
    private Tournament[] tournaments;
    private Teamsheet[] teamsheet;

    public ClubInfo(Club club, Tournament[] tournaments, Teamsheet[] teamsheet) {
        this.club = club;
        this.tournaments = tournaments;
        this.teamsheet = teamsheet;
    }

    public Club getClub() {
        return club;
    }

    public void setClub(Club club) {
        this.club = club;
    }

    public Tournament[] getTournaments() {
        return tournaments;
    }

    public void setTournaments(Tournament[] tournaments) {
        this.tournaments = tournaments;
    }

    public Teamsheet[] getTeamsheet() {
        return teamsheet;
    }

    public void setTeamsheet(Teamsheet[] teamsheet) {
        this.teamsheet = teamsheet;
    }
    
}

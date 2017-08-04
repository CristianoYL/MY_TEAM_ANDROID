package com.example.cristiano.myteam.structure;

import java.util.List;

/**
 * Created by Cristiano on 2017/4/19.
 */

public class ClubInfo {
    private Club club;
    private List<Tournament> tournaments;
    private List<Member> member;

    public ClubInfo(Club club, List<Tournament> tournaments, List<Member> member) {
        this.club = club;
        this.tournaments = tournaments;
        this.member = member;
    }

    public Club getClub() {
        return club;
    }

    public void setClub(Club club) {
        this.club = club;
    }

    public List<Tournament> getTournaments() {
        return tournaments;
    }

    public List<Member> getMember() {
        return member;
    }

}

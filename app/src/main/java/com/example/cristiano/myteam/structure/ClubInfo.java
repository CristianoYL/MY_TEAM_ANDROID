package com.example.cristiano.myteam.structure;

/**
 * Created by Cristiano on 2017/4/19.
 */

public class ClubInfo {
    private Club club;
    private Tournament[] tournaments;
    private Member[] member;

    public ClubInfo(Club club, Tournament[] tournaments, Member[] member) {
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

    public Tournament[] getTournaments() {
        return tournaments;
    }

    public void setTournaments(Tournament[] tournaments) {
        this.tournaments = tournaments;
    }

    public Member[] getMember() {
        return member;
    }

    public void setMember(Member[] member) {
        this.member = member;
    }
    
}

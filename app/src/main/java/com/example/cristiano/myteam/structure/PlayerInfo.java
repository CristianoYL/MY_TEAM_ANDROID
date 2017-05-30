package com.example.cristiano.myteam.structure;

import java.util.HashMap;

/**
 * Created by Cristiano on 2017/4/15.
 */

public class PlayerInfo {
    private Player player;
    private Club[] clubs;
    private HashMap<Integer,Tournament[]> clubTournaments;
    private Stats totalStats;

    public PlayerInfo(Player player, Club[] clubs, Stats totalStats) {
        this.player = player;
        this.clubs = clubs;
        this.clubTournaments = new HashMap<>();
        this.totalStats = totalStats;
    }

    public Player getPlayer() {
        return player;
    }

    public Club[] getClubs() {
        return clubs;
    }

    public Club[] addClub(Club club) {
        Club[] clubs = new Club[this.clubs.length+1];
        for ( int i = 0; i < this.clubs.length; i++ ) {
            clubs[i] = this.clubs[i];
        }
        clubs[clubs.length-1] = club;
        this.clubs = clubs;
        return this.clubs;
    }

    public void addClubTournament(int clubID, Tournament[] tournaments) {
        this.clubTournaments.put(clubID,tournaments);
    }

    public Tournament[] getClubTournaments(int clubID) {
        return this.clubTournaments.get(clubID);
    }

    public Stats getTotalStats() {
        return this.totalStats;
    }
}

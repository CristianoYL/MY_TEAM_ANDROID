package com.example.cristiano.myteam.structure;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Cristiano on 2017/4/15.
 */

public class PlayerInfo {
    private Player player;
    private ArrayList<Club> clubs;
    private HashMap<Integer,Tournament[]> clubTournaments;
    private Stats totalStats;

    public PlayerInfo(Player player, ArrayList<Club> clubs, Stats totalStats) {
        this.player = player;
        this.clubs = clubs;
        this.clubTournaments = new HashMap<>();
        this.totalStats = totalStats;
    }

    public Player getPlayer() {
        return player;
    }

    public ArrayList<Club> getClubs() {
        return clubs;
    }

    public void addClub(Club club) {
        this.clubs.add(club);
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

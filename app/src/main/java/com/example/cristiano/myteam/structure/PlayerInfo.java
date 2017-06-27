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
    private HashMap<Integer,ArrayList<Tournament>> clubTournaments;
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

    public void setClubs(ArrayList<Club> clubs){
        this.clubs = clubs;
    }

    public void addClubTournament(int clubID, Tournament tournament) {
        ArrayList<Tournament> tournaments = clubTournaments.get(clubID);
        if ( tournaments == null ) {
            tournaments = new ArrayList<>();
        }
        tournaments.add(tournament);
        clubTournaments.put(clubID,tournaments);
    }

    public ArrayList<Tournament> getClubTournaments(int clubID) {
        return clubTournaments.get(clubID);
    }

    public Stats getTotalStats() {
        return this.totalStats;
    }
}

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
    private HashMap <Integer,Stats> clubStats;
    private HashMap <Integer,Stats> tournamentStats;

    public PlayerInfo(Player player, Club[] clubs, Stats totalStats) {
        this.player = player;
        this.clubs = clubs;
        this.clubTournaments = new HashMap<>();
        this.totalStats = totalStats;
        this.clubStats = new HashMap<>();
        this.tournamentStats = new HashMap<>();
    }

    public Player getPlayer() {
        return player;
    }

    public Club[] getClubs() {
        return clubs;
    }

    public boolean hasClubStats(int clubID) {
        return this.clubStats.containsKey(clubID);
    }

    public boolean hasTournamentStats(int tournamentID) {
        return this.tournamentStats.containsKey(tournamentID);
    }

    public void addClubTournament(int clubID, Tournament[] tournaments) {
        this.clubTournaments.put(clubID,tournaments);
    }

    public Tournament[] getClubTournaments(int clubID) {
        return this.clubTournaments.get(clubID);
    }

    public void addClubStats(int clubID, Stats stats) {
        this.clubStats.put(clubID,stats);
    }

    public void addTournamentStats(int tournamentID, Stats stats) {
        this.clubStats.put(tournamentID,stats);
    }

    public Stats getClubStats(int clubID) {
        return this.clubStats.get(clubID);
    }

    public Stats getTournamentStats(int tournamentID) {
        return this.tournamentStats.get(tournamentID);
    }

    public Stats getTotalStats() {
        return this.totalStats;
    }
}

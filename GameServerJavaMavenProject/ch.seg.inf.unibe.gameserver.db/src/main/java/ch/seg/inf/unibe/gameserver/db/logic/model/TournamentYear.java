package ch.seg.inf.unibe.gameserver.db.logic.model;

import java.util.ArrayList;
import java.util.List;

public class TournamentYear extends IdentifiableElement {

    private List<Tournament> tournaments;

    private int year;

    public List<Tournament> getTournaments() {
        return tournaments;
    }

    public void setTournaments(List<Tournament> tournaments) {
        this.tournaments = tournaments;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String toString(int indent) {
        indent += 4;
        return ref(this) + "{" + "\n" +
                " ".repeat(indent) + "year=" + year + ",\n" +
                " ".repeat(indent) + "tournaments=" + subs(tournaments, indent) + "\n" +
                " ".repeat(indent - 4) + '}';
    }

    public static TournamentYear createExampleData(List<Player> players, List<GameDescription> gamesDescriptions) {
        TournamentYear tournamentYear = new TournamentYear();
        tournamentYear.tournaments = new ArrayList<>();
        tournamentYear.tournaments.add(Tournament.createExampleData(players, gamesDescriptions));
        tournamentYear.tournaments.add(Tournament.createExampleData(players, gamesDescriptions));
        return tournamentYear;
    }

    public void modifyExampleData() {
        tournaments.forEach(Tournament::modifyExampleData);
    }
}

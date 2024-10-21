package ch.seg.inf.unibe.gameserver.db.logic.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Tournament extends IdentifiableElement {

    private List<BonusPoints> boni;

    private Highscore highscore;

    private List<Game> games;
    
    private List<Player> participants;

    private int startMonth;

    private  int endMonth;

    public List<BonusPoints> getBoni() {
        return boni;
    }

    public void setBoni(List<BonusPoints> boni) {
        this.boni = boni;
    }

    public Highscore getHighscore() {
        return highscore;
    }

    public void setHighscore(Highscore highscore) {
        this.highscore = highscore;
    }

    public List<Game> getGames() {
        return games;
    }

    public void setGames(List<Game> games) {
        this.games = games;
    }
    
    public List<Player> getParticipants() {
        return participants;
    }

    public void setParticipants(List<Player> participants) {
        this.participants = participants;
    }

    public int getStartMonth() {
        return startMonth;
    }

    public void setStartMonth(int startMonth) {
        this.startMonth = startMonth;
    }

    public int getEndMonth() {
        return endMonth;
    }

    public void setEndMonth(int endMonth) {
        this.endMonth = endMonth;
    }

    public String toString(int indent) {
        indent += 4;
        return ref(this) +  "{" + "\n" +
                " ".repeat(indent) + "startMonth=" + startMonth + ",\n" +
                " ".repeat(indent) + "endMonth=" + endMonth + ",\n" +
                " ".repeat(indent) + "participants=" + refs(participants) + ",\n" +
                " ".repeat(indent) + "boni=" + subs(boni, indent) + ",\n" +
                " ".repeat(indent) + "highscore=" + sub(highscore, indent) + ",\n" +
                " ".repeat(indent) + "games=" + subs(games, indent) + "\n" +
                " ".repeat(indent - 4) + '}';
    }

    public static Tournament createExampleData(List<Player> players, List<GameDescription> gamesDescriptions) {
        Tournament tournament = new Tournament();

        tournament.boni = new ArrayList<>();
        tournament.boni.add(BonusPoints.createExampleData());
        tournament.boni.add(BonusPoints.createExampleData());

        tournament.participants = new ArrayList<>();
        tournament.participants.addAll(players);

        for (Player player : tournament.participants) {
            player.getParticipates().add(tournament);
        }

        tournament.games = new ArrayList<>();
        tournament.games.add(Game.createExampleData(gamesDescriptions, tournament));
        tournament.games.add(Game.createExampleData(gamesDescriptions, tournament));
        tournament.games.add(Game.createExampleData(gamesDescriptions, tournament));
        tournament.games.add(Game.createExampleData(gamesDescriptions, tournament));

        tournament.highscore = Highscore.createExampleData(tournament);

        Random random = new Random();
        tournament.startMonth = random.nextInt(12);
        tournament.endMonth = (tournament.startMonth + random.nextInt(12)) % 12;

        return tournament;
    }

    public void modifyExampleData() {
        Random random = new Random();
        startMonth = random.nextInt(12);
        endMonth = (startMonth + random.nextInt(12)) % 12;
        boni.forEach(BonusPoints::modifyExampleData);
        games.forEach(g -> g.modifyExampleData(this));
        highscore.modifyExampleData();
    }
}

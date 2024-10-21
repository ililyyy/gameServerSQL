package ch.seg.inf.unibe.gameserver.db.logic.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameServer extends IdentifiableElement {

    private String name;

    private GameCategory games;

    private List<TournamentYear> tournamentYears;

    private List<Player> players;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public GameCategory getGames() {
        return games;
    }

    public void setGames(GameCategory games) {
        this.games = games;
    }

    public List<TournamentYear> getTournamentYears() {
        return tournamentYears;
    }

    public void setTournamentYears(List<TournamentYear> tournamentYears) {
        this.tournamentYears = tournamentYears;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }

    public String toString(int indent) {
        indent += 4;
        return ref(this) + "{" + "\n" +
                " ".repeat(indent) + "gameLibrary=" + sub(games, indent) + ",\n" +
                " ".repeat(indent) + "players=" + subs(players, indent) + ",\n" +
                " ".repeat(indent) + "tournamentYears=" + subs(tournamentYears, indent) + "\n" +
                " ".repeat(indent - 4) + '}';
    }

    public static GameServer createExampleData() {
        GameServer gameServer = new GameServer();

        Random random = new Random();
        gameServer.setName("Name " + random.nextInt(100));

        Object[] gamesLibrary = GameCategory.createExampleDataTree();
        GameCategory gamesTree = (GameCategory) gamesLibrary[0];
        List<GameDescription> gamesDescriptions = (List<GameDescription>) gamesLibrary[1];

        gameServer.games = gamesTree;

        gameServer.players = new ArrayList<>();
        gameServer.players.add(Player.createExampleData());
        gameServer.players.add(Player.createExampleData());
        gameServer.players.add(Player.createExampleData());
        gameServer.players.add(Player.createExampleData());

        gameServer.tournamentYears = new ArrayList<>();
        gameServer.tournamentYears.add(TournamentYear.createExampleData(gameServer.players, gamesDescriptions));
        gameServer.tournamentYears.add(TournamentYear.createExampleData(gameServer.players, gamesDescriptions));

        return gameServer;
    }

    public void modifyExampleData() {
        Random random = new Random();
        setName("Name " + random.nextInt(100));
        games.modifyExampleData();
        players.forEach(Player::modifyExampleData);
        tournamentYears.forEach(TournamentYear::modifyExampleData);
    }
}

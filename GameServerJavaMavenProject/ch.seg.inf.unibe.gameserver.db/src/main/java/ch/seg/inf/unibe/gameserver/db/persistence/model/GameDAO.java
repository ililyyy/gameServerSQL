package ch.seg.inf.unibe.gameserver.db.persistence.model;

import ch.seg.inf.unibe.gameserver.db.logic.model.Game;
import ch.seg.inf.unibe.gameserver.db.logic.model.Player;
import ch.seg.inf.unibe.gameserver.db.logic.model.Tournament;
import ch.seg.inf.unibe.gameserver.db.persistence.DataAccessLayer;
import ch.seg.inf.unibe.gameserver.db.persistence.DatabaseAccess;
import ch.seg.inf.unibe.gameserver.db.persistence.DatabaseAccess.ConnectedResult;
import ch.seg.inf.unibe.gameserver.db.persistence.DatabaseAccessUtil;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GameDAO  extends IdentifiableElementDAO<Game> {

    /**
     * Provides access to all DAOs by singleton data access layer.
     */
    private DataAccessLayer dataAccess = DataAccessLayer.getInstance();

    public void createTable(boolean recursive) {
        String createTable_Game = """
                CREATE TABLE IF NOT EXISTS Game (
                    id INTEGER PRIMARY KEY,
                    container_id INTEGER,
                    start_time TIMESTAMP,
                    end_time TIMESTAMP,
                    is_of_type_id INTEGER,
                    winner_id INTEGER,
                    FOREIGN KEY (container_id) REFERENCES Tournament(id),
                    FOREIGN KEY (winner_id) REFERENCES Player(id),
                    FOREIGN KEY (is_of_type_id) REFERENCES GameCategory(id)
                );
                """;
        DatabaseAccess.getInstance().executeUpdate(createTable_Game);

        // Table representing the association:
        // Game<-*-participates--players--participants-*->Player
        String createTable_Game_players_Player = """
                CREATE TABLE IF NOT EXISTS Game_players_Player (
                    game_participants_id INTEGER,
                    player_participates_id INTEGER,
                    FOREIGN KEY (game_participants_id) REFERENCES Game(id),
                    FOREIGN KEY (player_participates_id) REFERENCES Player(id)
                );
                """;
        DatabaseAccess.getInstance().executeUpdate(createTable_Game_players_Player);

        // Tables for contained objects:
        if (recursive) {
            dataAccess.getChatDAO().createTable(recursive);
        }
    }

    public void createTree(List<Game> games, Tournament tournamentContainer) {
        for (Game game : games) {
            create(game, tournamentContainer);
            dataAccess.getChatDAO().createTree(game.getChatOfSpectators(), game);
        }
    }

    public void create(Game game, Tournament container) {
        // TODO: Hints:
        //  - Use DatabaseAccessUtil.instantToSQLTimestamp() to convert Java Instant to SQL Timestamp.
        //  - Note that object reference can be null.
        String row = ""; // TODO: SQL query
        DatabaseAccess.getInstance().insertRow(row);

        // TODO: Implement createPlayers(game) TODOs to create connection table rows for players association.
        createPlayers(game);

        // update index
        loaded(game);
    }

    public void createPlayers(Game game) {
        for (Player player : game.getPlayers()) {
            String add = ""; // TODO: SQL query to insert rows representing players references.
            //                    - Hint: See TournamentDAO.createParticipants()
            DatabaseAccess.getInstance().executeUpdate(add);
        }
    }

    public List<Game> readTree(Tournament tournamentContainer) {
        List<Game> games = readByContainer(tournamentContainer);

        for (Game game : games) {
            game.setChatOfSpectators(dataAccess.getChatDAO().readTree(game));
        }

        return games;
    }

    public Game readByID(int id) {
        if (id != 0) { // NULL is interpreted 0 by SQL getInt().
            if (isLoaded(id)) {
                return getLoaded(id);
            } else {
                return checkedResults(read("id = " + id), Game.class, id);
            }
        } else {
            return null;
        }
    }

    public List<Game> readByContainer(Tournament container) {
        return read("container_id = " + container.getID());
    }

    protected List<Game> read(String sqlWhereClause) {
        List<Game> games = new ArrayList<>();

        String read = ""; // TODO: SQL query to select rows based on given WHERE clause.

        // Process the rows:
        try (ConnectedResult connectedResult = DatabaseAccess.getInstance().executeQuery(read)) {
            ResultSet resultSet = connectedResult.resultSet;

            while (resultSet.next()) {
                int id = resultSet.getInt("id");

                if (isLoaded(id)) {
                    games.add(getLoaded(id));
                } else {
                    Game game = new Game();
                    game.setID(id);
                    // TODO: Initialize new game object.
                    //  - Use DatabaseAccessUtil.sqlTimestampToInstant() to convert SQL Timestamp to Java Instant.
                    //  - Use dataAccess.getPlayerDAO().readByID() to resolve a Player.
                    //  - Use dataAccess.getGameDescriptionDAO().readByID() to resolve a GameDescription.
                    // TODO: Implement readPlayers(game) TODOs to read connection table for players association.
                    //  - Hint: See TournamentDAO.readParticipants()
                    game.setPlayers(readPlayers(game));
                    games.add(game);

                    // update index
                    loaded(game);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return games;
    }

    public List<Player> readPlayers(Game game) {
        List<Player> players = new ArrayList<>();

        String read = ""; // TODO: SQL query to select rows representing players references.
        //                     - Hint: See TournamentDAO.readParticipants()

        // Process the rows:
        try (ConnectedResult connectedResult = DatabaseAccess.getInstance().executeQuery(read)) {
            ResultSet resultSet = connectedResult.resultSet;

            while (resultSet.next()) {
                int playerID = resultSet.getInt("player_participates_id");
                players.add(dataAccess.getPlayerDAO().readByID(playerID));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return players;
    }

    public void updateTree(List<Game> games, Tournament container) {
        for (Game game : games) {
            update(game, container);
            dataAccess.getChatDAO().updateTree(game.getChatOfSpectators(), game);
        }
    }

    public void update(Game game, Tournament container) {
        String update = "UPDATE Game SET " +
                "container_id = " + container.getID() + ", " +
                "start_time = \"" + DatabaseAccessUtil.instantToSQLTimestamp(game.getStartTime()) + "\", " +
                "end_time = \"" + DatabaseAccessUtil.instantToSQLTimestamp(game.getEndTime()) + "\", " +
                "is_of_type_id = " + game.getIsOfType().getID() + ", " +
                "winner_id = " + (game.getWinner() != null ? game.getWinner().getID() : "NULL") + " " +
                "WHERE id = " + game.getID();
        DatabaseAccess.getInstance().executeUpdate(update);

        updatePlayers(game);
    }

    public void updatePlayers(Game game) {
        deletePlayers(game);
        createPlayers(game);
    }

    public void deleteTree(List<Game> games) {
        for (Game game : games) {
            delete(game);
            dataAccess.getChatDAO().deleteTree(game.getChatOfSpectators());
        }
    }

    public void delete(Game game) {
        String delete = "DELETE FROM Game " +
                "WHERE id = " + game.getID();
        DatabaseAccess.getInstance().executeUpdate(delete);

        deletePlayers(game);

        // update index
        unloaded(game);
    }

    public void deletePlayers(Game game) {
        String remove = "DELETE FROM Game_players_Player WHERE " +
                "game_participants_id = " + game.getID();
        DatabaseAccess.getInstance().executeUpdate(remove);
    }
}

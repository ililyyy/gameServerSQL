package ch.seg.inf.unibe.gameserver.db.persistence.model;

import ch.seg.inf.unibe.gameserver.db.logic.model.GameServer;
import ch.seg.inf.unibe.gameserver.db.logic.model.Player;
import ch.seg.inf.unibe.gameserver.db.persistence.DataAccessLayer;
import ch.seg.inf.unibe.gameserver.db.persistence.DatabaseAccess;
import ch.seg.inf.unibe.gameserver.db.persistence.DatabaseAccess.ConnectedResult;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
public class PlayerDAO extends IdentifiableElementDAO<Player> {

    /**
     * Provides access to all DAOs by singleton data access layer.
     */
    private DataAccessLayer dataAccess = DataAccessLayer.getInstance();

    public void createTable(boolean recursive) {
        // Table representing the class Player
        String createTable_Player = """
                CREATE TABLE IF NOT EXISTS Player (
                    id INTEGER PRIMARY KEY,
                    container_id INTEGER,
                    name STRING,
                    FOREIGN KEY (container_id) REFERENCES GameServer(id)
                );
                """;
        DatabaseAccess.getInstance().executeUpdate(createTable_Player);
    }

    public void createTree(List<Player> players, GameServer gameServerContainer) {
        for (Player player : players) {
            create(player, gameServerContainer);
        }
    }

    public void create(Player player, GameServer container) {
        String row = ""; // TODO: SQL Query
        DatabaseAccess.getInstance().insertRow(row);

        // update index
        loaded(player);
    }

    public List<Player> readTree(GameServer gameServerContainer) {
        List<Player> players = readByContainer(gameServerContainer);
        return players;
    }

    public Player readByID(int id) {
        if (id != 0) { // NULL is interpreted 0 by SQL getInt().
            if (isLoaded(id)) {
                return getLoaded(id);
            } else {
                return checkedResults(read("id = " + id), Player.class, id);
            }
        } else {
            return null;
        }
    }

    public List<Player> readByContainer(GameServer container) {
        return read("container_id = " + container.getID());
    }

    public List<Player> read(String sqlWhereClause) {
        List<Player> players = new ArrayList<>();

        String read = ""; // TODO: SQL Query to select rows based on given WHERE clause.

        // Process the rows:
        try (ConnectedResult connectedResult = DatabaseAccess.getInstance().executeQuery(read)) {
            ResultSet resultSet = connectedResult.resultSet;

            while (resultSet.next()) {
                int id = resultSet.getInt("id");

                if (isLoaded(id)) {
                    players.add(getLoaded(id));
                } else {
                    Player player = new Player();
                    player.setID(id);
                    // TODO: Initialize new player object.
                    players.add(player);

                    // update index
                    loaded(player);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return players;
    }

    public void updateTree(List<Player> players, GameServer container) {
        for (Player player : players) {
            update(player, container);
        }
    }

    public void update(Player player, GameServer container) {
        String update = "UPDATE Player SET " +
                "container_id = " + container.getID() + ", " +
                "name = \"" + player.getName() + "\" " +
                "WHERE id = " + player.getID();
        DatabaseAccess.getInstance().executeUpdate(update);
    }

    public void deleteTree(List<Player> players) {
        for (Player player : players) {
            delete(player);
        }
    }

    public void delete(Player player) {
        String delete = "DELETE FROM Player " +
                "WHERE id = " + player.getID();
        DatabaseAccess.getInstance().executeUpdate(delete);

        // update index
        unloaded(player);
    }
}

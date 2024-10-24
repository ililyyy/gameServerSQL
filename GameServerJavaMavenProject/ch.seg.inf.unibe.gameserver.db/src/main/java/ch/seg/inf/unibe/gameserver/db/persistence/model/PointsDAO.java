package ch.seg.inf.unibe.gameserver.db.persistence.model;

import ch.seg.inf.unibe.gameserver.db.logic.model.Highscore;
import ch.seg.inf.unibe.gameserver.db.logic.model.Points;
import ch.seg.inf.unibe.gameserver.db.persistence.DataAccessLayer;
import ch.seg.inf.unibe.gameserver.db.persistence.DatabaseAccess;
import ch.seg.inf.unibe.gameserver.db.persistence.DatabaseAccess.ConnectedResult;
import ch.seg.inf.unibe.gameserver.db.persistence.DatabaseAccessUtil;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import ch.seg.inf.unibe.gameserver.db.logic.model.Player;
public class PointsDAO extends IdentifiableElementDAO<Points> {

    /**
     * Provides access to all DAOs by singleton data access layer.
     */
    private DataAccessLayer dataAccess = DataAccessLayer.getInstance();

    public void createTable(boolean recursive) {
        String createTable_Points = """
                CREATE TABLE IF NOT EXISTS Points (
                    id INTEGER PRIMARY KEY,
                    container_id INTEGER,
                    current_points INTEGER,
                    player_id INTEGER,
                    FOREIGN KEY (container_id) REFERENCES Highscore(id),
                    FOREIGN KEY (player_id) REFERENCES Player(id)
                );
                """;
        DatabaseAccess.getInstance().executeUpdate(createTable_Points);
    }

    public void createTree(List<Points> points, Highscore highscoreContainer) {
        for (Points point : points) {
            create(point, highscoreContainer);
        }
    }

    public void create(Points points, Highscore container) {
        // TODO: SQL Query
        String create = String.format("""
                INSERT INTO Points
                (id, container_id, current_points, player_id)
                VALUES (%d, %d, %d, %d);""",
                points.getID(),
                container.getID(),
                points.getCurrentPoints(),
                (points.getPlayer()).getID());
        DatabaseAccess.getInstance().executeUpdate(create);

        // update index
        loaded(points);
    }

    public Points readByID(int id) {
        if (id != 0) { // NULL is interpreted 0 by SQL getInt().
            if (isLoaded(id)) {
                return getLoaded(id);
            } else {
                return checkedResults(read("id = " + id), Points.class, id);
            }
        } else {
            return null;
        }
    }

    public List<Points> readByContainer(Highscore container) {
        return read("container_id = " + container.getID());
    }

    public List<Points> readTree(Highscore highscoreContainer) {
        List<Points> points = readByContainer(highscoreContainer);
        return points;
    }

    protected List<Points> read(String sqlWhereClause) {
        List<Points> points = new ArrayList<>();

        String read = "SELECT * FROM Points WHERE " + sqlWhereClause;
        // TODO: SQL Query to select rows based on given WHERE clause.

        // Process the rows:
        try (ConnectedResult connectedResult = DatabaseAccess.getInstance().executeQuery(read)) {
            ResultSet resultSet = connectedResult.resultSet;

            while (resultSet.next()) {
                int id = resultSet.getInt("id");

                if (isLoaded(id)) {
                    points.add(getLoaded(id));
                } else {
                    Points point = new Points();
                    point.setID(id);
                    // TODO: Initialize new point object.
                    //  - Use dataAccess.getPlayerDAO().readByID() to resolve a Player.
                    point.setID(resultSet.getInt("id"));
                    point.setCurrentPoints(resultSet.getInt("current_points"));
                    Player player_temp = new Player();
                    player_temp = dataAccess.getPlayerDAO().readByID(resultSet.getInt("player_id"));
                    point.setPlayer(player_temp);
                    points.add(point);
                    // update index
                    loaded(point);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return points;
    }

    public void updateTree(List<Points> points, Highscore container) {
        for (Points point : points) {
            update(point, container);
        }
    }

    public void update(Points points, Highscore container) {
        String update = "UPDATE Points SET " +
                "container_id = " + container.getID() + ", " +
                "current_points = " + points.getCurrentPoints() + ", " +
                "player_id = " + points.getPlayer().getID() + " " +
                "WHERE id = " + points.getID();
        DatabaseAccess.getInstance().executeUpdate(update);
    }

    public void deleteTree(List<Points> points) {
        for (Points point : points) {
            delete(point);
        }
    }

    public void delete(Points points) {
        String delete = "DELETE FROM Points " +
                "WHERE id = " + points.getID();
        DatabaseAccess.getInstance().executeUpdate(delete);

        // update index
        unloaded(points);
    }
}

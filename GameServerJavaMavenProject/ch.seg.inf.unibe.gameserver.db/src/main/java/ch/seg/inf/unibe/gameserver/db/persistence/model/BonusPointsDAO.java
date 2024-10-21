package ch.seg.inf.unibe.gameserver.db.persistence.model;

import ch.seg.inf.unibe.gameserver.db.logic.model.BonusPoints;
import ch.seg.inf.unibe.gameserver.db.logic.model.Tournament;
import ch.seg.inf.unibe.gameserver.db.persistence.DataAccessLayer;
import ch.seg.inf.unibe.gameserver.db.persistence.DatabaseAccess;
import ch.seg.inf.unibe.gameserver.db.persistence.DatabaseAccess.ConnectedResult;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BonusPointsDAO extends IdentifiableElementDAO<BonusPoints> {

    /**
     * Provides access to all DAOs by singleton data access layer.
     */
    private DataAccessLayer dataAccess = DataAccessLayer.getInstance();

    public void createTable(boolean recursive) {
        // Table representing the class BonusPoints
        String createTable_BonusPoints = """
                CREATE TABLE IF NOT EXISTS BonusPoints (
                    id INTEGER PRIMARY KEY,
                    container_id INTEGER,
                    name STRING,
                    rule STRING,
                    points INTEGER,
                    FOREIGN KEY (container_id) REFERENCES Tournament(id)
                );
                """;
        DatabaseAccess.getInstance().executeUpdate(createTable_BonusPoints);

        // Tables for contained objects:
        if (recursive) {
            dataAccess.getBonusStockDAO().createTable(recursive);
        }
    }

    public void createTree(List<BonusPoints> bonusPoints, Tournament tournamentContainer) {
        for (BonusPoints bonusPoint : bonusPoints) {
            create(bonusPoint, tournamentContainer);
            dataAccess.getBonusStockDAO().createTree(bonusPoint.getType(), bonusPoint);
        }
    }

    public void create(BonusPoints bonusPoints, Tournament container) {
        String row = String.format("""
                        INSERT INTO BonusPoints
                        (id, container_id, name, rule, points)
                        VALUES (%d, %d, "%s", "%s", %d);""",
                        bonusPoints.getID(),
                        container.getID(),
                        bonusPoints.getName(),
                        bonusPoints.getRule(),
                        bonusPoints.getPoints());
        DatabaseAccess.getInstance().insertRow(row);

        // update index
        loaded(bonusPoints);
    }

    public List<BonusPoints> readTree(Tournament tournamentContainer) {
        List<BonusPoints> tournamentBonusPoints = readByContainer(tournamentContainer);

        for (BonusPoints bonusPoints : tournamentBonusPoints) {
            bonusPoints.setType(dataAccess.getBonusStockDAO().readTree(bonusPoints));
        }

        return tournamentBonusPoints;
    }

    public BonusPoints readByID(int id) {
        if (id != 0) { // NULL is interpreted 0 by SQL getInt().
            if (isLoaded(id)) {
                return getLoaded(id);
            } else {
                return checkedResults(read("id = " + id), BonusPoints.class, id);
            }
        } else {
            return null;
        }
    }

    public List<BonusPoints> readByContainer(Tournament container) {
        return read("container_id = " + container.getID());
    }

    protected List<BonusPoints> read(String sqlWhereClause) {
        List<BonusPoints> bonusPoints = new ArrayList<>();

        String read = "SELECT * FROM BonusPoints WHERE  " + sqlWhereClause;

        // Process the rows:
        try (ConnectedResult connectedResult = DatabaseAccess.getInstance().executeQuery(read)) {
            ResultSet resultSet = connectedResult.resultSet;

            while (resultSet.next()) {
                int id = resultSet.getInt("id");

                if (isLoaded(id)) {
                    bonusPoints.add(getLoaded(id));
                } else {
                    BonusPoints bonusPoint = new BonusPoints();
                    bonusPoint.setID(resultSet.getInt("id"));
                    bonusPoint.setName(resultSet.getString("name"));
                    bonusPoint.setRule(resultSet.getString("rule"));
                    bonusPoint.setPoints(resultSet.getInt("points"));
                    bonusPoints.add(bonusPoint);

                    // update index
                    loaded(bonusPoint);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return bonusPoints;
    }

    public void updateTree(List<BonusPoints> bonusPoints, Tournament container) {
        for (BonusPoints bonusPoint : bonusPoints) {
            update(bonusPoint, container);
            dataAccess.getBonusStockDAO().updateTree(bonusPoint.getType(), bonusPoint);
        }
    }

    public void update(BonusPoints bonusPoints, Tournament container) {
        String update = "UPDATE BonusPoints SET " +
                "container_id = " + container.getID() + ", " +
                "name = \"" + bonusPoints.getName() + "\", " +
                "rule = \"" + bonusPoints.getRule() + "\", " +
                "points = " + bonusPoints.getPoints() + " " +
                "WHERE id = " + bonusPoints.getID();
        DatabaseAccess.getInstance().executeUpdate(update);
    }

    public void deleteTree(List<BonusPoints> bonusPoints) {
        for (BonusPoints bonusPoint : bonusPoints) {
            delete(bonusPoint);
            dataAccess.getBonusStockDAO().deleteTree(bonusPoint.getType());
        }
    }

    public void delete(BonusPoints bonusPoints) {
        String delete = "DELETE FROM BonusPoints " +
                        "WHERE id = " + bonusPoints.getID();
        DatabaseAccess.getInstance().executeUpdate(delete);

        // update index
        unloaded(bonusPoints);
    }
}

package ch.seg.inf.unibe.gameserver.db.persistence.model;

import ch.seg.inf.unibe.gameserver.db.logic.model.BonusPoints;
import ch.seg.inf.unibe.gameserver.db.logic.model.BonusStock;
import ch.seg.inf.unibe.gameserver.db.persistence.DataAccessLayer;
import ch.seg.inf.unibe.gameserver.db.persistence.DatabaseAccess;
import ch.seg.inf.unibe.gameserver.db.persistence.DatabaseAccess.ConnectedResult;

import java.sql.ResultSet;
import java.sql.SQLException;

public class BonusStockDAO extends IdentifiableElementDAO<BonusStock> {

    /**
     * Provides access to all DAOs by singleton data access layer.
     */
    private DataAccessLayer dataAccess = DataAccessLayer.getInstance();

    public void createTable(boolean recursive) {

        // Table representing the class BonusStock
        String createTable_BonusStock = """
                CREATE TABLE IF NOT EXISTS BonusStock (
                    id INTEGER PRIMARY KEY,
                    container_id INTEGER,
                    remaining INTEGER,
                    FOREIGN KEY (container_id) REFERENCES BonusPoints(id)
                );
                """;
        DatabaseAccess.getInstance().executeUpdate(createTable_BonusStock);
    }

    public void createTree(BonusStock BonusStock, BonusPoints bonusPointsContainer) {
        create(BonusStock, bonusPointsContainer);
    }

    public void create(BonusStock bonusStock, BonusPoints container) {
        String row = String.format("""
                INSERT INTO BonusStock
                (id, container_id, remaining)
                VALUES (%d, %d, %d);""",
                bonusStock.getID(),
                container.getID(),
                bonusStock.getRemaining());
        DatabaseAccess.getInstance().insertRow(row);

        // update index
        loaded(bonusStock);
    }

    public BonusStock readTree(BonusPoints bonusPointsContainer) {
        BonusStock bonusStock = readByContainer(bonusPointsContainer);
        return bonusStock;
    }

    public BonusStock readByID(int id) {
        if (id != 0) { // NULL is interpreted 0 by SQL getInt().
            if (isLoaded(id)) {
                return getLoaded(id);
            } else {
                return checkedResult(read("id = " + id), BonusStock.class, id);
            }
        } else {
            return null;
        }
    }

    public BonusStock readByContainer(BonusPoints container) {
        return read("container_id = " + container.getID());
    }

    protected BonusStock read(String sqlWhereClause) {
        String read = "SELECT * FROM BonusStock WHERE " + sqlWhereClause;

        // Process the rows:
        try (ConnectedResult connectedResult = DatabaseAccess.getInstance().executeQuery(read)) {
            ResultSet resultSet = connectedResult.resultSet;

            if (resultSet.next()) {
                int id = resultSet.getInt("id");

                if (isLoaded(id)) {
                    return getLoaded(id);
                } else {
                    BonusStock bonusStock = new BonusStock();
                    bonusStock.setID(id);
                    bonusStock.setRemaining(resultSet.getInt("remaining"));

                    // update index
                    loaded(bonusStock);

                    return bonusStock;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return null;
    }

    public void updateTree(BonusStock BonusStock, BonusPoints container) {
        update(BonusStock, container);
    }

    public void update(BonusStock bonusStock, BonusPoints container) {
        String update = "UPDATE BonusStock SET " +
                "container_id = " + container.getID() + ", " +
                "remaining = " + bonusStock.getRemaining() + " " +
                "WHERE id = " + bonusStock.getID();
        DatabaseAccess.getInstance().executeUpdate(update);
    }

    public void deleteTree(BonusStock BonusStock) {
        delete(BonusStock);
    }

    public void delete(BonusStock bonusStock) {
        String delete = "DELETE FROM BonusStock " +
                "WHERE id = " + bonusStock.getID();
        DatabaseAccess.getInstance().executeUpdate(delete);

        // update index
        unloaded(bonusStock);
    }

}

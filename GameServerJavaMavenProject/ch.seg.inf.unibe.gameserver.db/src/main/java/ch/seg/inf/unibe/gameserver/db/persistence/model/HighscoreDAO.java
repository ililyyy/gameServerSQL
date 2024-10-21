package ch.seg.inf.unibe.gameserver.db.persistence.model;

import ch.seg.inf.unibe.gameserver.db.logic.model.Highscore;
import ch.seg.inf.unibe.gameserver.db.logic.model.Tournament;
import ch.seg.inf.unibe.gameserver.db.persistence.DataAccessLayer;
import ch.seg.inf.unibe.gameserver.db.persistence.DatabaseAccess;
import ch.seg.inf.unibe.gameserver.db.persistence.DatabaseAccess.ConnectedResult;

import java.sql.ResultSet;
import java.sql.SQLException;
public class HighscoreDAO extends IdentifiableElementDAO<Highscore> {

    /**
     * Provides access to all DAOs by singleton data access layer.
     */
    private DataAccessLayer dataAccess = DataAccessLayer.getInstance();

    public void createTable(boolean recursive) {
        String createTable_Highscore = """
                CREATE TABLE IF NOT EXISTS Highscore (
                    id INTEGER PRIMARY KEY,
                    container_id INTEGER,
                    closed BOOLEAN,
                    FOREIGN KEY (container_id) REFERENCES Tournament(id)
                );
                """;
        DatabaseAccess.getInstance().executeUpdate(createTable_Highscore);

        if (recursive) {
            dataAccess.getPointsDAO().createTable(recursive);
        }
    }

    public void createTree(Highscore highscore, Tournament tournamentContainer) {
        create(highscore, tournamentContainer);
        dataAccess.getPointsDAO().createTree(highscore.getPositions(), highscore);
    }

    public void create(Highscore highscore, Tournament container) {
        String create = String.format("""
                        INSERT INTO Highscore
                        (id, container_id, closed)
                        VALUES (%d, %d, %b);""",
                        highscore.getID(),
                        container.getID(),
                        highscore.isClosed());
        DatabaseAccess.getInstance().executeUpdate(create);

        // update index
        loaded(highscore);
    }

    public Highscore readTree(Tournament tournamentContainer) {
        Highscore highscore = readByContainer(tournamentContainer);
        highscore.setPositions(dataAccess.getPointsDAO().readTree(highscore));
        return highscore;
    }

    public Highscore readByID(int id) {
        if (id != 0) { // NULL is interpreted 0 by SQL getInt().
            if (isLoaded(id)) {
                return getLoaded(id);
            } else {
                return checkedResult(read("id = " + id), Highscore.class, id);
            }
        } else {
            return null;
        }
    }

    public Highscore readByContainer(Tournament container) {
        return read("container_id = " + container.getID());
    }

    protected Highscore read(String sqlWhereClause) {
        String read = "SELECT * FROM Highscore WHERE " + sqlWhereClause;

        // Process the rows:
        try (ConnectedResult connectedResult = DatabaseAccess.getInstance().executeQuery(read)) {
            ResultSet resultSet = connectedResult.resultSet;

            if (resultSet.next()) {
                int id = resultSet.getInt("id");

                if (isLoaded(id)) {
                    return getLoaded(id);
                } else {
                    Highscore highscore = new Highscore();
                    highscore.setID(id);
                    highscore.setClosed(resultSet.getBoolean("closed"));

                    // update index
                    loaded(highscore);

                    return highscore;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return null;
    }

    public void updateTree(Highscore highscore, Tournament container) {
        update(highscore, container);
        dataAccess.getPointsDAO().updateTree(highscore.getPositions(), highscore);
    }

    public void update(Highscore highscore, Tournament container) {
        String update = "UPDATE Highscore SET " +
                "container_id = " + container.getID() + ", " +
                "closed = " + (highscore.isClosed() ? 1 : 0) + " " +
                "WHERE id = " + highscore.getID();
        DatabaseAccess.getInstance().executeUpdate(update);
    }

    public void deleteTree(Highscore highscore) {
        delete(highscore);
        dataAccess.getPointsDAO().deleteTree(highscore.getPositions());
    }

    public void delete(Highscore highscore) {
        String delete = "DELETE FROM Highscore " +
                "WHERE id = " + highscore.getID();
        DatabaseAccess.getInstance().executeUpdate(delete);

        // update index
        unloaded(highscore);
    }
}

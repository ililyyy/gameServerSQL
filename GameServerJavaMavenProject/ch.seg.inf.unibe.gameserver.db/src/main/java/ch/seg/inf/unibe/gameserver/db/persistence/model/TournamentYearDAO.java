package ch.seg.inf.unibe.gameserver.db.persistence.model;

import ch.seg.inf.unibe.gameserver.db.logic.model.GameServer;
import ch.seg.inf.unibe.gameserver.db.logic.model.TournamentYear;
import ch.seg.inf.unibe.gameserver.db.persistence.DataAccessLayer;
import ch.seg.inf.unibe.gameserver.db.persistence.DatabaseAccess;
import ch.seg.inf.unibe.gameserver.db.persistence.DatabaseAccess.ConnectedResult;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TournamentYearDAO extends IdentifiableElementDAO<TournamentYear> {

    /**
     * Provides access to all DAOs by singleton data access layer.
     */
    private DataAccessLayer dataAccess = DataAccessLayer.getInstance();

    public void createTable(boolean recursive) {
        // Table representing the class TournamentYear
        String createTable_TournamentYear = """
                CREATE TABLE IF NOT EXISTS TournamentYear (
                    id INTEGER PRIMARY KEY,
                    container_id INTEGER,
                    year INTEGER,
                    FOREIGN KEY (container_id) REFERENCES GameServer(id)
                );
                """;
        DatabaseAccess.getInstance().executeUpdate(createTable_TournamentYear);

        // Tables for contained objects:
        if (recursive) {
            dataAccess.getTournamentDAO().createTable(recursive);
        }
    }

    public void createTree(List<TournamentYear> tournamentYears, GameServer gameServerContainer) {
        for (TournamentYear tournamentYear : tournamentYears) {
            create(tournamentYear, gameServerContainer);
            dataAccess.getTournamentDAO().createTree(tournamentYear.getTournaments(), tournamentYear);
        }
    }

    public void create(TournamentYear tournamentYear, GameServer container) {
        String create = String.format("""
                INSERT INTO TournamentYear (id, container_id, year)
                VALUES (%d, %d, %d);""",
                tournamentYear.getID(),
                container.getID(),
                tournamentYear.getYear());
        DatabaseAccess.getInstance().insertRow(create);

        // update index
        loaded(tournamentYear);
    }

    public List<TournamentYear> readTree(GameServer gameServerContainer) {
        List<TournamentYear> tournamentYears = readByContainer(gameServerContainer);

        for (TournamentYear tournamentYear : tournamentYears) {
            tournamentYear.setTournaments(dataAccess.getTournamentDAO().readTree(tournamentYear));
        }

        return tournamentYears;
    }

    public TournamentYear readByID(int id) {
        if (id != 0) { // NULL is interpreted 0 by SQL getInt().
            if (isLoaded(id)) {
                return getLoaded(id);
            } else {
                return checkedResults(read("id = " + id), TournamentYear.class, id);
            }
        } else {
            return null;
        }
    }

    public List<TournamentYear> readByContainer(GameServer container) {
        return read("container_id = " + container.getID());
    }

    protected List<TournamentYear> read(String sqlWhereClause) {
        List<TournamentYear> tournamentYears = new ArrayList<>();

        String read = "SELECT * FROM TournamentYear WHERE " + sqlWhereClause;

        // Process the rows:
        try (ConnectedResult connectedResult = DatabaseAccess.getInstance().executeQuery(read)) {
            ResultSet resultSet = connectedResult.resultSet;

            while (resultSet.next()) {
                int id = resultSet.getInt("id");

                if (isLoaded(id)) {
                    tournamentYears.add(getLoaded(id));
                } else {
                    TournamentYear tournamentYear = new TournamentYear();
                    tournamentYear.setID(id);
                    tournamentYear.setYear(resultSet.getInt("year"));
                    tournamentYears.add(tournamentYear);

                    // update index
                    loaded(tournamentYear);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return tournamentYears;
    }

    public void updateTree(List<TournamentYear> tournamentYears, GameServer container) {
        for (TournamentYear tournamentYear : tournamentYears) {
            update(tournamentYear, container);
            dataAccess.getTournamentDAO().updateTree(tournamentYear.getTournaments(), tournamentYear);
        }
    }

    public void update(TournamentYear tournamentYear, GameServer container) {
        String update = "UPDATE TournamentYear SET " +
                "container_id = " + container.getID() + ", " +
                "year = " + tournamentYear.getYear() + " " +
                "WHERE id = " + tournamentYear.getID();
        DatabaseAccess.getInstance().executeUpdate(update);
    }

    public void deleteTree(List<TournamentYear> tournamentYears) {
        for (TournamentYear tournamentYear : tournamentYears) {
            delete(tournamentYear);
            dataAccess.getTournamentDAO().deleteTree(tournamentYear.getTournaments());
        }
    }

    public void delete(TournamentYear tournamentYear) {
        String delete = "DELETE FROM TournamentYear " +
                "WHERE id = " + tournamentYear.getID();
        DatabaseAccess.getInstance().executeUpdate(delete);

        // update index
        unloaded(tournamentYear);
    }
}

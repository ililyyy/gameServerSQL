package ch.seg.inf.unibe.gameserver.db.persistence.model;

import ch.seg.inf.unibe.gameserver.db.logic.model.Player;
import ch.seg.inf.unibe.gameserver.db.logic.model.Tournament;
import ch.seg.inf.unibe.gameserver.db.logic.model.TournamentYear;
import ch.seg.inf.unibe.gameserver.db.persistence.DataAccessLayer;
import ch.seg.inf.unibe.gameserver.db.persistence.DatabaseAccess;
import ch.seg.inf.unibe.gameserver.db.persistence.DatabaseAccess.ConnectedResult;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TournamentDAO extends IdentifiableElementDAO<Tournament> {

    /**
     * Provides access to all DAOs by singleton data access layer.
     */
    private DataAccessLayer dataAccess = DataAccessLayer.getInstance();

    public void createTable(boolean recursive) {

        // Table representing the class Tournament
        String createTable_Tournament = """
                CREATE TABLE IF NOT EXISTS Tournament (
                    id INTEGER PRIMARY KEY,
                    container_id INTEGER,
                    start_month INTEGER,
                    end_month INTEGER,
                    FOREIGN KEY (container_id) REFERENCES TournamentYear(id)
                );
                """;
        DatabaseAccess.getInstance().executeUpdate(createTable_Tournament);

        // Table representing the association:
        // Tournament<-*-participates--participation--participants-*->Player
        String createTable_Tournament_participation_Player = """
                CREATE TABLE IF NOT EXISTS Tournament_participation_Player (
                    tournament_participants_id INTEGER,
                    player_participates_id INTEGER,
                    FOREIGN KEY (tournament_participants_id) REFERENCES Tournament(id),
                    FOREIGN KEY (player_participates_id) REFERENCES Player(id)
                );
                """;
        DatabaseAccess.getInstance().executeUpdate(createTable_Tournament_participation_Player);

        // Tables for contained objects:
        if (recursive) {
            dataAccess.getHighscoreDAO().createTable(recursive);
            dataAccess.getBonusPointsDAO().createTable(recursive);
            dataAccess.getGameDAO().createTable(recursive);

        }
    }

    public void createTree(List<Tournament> tournaments, TournamentYear tournamentYearContainer) {
        for (Tournament tournament : tournaments) {
            create(tournament, tournamentYearContainer);
            dataAccess.getBonusPointsDAO().createTree(tournament.getBoni(), tournament);
            dataAccess.getHighscoreDAO().createTree(tournament.getHighscore(), tournament);
            dataAccess.getGameDAO().createTree(tournament.getGames(), tournament);
        }
    }

    public void create(Tournament tournament, TournamentYear container) {
        String create = String.format("""
                        INSERT INTO Tournament
                        (id, container_id, start_month, end_month)
                        VALUES (%d, %d, %d, %d);""",
                        tournament.getID(),
                        container.getID(),
                        tournament.getStartMonth(),
                        tournament.getEndMonth());
        DatabaseAccess.getInstance().executeUpdate(create);

        createParticipants(tournament);

        // update index
        loaded(tournament);
    }

    public void createParticipants(Tournament tournament) {
        for (Player player : tournament.getParticipants()) {
            String add = "INSERT INTO Tournament_participation_Player (tournament_participants_id, player_participates_id) " +
                    "VALUES (" +
                    tournament.getID() + ", " +
                    player.getID() +
                    ");";
            DatabaseAccess.getInstance().executeUpdate(add);
        }
    }

    public List<Tournament> readTree(TournamentYear tournamentYearContainer) {
        List<Tournament> tournaments = readByContainer(tournamentYearContainer);

        for (Tournament tournament : tournaments) {
            tournament.setBoni(dataAccess.getBonusPointsDAO().readTree(tournament));
            tournament.setHighscore(dataAccess.getHighscoreDAO().readTree(tournament));
            tournament.setGames(dataAccess.getGameDAO().readTree(tournament));
        }

        return tournaments;
    }

    public Tournament readByID(int id) {
        if (id != 0) { // NULL is interpreted 0 by SQL getInt().
            if (isLoaded(id)) {
                return getLoaded(id);
            } else {
                return checkedResults(read("id = " + id), Tournament.class, id);
            }
        } else {
            return null;
        }
    }

    public List<Tournament> readByContainer(TournamentYear container) {
        return read("container_id = " + container.getID());
    }

    protected List<Tournament> read(String sqlWhereClause) {
        List<Tournament> tournaments = new ArrayList<>();

        String read = "SELECT * FROM Tournament WHERE " + sqlWhereClause;

        // Process the rows:
        try (ConnectedResult connectedResult = DatabaseAccess.getInstance().executeQuery(read)) {
            ResultSet resultSet = connectedResult.resultSet;

            while (resultSet.next()) {
                int id = resultSet.getInt("id");

                if (isLoaded(id)) {
                    tournaments.add(getLoaded(id));
                } else {
                    Tournament tournament = new Tournament();
                    tournament.setID(id);
                    tournament.setStartMonth(resultSet.getInt("start_month"));
                    tournament.setEndMonth(resultSet.getInt("end_month"));
                    tournament.setParticipants(readParticipants(tournament));
                    tournaments.add(tournament);

                    // add opposite references participants/
                    for (Player participant : tournament.getParticipants()) {
                        if (participant.getParticipates() == null) {
                            participant.setParticipates(new ArrayList<>());
                        }
                        participant.getParticipates().add(tournament);
                    }

                    // update index
                    loaded(tournament);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return tournaments;
    }

    public List<Player> readParticipants(Tournament tournament) {
        List<Player> players = new ArrayList<>();

        String read = "SELECT * FROM Tournament_participation_Player WHERE tournament_participants_id = " + tournament.getID();

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

    public void updateTree(List<Tournament> tournaments, TournamentYear container) {
        for (Tournament tournament : tournaments) {
            update(tournament, container);
            dataAccess.getBonusPointsDAO().updateTree(tournament.getBoni(), tournament);
            dataAccess.getHighscoreDAO().updateTree(tournament.getHighscore(), tournament);
            dataAccess.getGameDAO().updateTree(tournament.getGames(), tournament);
        }
    }

    public void update(Tournament tournament, TournamentYear container) {
        String update = "UPDATE Tournament SET " +
                "container_id = " + container.getID() + ", " +
                "start_month = " + tournament.getStartMonth() + ", " +
                "end_month = " + tournament.getEndMonth() + " " +
                "WHERE id = " + tournament.getID();
        DatabaseAccess.getInstance().executeUpdate(update);

        updateParticipants(tournament);
    }

    public void updateParticipants(Tournament tournament) {
        // NOTE: Alternatively (for efficiency) do this incrementally:
        deleteParticipants(tournament);
        createParticipants(tournament);
    }

    public void deleteTree(List<Tournament> tournaments) {
        for (Tournament tournament : tournaments) {
            delete(tournament);
            dataAccess.getBonusPointsDAO().deleteTree(tournament.getBoni());
            dataAccess.getHighscoreDAO().deleteTree(tournament.getHighscore());
            dataAccess.getGameDAO().deleteTree(tournament.getGames());
        }
    }

    public void delete(Tournament tournament) {
        String delete = "DELETE FROM Tournament " +
                "WHERE id = " + tournament.getID();
        DatabaseAccess.getInstance().executeUpdate(delete);

        deleteParticipants(tournament);

        // update index
        unloaded(tournament);
    }

    public void deleteParticipants(Tournament tournament) {
        String remove = "DELETE FROM Tournament_participation_Player WHERE " +
                "tournament_participants_id = " + tournament.getID();
        DatabaseAccess.getInstance().executeUpdate(remove);
    }
}

package ch.seg.inf.unibe.gameserver.db.persistence.model;

import ch.seg.inf.unibe.gameserver.db.logic.model.GameCategory;
import ch.seg.inf.unibe.gameserver.db.logic.model.GameServer;
import ch.seg.inf.unibe.gameserver.db.persistence.DataAccessLayer;
import ch.seg.inf.unibe.gameserver.db.persistence.DatabaseAccess;
import ch.seg.inf.unibe.gameserver.db.persistence.DatabaseAccess.ConnectedResult;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

public class GameServerDAO  extends IdentifiableElementDAO<GameServer> {

    /**
     * Provides access to all DAOs by singleton data access layer.
     */
    private DataAccessLayer dataAccess = DataAccessLayer.getInstance();

    public void createTable(boolean recursive) {

        // Table representing the class GameServer
        String createTable_GameServer = """
                CREATE TABLE IF NOT EXISTS GameServer (
                    id INTEGER PRIMARY KEY,
                    name STRING
                );
                """;
        DatabaseAccess.getInstance().executeUpdate(createTable_GameServer);

        // Tables for contained objects:
        if (recursive) {
            dataAccess.getGameCategoryDAO().createTable(recursive);
            dataAccess.getPlayerDAO().createTable(recursive);
            dataAccess.getTournamentYearDAO().createTable(recursive);
        }
    }

    public void createTree(GameServer gameServer) {
        create(gameServer);
        dataAccess.getGameCategoryDAO().createTree(Collections.singletonList(gameServer.getGames()), gameServer);
        dataAccess.getTournamentYearDAO().createTree(gameServer.getTournamentYears(), gameServer);
        dataAccess.getPlayerDAO().createTree(gameServer.getPlayers(), gameServer);
    }

    public void create(GameServer gameServer) {
        String row = String.format("""
                INSERT INTO GameServer (id, name)
                VALUES (%d, "%s")""",
                gameServer.getID(),
                gameServer.getName());
        DatabaseAccess.getInstance().insertRow(row);

        // update index
        loaded(gameServer);
    }

    public GameServer readTree() {

        // Load object tree:
        GameServer gameServer = read();

        // If game server already exists, load object tree:
        if (gameServer != null) {
            gameServer.setPlayers(dataAccess.getPlayerDAO().readTree(gameServer));
            List<GameCategory> rootGameCategory = dataAccess.getGameCategoryDAO().readTree(gameServer);
            gameServer.setGames(rootGameCategory.isEmpty() ? null : rootGameCategory.get(0));
            gameServer.setTournamentYears(dataAccess.getTournamentYearDAO().readTree(gameServer));
        }

        return gameServer;
    }

    public GameServer read() {

        String read = "SELECT * FROM GameServer";

        try (ConnectedResult connectedResult = DatabaseAccess.getInstance().executeQuery(read)) {
            ResultSet resultSet = connectedResult.resultSet;

            if (resultSet.next()) {
                int id = resultSet.getInt("id");

                if (isLoaded(id)) {
                    return getLoaded(id);
                } else {
                    GameServer gameServer = new GameServer();
                    gameServer.setID(id);
                    gameServer.setName(resultSet.getString("name"));
                    return gameServer;
                }
            } else {
                System.out.println("GameServer does not exist yet.");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return null;
    }

    public void updateTree(GameServer gameServer) {
        update(gameServer);
        dataAccess.getGameCategoryDAO().updateTree(Collections.singletonList(gameServer.getGames()), gameServer);
        dataAccess.getTournamentYearDAO().updateTree(gameServer.getTournamentYears(), gameServer);
        dataAccess.getPlayerDAO().updateTree(gameServer.getPlayers(), gameServer);
    }

    public void update(GameServer gameServer) {
        String update = "UPDATE GameServer SET " +
                "name = \"" + gameServer.getName() + "\" " +
                "WHERE id = " + gameServer.getID() + ";";
        DatabaseAccess.getInstance().executeUpdate(update);
    }

    public void deleteTree(GameServer gameServer) {
        delete(gameServer);
        dataAccess.getGameCategoryDAO().deleteTree(Collections.singletonList(gameServer.getGames()));
        dataAccess.getTournamentYearDAO().deleteTree(gameServer.getTournamentYears());
        dataAccess.getPlayerDAO().deleteTree(gameServer.getPlayers());
    }

    public void delete(GameServer gameServer) {
        String delete = "DELETE FROM GameServer " +
                "WHERE id = " + gameServer.getID();
        DatabaseAccess.getInstance().executeUpdate(delete);

        // update index
        unloaded(gameServer);
    }
}

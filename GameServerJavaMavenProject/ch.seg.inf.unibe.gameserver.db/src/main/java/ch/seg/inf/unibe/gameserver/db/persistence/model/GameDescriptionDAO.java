package ch.seg.inf.unibe.gameserver.db.persistence.model;

import ch.seg.inf.unibe.gameserver.db.logic.model.GameCategory;
import ch.seg.inf.unibe.gameserver.db.logic.model.GameDescription;
import ch.seg.inf.unibe.gameserver.db.persistence.DataAccessLayer;
import ch.seg.inf.unibe.gameserver.db.persistence.DatabaseAccess;
import ch.seg.inf.unibe.gameserver.db.persistence.DatabaseAccess.ConnectedResult;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GameDescriptionDAO  extends IdentifiableElementDAO<GameDescription> {

    /**
     * Provides access to all DAOs by singleton data access layer.
     */
    private DataAccessLayer dataAccess = DataAccessLayer.getInstance();

    public void createTable(boolean recursive) {
        String createTable_GameDescription = """
                CREATE TABLE IF NOT EXISTS GameDescription (
                    id INTEGER PRIMARY KEY,
                    container_id INTEGER,
                    name STRING,
                    description STRING,
                    rules STRING,
                    FOREIGN KEY (container_id) REFERENCES GameCategory(id)
                );
                """;

        DatabaseAccess.getInstance().executeUpdate(createTable_GameDescription);
    }

    public void createTree(List<GameDescription> gameDescriptions, GameCategory gameCategoryContainer) {
        for (GameDescription gameDescription : gameDescriptions) {
            create(gameDescription, gameCategoryContainer);
        }
    }

    public void create(GameDescription gameDescription, GameCategory container) {
        // TODO: SQL Query
        String row = String.format("""
                INSERT INTO GameDescription
                (id, container_id, name, description, rules)
                VALUES (%d, %d, "%s", "%s", "%s")
                """,
                gameDescription.getID(),
                container.getID(),
                gameDescription.getName(),
                gameDescription.getDescription(),
                gameDescription.getRules());
        DatabaseAccess.getInstance().insertRow(row);

        // update index
        loaded(gameDescription);
    }

    public List<GameDescription> readTree(GameCategory gameCategoryContainer) {
        List<GameDescription> gameDescriptions = readByContainer(gameCategoryContainer);
        return gameDescriptions;
    }

    public GameDescription readByID(int id) {
        if (id != 0) { // NULL is interpreted 0 by SQL getInt().
            if (isLoaded(id)) {
                return getLoaded(id);
            } else {
                return checkedResults(read("id = " + id), GameDescription.class, id);
            }
        } else {
            return null;
        }
    }

    public List<GameDescription> readByContainer(GameCategory container) {
        return read("container_id = " + container.getID());
    }

    public List<GameDescription> read(String sqlWhereClause) {
        List<GameDescription> gameDescriptions = new ArrayList<>();

        String read = ""; // TODO: SQL Query to select rows based on given WHERE clause.

        // Process the rows:
        try (ConnectedResult connectedResult = DatabaseAccess.getInstance().executeQuery(read)) {
            ResultSet resultSet = connectedResult.resultSet;

            while (resultSet.next()) {
                int id = resultSet.getInt("id");

                if (isLoaded(id)) {
                    gameDescriptions.add(getLoaded(id));
                } else {
                    GameDescription gameDescription = new GameDescription();
                    gameDescription.setID(id);
                    // TODO: Initialize new gameDescription object.
                    gameDescriptions.add(gameDescription);

                    // update index
                    loaded(gameDescription);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return gameDescriptions;
    }

    public void updateTree(List<GameDescription> gameDescriptions, GameCategory container) {
        for (GameDescription gameDescription : gameDescriptions) {
            update(gameDescription, container);
        }
    }

    public void update(GameDescription gameDescription, GameCategory container) {
        String update = ""; // TODO: SQL Query
        DatabaseAccess.getInstance().executeUpdate(update);
    }

    public void deleteTree(List<GameDescription> gameDescriptions) {
        for (GameDescription gameDescription : gameDescriptions) {
            delete(gameDescription);
        }
    }

    public void delete(GameDescription gameDescription) {
        String delete = ""; // TODO: SQL Query
        DatabaseAccess.getInstance().executeUpdate(delete);

        // update index
        unloaded(gameDescription);
    }
}

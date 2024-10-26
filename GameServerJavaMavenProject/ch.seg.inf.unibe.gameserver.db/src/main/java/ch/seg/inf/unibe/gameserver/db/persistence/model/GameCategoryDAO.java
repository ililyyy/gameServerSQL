package ch.seg.inf.unibe.gameserver.db.persistence.model;

import ch.seg.inf.unibe.gameserver.db.logic.model.*;
import ch.seg.inf.unibe.gameserver.db.persistence.DataAccessLayer;
import ch.seg.inf.unibe.gameserver.db.persistence.DatabaseAccess;
import ch.seg.inf.unibe.gameserver.db.persistence.DatabaseAccess.ConnectedResult;
import ch.seg.inf.unibe.gameserver.db.persistence.DatabaseAccessUtil;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GameCategoryDAO extends IdentifiableElementDAO<GameCategory> {

    /**
     * Provides access to all DAOs by singleton data access layer.
     */
    private DataAccessLayer dataAccess = DataAccessLayer.getInstance();

    public void createTable(boolean recursive) {
        String createTable_GameCategory = """
                CREATE TABLE IF NOT EXISTS GameCategory (
                    id INTEGER PRIMARY KEY,
                    container_id INTEGER,
                    name STRING,
                    type STRING,
                    FOREIGN KEY (container_id) REFERENCES GameCategory(id)
                );
                """;
        DatabaseAccess.getInstance().executeUpdate(createTable_GameCategory);

        // Tables for contained objects:
        if (recursive) {
            dataAccess.getGameDescriptionDAO().createTable(recursive);
        }
    }

    public void createTree(List<GameCategory> gameCategories, IdentifiableElement container) {
        for (GameCategory gameCategory : gameCategories) {
            create(gameCategory, container);

            // Add children:
            for (GameLibrary contained : gameCategory.getContains()) {
                if (contained instanceof GameDescription containedGameDescription) {
                    dataAccess.getGameDescriptionDAO().createTree(Collections.singletonList(containedGameDescription), gameCategory);
                } else if (contained instanceof GameCategory containedGameCategory) {
                    this.createTree(Collections.singletonList(containedGameCategory), gameCategory);
                }
            }
        }
    }

    public void create(GameCategory gameCategory, IdentifiableElement container) {
        // TODO: SQL Query
        String row = String.format("""
                        INSERT INTO GameCategory
                        (id, container_id, name, type)
                        VALUES (%d, %d, "%s", "%s")
                        """,
                gameCategory.getID(),
                container.getID(),
                gameCategory.getName(),
                gameCategory.getType());
        DatabaseAccess.getInstance().insertRow(row);

        // update index
        loaded(gameCategory);
    }

    public List<GameCategory> readTree(IdentifiableElement container) {
        List<GameCategory> gameCategories = readByContainer(container);

        for (GameCategory gameCategory : gameCategories) {
            List<GameLibrary> contains = new ArrayList<>();
            contains.addAll(dataAccess.getGameDescriptionDAO().readTree(gameCategory));
            contains.addAll(this.readTree(gameCategory));
            gameCategory.setContains(contains);
        }

        return gameCategories;
    }

    public GameCategory readByID(int id) {
        if (id != 0) { // NULL is interpreted 0 by SQL getInt().
            if (isLoaded(id)) {
                return getLoaded(id);
            } else {
                return checkedResults(read("id = " + id), GameCategory.class, id);
            }
        } else {
            return null;
        }
    }

    public List<GameCategory> readByContainer(IdentifiableElement container) {
        return read("container_id = " + container.getID());
    }

    protected List<GameCategory> read(String sqlWhereClause) {
        List<GameCategory> gameCategories = new ArrayList<>();

        String read = "SELECT * FROM GameCategory WHERE " + sqlWhereClause;
        // TODO: SQL Query to select rows based on given WHERE clause.

        // Process the rows:
        try (ConnectedResult connectedResult = DatabaseAccess.getInstance().executeQuery(read)) {
            ResultSet resultSet = connectedResult.resultSet;

            while (resultSet.next()) {
                int id = resultSet.getInt("id");

                if (isLoaded(id)) {
                    gameCategories.add(getLoaded(id));
                } else {
                    GameCategory gameCategory = new GameCategory();
                    gameCategory.setID(id);
                    // TODO: Initialize new gameCategory object.
                    gameCategory.setName(resultSet.getString("name"));
                    gameCategory.setType(resultSet.getString("type"));

                    gameCategories.add(gameCategory);

                    // update index
                    loaded(gameCategory);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return gameCategories;
    }

    public void updateTree(List<GameCategory> gameCategories, IdentifiableElement container) {
        for (GameCategory gameCategory : gameCategories) {
            update(gameCategory, container);

            // Add children:
            for (GameLibrary contained : gameCategory.getContains()) {
                if (contained instanceof GameDescription containedGameDescription) {
                    dataAccess.getGameDescriptionDAO().updateTree(Collections.singletonList(containedGameDescription), gameCategory);
                } else if (contained instanceof GameCategory containedGameCategory) {
                    this.updateTree(Collections.singletonList(containedGameCategory), gameCategory);
                }
            }
        }
    }

    public void update(GameCategory gameCategory, IdentifiableElement container) {
        String update = "UPDATE GameCategory SET " +
                "id = " + gameCategory.getID() + ", " +
                "container_id = " + container.getID() + ", " +
                "name = \"" + gameCategory.getName() + "\", " +
                "type = \"" + gameCategory.getType()+ "\"" +
                "WHERE id = " + gameCategory.getID(); // TODO: SQL Query
        DatabaseAccess.getInstance().executeUpdate(update);
    }
    public void deleteTree(List<GameCategory> gameCategories) {
        for (GameCategory gameCategory : gameCategories) {
            delete(gameCategory);

            // Add children:
            for (GameLibrary contained : gameCategory.getContains()) {
                if (contained instanceof GameDescription containedGameDescription) {
                    dataAccess.getGameDescriptionDAO().deleteTree(Collections.singletonList(containedGameDescription));
                } else if (contained instanceof GameCategory containedGameCategory) {
                    this.deleteTree(Collections.singletonList(containedGameCategory));
                }
            }
        }
    }

    public void delete(GameCategory gameCategory) {
        String delete = "DELETE FROM GameCategory " +
                "WHERE id = " + gameCategory.getID(); // TODO: SQL Query
        DatabaseAccess.getInstance().executeUpdate(delete);

        // update index
        unloaded(gameCategory);
    }
}

package ch.seg.inf.unibe.gameserver.db.logic;

import ch.seg.inf.unibe.gameserver.db.Main;
import ch.seg.inf.unibe.gameserver.db.logic.model.GameServer;
import ch.seg.inf.unibe.gameserver.db.persistence.DataAccessLayer;
import ch.seg.inf.unibe.gameserver.db.persistence.DatabaseAccess;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;

public class GameServerTest {

    private static final String createReadTestDatabaseFile = Main.databaseFile + ".create.read.test.db";

    private static final String updateTestDatabaseFile = Main.databaseFile + ".update.test.db";

    private static final String deleteTestDatabaseFile = Main.databaseFile + ".delete.test.db";

    private static String gameServerDump;

    @Test
    @Order(1)
    public void testDAOCreateOperations() {
        // Test DAO create operations:
        // Create GameServer sample project:
        GameServer gameServer = createSampleDatabase(createReadTestDatabaseFile);
        GameServerTest.gameServerDump = gameServer.toString(); // to be compared by read()

        // Tables should not be empty based on generated example!
        Assertions.assertTrue(isNotEmpty("BonusPoints"));
        Assertions.assertTrue(isNotEmpty("BonusStock"));
        Assertions.assertTrue(isNotEmpty("Chat"));
        Assertions.assertTrue(isNotEmpty("Game"));
        Assertions.assertTrue(isNotEmpty("GameDescription"));
        Assertions.assertTrue(isNotEmpty("GameServer"));
        Assertions.assertTrue(isNotEmpty("Highscore"));
        Assertions.assertTrue(isNotEmpty("Player"));
        Assertions.assertTrue(isNotEmpty("Points"));
        Assertions.assertTrue(isNotEmpty("Tournament"));
        Assertions.assertTrue(isNotEmpty("TournamentYear"));
    }

    @Test
    @Order(2)
    public void testDAOReadOperations() {

        // Reset/Setup database connection, i.e., forget loaded objects:
        DatabaseAccess.resetInstance();
        DataAccessLayer.resetInstance();
        Main.init(Main.databasePath, createReadTestDatabaseFile);

        // Test DAO read operations:
        GameServer loadedGameServer = DataAccessLayer.getInstance().readTree();

        // Compare stored data with loaded data based on its String representation:
        Assertions.assertEquals(GameServerTest.gameServerDump, loadedGameServer.toString());
    }

    @Test
    @Order(3)
    public void testDAOUpdateOperations() {

        // Create GameServer sample project:
        GameServer gameServer = createSampleDatabase(updateTestDatabaseFile);

        // Generate updated test data:
        gameServer.modifyExampleData();

        // Test DAO update operations:
        DataAccessLayer.getInstance().updateTree(gameServer);

        // Reset/Setup database connection, i.e., forget loaded objects:
        DatabaseAccess.resetInstance();
        DataAccessLayer.resetInstance();
        Main.init(Main.databasePath, updateTestDatabaseFile);

        // Load updated data:
        GameServer updatedGameServer = DataAccessLayer.getInstance().readTree();

        // Compare stored/updated data with loaded data based on its String representation:
        Assertions.assertEquals(gameServer.toString(), updatedGameServer.toString());
    }

    @Test
    @Order(4)
    public void testDAODeleteOperations() {

        // Create GameServer sample project:
        GameServer gameServer = createSampleDatabase(deleteTestDatabaseFile);

        // Tables should not be empty based on generated example!
        Assertions.assertTrue(isNotEmpty("BonusPoints"));
        Assertions.assertTrue(isNotEmpty("BonusStock"));
        Assertions.assertTrue(isNotEmpty("Chat"));
        Assertions.assertTrue(isNotEmpty("Game"));
        Assertions.assertTrue(isNotEmpty("GameDescription"));
        Assertions.assertTrue(isNotEmpty("GameServer"));
        Assertions.assertTrue(isNotEmpty("Highscore"));
        Assertions.assertTrue(isNotEmpty("Player"));
        Assertions.assertTrue(isNotEmpty("Points"));
        Assertions.assertTrue(isNotEmpty("Tournament"));
        Assertions.assertTrue(isNotEmpty("TournamentYear"));

        // Test DAO delete operations:
        DataAccessLayer.getInstance().deleteTree(gameServer);

        // Tables must be empty after full delete!
        Assertions.assertTrue(isEmpty("BonusPoints"));
        Assertions.assertTrue(isEmpty("BonusStock"));
        Assertions.assertTrue(isEmpty("Chat"));
        Assertions.assertTrue(isEmpty("Game"));
        Assertions.assertTrue(isEmpty("GameDescription"));
        Assertions.assertTrue(isEmpty("GameLibrary"));
        Assertions.assertTrue(isEmpty("GameServer"));
        Assertions.assertTrue(isEmpty("Highscore"));
        Assertions.assertTrue(isEmpty("Player"));
        Assertions.assertTrue(isEmpty("Points"));
        Assertions.assertTrue(isEmpty("Tournament"));
        Assertions.assertTrue(isEmpty("TournamentYear"));
    }

    private GameServer createSampleDatabase(String testDatabaseFile ) {

        // Shut down the database connection pool:
        DatabaseAccess.getInstance().close();

        // Reset database connection, i.e., forget loaded objects:
        DatabaseAccess.resetInstance();
        DataAccessLayer.resetInstance();

        // Remove old database:
        try {
            Files.deleteIfExists(Main.databasePath.resolve(testDatabaseFile));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Setup database connection, i.e., forget loaded objects:
        Main.init(Main.databasePath, testDatabaseFile);
        DatabaseAccess.getInstance();
        DataAccessLayer.getInstance();

        // Create/Save GameServer sample project:
        GameServerApplication application = new GameServerApplication();
        GameServer gameServer = application.createExampleData();
        DataAccessLayer.getInstance().createTree(gameServer);

        return gameServer;
    }

    @AfterAll
    public static void testDAOOperationsAfter() {
        // Shut down the database connection pool:
        DatabaseAccess.getInstance().close();
    }

    private boolean isEmpty(String table) {
        return !isNotEmpty(table);
    }

    private boolean isNotEmpty(String table) {
        return tableSize(table) > 0;
    }

    private int tableSize(String table) {
        try (DatabaseAccess.ConnectedResult result = DatabaseAccess.getInstance().executeQuery("SELECT COUNT(*) FROM " + table + ";")) {
            if (result.resultSet.next()) {
                return result.resultSet.getInt(1);
            }
            return -1;
        } catch (Throwable e) {
            return -1;
        }
    }
    
}
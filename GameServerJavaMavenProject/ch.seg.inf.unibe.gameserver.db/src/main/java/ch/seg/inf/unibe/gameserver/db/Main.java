package ch.seg.inf.unibe.gameserver.db;

import ch.seg.inf.unibe.gameserver.db.logic.GameServerApplication;
import ch.seg.inf.unibe.gameserver.db.persistence.DataAccessLayer;
import ch.seg.inf.unibe.gameserver.db.persistence.DatabaseAccess;
import ch.seg.inf.unibe.gameserver.db.persistence.DatabaseAccessUtil;
import ch.seg.inf.unibe.gameserver.db.presentation.PrintToFile;

import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {

    public static Path databasePath = Paths.get("db");

    public static String databaseFile = "gamestore.db";

    public static Path outputFile = databasePath.resolve(databaseFile + ".output.txt");

    /**
     * @param args The command line arguments.
     */
    public static void main(String[] args) {
        try {
            // Setup database connection:
            init(databasePath, databaseFile);

            // Start application:
            GameServerApplication application = new GameServerApplication();
            application.run();

            // Output game server data to file:
            PrintToFile printToFile = new PrintToFile();
            printToFile.print(application.getGameServer(), outputFile);
        } finally {
            // Shut down the database connection pool:
            DatabaseAccess.getInstance().close();
        }

        System.out.println("Exit Application!");
    }

    public static void init(Path databasePath, String databaseFile) {

        // Setup database connection:
        String databaseURL = DatabaseAccessUtil.createSQLiteDatabaseURL(databasePath, databaseFile);

        System.out.println("Database path: " + databasePath.toAbsolutePath());
        System.out.println("Database file: " + databaseFile);
        System.out.println("Database URL: " + databaseURL);

        DatabaseAccess.getInstance().setDatabaseURL(databaseURL);
        DatabaseAccess.getInstance().setLog(true);

        DataAccessLayer.getInstance().createTables();
    }

}
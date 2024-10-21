package ch.seg.inf.unibe.gameserver.db.persistence;

import ch.seg.inf.unibe.gameserver.db.logic.model.GameServer;
import ch.seg.inf.unibe.gameserver.db.persistence.DatabaseAccess.ConnectedResult;
import ch.seg.inf.unibe.gameserver.db.persistence.model.*;

import java.sql.ResultSet;
import java.sql.SQLException;

public class DataAccessLayer {

    /**
     * Singleton instance of the data access layer.
     */
    private static DataAccessLayer instance;

    /**
     * Global unique object IDs for SQL database.
     * NOTE: NULL is interpreted 0 by SQL getInt().
     * NOTE: Autoincrement index in SQL is counted per table.
     */
    private int lastID = 1;

    private GameServerDAO gameServerDAO;

    private BonusStockDAO bonusStockDAO;

    private GameDescriptionDAO gameDescriptionDAO;

    private ChatDAO chatDAO;

    private GameCategoryDAO gameCategoryDAO;

    private TournamentYearDAO tournamentYearDAO;

    private PlayerDAO playerDAO;

    private PointsDAO pointsDAO ;

    private BonusPointsDAO bonusPointsDAO;

    private HighscoreDAO highscoreDAO;

    private GameDAO gameDAO;

    private TournamentDAO tournamentDAO;

    public static DataAccessLayer getInstance() {
        if (DataAccessLayer.instance == null) {
            DataAccessLayer instance  = new DataAccessLayer();
            DataAccessLayer.instance = instance;

            instance.gameServerDAO = new GameServerDAO();
            instance.bonusStockDAO = new BonusStockDAO();
            instance.gameDescriptionDAO = new GameDescriptionDAO();
            instance.chatDAO = new ChatDAO();
            instance.gameCategoryDAO = new GameCategoryDAO();
            instance.tournamentYearDAO = new TournamentYearDAO();
            instance.playerDAO = new PlayerDAO();
            instance.pointsDAO = new PointsDAO();
            instance.bonusPointsDAO = new BonusPointsDAO();
            instance.highscoreDAO = new HighscoreDAO();
            instance.gameDAO = new GameDAO();
            instance.tournamentDAO = new TournamentDAO();
        }
        return DataAccessLayer.instance;
    }

    public static void resetInstance() {
        DataAccessLayer.instance = null;
    }

    public DataAccessLayer() {
        createDataAccessLayerTable();

        // Read stored last ID from database:
        try (ConnectedResult connectedResult = DatabaseAccess.getInstance().executeQuery("SELECT * FROM DataAccessLayer")) {
            ResultSet resultSet = connectedResult.resultSet;

            if (resultSet.next()) {
                this.lastID = resultSet.getInt("last_id");
            } else {
                DatabaseAccess.getInstance().insertRow("INSERT INTO DataAccessLayer (last_id) VALUES (" + this.lastID + ");");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void createTree(GameServer gameServer) {
        System.out.println("\n\nSave SQL Database:\n\n");
        gameServerDAO.createTree(gameServer);
    }

    public GameServer readTree() {
        return gameServerDAO.readTree();
    }

    public void updateTree(GameServer gameServer) {
        gameServerDAO.updateTree(gameServer);
    }

    public void deleteTree(GameServer gameServer) {
        gameServerDAO.deleteTree(gameServer);
    }

    private void createDataAccessLayerTable() {

        // Table representing the class DataAccessLayer
        String createTable_DataAccessLayer = """
                CREATE TABLE IF NOT EXISTS DataAccessLayer (
                    last_id INTEGER
                );
                """;
        DatabaseAccess.getInstance().executeUpdate(createTable_DataAccessLayer);
    }

    public void createTables() {
        // Tables representing the data model:
        System.out.println("\n\nCreate SQL Tables:\n\n");
        instance.gameServerDAO.createTable(true);
    }

    public GameServerDAO getGameServerDAO() {
        return gameServerDAO;
    }

    public int getNextID() {
        ++this.lastID;

        // Store last ID from database:
        boolean logging = DatabaseAccess.getInstance().getLog();
        DatabaseAccess.getInstance().setLog(false);
        DatabaseAccess.getInstance().executeUpdate("UPDATE DataAccessLayer SET last_id = " + this.lastID);
        DatabaseAccess.getInstance().setLog(logging);

        return  this.lastID;
    }

    public BonusStockDAO getBonusStockDAO() {
        return bonusStockDAO;
    }

    public GameDescriptionDAO getGameDescriptionDAO() {
        return gameDescriptionDAO;
    }

    public ChatDAO getChatDAO() {
        return chatDAO;
    }

    public GameCategoryDAO getGameCategoryDAO() {
        return gameCategoryDAO;
    }

    public TournamentYearDAO getTournamentYearDAO() {
        return tournamentYearDAO;
    }

    public PlayerDAO getPlayerDAO() {
        return playerDAO;
    }

    public PointsDAO getPointsDAO() {
        return pointsDAO;
    }

    public BonusPointsDAO getBonusPointsDAO() {
        return bonusPointsDAO;
    }

    public HighscoreDAO getHighscoreDAO() {
        return highscoreDAO;
    }

    public GameDAO getGameDAO() {
        return gameDAO;
    }

    public TournamentDAO getTournamentDAO() {
        return tournamentDAO;
    }
}

package ch.seg.inf.unibe.gameserver.db.logic;

import ch.seg.inf.unibe.gameserver.db.logic.model.GameServer;
import ch.seg.inf.unibe.gameserver.db.persistence.DataAccessLayer;

public class GameServerApplication {

    private GameServer gameServer;

    public GameServer getGameServer() {
        return gameServer;
    }

    public void setGameServer(GameServer gameServer) {
        this.gameServer = gameServer;
    }

    public void run() {

        // Load GameServer:
        this.gameServer = DataAccessLayer.getInstance().readTree();

        // If GameServer does not exist yet, create a new one:
        if (gameServer == null) {
            // Create sample objects:
            this.gameServer = createExampleData();
            // Write object tree to database:
            DataAccessLayer.getInstance().createTree(gameServer);
            System.out.println("New GameServer created.");
        }
    }

    public GameServer createExampleData() {
        return GameServer.createExampleData();
    }

}

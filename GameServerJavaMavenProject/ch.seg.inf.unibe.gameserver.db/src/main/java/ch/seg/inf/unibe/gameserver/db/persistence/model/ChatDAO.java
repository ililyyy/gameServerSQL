package ch.seg.inf.unibe.gameserver.db.persistence.model;

import ch.seg.inf.unibe.gameserver.db.logic.model.Chat;
import ch.seg.inf.unibe.gameserver.db.logic.model.Game;
import ch.seg.inf.unibe.gameserver.db.persistence.DataAccessLayer;
import ch.seg.inf.unibe.gameserver.db.persistence.DatabaseAccess;
import ch.seg.inf.unibe.gameserver.db.persistence.DatabaseAccess.ConnectedResult;
import ch.seg.inf.unibe.gameserver.db.persistence.DatabaseAccessUtil;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ChatDAO  extends IdentifiableElementDAO<Chat> {

    /**
     * Provides access to all DAOs by singleton data access layer.
     */
    private DataAccessLayer dataAccess = DataAccessLayer.getInstance();

    public void createTable(boolean recursive) {
        String createTable_Chat = """
                CREATE TABLE IF NOT EXISTS Chat (
                    id INTEGER PRIMARY KEY,
                    container_id INTEGER,
                    text STRING,
                    timestamp TIMESTAMP,
                    player_id INTEGER,
                    FOREIGN KEY (container_id) REFERENCES Game(id),
                    FOREIGN KEY (player_id) REFERENCES Player(id)
                );
                """;
        DatabaseAccess.getInstance().executeUpdate(createTable_Chat);
    }

    public void createTree(List<Chat> chats, Game gameContainer) {
        for (Chat chat : chats) {
            create(chat, gameContainer);
        }
    }

    public void create(Chat chat, Game container) {
        String create = String.format("""
                        INSERT INTO Chat
                        (id, container_id, text, timestamp, player_id)
                        VALUES (%d, %d, "%s", "%s", %d);""",
                        chat.getID(),
                        container.getID(),
                        chat.getText(),
                        DatabaseAccessUtil.instantToSQLTimestamp(chat.getTimestamp()),
                        chat.getPlayer().getID());
        DatabaseAccess.getInstance().executeUpdate(create);

        // update index
        loaded(chat);
    }

    public List<Chat> readTree(Game gameContainer) {
        List<Chat> chats = readByContainer(gameContainer);
        return chats;
    }

    public Chat readByID(int id) {
        if (id != 0) { // NULL is interpreted 0 by SQL getInt().
            if (isLoaded(id)) {
                return getLoaded(id);
            } else {
                return checkedResults(read("id = " + id), Chat.class, id);
            }
        } else {
            return null;
        }
    }

    public List<Chat> readByContainer(Game container) {
        return read("container_id = " + container.getID());
    }

    protected List<Chat> read(String sqlWhereClause) {
        List<Chat> chats = new ArrayList<>();

        String read = "SELECT * FROM Chat WHERE " + sqlWhereClause;

        // Process the rows:
        try (ConnectedResult connectedResult = DatabaseAccess.getInstance().executeQuery(read)) {
            ResultSet resultSet = connectedResult.resultSet;

            while (resultSet.next()) {
                int id = resultSet.getInt("id");

                if (isLoaded(id)) {
                    chats.add(getLoaded(id));
                } else {
                    Chat chat = new Chat();
                    chat.setID(id);
                    chat.setText(resultSet.getString("text"));
                    chat.setTimestamp(DatabaseAccessUtil.sqlTimestampToInstant(resultSet.getTimestamp("timestamp")));
                    chat.setPlayer(dataAccess.getPlayerDAO().readByID(resultSet.getInt("player_id")));
                    chats.add(chat);

                    // update index
                    loaded(chat);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return chats;
    }

    public void updateTree(List<Chat> chats, Game container) {
        for (Chat chat : chats) {
            update(chat, container);
        }
    }

    public void update(Chat chat, Game container) {
        String update = "UPDATE Chat SET " +
                "container_id = " + container.getID() + ", " +
                "text = \"" + chat.getText() + "\", " +
                "timestamp = \"" + DatabaseAccessUtil.instantToSQLTimestamp(chat.getTimestamp()) + "\", " +
                "player_id = " + chat.getPlayer().getID() + " " +
                "WHERE id = " + chat.getID();
        DatabaseAccess.getInstance().executeUpdate(update);
    }

    public void deleteTree(List<Chat> chats) {
        for (Chat chat : chats) {
            delete(chat);
        }
    }

    public void delete(Chat chat) {
        String delete = "DELETE FROM Chat " +
                "WHERE id = " + chat.getID();
        DatabaseAccess.getInstance().executeUpdate(delete);

        // update index
        unloaded(chat);
    }
}

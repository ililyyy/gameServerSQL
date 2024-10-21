package ch.seg.inf.unibe.gameserver.db.logic.model;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Random;

public class Chat extends IdentifiableElement {

    private Player player;

    private String text;

    private Instant timestamp;

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp.truncatedTo(ChronoUnit.SECONDS);
    }

    public String toString(int indent) {
        indent += 4;
        return ref(this) +  "{" + "\n" +
                " ".repeat(indent) + "text='" + text + '\'' + ",\n" +
                " ".repeat(indent) + "timestamp=" + timestamp + ",\n" +
                " ".repeat(indent) + "player=" + ref(player) + "\n" +
                " ".repeat(indent - 4) + '}';
    }

    public static Chat createExampleData(Game game) {
        Chat chat = new Chat();

        Random random = new Random();
        chat.player = game.getPlayers().get(random.nextInt(game.getPlayers().size() - 1)) ;
        chat.text = "Text " + random.nextInt(100);
        chat.setTimestamp(Instant.now().plusSeconds(random.nextInt(1000)));

        return chat;
    }

    public void modifyExampleData(Game game) {
        Random random = new Random();
        player = game.getPlayers().get(random.nextInt(game.getPlayers().size() - 1)) ;
        text = "Text " + random.nextInt(100);
        setTimestamp(Instant.now().plusSeconds(random.nextInt(1000)));
    }
}

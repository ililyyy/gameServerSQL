package ch.seg.inf.unibe.gameserver.db.logic.model;

import java.util.Random;

public class Points extends IdentifiableElement {

    private Player player;

    private int currentPoints;

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public int getCurrentPoints() {
        return currentPoints;
    }

    public void setCurrentPoints(int currentPoints) {
        this.currentPoints = currentPoints;
    }

    public String toString(int indent) {
        indent += 4;
        return ref(this) +  "{" + "\n" +
                " ".repeat(indent) + "currentPoints=" + currentPoints + ",\n" +
                " ".repeat(indent) + "player=" + ref(player) + "\n" +
                " ".repeat(indent - 4) + '}';
    }

    public static Points createExampleData(Player player) {
        Points points = new Points();
        points.player = player;
        Random random = new Random();
        points.currentPoints = random.nextInt(100);
        return points;
    }

    public void modifyExampleData() {
        Random random = new Random();
        currentPoints = random.nextInt(100);
    }
}

package ch.seg.inf.unibe.gameserver.db.logic.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Highscore extends IdentifiableElement {

    private List<Points> positions;

    private boolean closed;

    public List<Points> getPositions() {
        return positions;
    }

    public void setPositions(List<Points> positions) {
        this.positions = positions;
    }

    public boolean isClosed() {
        return closed;
    }

    public void setClosed(boolean closed) {
        this.closed = closed;
    }

    public String toString(int indent) {
        indent += 4;
        return ref(this) + "{" + "\n" +
                " ".repeat(indent) + "closed=" + closed + ",\n" +
                " ".repeat(indent) + "positions=" + subs(positions, indent) + "\n" +
                " ".repeat(indent - 4) + '}';
    }

    public static Highscore createExampleData(Tournament tournament) {
        Highscore highscore = new Highscore();

        Random random = new Random();
        highscore.closed = random.nextBoolean();
        highscore.positions = new ArrayList<>();

        for (Player player : tournament.getParticipants()) {
            highscore.positions.add(Points.createExampleData(player));
        }

        return highscore;
    }

    public void modifyExampleData() {
        Random random = new Random();
        closed = random.nextBoolean();
        positions.forEach(Points::modifyExampleData);
    }
}

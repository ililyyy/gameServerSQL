package ch.seg.inf.unibe.gameserver.db.logic.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Player extends IdentifiableElement {

    private List<Tournament> participates;

    private String name;

    public List<Tournament> getParticipates() {
        return participates;
    }

    public void setParticipates(List<Tournament> participates) {
        this.participates = participates;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String toString(int indent) {
        indent += 4;
        return ref(this) + "{" + "\n" +
                " ".repeat(indent) + "name='" + name + "',\n" +
                " ".repeat(indent) + "participates='" + refs(participates) + "'\n" +
                " ".repeat(indent - 4) + '}';
    }

    public static Player createExampleData() {
        Player player = new Player();
        Random random = new Random();
        player.name = "Name " + random.nextInt(100);
        player.participates = new ArrayList<>();
        return player;
    }

    public void modifyExampleData() {
        Random random = new Random();
        name = "Name " + random.nextInt(100);
    }
}

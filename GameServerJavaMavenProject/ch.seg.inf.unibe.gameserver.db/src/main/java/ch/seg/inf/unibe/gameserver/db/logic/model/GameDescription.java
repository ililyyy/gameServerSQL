package ch.seg.inf.unibe.gameserver.db.logic.model;

import java.util.Random;

public class GameDescription extends GameLibrary {

    private String description;

    private String rules;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRules() {
        return rules;
    }

    public void setRules(String rules) {
        this.rules = rules;
    }

    public String toString(int indent) {
        indent += 4;
        return ref(this) + "{" + "\n" +
                " ".repeat(indent) + "name='" + this.getName() + '\'' + ",\n" +
                " ".repeat(indent) + "description='" + description + '\'' + ",\n" +
                " ".repeat(indent) + "rules='" + rules + '\'' + "\n" +
                " ".repeat(indent - 4) + '}';
    }

    public static GameDescription createExampleData() {
        GameDescription gameDescription = new GameDescription();

        Random random = new Random();
        gameDescription.setName("Name " + random.nextInt(100));
        gameDescription.description = "Description " + random.nextInt(100);
        gameDescription.rules = "Rules " + random.nextInt(100);

        return gameDescription;
    }

    public void modifyExampleData() {
        Random random = new Random();
        setName("Name " + random.nextInt(100));
        description = "Description " + random.nextInt(100);
        rules = "Rules " + random.nextInt(100);
    }
}

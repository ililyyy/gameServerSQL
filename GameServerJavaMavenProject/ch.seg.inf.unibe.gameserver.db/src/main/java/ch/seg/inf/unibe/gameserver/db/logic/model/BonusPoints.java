package ch.seg.inf.unibe.gameserver.db.logic.model;

import java.util.Random;

public class BonusPoints extends IdentifiableElement {

    private BonusStock type;

    private String name;

    private String rule;

    private int points;

    public BonusStock getType() {
        return type;
    }

    public void setType(BonusStock type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRule() {
        return rule;
    }

    public void setRule(String rule) {
        this.rule = rule;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public String toString(int indent) {
        indent += 4;
        return ref(this) + "\n" +
                " ".repeat(indent) + "name='" + name + '\'' + ",\n" +
                " ".repeat(indent) + "rule='" + rule + '\'' + ",\n" +
                " ".repeat(indent) + "points=" + points + ",\n" +
                " ".repeat(indent) + "type=" + sub(type, indent) + "\n" +
                " ".repeat(indent - 4) + '}';
    }

    public static BonusPoints createExampleData() {
        BonusPoints bonusPoints = new BonusPoints();
        bonusPoints.type = BonusStock.createExampleData();

        Random random = new Random();
        bonusPoints.name = "Name " + random.nextInt(100);
        bonusPoints.rule = "Rule " + random.nextInt(100);
        bonusPoints.points = random.nextInt(100);

        return bonusPoints;
    }

    public void modifyExampleData() {
        Random random = new Random();
        name = "Name " + random.nextInt(100);
        rule = "Rule " + random.nextInt(100);
        points = random.nextInt(100);
        type.modifyExampleData();
    }
}

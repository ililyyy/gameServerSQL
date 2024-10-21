package ch.seg.inf.unibe.gameserver.db.logic.model;

import java.util.Random;

public class BonusStock extends IdentifiableElement {

    private int remaining;

    public int getRemaining() {
        return remaining;
    }

    public void setRemaining(int remaining) {
        this.remaining = remaining;
    }

    public String toString(int indent) {
        indent += 4;
        return ref(this) +  "{" + "\n" +
                " ".repeat(indent) + "remaining=" + remaining + "\n" +
                " ".repeat(indent - 4) + '}';
    }

    public static BonusStock createExampleData() {
        BonusStock bonusStock = new BonusStock();

        Random random = new Random();
        bonusStock.remaining = random.nextInt(20);

        return bonusStock;
    }

    public void modifyExampleData() {
        Random random = new Random();
        remaining = random.nextInt(20);
    }
}

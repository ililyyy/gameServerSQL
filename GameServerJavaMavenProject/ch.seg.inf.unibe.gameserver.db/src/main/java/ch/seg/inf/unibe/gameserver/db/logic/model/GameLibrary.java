package ch.seg.inf.unibe.gameserver.db.logic.model;

public abstract class GameLibrary extends IdentifiableElement {

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public abstract void modifyExampleData();
}

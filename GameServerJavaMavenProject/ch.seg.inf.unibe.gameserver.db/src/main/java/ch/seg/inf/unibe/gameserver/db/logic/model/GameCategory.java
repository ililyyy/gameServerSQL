package ch.seg.inf.unibe.gameserver.db.logic.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameCategory extends GameLibrary {

    private List<GameLibrary> contains;

    private String type;

    public List<GameLibrary> getContains() {
        return contains;
    }

    public void setContains(List<GameLibrary> contains) {
        this.contains = contains;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String toString(int indent) {
        indent += 4;
        return ref(this) + "{" + "\n" +
                " ".repeat(indent) + "name='" + this.getName() + '\'' + ",\n" +
                " ".repeat(indent) + "type='" + type + '\'' + ",\n" +
                " ".repeat(indent) + "contains=" + subs(contains, indent) + "\n" +
                " ".repeat(indent - 4) + '}';
    }

    public static GameCategory createExampleData() {
        GameCategory gameCategory = new GameCategory();

        Random random = new Random();
        gameCategory.setName("Name " + random.nextInt(100));
        gameCategory.type = "Text " + random.nextInt(100);
        gameCategory.contains = new ArrayList<>();

        return gameCategory;
    }

    public static Object[] createExampleDataTree() {
        GameCategory gameCategoryRoot = createExampleData();

        GameCategory gameCategory1 = createExampleData();
        gameCategoryRoot.getContains().add(gameCategory1);
        gameCategory1.getContains().add(GameDescription.createExampleData());
        gameCategory1.getContains().add(GameDescription.createExampleData());
        gameCategory1.getContains().add(GameDescription.createExampleData());

        GameCategory gameCategory2 = createExampleData();
        gameCategoryRoot.getContains().add(gameCategory2);
        gameCategory2.getContains().add(GameDescription.createExampleData());
        gameCategory2.getContains().add(GameDescription.createExampleData());

        List<Object> games = new ArrayList<>();
        games.addAll(gameCategory1.getContains());
        games.addAll(gameCategory2.getContains());

        return new Object[] {gameCategoryRoot, games};
    }

    public void modifyExampleData() {
        Random random = new Random();
        setName("Name " + random.nextInt(100));
        type = "Text " + random.nextInt(100);
        contains.forEach(GameLibrary::modifyExampleData);
    }
}

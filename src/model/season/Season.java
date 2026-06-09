package model.season;

public class Season {
    private String name;
    private int levelCount;

    public Season(String name, int levelCount) {
        this.name = name;
        this.levelCount = levelCount;
    }

    public String getName() {
        return name;
    }

    public int getLevelCount() {
        return levelCount;
    }
}


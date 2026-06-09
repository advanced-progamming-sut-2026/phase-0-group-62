package model.quest;

public class Quest {
    private String title;
    private String description;
    private boolean completed;

    public Quest(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public void complete() {
        completed = true;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public boolean isCompleted() {
        return completed;
    }
}


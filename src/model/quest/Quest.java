package model.quest;

import model.User;
import java.util.HashMap;
import java.util.Map;

public class Quest {
    private String id;
    private String title;
    private String description;
    private boolean completed;
    private QuestType type;
    private QuestStatus status;
    private int progress;
    private int target;
    private int rewardCoins;
    private int rewardDiamonds;
    private String rewardUnlockable;
    private Priority priority;
    private String additionalCondition;
    private int variableN;

    public enum QuestType {
        STORY,
        EPIC,
        DAILY
    }

    public enum QuestStatus {
        LOCKED,
        AVAILABLE,
        IN_PROGRESS,
        COMPLETED,
        CLAIMED
    }

    public enum Priority {
        CRITICAL,
        HIGH,
        MEDIUM,
        LOW
    }

    public Quest(String title, String description, QuestType type, Priority priority) {
        this.id = title.toLowerCase().replace(" ", "_").replace("'", "");
        this.title = title;
        this.description = description;
        this.completed = false;
        this.type = type;
        this.status = QuestStatus.AVAILABLE;
        this.progress = 0;
        this.target = 1;
        this.rewardCoins = 0;
        this.rewardDiamonds = 0;
        this.priority = priority;
        this.additionalCondition = "";
        this.variableN = 0;
    }

    public void applyReward(User user) {
        if (status == QuestStatus.COMPLETED) {
            user.setCoins(user.getCoins() + rewardCoins);
            user.setGems(user.getGems() + rewardDiamonds);
            status = QuestStatus.CLAIMED;
        }
    }

    public void complete() {
        completed = true;
        status = QuestStatus.COMPLETED;
    }

    public void updateProgress(int amount) {
        if (status == QuestStatus.AVAILABLE) {
            status = QuestStatus.IN_PROGRESS;
        }
        progress = Math.min(progress + amount, target);
        if (progress >= target) {
            complete();
        }
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public boolean isCompleted() { return completed; }
    public QuestType getType() { return type; }
    public QuestStatus getStatus() { return status; }
    public void setStatus(QuestStatus status) { this.status = status; }
    public int getProgress() { return progress; }
    public int getTarget() { return target; }
    public void setTarget(int target) { this.target = target; }
    public int getRewardCoins() { return rewardCoins; }
    public void setRewardCoins(int rewardCoins) { this.rewardCoins = rewardCoins; }
    public int getRewardDiamonds() { return rewardDiamonds; }
    public void setRewardDiamonds(int rewardDiamonds) { this.rewardDiamonds = rewardDiamonds; }
    public Priority getPriority() { return priority; }
    public void setPriority(Priority priority) { this.priority = priority; }
    public String getAdditionalCondition() { return additionalCondition; }
    public void setAdditionalCondition(String cond) { this.additionalCondition = cond; }
    public int getVariableN() { return variableN; }
    public void setVariableN(int n) { this.variableN = n; }
}
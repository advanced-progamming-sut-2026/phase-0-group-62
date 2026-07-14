package model.quest;

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
    private Map<String, Integer> requirements;

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

    public Quest(String title, String description) {
        this.id = title.toLowerCase().replace(" ", "_");
        this.title = title;
        this.description = description;
        this.completed = false;
        this.type = QuestType.DAILY;
        this.status = QuestStatus.AVAILABLE;
        this.progress = 0;
        this.target = 1;
        this.rewardCoins = 0;
        this.rewardDiamonds = 0;
        this.requirements = new HashMap<>();
    }

    public Quest(String title, String description, QuestType type) {
        this(title, description);
        this.type = type;
        if (type == QuestType.STORY) {
            this.status = QuestStatus.AVAILABLE;
        } else if (type == QuestType.EPIC) {
            this.status = QuestStatus.LOCKED;
        }
    }

    public void complete() {
        completed = true;
        status = QuestStatus.COMPLETED;
    }

    public void claimReward() {
        if (status == QuestStatus.COMPLETED) {
            status = QuestStatus.CLAIMED;
        }
    }

    public void updateProgress(int amount) {
        if (status != QuestStatus.IN_PROGRESS && status != QuestStatus.AVAILABLE) {
            status = QuestStatus.IN_PROGRESS;
        }
        progress = Math.min(progress + amount, target);
        if (progress >= target) {
            complete();
        }
    }

    public void reset() {
        if (type == QuestType.DAILY) {
            progress = 0;
            status = QuestStatus.AVAILABLE;
            completed = false;
        }
    }

    public boolean isAvailable() {
        return status == QuestStatus.AVAILABLE || status == QuestStatus.IN_PROGRESS;
    }

    public double getProgressPercentage() {
        return target > 0 ? (double) progress / target * 100 : 0;
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
    public String getRewardUnlockable() { return rewardUnlockable; }
    public void setRewardUnlockable(String rewardUnlockable) { this.rewardUnlockable = rewardUnlockable; }
    public Map<String, Integer> getRequirements() { return new HashMap<>(requirements); }
    public void addRequirement(String key, int value) { requirements.put(key, value); }
    public void setStatusLocket() { 
        if (type == QuestType.EPIC) {
            status = QuestStatus.LOCKED;
        }
    }
    public void unlock() { 
        if (status == QuestStatus.LOCKED) {
            status = QuestStatus.AVAILABLE;
        }
    }
}

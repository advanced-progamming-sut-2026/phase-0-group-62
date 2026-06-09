package model.leaderboard;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import model.User;

public class Leaderboard {
    private final List<User> users;

    public Leaderboard() {
        this.users = new ArrayList<>();
    }

    public void addUser(User user) {
        users.add(user);
    }

    public List<User> getTopUsers() {
        users.sort(Comparator.comparingInt(User::getScore).reversed());
        return users;
    }
}


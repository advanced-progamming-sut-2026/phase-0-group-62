package model;

import model.enums.Gender;

public class User {
    private String username;
    private String passwordHash;
    private String nickname;
    private String email;
    private Gender gender;
    private String securityQuestion;
    private String securityAnswer;
    private int score;

    public User(String username, String passwordHash, String nickname, String email, Gender gender , String securityQuestion , String securityAnswer) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.nickname = nickname;
        this.email = email;
        this.gender = gender;
        this.securityQuestion = securityQuestion;
        this.securityAnswer = securityAnswer;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return passwordHash;
    }

    public String getNickname() {
        return nickname;
    }

    public String getEmail() {
        return email;
    }

    public Gender getGender() {
        return gender;
    }

    public int getScore() {
        return score;
    }

    public String getSecurityQuestion() {
        return securityQuestion;
    }

    public String getSecurityAnswer() {
        return securityAnswer;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void setSecurityQuestion(String securityQuestion) {
        this.securityQuestion = securityQuestion;
    }

    public void setSecurityAnswer(String securityAnswer) {
        this.securityAnswer = securityAnswer;
    }

    public void addScore(int amount) {
        score += amount;
    }
}
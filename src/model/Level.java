package model;

public class Level {
    private final int number;
    private Wave wave;

    public Level(int number) {
        this.number = number;
        this.wave = new Wave(number);
    }

    public int getNumber() {
        return number;
    }

    public Wave getWave() {
        return wave;
    }
}


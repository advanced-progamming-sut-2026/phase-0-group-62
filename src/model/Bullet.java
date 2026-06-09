package model;

public class Bullet {
    private int damage;
    private int row;
    private int column;

    public Bullet(int damage, int row, int column) {
        this.damage = damage;
        this.row = row;
        this.column = column;
    }

    public void move() {
        column++;
    }

    public int getDamage() {
        return damage;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }
}


package com.example.celestialdungeon;

public class Player {
    private final String name;
    private int hp;
    private final int maxHp;
    private final int attack;
    private final int armor;
    private int gold;
    private int relics;
    private int x;
    private int y;

    public Player(String name) {
        this.name = name;
        this.maxHp = 30;
        this.hp = maxHp;
        this.attack = 8;
        this.armor = 2;
        this.gold = 0;
        this.relics = 0;
        this.x = 0;
        this.y = 0;
    }

    public void move(int deltaX, int deltaY, int boardSize) {
        int nextX = Math.max(0, Math.min(boardSize, x + deltaX));
        int nextY = Math.max(0, Math.min(boardSize, y + deltaY));
        this.x = nextX;
        this.y = nextY;
    }

    public void takeDamage(int damage) {
        this.hp = Math.max(0, hp - damage);
    }

    public void heal(int amount) {
        this.hp = Math.min(maxHp, hp + amount);
    }

    public void addGold(int amount) {
        this.gold += amount;
    }

    public void addRelic() {
        this.relics += 1;
    }

    public int getAttack() {
        return attack;
    }

    public int getArmor() {
        return armor;
    }

    public int getHp() {
        return hp;
    }

    public int getMaxHp() {
        return maxHp;
    }

    public int getGold() {
        return gold;
    }

    public int getRelics() {
        return relics;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public String getName() {
        return name;
    }
}

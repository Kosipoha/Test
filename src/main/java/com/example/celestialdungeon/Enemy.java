package com.example.celestialdungeon;

public class Enemy {
    private final String name;
    private int hp;
    private final int attack;
    private final int armor;
    private final int reward;
    private final int x;
    private final int y;

    public Enemy(String name, int hp, int attack, int armor, int reward, int x, int y) {
        this.name = name;
        this.hp = hp;
        this.attack = attack;
        this.armor = armor;
        this.reward = reward;
        this.x = x;
        this.y = y;
    }

    public void takeDamage(int amount) {
        this.hp = Math.max(0, hp - amount);
    }

    public boolean isAlive() {
        return hp > 0;
    }

    public int getAttack() {
        return attack;
    }

    public int getArmor() {
        return armor;
    }

    public int getReward() {
        return reward;
    }

    public int getHp() {
        return hp;
    }

    public String getName() {
        return name;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}

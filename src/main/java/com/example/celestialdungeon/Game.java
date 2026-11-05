package com.example.celestialdungeon;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

public class Game {
    private static final int MAP_SIZE = 5;
    private final Player player;
    private final List<Enemy> enemies;
    private final List<Cell> relics;
    private final Scanner scanner;
    private boolean running;
    private boolean victory;

    public Game(String playerName) {
        this.player = new Player(playerName);
        this.enemies = new ArrayList<>();
        this.relics = new ArrayList<>();
        this.scanner = new Scanner(System.in);
        this.running = true;
        this.victory = false;
        spawnEnemies();
        spawnRelics();
    }

    public void play() {
        printWelcome();

        while (running) {
            printBoard();
            System.out.println("\nStatus: HP=" + player.getHp() + "/" + player.getMaxHp() +
                    " | ATK=" + player.getAttack() + " | ARMOR=" + player.getArmor() +
                    " | GOLD=" + player.getGold() + " | RELICS=" + player.getRelics() + "/2");
            System.out.print("Command [w/a/s/d, f, h, q]: ");
            String input = scanner.nextLine().trim().toLowerCase();
            processInput(input);
        }

        if (victory) {
            System.out.println("\nYou conquered the Celestial Vault and secured the Starheart!\n");
        } else {
            System.out.println("\nMission failed. The void consumes another pilot.\n");
        }
    }

    private void processInput(String input) {
        switch (input) {
            case "w" -> movePlayer(0, -1);
            case "a" -> movePlayer(-1, 0);
            case "s" -> movePlayer(0, 1);
            case "d" -> movePlayer(1, 0);
            case "f" -> attackNearbyEnemy();
            case "h" -> useHeal();
            case "q" -> running = false;
            default -> System.out.println("Unknown command. Use w, a, s, d, f, h or q.");
        }
    }

    private void movePlayer(int dx, int dy) {
        int nextX = Math.max(0, Math.min(MAP_SIZE - 1, player.getX() + dx));
        int nextY = Math.max(0, Math.min(MAP_SIZE - 1, player.getY() + dy));

        if (nextX == player.getX() && nextY == player.getY()) {
            System.out.println("The edge of the star map blocks your path.");
            return;
        }

        player.move(dx, dy, MAP_SIZE - 1);
        Enemy enemy = findEnemyAt(player.getX(), player.getY());
        if (enemy != null) {
            resolveBattle(enemy);
        }

        if (player.getHp() <= 0) {
            running = false;
            return;
        }

        collectRelicIfPresent();

        if (GameLogic.isWinningTile(player.getX(), player.getY()) && player.getRelics() >= 2) {
            victory = true;
            running = false;
            return;
        }

        if (GameLogic.isWinningTile(player.getX(), player.getY())) {
            System.out.println("The vault door hums with energy. Two relics are required to open it.");
        }
    }

    private void attackNearbyEnemy() {
        Enemy enemy = findAdjacentEnemy();
        if (enemy == null) {
            System.out.println("No enemy is close enough to strike.");
            return;
        }

        int damage = GameLogic.calculateDamage(player.getAttack(), enemy.getArmor());
        enemy.takeDamage(damage);
        System.out.println("You hit " + enemy.getName() + " for " + damage + " damage.");

        if (!enemy.isAlive()) {
            player.addGold(enemy.getReward());
            System.out.println("Enemy defeated! You gain " + enemy.getReward() + " credits.");
            removeEnemy(enemy);
            return;
        }

        int counterDamage = GameLogic.calculateDamage(enemy.getAttack(), player.getArmor());
        player.takeDamage(counterDamage);
        System.out.println(enemy.getName() + " strikes back for " + counterDamage + " damage.");

        if (player.getHp() <= 0) {
            running = false;
        }
    }

    private void useHeal() {
        if (player.getHp() >= player.getMaxHp()) {
            System.out.println("Your suit is already fully charged.");
            return;
        }

        player.heal(10);
        System.out.println("Your nanite patch restores 10 HP.");
    }

    private void collectRelicIfPresent() {
        for (Cell relic : relics) {
            if (relic.x == player.getX() && relic.y == player.getY()) {
                player.addRelic();
                relics.remove(relic);
                System.out.println("You found a relic! The vault is one step closer to opening.");
                break;
            }
        }
    }

    private void resolveBattle(Enemy enemy) {
        int playerDamage = GameLogic.calculateDamage(player.getAttack(), enemy.getArmor());
        enemy.takeDamage(playerDamage);
        System.out.println("You attack " + enemy.getName() + " for " + playerDamage + " damage.");

        if (!enemy.isAlive()) {
            player.addGold(enemy.getReward());
            System.out.println("You defeat " + enemy.getName() + " and collect " + enemy.getReward() + " gold.");
            removeEnemy(enemy);
            return;
        }

        int enemyDamage = GameLogic.calculateDamage(enemy.getAttack(), player.getArmor());
        player.takeDamage(enemyDamage);
        System.out.println(enemy.getName() + " hits you for " + enemyDamage + " damage.");

        if (player.getHp() <= 0) {
            running = false;
        }
    }

    private void printWelcome() {
        System.out.println("====================================");
        System.out.println("        CELESTIAL DUNGEON");
        System.out.println("====================================");
        System.out.println("A lost starship hides a vault at the edge of the galaxy.");
        System.out.println("Collect 2 relics, defeat guardians, and reach the exit at the far corner.");
        System.out.println("Controls: w a s d move, f attack nearby, h heal, q quit.");
    }

    private void printBoard() {
        System.out.println("\n=== MAP ===");
        for (int y = 0; y < MAP_SIZE; y++) {
            StringBuilder row = new StringBuilder();
            for (int x = 0; x < MAP_SIZE; x++) {
                char tile = '.';
                if (x == player.getX() && y == player.getY()) {
                    tile = 'P';
                } else if (GameLogic.isWinningTile(x, y)) {
                    tile = 'G';
                } else if (containsRelic(x, y)) {
                    tile = 'R';
                } else if (containsEnemy(x, y)) {
                    tile = 'E';
                }
                row.append(tile).append(' ');
            }
            System.out.println(row);
        }
        System.out.println("Legend: P=player, E=enemy, R=relic, G=exit");
    }

    private void spawnEnemies() {
        enemies.add(new Enemy("Void Wisp", 12, 5, 2, 18, 1, 1));
        enemies.add(new Enemy("Nebula Stalker", 16, 6, 3, 25, 2, 3));
        enemies.add(new Enemy("Abyssal Knight", 20, 8, 4, 32, 4, 2));
    }

    private void spawnRelics() {
        relics.add(new Cell(0, 3));
        relics.add(new Cell(3, 1));
    }

    private Enemy findEnemyAt(int x, int y) {
        for (Enemy enemy : enemies) {
            if (enemy.isAlive() && enemy.getX() == x && enemy.getY() == y) {
                return enemy;
            }
        }
        return null;
    }

    private Enemy findAdjacentEnemy() {
        for (Enemy enemy : enemies) {
            if (!enemy.isAlive()) {
                continue;
            }
            int dx = Math.abs(enemy.getX() - player.getX());
            int dy = Math.abs(enemy.getY() - player.getY());
            if (dx <= 1 && dy <= 1 && !(dx == 0 && dy == 0)) {
                return enemy;
            }
        }
        return null;
    }

    private void removeEnemy(Enemy enemy) {
        enemies.removeIf(candidate -> Objects.equals(candidate, enemy));
    }

    private boolean containsEnemy(int x, int y) {
        return findEnemyAt(x, y) != null;
    }

    private boolean containsRelic(int x, int y) {
        for (Cell relic : relics) {
            if (relic.x == x && relic.y == y) {
                return true;
            }
        }
        return false;
    }

    private static final class Cell {
        private final int x;
        private final int y;

        private Cell(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }
}

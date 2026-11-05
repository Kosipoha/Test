package com.example.celestialdungeon;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class SpaceGameWindow extends JFrame {
    private static final int MAP_SIZE = 7;

    private final Player player;
    private final List<Enemy> enemies;
    private final List<Point> relics;
    private final Point exit = new Point(6, 6);

    private final JLabel statusLabel;
    private final JTextArea logArea;
    private final BoardPanel boardPanel;
    private final StringBuilder logBuilder;

    public SpaceGameWindow() {
        super("Celestial Dungeon");
        this.player = new Player("Nova");
        this.enemies = new ArrayList<>();
        this.relics = new ArrayList<>();
        this.statusLabel = new JLabel();
        this.logArea = new JTextArea(10, 22);
        this.boardPanel = new BoardPanel();
        this.logBuilder = new StringBuilder();

        configureLookAndFeel();
        initEntities();
        buildUi();
        appendLog("Mission started. Recover two relics and reach the star vault.");
        refreshStatus();
    }

    private void configureLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
            // Fallback to default look if system L&F cannot be initialized.
        }
    }

    private void initEntities() {
        enemies.add(new Enemy("Void Wisp", 12, 5, 2, 18, 1, 1));
        enemies.add(new Enemy("Nebula Stalker", 16, 6, 3, 25, 2, 4));
        enemies.add(new Enemy("Abyssal Knight", 20, 8, 4, 32, 4, 2));

        relics.add(new Point(0, 4));
        relics.add(new Point(3, 1));
        relics.add(new Point(5, 5));
    }

    private void buildUi() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(14, 14));
        setBackground(new Color(8, 10, 22));

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
        statusLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
        statusLabel.setForeground(new Color(220, 240, 255));
        statusLabel.setOpaque(true);
        statusLabel.setBackground(new Color(18, 24, 42));
        statusLabel.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        topPanel.add(statusLabel, BorderLayout.CENTER);
        add(topPanel, BorderLayout.NORTH);

        boardPanel.setPreferredSize(new Dimension(520, 520));
        add(boardPanel, BorderLayout.CENTER);

        JPanel rightPanel = new JPanel(new BorderLayout(10, 10));
        rightPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 10));

        logArea.setEditable(false);
        logArea.setLineWrap(true);
        logArea.setWrapStyleWord(true);
        logArea.setBackground(new Color(15, 20, 35));
        logArea.setForeground(new Color(228, 240, 255));
        logArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        JScrollPane scrollPane = new JScrollPane(logArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Mission Log"));
        rightPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel actionPanel = new JPanel(new GridLayout(2, 3, 8, 8));
        actionPanel.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));

        addButton(actionPanel, "↑", e -> movePlayer(0, -1));
        addButton(actionPanel, "←", e -> movePlayer(-1, 0));
        addButton(actionPanel, "↓", e -> movePlayer(0, 1));
        addButton(actionPanel, "→", e -> movePlayer(1, 0));
        addButton(actionPanel, "Attack", e -> attackNearbyEnemy());
        addButton(actionPanel, "Heal", e -> useHeal());

        rightPanel.add(actionPanel, BorderLayout.SOUTH);
        add(rightPanel, BorderLayout.EAST);

        setResizable(false);
        pack();
        setLocationRelativeTo(null);

        installKeyboardShortcuts();
    }

    private void addButton(JPanel panel, String text, ActionListener action) {
        JButton button = new JButton(text);
        button.addActionListener(action);
        button.setFocusPainted(false);
        panel.add(button);
    }

    private void installKeyboardShortcuts() {
        boardPanel.getInputMap().put(javax.swing.KeyStroke.getKeyStroke("UP"), "moveUp");
        boardPanel.getActionMap().put("moveUp", new javax.swing.AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                movePlayer(0, -1);
            }
        });

        boardPanel.getInputMap().put(javax.swing.KeyStroke.getKeyStroke("DOWN"), "moveDown");
        boardPanel.getActionMap().put("moveDown", new javax.swing.AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                movePlayer(0, 1);
            }
        });

        boardPanel.getInputMap().put(javax.swing.KeyStroke.getKeyStroke("LEFT"), "moveLeft");
        boardPanel.getActionMap().put("moveLeft", new javax.swing.AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                movePlayer(-1, 0);
            }
        });

        boardPanel.getInputMap().put(javax.swing.KeyStroke.getKeyStroke("RIGHT"), "moveRight");
        boardPanel.getActionMap().put("moveRight", new javax.swing.AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                movePlayer(1, 0);
            }
        });

        boardPanel.getInputMap().put(javax.swing.KeyStroke.getKeyStroke("pressed F"), "attack");
        boardPanel.getActionMap().put("attack", new javax.swing.AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                attackNearbyEnemy();
            }
        });

        boardPanel.getInputMap().put(javax.swing.KeyStroke.getKeyStroke("pressed H"), "heal");
        boardPanel.getActionMap().put("heal", new javax.swing.AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                useHeal();
            }
        });
    }

    private void movePlayer(int dx, int dy) {
        if (player.getHp() <= 0) {
            appendLog("The pilot is down. Restart the run.");
            refreshStatus();
            return;
        }

        int nextX = Math.max(0, Math.min(MAP_SIZE - 1, player.getX() + dx));
        int nextY = Math.max(0, Math.min(MAP_SIZE - 1, player.getY() + dy));

        if (nextX == player.getX() && nextY == player.getY()) {
            appendLog("The edge of the star field is sealed.");
            refreshStatus();
            return;
        }

        player.move(dx, dy, MAP_SIZE - 1);

        Enemy enemyAtTile = findEnemyAt(player.getX(), player.getY());
        if (enemyAtTile != null) {
            resolveBattle(enemyAtTile);
        }

        if (player.getHp() <= 0) {
            appendLog("Mission failed. The void has consumed the squad.");
            refreshStatus();
            return;
        }

        collectRelicIfPresent();
        if (player.getX() == exit.x && player.getY() == exit.y) {
            if (player.getRelics() >= 2) {
                appendLog("Vault unlocked. The Starheart is yours! Victory.");
            } else {
                appendLog("The vault door hums with energy. Two relics are required to open it.");
            }
        }

        refreshStatus();
    }

    private void attackNearbyEnemy() {
        if (player.getHp() <= 0) {
            appendLog("You cannot fight while incapacitated.");
            refreshStatus();
            return;
        }

        Enemy enemy = findAdjacentEnemy();
        if (enemy == null) {
            appendLog("No enemy is close enough for a strike.");
            refreshStatus();
            return;
        }

        int damage = GameLogic.calculateDamage(player.getAttack(), enemy.getArmor());
        enemy.takeDamage(damage);
        appendLog("You hit " + enemy.getName() + " for " + damage + " damage.");

        if (!enemy.isAlive()) {
            player.addGold(enemy.getReward());
            appendLog(enemy.getName() + " is defeated. Credits gained: " + enemy.getReward());
            enemies.removeIf(candidate -> candidate == enemy);
            refreshStatus();
            return;
        }

        int counterDamage = GameLogic.calculateDamage(enemy.getAttack(), player.getArmor());
        player.takeDamage(counterDamage);
        appendLog(enemy.getName() + " counters for " + counterDamage + " damage.");

        if (player.getHp() <= 0) {
            appendLog("The hunter falls. Mission failed.");
        }

        refreshStatus();
    }

    private void useHeal() {
        if (player.getHp() >= player.getMaxHp()) {
            appendLog("The med-bay suit is already at full power.");
            refreshStatus();
            return;
        }

        player.heal(12);
        appendLog("Nanite patch restores 12 HP.");
        refreshStatus();
    }

    private void collectRelicIfPresent() {
        Iterator<Point> iterator = relics.iterator();
        while (iterator.hasNext()) {
            Point point = iterator.next();
            if (point.x == player.getX() && point.y == player.getY()) {
                iterator.remove();
                player.addRelic();
                appendLog("Relic recovered. Vault integrity: " + player.getRelics() + "/2");
                break;
            }
        }
    }

    private void resolveBattle(Enemy enemy) {
        int damage = GameLogic.calculateDamage(player.getAttack(), enemy.getArmor());
        enemy.takeDamage(damage);
        appendLog("Close combat: you hit " + enemy.getName() + " for " + damage + ".");

        if (!enemy.isAlive()) {
            player.addGold(enemy.getReward());
            appendLog("Victory! " + enemy.getName() + " drops " + enemy.getReward() + " credits.");
            enemies.removeIf(candidate -> candidate == enemy);
            refreshStatus();
            return;
        }

        int counter = GameLogic.calculateDamage(enemy.getAttack(), player.getArmor());
        player.takeDamage(counter);
        appendLog(enemy.getName() + " returns fire for " + counter + " damage.");

        if (player.getHp() <= 0) {
            appendLog("Your ship falls silent in the dark.");
        }

        refreshStatus();
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

    private boolean containsRelic(int x, int y) {
        for (Point point : relics) {
            if (point.x == x && point.y == y) {
                return true;
            }
        }
        return false;
    }

    private void appendLog(String message) {
        logBuilder.append(message).append(System.lineSeparator()).append(System.lineSeparator());
        logArea.setText(logBuilder.toString());
    }

    private void refreshStatus() {
        String status = String.format(
                "HP %d/%d | ATK %d | ARMOR %d | GOLD %d | RELICS %d/2 | EXIT %s",
                player.getHp(),
                player.getMaxHp(),
                player.getAttack(),
                player.getArmor(),
                player.getGold(),
                player.getRelics(),
                (player.getX() == exit.x && player.getY() == exit.y) ? "READY" : "LOCKED"
        );
        statusLabel.setText(status);
        boardPanel.repaint();
    }

    private class BoardPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int cellSize = Math.min(getWidth(), getHeight()) / MAP_SIZE;
            for (int y = 0; y < MAP_SIZE; y++) {
                for (int x = 0; x < MAP_SIZE; x++) {
                    int px = x * cellSize;
                    int py = y * cellSize;
                    g2.setColor(new Color(14, 20, 35));
                    g2.fillRect(px, py, cellSize, cellSize);
                    g2.setColor(new Color(44, 60, 88));
                    g2.drawRect(px, py, cellSize, cellSize);

                    if (x == player.getX() && y == player.getY()) {
                        g2.setColor(new Color(255, 210, 90));
                        g2.fillOval(px + 12, py + 12, cellSize - 24, cellSize - 24);
                        g2.setColor(Color.BLACK);
                        g2.drawString("P", px + cellSize / 2 - 4, py + cellSize / 2 + 5);
                    } else if (x == exit.x && y == exit.y) {
                        g2.setColor(new Color(135, 208, 255));
                        g2.fillRoundRect(px + 14, py + 14, cellSize - 28, cellSize - 28, 12, 12);
                        g2.setColor(Color.BLACK);
                        g2.drawString("G", px + cellSize / 2 - 4, py + cellSize / 2 + 5);
                    } else if (containsRelic(x, y)) {
                        g2.setColor(new Color(105, 255, 165));
                        g2.fillOval(px + 18, py + 18, cellSize - 36, cellSize - 36);
                        g2.setColor(Color.BLACK);
                        g2.drawString("R", px + cellSize / 2 - 4, py + cellSize / 2 + 5);
                    } else if (findEnemyAt(x, y) != null) {
                        g2.setColor(new Color(255, 110, 110));
                        g2.fillOval(px + 16, py + 16, cellSize - 32, cellSize - 32);
                        g2.setColor(Color.BLACK);
                        g2.drawString("E", px + cellSize / 2 - 4, py + cellSize / 2 + 5);
                    }
                }
            }

            g2.dispose();
        }
    }

    public static void open() {
        SwingUtilities.invokeLater(() -> {
            SpaceGameWindow window = new SpaceGameWindow();
            window.setVisible(true);
        });
    }
}

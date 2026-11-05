package com.example.celestialdungeon;

public final class GameLogic {
    private GameLogic() {
    }

    public static int calculateDamage(int attackPower, int defensePower) {
        return Math.max(1, attackPower - defensePower);
    }

    public static boolean isWinningTile(int x, int y) {
        return x == 4 && y == 4;
    }
}

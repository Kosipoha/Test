package com.example.celestialdungeon;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class GameLogicTest {

    @Test
    void calculateDamage_shouldNeverBeBelowOne() {
        assertEquals(1, GameLogic.calculateDamage(3, 10));
        assertEquals(4, GameLogic.calculateDamage(9, 5));
    }

    @Test
    void isWinningTile_shouldDetectExitPosition() {
        assertTrue(GameLogic.isWinningTile(4, 4));
        assertFalse(GameLogic.isWinningTile(2, 2));
    }
}

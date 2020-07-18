package ly.unnecessary.frontend;

import org.junit.jupiter.api.Test;

import static ly.unnecessary.frontend.GameLogic.*;
import static org.junit.jupiter.api.Assertions.*;

class GameLogicTest {

    /**
     * Positive Tests for the collision with the player and ball. Also converts to degrees to make is easier to read.
     */
    @Test
    void calculateAngleTestPositive() {

        //Collides in the center of the player
        double ballX = 100 + 160, playerX = 100, playerWidth = 320;
        double collidePoint = getCollidePoint(ballX, playerX, playerWidth);

        assertEquals(0.0, collidePoint, 0.001);
        assertEquals(0.0, getCollidePointNormalize(collidePoint, playerWidth), 0.001);
        assertEquals(0.0, calculateAngle(ballX, playerX, playerWidth), 0.001);
        assertEquals(0.0, Math.toDegrees(calculateAngle(ballX, playerX, playerWidth)), 0.001);

        //Collide left
        ballX = 100;
        playerX = 100;
        playerWidth = 320;
        collidePoint = getCollidePoint(ballX, playerX, playerWidth);

        assertEquals(-160.0, collidePoint, 0.001);
        assertEquals(-1.0, getCollidePointNormalize(collidePoint, playerWidth), 0.001);
        assertEquals(-1.0471975511965976, calculateAngle(ballX, playerX, playerWidth), 0.001);
        assertEquals(-60.0, Math.toDegrees(calculateAngle(ballX, playerX, playerWidth)), 0.01);

        //Collide right
        ballX = 100 + 320;
        playerX = 100;
        playerWidth = 320;
        collidePoint = getCollidePoint(ballX, playerX, playerWidth);

        assertEquals(160.0, collidePoint, 0.001);
        assertEquals(1.0, getCollidePointNormalize(collidePoint, playerWidth), 0.001);
        assertEquals(1.0471975511965976, calculateAngle(ballX, playerX, playerWidth), 0.001);
        assertEquals(60.0, Math.toDegrees(calculateAngle(ballX, playerX, playerWidth)), 0.01);


        //Collide between left and center
        ballX = 100 + 80;
        playerX = 100;
        playerWidth = 320;
        collidePoint = getCollidePoint(ballX, playerX, playerWidth);

        assertEquals(-80, collidePoint, 0.001);
        assertEquals(-0.5, getCollidePointNormalize(collidePoint, playerWidth), 0.001);
        assertEquals(-0.52359877559, calculateAngle(ballX, playerX, playerWidth), 0.001);
        assertEquals(-30.0, Math.toDegrees(calculateAngle(ballX, playerX, playerWidth)), 0.01);

        //Collide between center and right
        ballX = 100 + 80 + 160;
        playerX = 100;
        playerWidth = 320;
        collidePoint = getCollidePoint(ballX, playerX, playerWidth);

        assertEquals(80, collidePoint, 0.001);
        assertEquals(0.5, getCollidePointNormalize(collidePoint, playerWidth), 0.001);
        assertEquals(0.52359877559, calculateAngle(ballX, playerX, playerWidth), 0.001);
        assertEquals(30.0, Math.toDegrees(calculateAngle(ballX, playerX, playerWidth)), 0.01);
    }

    /**
     * Negative Tests for the collision with the player and ball. Also converts to degrees to make is easier to read.
     */
    @Test
    void calculateAngleTestNegative() {

        //Collides in the center of the player
        double ballX = 100 + 160, playerX = 100, playerWidth = 320;
        double collidePoint = getCollidePoint(ballX, playerX, playerWidth);

        assertNotEquals(160.0, collidePoint, 0.001);
        assertNotEquals(160.0, getCollidePointNormalize(collidePoint, playerWidth), 0.001);
        assertNotEquals(160.0, calculateAngle(ballX, playerX, playerWidth), 0.001);
        assertNotEquals(160.0, Math.toDegrees(calculateAngle(ballX, playerX, playerWidth)), 0.001);

        //Collide left
        ballX = 100;
        playerX = 100;
        playerWidth = 320;
        collidePoint = getCollidePoint(ballX, playerX, playerWidth);

        assertNotEquals(160.0, collidePoint, 0.001);
        assertNotEquals(1.0, getCollidePointNormalize(collidePoint, playerWidth), 0.001);
        assertNotEquals(1.0471975511965976, calculateAngle(ballX, playerX, playerWidth), 0.001);
        assertNotEquals(60.0, Math.toDegrees(calculateAngle(ballX, playerX, playerWidth)), 0.01);

        //Collide right
        ballX = 100 + 320;
        playerX = 100;
        playerWidth = 320;
        collidePoint = getCollidePoint(ballX, playerX, playerWidth);

        assertNotEquals(-160.0, collidePoint, 0.001);
        assertNotEquals(-1.0, getCollidePointNormalize(collidePoint, playerWidth), 0.001);
        assertNotEquals(-1.0471975511965976, calculateAngle(ballX, playerX, playerWidth), 0.001);
        assertNotEquals(-60.0, Math.toDegrees(calculateAngle(ballX, playerX, playerWidth)), 0.01);


        //Collide between left and center
        ballX = 100 + 80;
        playerX = 100;
        playerWidth = 320;
        collidePoint = getCollidePoint(ballX, playerX, playerWidth);

        assertNotEquals(80, collidePoint, 0.001);
        assertNotEquals(0.5, getCollidePointNormalize(collidePoint, playerWidth), 0.001);
        assertNotEquals(0.52359877559, calculateAngle(ballX, playerX, playerWidth), 0.001);
        assertNotEquals(30.0, Math.toDegrees(calculateAngle(ballX, playerX, playerWidth)), 0.01);

        //Collide between center and right
        ballX = 100 + 80 + 160;
        playerX = 100;
        playerWidth = 320;
        collidePoint = getCollidePoint(ballX, playerX, playerWidth);

        assertNotEquals(-80, collidePoint, 0.001);
        assertNotEquals(-0.5, getCollidePointNormalize(collidePoint, playerWidth), 0.001);
        assertNotEquals(-0.52359877559, calculateAngle(ballX, playerX, playerWidth), 0.001);
        assertNotEquals(-30.0, Math.toDegrees(calculateAngle(ballX, playerX, playerWidth)), 0.01);
    }
}
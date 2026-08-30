package model.physics;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class TrajectoryPredictorTest {

    @Test
    void testPredictreturnsNonEmptyList() {
        double startX = 0;
        double startY = 0;
        int maxSteps = 10;
        double angleRad = Math.PI / 4;
        double speed = 10;
        double gravity = 9.8;
        double timeStep = 0.1;

        ArrayList<double[]> points = TrajectoryPredictor.predict(
                startX, startY, maxSteps, angleRad, speed, gravity, timeStep
        );

        assertNotNull(points, "Predict should return a non-null list");
        assertFalse(points.isEmpty(), "Predict should return a non-empty list");
        assertTrue(points.size() <= maxSteps, "List size should not exceed maxSteps");
    }

    @Test
    void testPredictfirstPointCloseToStart() {
        double startX = 5;
        double startY = 10;
        int maxSteps = 5;
        double angleRad = 0;
        double speed = 5;
        double gravity = 9.8;
        double timeStep = 0.1;

        ArrayList<double[]> points = TrajectoryPredictor.predict(
                startX, startY, maxSteps, angleRad, speed, gravity, timeStep
        );

        double[] firstPoint = points.get(0);
        assertTrue(Math.abs(firstPoint[0] - startX) < 1e-6);
        assertFalse(Math.abs(firstPoint[1] - startY) < 1e-6);
    }

    @Test
    void testPredictstopsAtGround() {
        double startX = 0;
        double startY = 0;
        int maxSteps = 100;
        double angleRad = Math.PI / 2;
        double speed = 10;
        double gravity = 10;
        double timeStep = 0.1;

        ArrayList<double[]> points = TrajectoryPredictor.predict(
                startX, startY, maxSteps, angleRad, speed, gravity, timeStep
        );

        double lastY = points.get(points.size() - 1)[1];
        assertTrue(lastY < 0);
        assertTrue(points.size() <= maxSteps);
    }

    @Test
    void testPredicttrajectoryGoesUpAndDown() {
        double startX = 0;
        double startY = 0;
        int maxSteps = 50;
        double angleRad = Math.PI / 4;
        double speed = 10;
        double gravity = 9.8;
        double timeStep = 0.1;

        ArrayList<double[]> points = TrajectoryPredictor.predict(
                startX, startY, maxSteps, angleRad, speed, gravity, timeStep
        );

        boolean wentUp = false;
        double lastY = startY;
        for (double[] point : points) {
            if (point[1] > lastY) {
                wentUp = true;
                break;
            }
        }

        assertFalse(wentUp);
    }
}

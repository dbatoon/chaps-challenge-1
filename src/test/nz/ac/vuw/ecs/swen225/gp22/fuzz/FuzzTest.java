package nz.ac.vuw.ecs.swen225.gp22.fuzz;

import static org.junit.jupiter.api.Assertions.assertTimeout;

import nz.ac.vuw.ecs.swen225.gp22.app.Base;
import org.junit.jupiter.api.Test;

import java.util.*;
import javax.swing.SwingUtilities;
import java.awt.Robot;
import java.awt.AWTException;
import java.awt.event.KeyEvent;
import java.time.Duration;

/**
 * Class for Fuzz Testing.
 *
 * @author Gavin
 * @version 1.6
 */
public class FuzzTest {

    // Random used for generating inputs.
    private static final Random r = new Random();

    // Base for loading games.
    private static Base base;

    // Delay of inputs for Fuzz Test.
    private int inputDelay = 5;

    // List of possible inputs.
    private final List<Integer> inputs = List.of(KeyEvent.VK_UP,
            KeyEvent.VK_RIGHT, KeyEvent.VK_LEFT, KeyEvent.VK_DOWN);

    // Map of inputs and their opposite inputs.
    private final Map<Integer, Integer> inputsAndOpposite =
            Map.of(KeyEvent.VK_UP, KeyEvent.VK_DOWN,
                    KeyEvent.VK_DOWN, KeyEvent.VK_UP,
                    KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT,
                    KeyEvent.VK_RIGHT, KeyEvent.VK_LEFT);

    /**
     * Generates a list of inputs of size numOfInputs
     *
     * @param numOfInputs - numberOfInputs
     * @return - list of inputs
     */
    private List<Integer> generateInputs(int numOfInputs) {
        int prev = -1;
        List<Integer> moves = new ArrayList<>();
        for (int i = 0; i < numOfInputs; i++) {
            while (true) {
                int random = r.nextInt(inputs.size());
                int move = inputs.get(random);
                if (prev == -1 || move != inputsAndOpposite.get(prev)) {
                    moves.add(move);
                    prev = move;
                    break;
                }
            }
        }
        return moves;
    }

    /**
     * Inputs random inputs for Level 1 of Chap's Challenge.
     * Level 1 has an input delay of 5ms and 8000 inputs.
     */
    private void test1() {
        inputDelay = 5;
        try {
            SwingUtilities.invokeLater(() -> (base = new Base()).newGame(1));
        } catch (Error ignored) {
        }
        runTest(generateInputs(8000));
    }

    /**
     * Inputs random inputs for Level 2 of Chap's Challenge.
     * Level 2 has an input delay of 100ms and 500 inputs.
     */
    private void test2() {
        inputDelay = 100;
        try {
            SwingUtilities.invokeLater(() -> base.newGame(2));
        } catch (Error ignored) {
        }
        runTest(generateInputs(500));
    }

    /**
     * Runs test inputs on current Base/Level.
     *
     * @param generatedInputs - List of inputs for test.
     */
    private void runTest(List<Integer> generatedInputs) {
        try {
            Robot robot = new Robot();
            for (int i : generatedInputs) {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        robot.keyPress(i);
                    }
                });
                try {
                    Thread.sleep(inputDelay);
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            robot.keyRelease(i);
                        }
                    });
                } catch (InterruptedException ignored) {
                }
            }

        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

    /**
     * Runs tests for both level 1 and 2 of Chap's Challenge.
     * Tests are limited to a one minute timer.
     */
    @Test
    public void fuzzTests() {
        try {
            assertTimeout(Duration.ofSeconds(60), this::test1);
            assertTimeout(Duration.ofSeconds(60), this::test2);
        } catch (Exception ignored) {

        }
    }
}

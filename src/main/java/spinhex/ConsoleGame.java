package spinhex;

import spinhex.model.AxialPosition;
import spinhex.model.Rotation;
import spinhex.model.SpinHexModel;
import spinhex.model.TwoPhaseActionState.TwoPhaseAction;

import java.util.Scanner;
import java.util.function.Supplier;

public class ConsoleGame {

    private static AxialPosition readPosition() {

        String inputStr = System.console().readLine("Enter position (q s): ");
        inputStr = inputStr.trim();
        if (!inputStr.matches("\\d+\\s+\\d+")) {
            throw new IllegalArgumentException("Input must be in the format 'q s' and both must be integers.");
        }
        var scanner = new Scanner(inputStr);
        AxialPosition pos = new AxialPosition(scanner.nextInt(), scanner.nextInt());
        scanner.close();
        return pos;

    }

    private static Rotation readRotation() {
        String input = System.console().readLine("Enter rotation (clockwise (cw)/counterclockwise (ccw)): ");
        return switch (input.trim().toLowerCase()) {
            case "cw" -> Rotation.CLOCKWISE;
            case "ccw" -> Rotation.COUNTERCLOCKWISE;
            default -> throw new IllegalArgumentException("Invalid rotation. Use 'cw' or 'ccw'.");
        };
    }

    private static void printBoard(SpinHexModel game) {
        System.out.println("Current board state:");
        for (int row = 0; row < game.getBoardSize(); row++) {
            var offset = Math.abs(game.getBoard().getRadius() - row);
            for (int i = 0; i < offset; i++) {
                System.out.print("  ");
            }
            for (int col = 0; col < game.getBoardSize(); col++) {
                AxialPosition pos = new AxialPosition(row, col);
                if (game.getBoard().isInBounds(pos)) {
                    System.out.print(game.getHex(pos) + "   ");
                }
            }
            System.out.println();
        }
    }

    private static <T> T tryUntilValid(Supplier<T> func) {
        while (true) {
            try {
                return func.get();
            } catch (IllegalArgumentException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        var game = new SpinHexModel();
        do {
            try {
                printBoard(game);
                var userPos = tryUntilValid(ConsoleGame::readPosition);
                if (!game.isLegalToMoveFrom(userPos)) {
                    throw new IllegalArgumentException("It's Illegal to move from here.");
                }
                var userRot = tryUntilValid(ConsoleGame::readRotation);
                game.makeMove(new TwoPhaseAction<>(userPos, userRot));
            } catch (IllegalArgumentException e) {
                System.out.println("Error: " + e.getMessage());
            }
        } while (!game.isSolved());
    }

}

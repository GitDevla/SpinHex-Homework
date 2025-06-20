package spinhex;

import puzzle.solver.BreadthFirstSearch;
import solver.BreadthFirstSearchModified;
import spinhex.model.AxialPosition;
import spinhex.model.HexColor;
import spinhex.model.Rotation;
import spinhex.model.SpinHexModel;
import spinhex.model.TwoPhaseActionState.TwoPhaseAction;

public class BFS {
    static Preset closerBoardConfig = new Preset(new byte[][] {
            { HexColor.NONE, HexColor.NONE, HexColor.RED, HexColor.RED, HexColor.RED },
            { HexColor.NONE, HexColor.RED, HexColor.BLUE, HexColor.GREEN, HexColor.BLUE },
            { HexColor.BLUE, HexColor.RED, HexColor.BLUE, HexColor.GREEN, HexColor.BLUE },
            { HexColor.BLUE, HexColor.RED, HexColor.BLUE, HexColor.GREEN, HexColor.NONE },
            { HexColor.GREEN, HexColor.GREEN, HexColor.GREEN, HexColor.NONE, HexColor.NONE }
    },new byte[][] {
            { HexColor.NONE, HexColor.NONE, HexColor.GREEN, HexColor.RED, HexColor.GREEN },
            { HexColor.NONE, HexColor.RED, HexColor.BLUE, HexColor.BLUE, HexColor.RED },
            { HexColor.GREEN, HexColor.BLUE, HexColor.BLUE, HexColor.BLUE, HexColor.GREEN },
            { HexColor.RED, HexColor.BLUE, HexColor.BLUE, HexColor.RED, HexColor.NONE },
            { HexColor.GREEN, HexColor.RED, HexColor.GREEN, HexColor.NONE, HexColor.NONE }
    });

    static Preset originalBoardConfig = new Preset(new byte[][] {
            { HexColor.NONE, HexColor.NONE, HexColor.RED, HexColor.RED, HexColor.RED },
            { HexColor.NONE, HexColor.RED, HexColor.RED, HexColor.RED, HexColor.BLUE },
            { HexColor.BLUE, HexColor.BLUE, HexColor.BLUE, HexColor.BLUE, HexColor.BLUE },
            { HexColor.BLUE, HexColor.GREEN, HexColor.GREEN, HexColor.GREEN, HexColor.NONE },
            { HexColor.GREEN, HexColor.GREEN, HexColor.GREEN, HexColor.NONE, HexColor.NONE }
    },new byte[][] {
            { HexColor.NONE, HexColor.NONE, HexColor.GREEN, HexColor.RED, HexColor.GREEN },
            { HexColor.NONE, HexColor.RED, HexColor.BLUE, HexColor.BLUE, HexColor.RED },
            { HexColor.GREEN, HexColor.BLUE, HexColor.BLUE, HexColor.BLUE, HexColor.GREEN },
            { HexColor.RED, HexColor.BLUE, HexColor.BLUE, HexColor.RED, HexColor.NONE },
            { HexColor.GREEN, HexColor.RED, HexColor.GREEN, HexColor.NONE, HexColor.NONE }
    });


    public static void main(String[] args) {
        System.out.println("SpinHex BFS Solver (refer to performance_analysis.md)");
        System.out.println("""
                Select a board configuration:
                \t1. Original Board (9 step solution, ~80s)
                \t2. Board closer to solution (7 step solution, ~25s)
                """);
        System.out.print("Enter your choice (1 or 2): ");
        int input = new java.util.Scanner(System.in).nextInt();
        var boardConfig = switch (input){
            case 1 -> originalBoardConfig;
            case 2 -> closerBoardConfig;
            default -> throw new IllegalStateException("Unexpected value: " + input);
        };

        System.out.println("""
                Select a search algorithm:
                \t1. Original BFS
                \t2. Modified BFS
                """);
        System.out.print("Enter your choice (1 or 2): ");
        input = new java.util.Scanner(System.in).nextInt();
        var startTime = System.currentTimeMillis();
        switch (input) {
            case 1:
                new BreadthFirstSearch<TwoPhaseAction<AxialPosition, Rotation>>()
                        .solveAndPrintSolution(new SpinHexModel(boardConfig.startingBoard, boardConfig.targetBoard));
                break;

            case 2:
                new BreadthFirstSearchModified<TwoPhaseAction<AxialPosition, Rotation>>()
                        .solveAndPrintSolution(new SpinHexModel(boardConfig.startingBoard, boardConfig.targetBoard));
            default:
                throw new IllegalStateException("Unexpected value: " + input);
        }
        var endTime = System.currentTimeMillis();
        System.out.println("Time taken: " + (endTime - startTime) + " ms");
    }

    record Preset(byte[][] startingBoard, byte[][] targetBoard){}
}

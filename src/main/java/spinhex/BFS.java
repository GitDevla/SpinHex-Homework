package spinhex;

import puzzle.solver.BreadthFirstSearch;
import solver.BreadthFirstSearchModified;
import spinhex.model.AxialPosition;
import spinhex.model.Rotation;
import spinhex.model.SpinHexModel;
import spinhex.model.TwoPhaseActionState.TwoPhaseAction;

public class BFS {
    public static void main(String[] args) {
        System.out.println("""
                SpinHex BFS Solver (refer to performance_analysis.md)
                \t1. Original BFS
                \t2. Modified BFS
                """);
        System.out.print("Enter your choice (1 or 2): ");
        int input = new java.util.Scanner(System.in).nextInt();
        var startTime = System.currentTimeMillis();
        switch (input) {
            case 1:
                new BreadthFirstSearch<TwoPhaseAction<AxialPosition, Rotation>>()
                        .solveAndPrintSolution(new SpinHexModel());
                break;

            case 2:
                new BreadthFirstSearchModified<TwoPhaseAction<AxialPosition, Rotation>>()
                        .solveAndPrintSolution(new SpinHexModel());
            default:
                System.out.println("Invalid input. Please enter 1 or 2.");
                break;
        }
        var endTime = System.currentTimeMillis();
        System.out.println("Time taken: " + (endTime - startTime) + " ms");
        System.out.println("Memory used: "
                + (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / (1024 * 1024) + " MB");
    }
}

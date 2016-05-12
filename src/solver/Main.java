package solver;

import java.util.ArrayList;
import java.util.Collections;
import java.util.PriorityQueue;
import java.util.Random;

import static solver.Grid.*;

public class Main {
    final static int X_SIZE = 19;
    final static int Y_SIZE = 14;
    final static int Z_SIZE = 7;

    public static void main(String[] args) {

        Grid grid = new Grid(Y_SIZE, X_SIZE, Z_SIZE);

        GridScore solution = generateSolution(grid);
        while (solution == null) {
            solution = generateSolution(grid);
        }
        System.out.println("Score " + solution.score);
        System.out.println("Initialising Iterative round");
        optimiseSolution(solution);
    }

    private static GridScore astar(GridScore currentGrid, int lineNumber, PoleCoordinates coordinates, Grid trialGrid) {

        ArrayList<ExpandGrid> memory = new ArrayList<>();
        PriorityQueue<ExpandGrid> gridQueue = new PriorityQueue<>();

        Net net = currentGrid.netDatabase.get(lineNumber);


        ExpandGrid firstLine = new ExpandGrid(trialGrid, lineNumber, coordinates.x1, coordinates.y1, coordinates.z1, 0, 0);

        gridQueue.add(firstLine);

        // uitbreden van de grid
        int counter = 0;
        while (!gridQueue.isEmpty() && counter < 3000) {
            ArrayList<ExpandGrid> allChildren = trialGrid.create_possible_lines(gridQueue.remove(), coordinates.x2, coordinates.y2, coordinates.z2);
            for (ExpandGrid childGrid : allChildren) {

                boolean exist = false;
                for (int i = 0; i < memory.size(); i++) {
                    int memoryx = memory.get(i).x;
                    int memoryy = memory.get(i).y;
                    int memoryz = memory.get(i).z;
                    int memorysteps = memory.get(i).steps;
                    int memoryestimate = memory.get(i).estimate;
                    if (childGrid.x == memoryx && childGrid.y == memoryy && childGrid.z == memoryz &&
                            childGrid.steps == memorysteps && childGrid.estimate == memoryestimate) {
                        exist = true;
                        break;
                    }
                }

                if (exist == false) {
                    counter++;
                    if (childGrid.estimate <= 1) {
                        return new GridScore(childGrid.grid, childGrid.steps, trialGrid.netDatabase);
                    }
                    gridQueue.add(childGrid);
                    memory.add(childGrid);
                }
            }
        }
        //System.out.println("Error: could not generate line " + lineNumber + ", " + net);
        return null;
    }


    private static int[] countGateOccurrence(ArrayList<Net> nets) {
        int[] gateOccurrence = new int[26];

        for (int i = 0; i < nets.size(); i++) {
            int gate1 = nets.get(i).gate1;
            int gate2 = nets.get(i).gate2;
            gateOccurrence[gate1]++;
            gateOccurrence[gate2]++;
        }
        return gateOccurrence;
    }

    private static GridScore generateSolution(Grid grid) {
        ArrayList<Net> nets = grid.netDatabase;
        Collections.shuffle(nets);
        int[] occ = countGateOccurrence(nets);
        for (int i = 0; i < occ.length; i++) {
            if (occ[i] > 5) {
                System.out.println("There are no solutions.");
                break;
            }
        }
        System.out.println("Calculating solution...");


        int totalScore = 0;
        GridScore currentGrid = new GridScore(grid, 0, nets);

        ArrayList<PoleCoordinates> pooolie = null;
        boolean error = true;
        currentGrid = new GridScore(grid, 0, nets);
        int totalPole = 0;
        while (error == true) {
            pooolie = new ArrayList<>();
            Collections.shuffle(nets);
            grid = new Grid(Y_SIZE, X_SIZE, Z_SIZE);
            currentGrid = new GridScore(grid, 0, nets);
            int layerNumber = 7;
            error = false;
            totalPole = 0;
            for (int lineNumber = 0; lineNumber < nets.size(); lineNumber++) {
                Net net1 = nets.get(lineNumber);

                int[] coordinates = currentGrid.grid.create_line(net1, layerNumber, lineNumber);
                if (coordinates[0] != -1) {
                    totalPole += coordinates[5];
                }
                if (coordinates[0] == -1) {
                    error = true;
                } else {
                    PoleCoordinates alpha = new PoleCoordinates(lineNumber, coordinates[0], coordinates[1], coordinates[4], coordinates[2], coordinates[3], coordinates[4]);
                    pooolie.add(alpha);

                    int devisionNumber = (nets.size() / Z_SIZE) + 1;
                    if (lineNumber % devisionNumber == 0 && layerNumber > 0 && lineNumber > 0)
                        layerNumber--;
                }
            }
        }

        System.out.println("Succesfully placed poles.");

        GridScore trialGrid = currentGrid;
        int lineNumber = 0;
        int counter = 0;
        int totalALineLength = 0;
        while (lineNumber < grid.netDatabase.size()) {

            // After 20 attempts the whole try is canceled and the program is restarted
            if (counter > 20) return null;
            for (lineNumber = 0; lineNumber < grid.netDatabase.size(); lineNumber++) {

                trialGrid = astar(currentGrid, pooolie.get(lineNumber).lineNum, pooolie.get(lineNumber), trialGrid.grid);

                if (trialGrid == null) {
                    lineNumber = 0;
                    trialGrid = currentGrid;
                    Collections.shuffle(pooolie);
                    counter++;
                    totalALineLength = 0;
                }
                totalALineLength += trialGrid.score;
            }
        }
        return new GridScore(trialGrid.grid, (totalALineLength + totalPole), trialGrid.netDatabase);
    }


    private static GridScore optimiseSolution(GridScore solution) {

        for(int lineNum = 0; lineNum < solution.netDatabase.size(); lineNum ++){
            solution = removeLine(solution, lineNum);
            solution.grid.printGrid();
            break;
        }


    return new GridScore(solution.grid, 0, solution.netDatabase);
    }


    private static GridScore removeLine(GridScore solution, int lineNum){

        int removeCount = 0;
        for (int i = 0; i <  solution.grid.grid[0][0].length; i++) {
            for (int k = 0; k < solution.grid.grid[0].length; k++) {
                for (int n = 0; n < solution.grid.grid.length; n++) {

                    String line = "L"+ lineNum;
                    if(solution.grid.grid[n][k][i] != null && solution.grid.grid[n][k][i].equals(line)){
                        solution.grid.grid[n][k][i] = null;
                        removeCount++;
                    }
                }
            }
        }
        System.out.println("L" + lineNum);
        return new GridScore(solution.grid, (solution.score - removeCount), solution.netDatabase);
    }

    
}





package solver;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.PriorityQueue;

public class Main {
    final static int X_SIZE = 19;
    final static int Y_SIZE = 14;
    final static int Z_SIZE = 7;

    public static void main(String[] args) {
        // initializing grid to work with
        Grid grid = new Grid(X_SIZE, Y_SIZE, Z_SIZE);


        long time1 = System.currentTimeMillis();


        if(isSolutionPossible(grid)) {
            // shows the theoretical minimumscore
            int minimumScore = grid.totalMinimumScore(grid.netDatabase);
            System.out.println("Theoretical minimum score: " + minimumScore);

            // generate a solution
            GridScore solution = generateSolution(grid);
            while (solution == null) {
                solution = generateSolution(grid);
            }
            solution.grid.printGrid();
            System.out.println("Total grid score " + solution.score);

            // Shoelace - iterative round
            System.out.println("Initializing Iterative round...");
            optimizeSolution(solution).grid.printGrid();
        }

        long time2 = System.currentTimeMillis();
        System.out.println("It took " + (time2 - time1) + " miliseconds.");
    }


    // astar search
    private static GridScore astar(GridScore currentGrid, int lineNumber, PoleCoordinates coordinates, Grid trialGrid) {

        // save the visited nodes
        ArrayList<ExpandGrid> memory = new ArrayList<>();

        // priority queue
        PriorityQueue<ExpandGrid> gridQueue = new PriorityQueue<>();

        // adds the first line piece to the queue
        gridQueue.add(new ExpandGrid(trialGrid, lineNumber, coordinates.x1, coordinates.y1, coordinates.z1, 0, 0));

        // counts the amount of grids that pass through the queue, that are not (yet) a solution
        int counter = 0;
        // while gridqueue is not empty and counter < state space, continue astar
        while (!gridQueue.isEmpty() && counter < Y_SIZE * X_SIZE * Z_SIZE) {
            ArrayList<ExpandGrid> allChildren = trialGrid.create_possible_lines(gridQueue.remove(), coordinates.x2, coordinates.y2, coordinates.z2);
            for (ExpandGrid childGrid : allChildren) {

                // checks if generated node already exist in memory
                boolean existInMemory = false;
                for (ExpandGrid aMemory : memory) {
                    int memory_x = aMemory.x;
                    int memory_y = aMemory.y;
                    int memory_z = aMemory.z;
                    int memory_steps = aMemory.steps;
                    int memory_estimate = aMemory.estimate;
                    if (childGrid.x == memory_x && childGrid.y == memory_y && childGrid.z == memory_z &&
                            childGrid.steps == memory_steps && childGrid.estimate == memory_estimate) {
                        existInMemory = true;
                        break;
                    }
                }

                // if node doesn't already exist in queue, add child to memory and queue
                if (!existInMemory) {
                    // if estimate <= 1, goal reached, return solution
                    if (childGrid.estimate <= 1) {
                        return new GridScore(childGrid.grid, childGrid.steps, trialGrid.netDatabase);
                    } else {
                        counter++;
                        gridQueue.add(childGrid);
                        memory.add(childGrid);
                    }
                }
            }
        }
        // If a line cannot be placed, return null;
        // System.out.println("Error: could not generate line " + lineNumber + ", " + net);
        return null;
    }

    // counts the occurrence of each gate in the netlist
    private static int[] countGateOccurrence(ArrayList<Net> nets, ArrayList<Gate> gates) {
        int[] gateOccurrence = new int[gates.size()];

        for (Net net : nets) {
            int gate1 = net.gate1.number;
            int gate2 = net.gate2.number;
            gateOccurrence[gate1]++;
            gateOccurrence[gate2]++;
        }
        return gateOccurrence;
    }


    // generates a solution
    private static GridScore generateSolution(Grid grid) {
        ArrayList<Net> nets = grid.netDatabase;

        // shuffle order of nets
        Collections.shuffle(nets);


        int totalScore = 0;
        GridScore currentGrid; // = new GridScore(grid, 0, nets);

        ArrayList<PoleCoordinates> poolCoordinates = null;

        // checks if an line could not be placed
        boolean error = true;

        currentGrid = new GridScore(grid, 0, nets);
        int totalPole = 0;

        // while poles could not be placed, try again in random order until poles are placed
        while (error) {
            poolCoordinates = new ArrayList<>();
            grid = new Grid(X_SIZE, Y_SIZE, Z_SIZE);
            Collections.shuffle(nets);
            currentGrid = new GridScore(grid, 0, nets);
            int layerNumber = Z_SIZE;
            error = false;
            totalPole = 0;

            for (int lineNumber = 0; lineNumber < nets.size(); lineNumber++) {
                Net net1 = nets.get(lineNumber);
                int[] coordinates = currentGrid.grid.create_poles(net1, layerNumber, lineNumber);
                if (coordinates[0] != -1) {
                    totalPole += coordinates[5];
                }
                if (coordinates[0] == -1) {
                    error = true;
                } else {
                    PoleCoordinates poleCoordinates = new PoleCoordinates(lineNumber, coordinates[0], coordinates[1],
                            coordinates[4], coordinates[2], coordinates[3], coordinates[4]);
                    poolCoordinates.add(poleCoordinates);
                    int devisionNumber = (nets.size() / Z_SIZE) + 1;
                    if (lineNumber % devisionNumber == 0 && layerNumber > 0 && lineNumber > 0)
                        layerNumber--;
                }
            }
        }
        //System.out.println("Succesfully placed poles.");

        // poles are placed, draw line between poles
        GridScore trialGrid = currentGrid;
        int lineNumber = 0;
        int counter = 0;
        int totalALineLength = 0;
        while (lineNumber < grid.netDatabase.size()) {
            for (lineNumber = 0; lineNumber < grid.netDatabase.size(); lineNumber++) {
                trialGrid = astar(currentGrid, poolCoordinates.get(lineNumber).lineNum, poolCoordinates.get(lineNumber), trialGrid.grid);
                if (trialGrid == null) {
                    lineNumber = -1;
                    trialGrid = currentGrid;
                    Collections.shuffle(poolCoordinates);
                    counter++;
                    totalScore += currentGrid.score;
                    if (counter > nets.size() / 4) return null;
                    totalALineLength = 0;
                }
            }
            totalALineLength += trialGrid.score;
        }
        // return solution
        return new GridScore(trialGrid.grid, (totalALineLength + totalPole), trialGrid.netDatabase);
    }


    // iterative shoelace method that erases a line and places it again with astar
    private static GridScore optimizeSolution(GridScore solution) {

        for(int lineNum = 0; lineNum < solution.netDatabase.size(); lineNum++){
            solution = removeLine(solution, lineNum);
            System.out.println("Score after removing " + lineNum + ": " + solution.score);
            //return astar(solution, lineNum, coordinates, solution.grid);
        }
        return new GridScore(solution.grid, 0, solution.netDatabase);
    }

    // removes a line
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
        //System.out.println("Removed: L" + lineNum);
        return new GridScore(solution.grid, (solution.score - removeCount), solution.netDatabase);
    }

    // calculates if solution is possible
    private static boolean isSolutionPossible(Grid grid) {
        ArrayList<Gate> gates = grid.gateDatabase;
        ArrayList<Net> nets = grid.netDatabase;

        int[] occ = countGateOccurrence(nets, gates);
        // when an gate occurs > 5 in the netlist, there is no solution
        for (int anOcc : occ) {
            if (anOcc > 5) {
                System.out.println("There is no solution.");
                return false;
            }
        }
        System.out.println("Calculating solution...");
        return true;
    }
}
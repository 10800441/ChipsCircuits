package solver;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.PriorityQueue;

public class Main {
    // Set dimension sizes of the grid
    final static int X_SIZE = 14;
    final static int Y_SIZE = 19;
    final static int Z_SIZE = 7;

    static int minimumScore;
    static long time1;
    static long time2;

    public static void main(String[] args) {
        // initializing grid to work with
        Grid grid = initializeGrid("src/print1Gates.txt", "src/print1Lines.txt");
        minimumScore = grid.totalMinimumScore(grid.netDatabase);

        // make a optimized solution
        GridScore optimizedSolution = makeOptimizedSolution(grid);

        // print messages for total line length and time of solution
        optimizedSolution.grid.printGrid();
        System.out.println("This solution has a total line length of " + optimizedSolution.score + ".");
        System.out.println("It took " + (time2 - time1) + " milliseconds to calculate this solution.");
    }

    // initialize grid to work with
    public static Grid initializeGrid(String gates, String lines) {
        ArrayList<Gate> gateDatabase = Grid.makeGateDatabase(gates);
        ArrayList<Net> netDatabase = Grid.makeNetDatabase(gateDatabase, lines);
        return new Grid(X_SIZE, Y_SIZE, Z_SIZE, gateDatabase, netDatabase);
    }


    private static GridScore makeOptimizedSolution(Grid grid){
        GridScore solution = null;
        if (isSolutionPossible(grid)) {
            time1 = System.currentTimeMillis();

            // generate a solution
            solution = generateSolution(grid);
            // while no solution is found, keep trying
            while (solution == null) {
                solution = generateSolution(grid);
            }

            // setting the current best score
            int currentBestScore = solution.score;

            // applying the shoelace method
            // one shoelace round applies the shoelace method to the whole netlist
            int shoelaceRound = 0;
            while (shoelaceRound <= 10){
                solution = optimizeSolution(solution);
                shoelaceRound++;
                if (solution.score + solution.netDatabase.size() < currentBestScore) {
                    currentBestScore = solution.score + solution.netDatabase.size();
                    shoelaceRound = 0;
                }
            }
            time2 = System.currentTimeMillis();
        }
        return solution;
    }



    // astar search
    private static GridScore astar(int lineNumber, int x1, int y1, int z1, int x2, int y2, int z2, GridScore trialGrid) {

        // save the visited nodes
        ArrayList<ExpandGrid> memory = new ArrayList<>();

        // priority queue
        PriorityQueue<ExpandGrid> gridQueue = new PriorityQueue<>();

        // adds the first line piece to the queue
        gridQueue.add(new ExpandGrid(trialGrid.grid, lineNumber, x1, y1, z1, 0, trialGrid.grid.manhattanDistance(x1, y1,
                x2, y2, z1, z2)));

        // counts the amount of grids that pass through the queue, that are not (yet) a solution
        // while gridqueue is not empty and counter < state space, continue astar
        while (!gridQueue.isEmpty()) {
            ArrayList<ExpandGrid> allChildren = trialGrid.grid.create_possible_lines(gridQueue.remove(), x2, y2, z2);
            for (ExpandGrid childGrid : allChildren) {

                // checks if generated node already exist in memory
                boolean existInMemory = false;
                for (ExpandGrid aMemory : memory) {
                    int memory_x = aMemory.x;
                    int memory_y = aMemory.y;
                    int memory_z = aMemory.z;
                    if (childGrid.x == memory_x && childGrid.y == memory_y && childGrid.z == memory_z) {
                        existInMemory = true;
                        break;
                    }
                }

                // if node doesn't already exist in queue, add child to memory and queue
                if (!existInMemory) {
                    // if estimate <= 1, goal reached, return solution
                    if (childGrid.estimate <= 1) {
                        return new GridScore(childGrid.grid, childGrid.steps + trialGrid.score, trialGrid.netDatabase);
                    } else {
                        gridQueue.add(childGrid);
                        memory.add(childGrid);
                    }
                }
            }
        }
        // If a line cannot be placed, return null;
        //System.out.println("Error: could not generate line " + lineNumber + ", " + net);
        return null;
    }


    // counts the occurrence of each gate in the netlist
    private static int[] countGateOccurrence(ArrayList<Net> nets, ArrayList<Gate> gates) {
        int[] gateOccurrence = new int[gates.size() + 1];

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
            grid = new Grid(X_SIZE, Y_SIZE, Z_SIZE, grid.gateDatabase, grid.netDatabase);
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
        // System.out.println("Succesfully placed poles.");
        // poles are placed, draw line between poles
        GridScore trialGrid = currentGrid;
        int lineNumber = 0;
        int counter = 0;
        int totalALineLength = 0;
        while (lineNumber < grid.netDatabase.size()) {
            for (lineNumber = 0; lineNumber < grid.netDatabase.size(); lineNumber++) {
                PoleCoordinates pole = poolCoordinates.get(lineNumber);
                trialGrid = astar(pole.lineNum, pole.x1, pole.y1, pole.z1, pole.x2, pole.y2, pole.z2,
                        trialGrid);
                if (trialGrid == null) {
                    //System.out.println("Error placing line " + lineNumber);
                    lineNumber = -1;
                    trialGrid = currentGrid;
                    Collections.shuffle(poolCoordinates);
                    counter++;
                    totalScore += currentGrid.score;
                    if (counter > nets.size() / 4) return null;
                    totalALineLength = 0;
                }// else {
                //    System.out.println("Succesfully placed line " + lineNumber);
                //}
            }
            totalALineLength += trialGrid.score;
        }
        // return solution
        // System.out.println("Succesfully placed lines.");
        return new GridScore(trialGrid.grid, (totalALineLength + totalPole), trialGrid.netDatabase);
    }


    // iterative shoelace method that erases a line and places it again with astar
    private static GridScore optimizeSolution(GridScore solution) {
        for (int lineNum = solution.netDatabase.size() - 1; lineNum >= 0; lineNum--) {
            GridScore solutionRemove = removeLine(solution, lineNum);

            Net net = solution.netDatabase.get(lineNum);
            solution = astar(lineNum, net.gate1.x, net.gate1.y, 0, net.gate2.x, net.gate2.y, 0, solutionRemove);

            if (solution == null) {
                solutionRemove.grid.printGrid();
            } else if (solution.score <= minimumScore) break;
        }
        return solution;
    }


    // removes a line
    private static GridScore removeLine(GridScore solution, int lineNum) {
        int removeCount = 0;
        for (int i = 0; i < solution.grid.grid[0][0].length; i++) {
            for (int k = 0; k < solution.grid.grid[0].length; k++) {
                for (int n = 0; n < solution.grid.grid.length; n++) {
                    String line = "L" + lineNum;
                    if (solution.grid.grid[n][k][i] != null && solution.grid.grid[n][k][i].equals(line)) {
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


    // writes the results to csv file
    public static void fileWriter(ArrayList<int[]> allScores, String adress) {
        try {
            FileWriter writer = new FileWriter(adress);

            writer.append("Theoretical minimum:");
            writer.append(',');
            writer.append("unoptimalised score:");
            writer.append(',');
            writer.append("optimalised score:");
            writer.append(',');
            writer.append("time:");
            writer.append('\n');

            for (int[] finalList : allScores) {
                writer.append("" + minimumScore);

                writer.append(',');
                writer.append("" + finalList[0]);

                writer.append(',');
                writer.append("" + finalList[1]);
                writer.append(',');
                writer.append("" + finalList[2]);

                writer.append('\n');
            }
            System.out.println("Output file complete!");
            writer.flush();
            writer.close();
        }


        catch(IOException e){
            e.printStackTrace();
        }
    }
}

/*
This code is an assignment of the course Heuristieken of the University of Amsterdam. It partly solves the case "Chips
'n Circuits" and is made by Marijn van Ham, Martijn Heijstek & Michelle Appel in 2016. The main goal of this program is
placing certain given connections between logical gates, as would be on a chip.

<Bladiebladiebla insert lulverhaal hier>

 */


package solver;

import java.util.ArrayList;
import java.util.Collections;
import java.util.PriorityQueue;

class Main {
    // Set dimension sizes of the grid
    final static int X_SIZE = 14;
    final static int Y_SIZE = 19;
    final static int Z_SIZE = 7;

    private static int minimumScore;
    private static long time1;
    private static long time2;

    public static void main(String[] args) {
        // Initializing grid to work with
        Grid grid = initializeGrid("src/print1Gates.txt", "src/print1Lines.txt");
        minimumScore = grid.totalMinimumScore(grid.netDatabase);

        // Make an optimized solution
        GridScore optimizedSolution = makeOptimizedSolution(grid);

        // Print messages for total line length and time of solution
        optimizedSolution.grid.printGrid();
        System.out.println("This solution has a total line length of " + optimizedSolution.score + ".");
        System.out.println("It took " + (time2 - time1) + " milliseconds to calculate this solution.");
    }


    // Initialize grid to work with
    // Makes the gatedatabase, netdatabase and makes the grid with gates added
    private static Grid initializeGrid(String gates, String lines) {
        ArrayList<Gate> gateDatabase = Grid.makeGateDatabase(gates);
        ArrayList<Net> netDatabase = Grid.makeNetDatabase(gateDatabase, lines);
        return new Grid(gateDatabase, netDatabase);
    }


    // Makes an optimized solution
    private static GridScore makeOptimizedSolution(Grid grid){
        GridScore solution = null;

        // If a solution is possible, generate a solution
        if (isSolutionPossible(grid)) {
            time1 = System.currentTimeMillis();

            // Generate a solution
            solution = generateSolution(grid);
            // While no solution is found, keep trying
            while (solution == null) {
                solution = generateSolution(grid);
            }

            // Setting the current best score
            int currentBestScore = solution.score;

            // The shoelace method
            // One shoelace round applies the shoelace method to the whole netlist
            int shoelaceRound = 0;
            // Hillclimber: if there is no improvement after 10 times, return solution
            while (shoelaceRound <= 10){
                // Applying the shoelace method
                solution = optimizeSolution(solution);
                shoelaceRound++;
                // When improvement is found, reset
                if (solution.score + solution.netDatabase.size() < currentBestScore) {
                    currentBestScore = solution.score + solution.netDatabase.size();
                    shoelaceRound = 0;
                }
            }
            time2 = System.currentTimeMillis();
        }
        return solution;
    }


    // Determines if a solution is possible
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


    // Generates a solution
    private static GridScore generateSolution(Grid grid) {
        ArrayList<Net> nets = grid.netDatabase;
        ArrayList<PoleCoordinates> poleCoordinatesList = null;
        GridScore currentGrid = null;
        int totalScore = 0;
        int totalPoleLength = 0;

        // Start with placing poles
        // Checks if an pole could not be placed
        boolean error = true;
        // While poles could not be placed, try again in random order until poles are placed
        while (error) {
            // Begin with empty grid
            grid = new Grid(grid.gateDatabase, grid.netDatabase);
            currentGrid = new GridScore(grid, 0, nets);

            // The pole coordinates will be stored here
            poleCoordinatesList = new ArrayList<>();

            // Shuffle netlist order
            Collections.shuffle(nets);

            error = false;
            totalPoleLength = 0;
            int layerNumber = Z_SIZE;
            for (int lineNumber = 0; lineNumber < nets.size(); lineNumber++) {
                Net net = nets.get(lineNumber);
                int[] coordinates = currentGrid.grid.create_poles(net, layerNumber, lineNumber);
                // If line could not be placed, there is an error
                if (coordinates[0] == -1) {
                    error = true;
                // If line could be placed, go on
                } else {
                    // Length of poles is added
                    totalPoleLength += coordinates[5];
                    // Pole coordinates are added to the storage
                    poleCoordinatesList.add(new PoleCoordinates(lineNumber, coordinates[0], coordinates[1],
                            coordinates[4], coordinates[2], coordinates[3], coordinates[4]));
                    // Equally distribute pole heights on grid
                    if (lineNumber % ((nets.size() / Z_SIZE) + 1) == 0 && layerNumber > 0 && lineNumber > 0)
                        layerNumber--;
                }
            }
        }

        // Poles are placed, place line between poles
        GridScore gridWithPoles = currentGrid;
        int lineNumber = 0;
        int counter = 0;
        int totalLineLength = 0;
        while (lineNumber < grid.netDatabase.size()) {
            for (lineNumber = 0; lineNumber < grid.netDatabase.size(); lineNumber++) {
                PoleCoordinates pole = poleCoordinatesList.get(lineNumber);
                gridWithPoles = astar(pole.lineNum, pole.x1, pole.y1, pole.z1, pole.x2, pole.y2, pole.z2,
                        gridWithPoles);
                // If line could not be placed
                if (gridWithPoles == null) {
                    lineNumber = -1;
                    gridWithPoles = currentGrid;
                    Collections.shuffle(poleCoordinatesList);
                    counter++;
                    totalScore += currentGrid.score;
                    // If counter limit is reached, return null
                    if (counter > nets.size() / 4) return null;
                    totalLineLength = 0;
                }
            }
            totalLineLength += gridWithPoles.score;
        }
        // Return solution
        return new GridScore(gridWithPoles.grid, (totalLineLength + totalPoleLength), gridWithPoles.netDatabase);
    }


    // Iterative shoelace method that erases a line and places it again with A*
    private static GridScore optimizeSolution(GridScore solution) {
        for (int lineNum = solution.netDatabase.size() - 1; lineNum >= 0; lineNum--) {
            GridScore solutionRemove = removeLine(solution, lineNum);

            Net net = solution != null ? solution.netDatabase.get(lineNum) : null;
            solution = astar(lineNum, net.gate1.x, net.gate1.y, 0, net.gate2.x, net.gate2.y, 0, solutionRemove);

            if (solution == null) {
                solutionRemove.grid.printGrid();
            } else if (solution.score <= minimumScore) break;
        }
        return solution;
    }


    // Removes a line
    // Sets all cells containing the line to null
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
        return new GridScore(solution.grid, (solution.score - removeCount), solution.netDatabase);
    }


    // A* search algorithm
    private static GridScore astar(int lineNumber, int x1, int y1, int z1, int x2, int y2, int z2, GridScore trialGrid) {

        // Save the visited nodes
        ArrayList<ExpandGrid> memory = new ArrayList<>();

        // Priority queue
        PriorityQueue<ExpandGrid> gridQueue = new PriorityQueue<>();

        // Adds the first line piece to the queue
        gridQueue.add(new ExpandGrid(trialGrid.grid, lineNumber, x1, y1, z1, 0, trialGrid.grid.manhattanDistance(x1, y1,
                x2, y2, z1, z2)));

        // Counts the amount of grids that pass through the queue, that are not (yet) a solution
        // While gridqueue is not empty, continue astar search
        while (!gridQueue.isEmpty()) {
            ArrayList<ExpandGrid> allChildren = trialGrid.grid.create_possible_lines(gridQueue.remove(), x2, y2, z2);
            for (ExpandGrid childGrid : allChildren) {

                // Checks if generated node already exist in memory
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

                // If node doesn't already exist in queue, add to memory and queue
                if (!existInMemory) {
                    // If estimate <= 1: goal reached, return solution
                    if (childGrid.estimate <= 1) {
                        return new GridScore(childGrid.grid, childGrid.steps + trialGrid.score, trialGrid.netDatabase);
                    } else {
                        gridQueue.add(childGrid);
                        memory.add(childGrid);
                    }
                }
            }
        }
        // If a line cannot be placed, return null
        return null;
    }


    // Counts the occurrence of each gate in the netlist
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
}

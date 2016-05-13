

package solver;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.PriorityQueue;

public class Grid {
    String[][][] grid;
    ArrayList<Gate> gateDatabase;
    ArrayList<Net> netDatabase = new ArrayList<>();


    public Grid(int width, int height, int depth, ArrayList<Gate> gateDatabase, ArrayList<Net> netDatabase) {
        grid = new String[width][height][depth];
        this.gateDatabase = gateDatabase;
        for (int i = 0; i < gateDatabase.size(); i++) {
            Gate gate = gateDatabase.get(i);
            addGate(gate.gateNumber, gate.x, gate.y);
        }
        this.netDatabase = netDatabase;


    }


    public Grid(int width, int height, int depth) {
        this(width, height, depth, makeGateDatabase(), makeNetDatabase());
    }

    // copy constructor to make copies of the current grid
    public Grid(Grid oldGrid) {

        grid = new String[oldGrid.grid.length][oldGrid.grid[0].length][oldGrid.grid[0][0].length];
        this.gateDatabase = oldGrid.gateDatabase;
        this.netDatabase = oldGrid.netDatabase;

        for (int i = 0; i < oldGrid.grid[0][0].length; i++) {
            for (int k = 0; k < oldGrid.grid[0].length; k++) {
                for (int n = 0; n < oldGrid.grid.length; n++) {

                    grid[n][k][i] = oldGrid.grid[n][k][i];
                }
            }
        }

    }


    public void printGrid() {
        for (int j = 0; j < grid[0][0].length; j++) {
            System.out.println("");
            int layer = j + 1;

            System.out.println("");
            System.out.println("Grid layer: " + layer);
            for (int i = 1; i < grid.length; i++) {   //creation of height Y
                System.out.println("");
                for (int k = 1; k < grid[0].length; k++) {    //creation of width X
                    if (grid[i][k][j] == null) {
                        System.out.print(" . ");
                    } else {

                        String gridContent = grid[i][k][j];
                        char identifier = gridContent.charAt(0);

                        // Labeling bij het printen

                        if (identifier == 'G' && grid[i][k][j].length() == 3) {
                            System.out.print("\033[31m");
                            System.out.print(grid[i][k][j]);
                            System.out.print("\033[0m");
                        } else if (identifier == 'G') {
                            System.out.print("\033[31m");
                            System.out.print(" " + grid[i][k][j]);
                            System.out.print("\033[0m");
                        } else if (identifier == 'L' && grid[i][k][j].length() == 3) {
                            System.out.print(grid[i][k][j]);
                        } else if (identifier == 'L') {
                            System.out.print(" " + grid[i][k][j]);
                        }
                    }
                }
            }
        }
        System.out.println("");
    }

    public void addGate(int number, int y_coordinate, int x_coordinate) {
        grid[x_coordinate][y_coordinate][0] = "G" + number;
    }

    public void addLine(int number, int x, int y, int z) {
        grid[x][y][z] = "L" + number;
    }

    // provides an expandgrid for the create_possible_lines method
    public ExpandGrid addLine(Grid input_grid, int number, int x, int y, int z, int steps, int x2, int y2, int z2) {
        Grid copy_grid = new Grid(input_grid);
        copy_grid.addLine(number, x, y, z);
        int estimate = manhattanDistance(x, y, x2, y2, z, z2);
        return new ExpandGrid(copy_grid, number, x, y, z, (steps + 1), estimate);
    }


    public ArrayList create_possible_lines(ExpandGrid inputExpandGrid, int x2, int y2, int z2) {
        Grid inputGrid = inputExpandGrid.grid;
        int number = inputExpandGrid.number;
        int x = inputExpandGrid.x;
        int y = inputExpandGrid.y;
        int z = inputExpandGrid.z;
        int steps = inputExpandGrid.steps;

        ArrayList<ExpandGrid> list = new ArrayList<>();



        if (x + 1 > 0 && x + 1 < grid.length && grid[x + 1][y][z] == null) {
            list.add(addLine(inputGrid, number, x + 1, y, z, steps, x2, y2, z2));
        }
        if (x - 1 > 0 && x - 1 < grid.length && grid[x - 1][y][z] == null) {
            list.add(addLine(inputGrid, number, x - 1, y, z, steps, x2, y2, z2));
        }
        if (y + 1 > 0 && y + 1 < grid[0].length && grid[x][y + 1][z] == null) {
            list.add(addLine(inputGrid, number, x, y + 1, z, steps, x2, y2, z2));
        }
        if (y - 1 > 0 && y - 1 < grid[0].length && grid[x][y - 1][z] == null) {
            list.add(addLine(inputGrid, number, x, y - 1, z, steps, x2, y2, z2));
        }
        if (z + 1 > 0 && z + 1 < grid[0][0].length && grid[x][y][z + 1] == null) {
            list.add(addLine(inputGrid, number, x, y, z + 1, steps, x2, y2, z2));
        }
        if (z - 1 > 0 && z - 1 < grid[0][0].length && grid[x][y][z - 1] == null) {
            list.add(addLine(inputGrid, number, x, y, z - 1, steps, x2, y2, z2));
        }
        return list;
    }


    public int[] create_line(Net net, int layer, int lineNumber) {
        int lineLength1 = 0;
        int lineLength2 = 0;
        int gate1 = net.gate1;
        int gate2 = net.gate2;

        int gate1X = gateDatabase.get(gate1).x;
        int gate1Y =gateDatabase.get(gate1).y;

        int gate2X = gateDatabase.get(gate2).x;
        int gate2Y = gateDatabase.get(gate2).y;

        int p1 = 1;
        if (this.grid[gate1X][gate1Y][1] != null) {
            p1 = 0;
            if (this.grid[gate1X + 1][gate1Y][0] == null) {
                gate1X = gate1X + 1;
            } else if (this.grid[gate1X - 1][gate1Y][0] == null) {
                gate1X = gate1X - 1;
            } else if (this.grid[gate1X][gate1Y + 1][0] == null) {
                gate1Y = gate1Y + 1;
            } else if (this.grid[gate1X][gate1Y - 1][0] == null) {
                gate1Y = gate1Y - 1;
            } else {
                //System.out.println("Error: Could not place pole " + lineNumber);
                int[] error = {-1, -1, -1, -1, -1};
                return error;
            }
        }

        int p2 = 1;
        if (this.grid[gate2X][gate2Y][1] != null) {
            p2 = 0;
            if (this.grid[gate2X + 1][gate2Y][0] == null) {
                gate2X = gate2X + 1;
            } else if (this.grid[gate2X - 1][gate2Y][0] == null) {
                gate2X = gate2X - 1;
            } else if (this.grid[gate2X][gate2Y + 1][0] == null) {
                gate2Y = gate2Y + 1;
            } else if (this.grid[gate2X][gate2Y - 1][0] == null) {
                gate2Y = gate2Y - 1;
            } else {
                int[] error = {-1, -1, -1, -1, -1};
                return error;
            }

        }

        int z;
        for(z = p1; z < layer; z++) {
            this.addLine(lineNumber, gate1X, gate1Y, z);
            lineLength1 = layer-p1;
        }
        for(z = p2; z < layer; z++) {
            this.addLine(lineNumber, gate2X, gate2Y, z);
            lineLength2 = layer-p2;
        }

        int[] coordinates = {gate1X, gate1Y, gate2X, gate2Y, z-1,  (lineLength1+lineLength2)};
        return coordinates;
    }


    public static ArrayList<Gate> makeGateDatabase() {
        ArrayList<Gate> gateDatabase = new ArrayList<>();
        try {
            BufferedReader rd = new BufferedReader(new FileReader("src/print1Gates.txt"));
            String line;
            while (true) {

                line = rd.readLine();
                if (line == null) break;
                String[] words = line.split(",");
                Gate gate = new Gate(Integer.valueOf(words[0]), Integer.valueOf(words[1]), Integer.valueOf(words[2]));
gateDatabase.add(gate);
            }
            rd.close();
        } catch (IOException ex) {
            System.err.println("Error: " + ex);
        }

        return gateDatabase;
    }

    // Read in the net database from the file "print1Lines.txt"
    public static ArrayList<Net> makeNetDatabase() {
        ArrayList<Net> netDatabase = new ArrayList<>();
        try {
            BufferedReader rd = new BufferedReader(new FileReader("src/print1Lines.txt"));
            String line;
            while (true) {
                line = rd.readLine();
                if (line == null) break;
                String[] words = line.split(",");

                Net net = new Net(Integer.valueOf(words[0])+1, Integer.valueOf(words[1])+1);
                netDatabase.add(net);

            }
            rd.close();
        } catch (IOException ex) {
            System.err.println("Error: " + ex);
        }
        return netDatabase;
    }


    public int totalMinimumScore(ArrayList<Net> nets) {
        int score = 0;
        for(int i = 0; i < nets.size(); i++) {
            int gate1 = nets.get(i).gate1;
            int gate2 = nets.get(i).gate2;

            score += manhattanDistance(gateDatabase.get(gate1).x,
                                        gateDatabase.get(gate1).y,
                                        gateDatabase.get(gate2).x,
                                        gateDatabase.get(gate2).y, 0, 0);
        }
        return score;
    }
    public int manhattanDistance(int x1, int y1, int x2, int y2, int z1, int z2) {
        return Math.abs(x2 - x1) + Math.abs(y2 - y1) + Math.abs(z1 - z2);
    }
}

package solver;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Grid {
    String[][][] grid;
    ArrayList<Gate> gateDatabase;
    ArrayList<Net> netDatabase;

    // supertype constructor
    public Grid(int width, int height, int depth, ArrayList<Gate> gateDatabase, ArrayList<Net> netDatabase) {
        grid = new String[width][height][depth];
        this.gateDatabase = gateDatabase;
        this.netDatabase = netDatabase;

        for (int i = 0; i < gateDatabase.size(); i++) {
            addGate(i, gateDatabase.get(i).y, gateDatabase.get(i).x);
        }
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



    // prints a given grid
    public void printGrid() {
        // creation of layers Z
        for (int d = 0; d < grid[0][0].length; d++) {
            System.out.println("");
            int layer = d + 1;

            System.out.println("");
            System.out.println("Grid layer: " + layer);
            //creation of height Y
            for (int h = 1; h < grid.length; h++) {
                System.out.println("");
                //creation of width X
                for (int w = 1; w < grid[0].length; w++) {
                    if (grid[h][w][d] == null) {
                        System.out.print(" . ");
                    } else {
                        String gridContent = grid[h][w][d];
                        char identifier = gridContent.charAt(0);

                        int n = 0;
                        if(grid[h][w][d].length() == 3) {
                            n = grid[h][w][d].charAt(2) % 5;
                        } else if (grid[h][w][d].length() == 2) {
                            n = grid[h][w][d].charAt(1)%5;
                        }
                        if(n == 0) n = 6;

                        // Labeling bij het printen
                        if (identifier == 'G' && grid[h][w][d].length() == 3) {
                            System.out.print("\033[47m");
                            System.out.print(grid[h][w][d]);
                            System.out.print("\033[0m");
                        } else if (identifier == 'G') {
                            System.out.print("\033[47m");
                            System.out.print(grid[h][w][d]);
                            System.out.print("\033[0m");
                            System.out.print(" ");
                        } else if (identifier == 'L' && grid[h][w][d].length() == 3) {
                            System.out.print("\033[3"+ n + "m");
                            System.out.print(grid[h][w][d]);
                            System.out.print("\033[0m");
                        } else if (identifier == 'L') {
                            System.out.print("\033[3"+ n + "m");
                            System.out.print(grid[h][w][d]);
                            System.out.print("\033[0m");
                            System.out.print(" ");
                        }
                    }
                }
            }
        }
        System.out.println("");
    }
    // adds a gate to the grid
    public void addGate(int number, int y_coordinate, int x_coordinate) {
        grid[x_coordinate][y_coordinate][0] = "G" + number;
    }

    // adds a line piece to the grid
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

    // creates all possible line pieces from given coordinates
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
        if (z + 1 >= 0 && z + 1 < grid[0][0].length && grid[x][y][z + 1] == null) {
            list.add(addLine(inputGrid, number, x, y, z + 1, steps, x2, y2, z2));
        }
        if (z - 1 >= 0 && z - 1 < grid[0][0].length && grid[x][y][z - 1] == null) {
            list.add(addLine(inputGrid, number, x, y, z - 1, steps, x2, y2, z2));
        }
        return list;
    }

    // creates poles
    public int[] create_poles(Net net, int layer, int lineNumber) {
        int lineLength1 = 0;
        int lineLength2 = 0;

        int gate1X = net.gate1.x;
        int gate1Y = net.gate1.y;

        int gate2X = net.gate2.x;
        int gate2Y = net.gate2.y;

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
                return new int[]{-1, -1, -1, -1, -1};
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
                return new int[]{-1, -1, -1, -1, -1};
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

        return new int[]{gate1X, gate1Y, gate2X, gate2Y, z-1, (lineLength1+lineLength2)};
    }

    // Read in the net database from the file "printGates.txt"
    public static ArrayList<Gate> makeGateDatabase(String name) {
        ArrayList<Gate> gateDatabase = new ArrayList<>();
        try {
            BufferedReader rd = new BufferedReader(new FileReader(name));
            String line;
            while (true) {
                line = rd.readLine();
                if (line == null) break;
                String[] words = line.split(",");

                int lineNumber = Integer.valueOf(words[0]);
                int x = Integer.valueOf(words[1]);
                int y = Integer.valueOf(words[2]);

                gateDatabase.add(new Gate(lineNumber, x, y, 0));
            }
            rd.close();
        } catch (IOException ex) {
            System.err.println("Error: " + ex);
        }
        return gateDatabase;
    }

    // Read in the net database from the file "print1Lines.txt"
    public static ArrayList<Net> makeNetDatabase(ArrayList<Gate> gates, String name) {
        ArrayList<Net> netDatabase = new ArrayList<>();
        try {
            BufferedReader rd = new BufferedReader(new FileReader(name));
            String line;
            while (true) {
                line = rd.readLine();
                if (line == null) break;
                String[] words = line.split(",");

                int gateNumber1 = Integer.valueOf(words[0]);
                int gateNumber2 = Integer.valueOf(words[1]);

                Gate gate1 = gates.get(gateNumber1);
                Gate gate2 = gates.get(gateNumber2);

                Net net = new Net(gate1, gate2);
                netDatabase.add(net);
            }
            rd.close();
        } catch (IOException ex) {
            System.err.println("Error: " + ex);
        }
        return netDatabase;
    }

    // calculates a minimumscore given a netDatabase
    public int totalMinimumScore(ArrayList<Net> nets) {
        int score = 0;
        for(int i = 0; i < nets.size(); i++) {

            int gate1X = netDatabase.get(i).gate1.x;
            int gate1Y = netDatabase.get(i).gate1.y;

            int gate2X = netDatabase.get(i).gate2.x;
            int gate2Y = netDatabase.get(i).gate2.y;

            score += manhattanDistance(gate1X, gate1Y, gate2X, gate2Y, 0, 0);
        }
        return score;
    }

    // calculates the manhattanDistance between 2 coordinates
    public int manhattanDistance(int x1, int y1, int x2, int y2, int z1, int z2) {
        return Math.abs(x2 - x1) + Math.abs(y2 - y1) + Math.abs(z1 - z2);
    }
}
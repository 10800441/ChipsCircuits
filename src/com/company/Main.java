package com.company;



public class Main {

    private static int x_SIZE = 4;
    private static int y_SIZE = 5;

    public static void main(String[] args) {

        String[][] grid = new String[x_SIZE][y_SIZE];
        grid = placeNodes(grid);
        grid = placeLines(grid);
         printGrid(grid);


    }
    public static String[][] placeNodes(String[][] grid){
       int x = 2;
        int y = 3;
            grid[x][y] = "node";
        return  grid;
    }

    public static String[][] placeLines(String[][] grid){
        int x = 2;
        int y = 2;
        grid[x][y] = "line";
        return  grid;
    }


    private static void printGrid(String[][] grid) {
        for(int i = 0; i < grid.length; i++){   //creation of height
            System.out.println("");

            for(int k = 0; k < grid[1].length; k++){    //creation of width
                if(grid[i][k] == null) {
                    System.out.print(" . ");
                } else if (grid[i][k] == "line"){
                    System.out.print(" * ");
                } else if (grid[i][k] == "node") {
                    System.out.print(" O ");
                }
            }

        }

    }

    private static void findLine(int x1, int y1, int x2, int y2) {
        String[][] line =  new String[x_SIZE][y_SIZE];

        if(x1 == x2) {
            if(y1 < y2)
            for(int i = y1 + 1; i < y2; i++ ) {
                line[x1][i] = "line";
            }
        }
    }

}

package com.company;



public class Main {


    public static void main(String[] args) {
        int x_SIZE = 4;
        int y_SIZE = 5;
        String[][] grid = new String[x_SIZE][y_SIZE];
        grid = placeNodes(grid);
         printGrid(grid);


    }
    private static String[][] placeNodes(String[][] grid){
       int x = 2;
        int y = 3;
            grid[x][y] = "knode";
        return  grid;
    }


    private static void printGrid(String[][] grid) {
        for(int i = 0; i < grid.length; i++){   //creation of height
            System.out.println("");

            for(int k = 0; k < grid[1].length; k++){    //creation of width
                if(grid[0][k] == null); System.out.print(" 0 ");

            }

        }

    }
}

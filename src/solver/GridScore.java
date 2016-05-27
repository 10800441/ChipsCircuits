/*
    Chips 'n Circuits
    Marijn van Ham, Martijn Heijstek, Michelle Appel
    University of Amsterdam
    27/05/2016

    Class GridScore.java
    This class is used inside the hillclimber.
 */
package solver;

import java.util.ArrayList;

class GridScore{
    final Grid grid;
    final int score;
    final ArrayList<Net> netDatabase;

    public GridScore(Grid grid, int score, ArrayList<Net> netDatabase) {
        this.grid = grid;
        this.score = score;
        this.netDatabase = netDatabase;
    }
    public String toString(){
        return "Grid with a total score of: " + score;

    }

}
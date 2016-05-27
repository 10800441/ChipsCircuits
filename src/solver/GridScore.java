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
        return "Net: " + grid + " " + score;

    }

}
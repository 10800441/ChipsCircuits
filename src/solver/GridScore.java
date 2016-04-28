package solver;

import java.util.ArrayList;

public class GridScore{

    final int score;
    ArrayList<Net> netDatabase;

    public GridScore(int score, ArrayList<Net> netDatabase) {

        this.score = score;
        this.netDatabase = netDatabase;
    }
    public String toString(){
        return "Net:   " + score;

    }

}
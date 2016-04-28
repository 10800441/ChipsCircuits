package solver;

import java.util.ArrayList;

public class MakeItWork{
    final Grid grid1;
    final int x1;
    final int x2;
    final int y1;
    final int y2;

    public MakeItWork(Grid grid1, int x1, int x2, int y1, int y2) {
        this.grid1 = grid1;
        this.x1 = x1;
        this.x2 = x2;
        this.y1 = y1;
        this.y2 = y2;
    }
    public String toString(){
        return "Net: " + grid1 + " " ;

    }

}
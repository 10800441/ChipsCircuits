/*
    Chips 'n Circuits
    Marijn van Ham, Martijn Heijstek, Michelle Appel
    University of Amsterdam
    27/05/2016

    Class ExpandGrid.java
    This class is used inside A* for the expansion of lines.
 */

package solver;

class ExpandGrid implements Comparable{
    final Grid grid;
    final int number;
    final int x;
    final int y;
    final int z;
    final int steps;
    final int estimate;


    public ExpandGrid(Grid grid, int number, int x, int y, int z, int steps, int estimate) {
            this.grid = grid;
        this.number = number;
        this.x = x;
        this.y = y;
        this.z = z;
        this.steps = steps;
        this.estimate = estimate;
    }
    public String toString(){
        return "Expand line: " + number + " from points x: " + x + " y: " + y + " z: " + z +
                ", with total estimate: " + (estimate+steps);

    }

    @Override
    public int compareTo(Object o) {
        ExpandGrid other = (ExpandGrid) o;
        if(this.steps + this.estimate < other.steps + other.estimate) return -1;
        if(this.steps + this.estimate > other.steps + other.estimate) return 1;
        return 0;
    }
}

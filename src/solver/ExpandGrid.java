package solver;

public class ExpandGrid implements Comparable{
    final int number;
    final int x;
    final int y;
    final int z;
    final int steps;
    final int estimate;


    public ExpandGrid( int number, int x, int y, int z, int steps, int estimate) {

        this.number = number;
        this.x = x;
        this.y = y;
        this.z = z;
        this.steps = steps;
        this.estimate = estimate;
    }
    public String toString(){
        return "CurrentGrid: " + number + " x: " + z + " y: " + y + " z: " + z;





    }

    @Override
    public int compareTo(Object o) {
        ExpandGrid other = (ExpandGrid) o;
        if(this.steps + this.estimate < other.steps + other.estimate) return -1;
        if((this.steps + this.estimate == other.steps + other.estimate) &&(this.estimate < other.estimate)) return -1;
        if(this.steps + this.estimate > other.steps + other.estimate) return 1;
        return 0;



    }
}

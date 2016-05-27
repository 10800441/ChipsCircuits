package solver;

class Gate {
    final int number;
    final int x;
    final int y;

    public Gate(int number, int x, int y) {
        this.number = number;
        this.x = x;
        this.y = y;
    }

    public String toString(){
        return "" + (number - 1);
    }

}
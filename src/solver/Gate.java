/*
    Chips 'n Circuits
    Marijn van Ham, Martijn Heijstek, Michelle Appel
    University of Amsterdam
    27/05/2016

    Class Gate.java
    This class contains the specifics from a gate.
 */
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
        return "Gate number: " + (number - 1);
    }

}
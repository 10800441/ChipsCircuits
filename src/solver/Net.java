/*
    Chips 'n Circuits
    Marijn van Ham, Martijn Heijstek, Michelle Appel
    University of Amsterdam
    27/05/2016

    Class Net.java
    This class contains a pair of gates that has to be connected.
 */
package solver;

class Net {
    final Gate gate1;
    final Gate gate2;


    public Net(Gate gate1, Gate gate2) {
        this.gate1 = gate1;
        this.gate2 = gate2;
    }
    public String toString(){
        return "Net with start gate: " + gate1 + " and end gate: " + gate2;

    }

}

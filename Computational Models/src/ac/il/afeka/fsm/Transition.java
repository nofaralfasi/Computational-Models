package ac.il.afeka.fsm;

import ac.il.afeka.fsm.Alphabet;
import ac.il.afeka.fsm.State;
import java.io.PrintStream;

public class Transition
implements Comparable<Transition> {
    private State fromState;
    private Character symbol;
    private State toState;

    public Transition(State fromState, Character symbol, State toState) {
        this.fromState = fromState;
        this.symbol = symbol;
        this.toState = toState;
    }

    public State fromState() {
        return this.fromState;
    }

    public Character symbol() {
        return this.symbol;
    }

    public State toState() {
        return this.toState;
    }

    public void prettyPrint(PrintStream out) {
        out.print("(");
        this.fromState.prettyPrint(out);
        out.print(", ");
        if (this.symbol == Alphabet.EPSILON) {
            out.print("\u03b5");
        } else {
            out.print(this.symbol);
        }
        out.print(", ");
        this.toState.prettyPrint(out);
        out.print(")");
    }

    public String encode() {
        return String.valueOf(this.fromState.encode()) + "," + (this.symbol == Alphabet.EPSILON ? "" : this.symbol) + "," + this.toState.encode();
    }

    @Override
    public int compareTo(Transition other) {
        int result = this.fromState.compareTo(other.fromState);
        if (result != 0) {
            return result;
        }
        result = this.symbol.compareTo(other.symbol);
        if (result != 0) {
            return result;
        }
        return this.toState.compareTo(other.toState);
    }

    public boolean equalsFromStateAndSymbol(Transition other) {
        return this.fromState.equals(other.fromState) && this.symbol.equals(other.symbol);
    }
}


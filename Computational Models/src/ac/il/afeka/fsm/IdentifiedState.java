package ac.il.afeka.fsm;

import ac.il.afeka.fsm.State;
import java.io.PrintStream;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class IdentifiedState
extends State {
    private Integer id;

    public IdentifiedState(Integer i) {
        this.id = i;
    }

    public static Set<Integer> parseStateIdList(String encoding) {
        Scanner scanner = new Scanner(encoding);
        HashSet<Integer> ids = new HashSet<Integer>();
        while (scanner.hasNext()) {
            ids.add(scanner.nextInt());
        }
        scanner.close();
        return ids;
    }

    @Override
    public void prettyPrint(PrintStream out) {
        out.print(this.id);
    }

    @Override
    public String toString() {
        return "id=" + this.id;
    }

    @Override
    public String encode() {
        return "" + this.id;
    }

    public int hashCode() {
        int prime = 31;
        int result = 1;
        result = prime * result + (this.id == null ? 0 : this.id.hashCode());
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        IdentifiedState other = (IdentifiedState)obj;
        return !(this.id == null ? other.id != null : !this.id.equals(other.id));
    }

    @Override
    public int compareTo(State other) {
        return this.id.compareTo(((IdentifiedState)other).id);
    }
}


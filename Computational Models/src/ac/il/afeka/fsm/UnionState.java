package ac.il.afeka.fsm;

import ac.il.afeka.fsm.State;
import java.io.PrintStream;
import java.util.HashSet;
import java.util.Set;

public class UnionState extends State {
	private Set<State> uSet;

	public UnionState(Set<State> set) {
		this.uSet = new HashSet<State>(set);
	}

	public UnionState() {
		this.uSet = new HashSet<State>();
	}

	public void add(State s) {
		this.uSet.add(s);
	}

	public void addAll(UnionState us) {
		this.uSet.addAll(us.uSet);
	}

	public Set<State> getuSet() {
		return this.uSet;
	}

	@Override
	public void prettyPrint(PrintStream out) {
		out.print(this.uSet);
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
		UnionState other = (UnionState) obj;
		return !(this.uSet == null ? other.uSet != null
				: !this.uSet.containsAll(other.uSet) || !other.uSet.containsAll(this.uSet));
	}

	@Override
	public int compareTo(State other) {
		return 1;
	}

	@Override
	public String toString() {
		return "id = {" + this.uSet.toString() + "}";
	}

	@Override
	public String encode() {
		return "id = {" + this.uSet.toString() + "}";
	}
}

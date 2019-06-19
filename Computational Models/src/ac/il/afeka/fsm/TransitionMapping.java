package ac.il.afeka.fsm;

import ac.il.afeka.fsm.Alphabet;
import ac.il.afeka.fsm.State;
import ac.il.afeka.fsm.Transition;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;

public abstract class TransitionMapping {
	public abstract Set<Transition> transitions();

	public abstract String prettyName();

	public void prettyPrint(PrintStream out) {
		out.print("{");
		Iterator<Transition> p = this.transitions().iterator();
		if (p.hasNext()) {
			p.next().prettyPrint(out);
		}
		while (p.hasNext()) {
			out.print(", ");
			p.next().prettyPrint(out);
		}
		out.print("}");
	}

	public String encode() {
		String encoding = "";
		ArrayList<Transition> transitionsList = new ArrayList<Transition>(this.transitions());
		Collections.sort(transitionsList);
		Iterator<Transition> p = transitionsList.iterator();
		if (p.hasNext()) {
			encoding = String.valueOf(encoding) + ((Transition) p.next()).encode();
		}
		while (p.hasNext()) {
			encoding = String.valueOf(encoding) + ";" + ((Transition) p.next()).encode();
		}
		return encoding;
	}

	public abstract Set<State> at(State var1, Character var2);

	public void verify(Set<State> states, Alphabet alphabet) throws Exception {
		for (Transition t : this.transitions()) {
			if (!states.contains(t.fromState())) {
				throw new Exception("Transition mapping contains a state (id " + t.fromState()
						+ ") that is not a part of the state machine.");
			}
			if (t.symbol() != Alphabet.EPSILON && !alphabet.contains(t.symbol())) {
				throw new Exception(
						"Transition contains symbol " + t.symbol() + " that is not a part of the machine's alphabet");
			}
			if (states.contains(t.toState()))
				continue;
			throw new Exception("Transition mapping contains a state (id " + t.toState()
					+ ") that is not a part of the state machine.");
		}
	}

	public int getNumOfTransactions() {
		return this.transitions().size();
	}

	public Set<Transition> getTransactions() {
		return this.transitions();
	}
}

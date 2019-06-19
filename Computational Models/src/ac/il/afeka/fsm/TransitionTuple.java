package ac.il.afeka.fsm;

import ac.il.afeka.fsm.Alphabet;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class TransitionTuple {
	public Integer fromStateId;
	public Character symbol;
	public Integer toStateId;

	public TransitionTuple(Integer fromStateId, Character symbol, Integer toStateId) {
		this.fromStateId = fromStateId;
		this.symbol = symbol;
		this.toStateId = toStateId;
	}

	public static TransitionTuple parseTuple(String encoding) {
		Scanner scanner = new Scanner(encoding);
		scanner.useDelimiter("\\s*,\\s*");
		Integer fromStateId = scanner.nextInt();
		String symbolOrNothing = scanner.next();
		Character symbol = symbolOrNothing.length() == 0 ? Alphabet.EPSILON
				: Character.valueOf(symbolOrNothing.charAt(0));
		Integer toStateId = scanner.nextInt();
		scanner.close();
		return new TransitionTuple(fromStateId, symbol, toStateId);
	}

	public static Set<TransitionTuple> parseTupleList(String encoding) {
		Scanner scanner = new Scanner(encoding);
		scanner.useDelimiter("\\s*;\\s*");
		HashSet<TransitionTuple> tuples = new HashSet<TransitionTuple>();
		while (scanner.hasNext()) {
			tuples.add(TransitionTuple.parseTuple(scanner.next()));
		}
		scanner.close();
		return tuples;
	}

	public Integer fromStateId() {
		return this.fromStateId;
	}

	public Character symbol() {
		return this.symbol;
	}

	public Integer toStateId() {
		return this.toStateId;
	}
}

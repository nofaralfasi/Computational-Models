package ac.il.afeka.fsm;

import ac.il.afeka.fsm.Alphabet;
import ac.il.afeka.fsm.DFSM;
import ac.il.afeka.fsm.IdentifiedState;
import ac.il.afeka.fsm.State;
import ac.il.afeka.fsm.Transition;
import ac.il.afeka.fsm.TransitionMapping;
import ac.il.afeka.fsm.TransitionRelation;
import ac.il.afeka.fsm.TransitionTuple;
import ac.il.afeka.fsm.UnionState;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Scanner;
import java.util.Set;
import java.util.Stack;

public class NDFSM {
    protected TransitionMapping transitions;
    protected Set<State> states;
    protected Set<State> acceptingStates;
    protected State initialState;
    protected Alphabet alphabet;

    public NDFSM(String encoding) throws Exception {
        this.parse(encoding);
        this.transitions.verify(this.states, this.alphabet);
    }

    public TransitionMapping getTransitions() {
        return this.transitions;
    }

    public Set<State> getStates() {
        return this.states;
    }

    public Set<State> getAcceptingStates() {
        return this.acceptingStates;
    }

    public State getInitialState() {
        return this.initialState;
    }

    public Alphabet getAlphabet() {
        return this.alphabet;
    }

    public NDFSM(Set<State> states, Alphabet alphabet, Set<Transition> transitions, State initialState, Set<State> acceptingStates) throws Exception {
        this.initializeFrom(states, alphabet, transitions, initialState, acceptingStates);
        this.transitions.verify(this.states, alphabet);
    }

    protected void initializeFrom(Set<State> states, Alphabet alphabet, Set<Transition> transitions, State initialState, Set<State> acceptingStates) {
        this.states = states;
        this.alphabet = alphabet;
        this.transitions = this.createMapping(transitions);
        this.initialState = initialState;
        this.acceptingStates = acceptingStates;
    }

    protected NDFSM() {
    }

    public void parse(String string) throws Exception {
        Scanner scanner = new Scanner(string);
        scanner.useDelimiter("\\s*/");
        HashMap<Integer, IdentifiedState> states = new HashMap<Integer, IdentifiedState>();
        for (Integer stateId : IdentifiedState.parseStateIdList(scanner.next())) {
            states.put(stateId, new IdentifiedState(stateId));
        }
        Alphabet alphabet = Alphabet.parse(scanner.next());
        HashSet<Transition> transitions = new HashSet<Transition>();
        for (TransitionTuple t : TransitionTuple.parseTupleList(scanner.next())) {
            transitions.add(new Transition((State)states.get(t.fromStateId()), t.symbol(), (State)states.get(t.toStateId())));
        }
        State initialState = (State)states.get(scanner.nextInt());
        HashSet<State> acceptingStates = new HashSet<State>();
        if (scanner.hasNext()) {
            for (Integer stateId : IdentifiedState.parseStateIdList(scanner.next())) {
                acceptingStates.add((State)states.get(stateId));
            }
        }
        scanner.close();
        this.initializeFrom(new HashSet<State>(states.values()), alphabet, transitions, initialState, acceptingStates);
        this.transitions.verify(this.states, alphabet);
    }

    protected TransitionMapping createMapping(Set<Transition> transitions) {
        return new TransitionRelation(transitions);
    }

    public NDFSM removeUnreachableStates() {
        Set<State> reachableStates = this.reachableStates();
        HashSet<Transition> transitionsToReachableStates = new HashSet<Transition>();
        for (Transition t : this.transitions.transitions()) {
            if (!reachableStates.contains(t.fromState()) || !reachableStates.contains(t.toState())) continue;
            transitionsToReachableStates.add(t);
        }
        HashSet<State> reachableAcceptingStates = new HashSet<State>();
        for (State s : this.acceptingStates) {
            if (!reachableStates.contains(s)) continue;
            reachableAcceptingStates.add(s);
        }
        NDFSM aNDFSM = this.create();
        aNDFSM.initializeFrom(reachableStates, this.alphabet, transitionsToReachableStates, this.initialState, reachableAcceptingStates);
        return aNDFSM;
    }

    protected NDFSM create() {
        return new NDFSM();
    }

    private Set<State> reachableStates() {
        ArrayList<Character> symbols = new ArrayList<Character>();
        symbols.add(Alphabet.EPSILON);
        for (Character c : this.alphabet) {
            symbols.add(c);
        }
        Alphabet alphabetWithEpsilon = new Alphabet(symbols);
        Set<State> reachable = new HashSet<State>();
        Set<State> newlyReachable = new HashSet<State>();
        newlyReachable.add(this.initialState);
        while (!newlyReachable.isEmpty()) {
            reachable.addAll(newlyReachable);
			newlyReachable = new HashSet<State>();
            for (State state : reachable) {
                for (Character symbol : alphabetWithEpsilon) {
                    for (State s : this.transitions.at(state, symbol)) {
                        if (reachable.contains(s)) continue;
                        newlyReachable.add(s);
                    }
                }
            }
        }
        return reachable;
    }

    public String encode() {
        return String.valueOf(State.encodeStateSet(this.states)) + "/" + this.alphabet.encode() + "/" + this.transitions.encode() + "/" + this.initialState.encode() + "/" + State.encodeStateSet(this.acceptingStates);
    }

    public void prettyPrint(PrintStream out) {
        out.print("K = ");
        State.prettyPrintStateSet(this.states, out);
        out.println("");
        out.print("\u03a3 = ");
        this.alphabet.prettyPrint(out);
        out.println("");
        out.print(String.valueOf(this.transitions.prettyName()) + " = ");
        this.transitions.prettyPrint(out);
        out.println("");
        out.print("s = ");
        this.initialState.prettyPrint(out);
        out.println("");
        out.print("A = ");
        State.prettyPrintStateSet(this.acceptingStates, out);
        out.println("");
    }

    public NDFSM toCanonicForm() {
        HashSet<Character> alphabetAndEpsilon = new HashSet<Character>();
        for (Character symbol : this.alphabet) {
            alphabetAndEpsilon.add(symbol);
        }
        alphabetAndEpsilon.add(Alphabet.EPSILON);
        HashSet<Transition> canonicTransitions = new HashSet<Transition>();
        Stack<State> todo = new Stack<State>();
        HashMap<State, IdentifiedState> canonicStates = new HashMap<State, IdentifiedState>();
        Integer free = 0;
        todo.push(this.initialState);
        canonicStates.put(this.initialState, new IdentifiedState(free));
        free = free + 1;
        while (!todo.isEmpty()) {
            State top = (State)todo.pop();
            for (Character symbol : alphabetAndEpsilon) {
                for (State nextState : this.transitions.at(top, symbol)) {
                    if (!canonicStates.containsKey(nextState)) {
                        canonicStates.put(nextState, new IdentifiedState(free));
                        todo.push(nextState);
                        free = free + 1;
                    }
                    canonicTransitions.add(new Transition((State)canonicStates.get(top), symbol, (State)canonicStates.get(nextState)));
                }
            }
        }
        HashSet<State> canonicAcceptingStates = new HashSet<State>();
        for (State s : this.acceptingStates) {
            if (!canonicStates.containsKey(s)) continue;
            canonicAcceptingStates.add((State)canonicStates.get(s));
        }
        NDFSM aNDFSM = this.create();
        aNDFSM.initializeFrom(new HashSet<State>(canonicStates.values()), this.alphabet, canonicTransitions, (State)canonicStates.get(this.initialState), canonicAcceptingStates);
        return aNDFSM;
    }

    public boolean compute(String input) throws Exception {
        return this.toDFSM().compute(input);
    }

    public DFSM toDFSM() throws Exception {
        HashMap<State, UnionState> epsTable = new HashMap<State, UnionState>();
        for (State s : this.states) {
            epsTable.put(s, this.eps(s));
            System.out.println(epsTable.get(s));
        }
        UnionState start = (UnionState)epsTable.get(this.initialState);
        ArrayList<UnionState> activeStates = new ArrayList<UnionState>();
        activeStates.add(start);
        HashSet<Transition> nonEpsilonTransitions = new HashSet<Transition>();
        ListIterator<UnionState> itr = activeStates.listIterator();
        while (itr.hasNext()) {
            Iterator<Character> symbols = this.alphabet.iterator();
            UnionState S = (UnionState)itr.next();
            while (symbols.hasNext()) {
                Character c = symbols.next();
                UnionState newState = new UnionState();
                for (State s : S.getuSet()) {
                    Set<State> P = this.transitions.at(s, c);
                    for (State p : P) {
                        newState.addAll(this.eps(p));
                    }
                }
                if (!activeStates.contains(newState)) {
                    itr.add(newState);
                    nonEpsilonTransitions.add(new Transition(S, c, newState));
                    itr.previous();
                    continue;
                }
                for (State s : activeStates) {
                    if (!s.equals(newState)) continue;
                    nonEpsilonTransitions.add(new Transition(S, c, s));
                }
            }
        }
        HashSet<State> acceptStates = new HashSet<State>();
        for (UnionState Q : activeStates) {
            for (State q : Q.getuSet()) {
                if (!this.acceptingStates.contains(q)) continue;
                acceptStates.add(Q);
            }
        }
        ListIterator<UnionState> activeStateItr = activeStates.listIterator();
        HashSet<State> aSet = new HashSet<State>();
        while (activeStateItr.hasNext()) {
            aSet.add((State)activeStateItr.next());
        }
        DFSM dfsm = new DFSM(aSet, this.alphabet, nonEpsilonTransitions, start, acceptStates);
        return dfsm;
    }

    public UnionState eps(State s) {
        ArrayList<State> temp = new ArrayList<State>();
        temp.add(s);
        ListIterator<State> itr = temp.listIterator();
        while (itr.hasNext()) {
            Set<State> eps = this.transitions.at((State)itr.next(), Alphabet.EPSILON);
            for (State st : eps) {
                if (temp.contains(st)) continue;
                itr.add(st);
                itr.previous();
            }
        }
        return new UnionState(new HashSet<State>(temp));
    }
}


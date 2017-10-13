package elements;

import java.util.ArrayList;

public class State {
	
	private ArrayList<String> predicates = new ArrayList<String>();
	private ArrayList<String> usedOperators = new ArrayList<String>();
	
	/*
	 * Getters: methods used to retrieve the private variables from the class.
	 */
	public ArrayList<String> getPredicates() {
		return predicates;
	}

	public ArrayList<String> getUsedOperators() {
		return usedOperators;
	}
	
	/*
	 * Setters: methods used to set the private variables of the class. 
	 */
	public void setPredicates(ArrayList<String> predicates) {
		this.predicates = predicates;
	}

	public void setUsedOperators(ArrayList<String> usedOperators) {
		this.usedOperators = usedOperators;
	}
	
	/**
	 * This method add an operation to the state list. 
	 * This will keep track of the plan calculated by the planner.
	 **/
	public void addOperator(String string){
		usedOperators.add(string);
	}

	/**
	 * This method will be used to compare two states. 
	 * We know that two states are equal if the list of predicates is equal.
	 **/
	public boolean comparePredicates(ArrayList<String> input_predicate){
		ArrayList<String> inputPredicates = input_predicate;
		inputPredicates.sort(null);
		predicates.sort(null);
				
		return predicates.equals(inputPredicates);
	}
	
}

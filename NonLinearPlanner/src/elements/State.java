package elements;

import java.util.ArrayList;

import elements.GenericPredicate.PredicateType;

/**
 * This class defines our states.
 */

public class State {
	
	public static ArrayList<String> environmentConditions = new ArrayList<String>(); //These are all the unchangeable conditions of the environment. These does not depend on the state, are implicit in the environment.  
	public static int MaxColumns = 3; //Number of spaces on the table (by default is set to 3).
	private ArrayList<String> predicates = new ArrayList<String>();
	private ArrayList<String> usedOperators = new ArrayList<String>();
	private int availableSpace;
	
	/**
	 * First possible constructor, where we specify the used operators.
	 * @param predicates : predicates of this state
	 * @param usedOperators : total amount of operators used so far to get to this state
	 */
	public State(ArrayList<String> predicates, ArrayList<String> usedOperators) {
		this.predicates = predicates;
		this.usedOperators = usedOperators;
		this.availableSpace = calculateAvailableSpace();
	}
	
	/**
	 * Second possible constructor, where we don't specify the used operators. In this case, the usedOperators list will remain empty.
	 * @param predicates : predicates of this state
	 */
	public State(ArrayList<String> predicates) {
		this.predicates = predicates;
		this.availableSpace =  calculateAvailableSpace();
	}
	
	/**
	 * Third possible constructor, we won't specify anything here, it's a blank state.
	 */
	public State() {
		
	}
	
	/*
	 * Getters and Setters
	 */
	public ArrayList<String> getPredicates() {
		return predicates;
	}
	
	public void setPredicates(ArrayList<String> predicates) {
		this.predicates = predicates;
	}

	public ArrayList<String> getUsedOperators() {
		return usedOperators;
	}
	
	public void setUsedOperators(ArrayList<String> usedOperators) {
		this.usedOperators = usedOperators;
	}
	
	/**
	 * This method adds an operation to the operators list. 
	 * This will keep track of the plan calculated by the planner.
	 **/
	public void addOperator(String string){
		usedOperators.add(string);
	}
	
	/**
	 * This method adds a predicate to the predicates list.
	 * @param predicate : new predicate to be added
	 */
	public void addPredicate(String predicate){
		//Before adding the predicate, we check if it is one of the special ones (USED_COLS_NUM or HOLDING)
		if(!isSpecialPredicate(predicate)) {
			predicates.add(predicate);
			//After adding predicates we have to recalculate the available space
			this.availableSpace = calculateAvailableSpace();
		}
	}
	
	/**
	 * This method adds all the predicates to the predicates list.
	 * @param predicates : ArrayList<String> with all the predicates to be added
	 */
	public void addAllPredicate(ArrayList<String> predicates){
		for(String predicate : predicates){
			if(!isSpecialPredicate(predicate)) {
				predicates.add(predicate);
			}
		}
		//After adding predicates we have to recalculate the available space
		this.availableSpace = calculateAvailableSpace();
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
	
	/**
	 * Prints a representation of the current state. This method is mainly used for testing.
	 */
	public void printState() {
		System.out.println(String.format("State with availableSpace = %d",availableSpace));
		System.out.println("------------------");
		for(String predicate : predicates) {
			System.out.println(predicate);
		}
		System.out.println("------------------\n");
	}
	
	/**
	 * This method checks if the state is valid with our system considering
	 * the knowledge of the system that we have. E.g. we know that we can't have ON(A,B) if A is Heavier than B 
	 * @return
	 */
	public boolean isStateValid() {
		
		return true;
	}
	
	/*
	 * Method used internally to calculate the available space on the table in the current state
	 */
	private int calculateAvailableSpace() {
		int available_space = MaxColumns;
		for(String predicate : predicates) {
			if(GenericPredicate.findType(predicate) == PredicateType.ON_TABLE) available_space -= 1;
		}
		return available_space;
	}
	
	private boolean isSpecialPredicate(String predicate) {
		if((GenericPredicate.findType(predicate) == PredicateType.USED_COLS_NUM_INC) 
				|| (GenericPredicate.findType(predicate) == PredicateType.USED_COLS_NUM_DEC)
				|| (GenericPredicate.findType(predicate) == PredicateType.USED_COLS_NUM_OK)) {
			return true;
		}else {
			return false;
		}
	}
}

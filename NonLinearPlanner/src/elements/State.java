package elements;

import java.util.ArrayList;
import java.util.regex.Pattern;

import elements.PredicateHelper.PredicateType;

/**
 * This class defines our states.
 */

public class State {
	
	public static ArrayList<String> environmentConditions = new ArrayList<String>(); //These are all the unchangeable conditions of the environment. These does not depend on the state, are implicit in the environment.  
	public static int maxColumns = 3; //Number of spaces on the table (by default is set to 3).
	private ArrayList<String> predicates = new ArrayList<String>();
	private ArrayList<String> usedOperators = new ArrayList<String>();
	private int availableSpace;
	public String stateExplanation = "Correct State"; //This will show if the state is correct or the reason why it's not after using method isStateValid.
	public State parentState = null;
	
	/**
	 * First possible constructor, where we specify the used operators.
	 * @param predicates : predicates of this state
	 * @param usedOperators : total amount of operators used so far to get to this state
	 */
	public State(ArrayList<String> predicates, ArrayList<String> usedOperators) {
		addAllPredicate(predicates);
		this.usedOperators = usedOperators;
		this.availableSpace = calculateAvailableSpace();
	}
	
	/**
	 * Second possible constructor, where we don't specify the used operators. In this case, the usedOperators list will remain empty.
	 * @param predicates : predicates of this state
	 */
	public State(ArrayList<String> predicates) {
		addAllPredicate(predicates);
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
		return new ArrayList<String>(predicates);
	}
	
	public void setPredicates(ArrayList<String> predicates) {
		this.predicates = predicates;
	}

	public ArrayList<String> getUsedOperators() {
		return new ArrayList<String>(usedOperators);
	}
	
	public void setUsedOperators(ArrayList<String> usedOperators) {
		this.usedOperators = usedOperators;
	}
	
	public int getAvailableSpace() {
		this.availableSpace = calculateAvailableSpace();
		return availableSpace;
	}

	public int getUsedSpace() {
		this.availableSpace = calculateAvailableSpace();
		return maxColumns - availableSpace;
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
		if(!isSpecialPredicate(predicate) && !predicates.contains(predicate)) {
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
			if(!isSpecialPredicate(predicate) && !this.predicates.contains(predicate)) {
				this.predicates.add(predicate);
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
		
		//Checks if we have too many elements on the table
		if(availableSpace<0) {
			stateExplanation = "Too many elements on the table.";
			return false;
		}
		
		//Checks if there are contradictory predicates
		boolean ContradictoryPredicate = false;
		for(String predicate : predicates) {
			ContradictoryPredicate = isPredicateContradictory(predicate);
			if(ContradictoryPredicate){
				return false;
			}
		}
		
		return true;
	}
	
	/**
	 * This method checks if a given predicate contradicts with one of the predicates that define the state
	 * or one of the environment predicates. 
	 * @param predicate
	 * @return
	 */
	private boolean isPredicateContradictory(String predicate) {
		String predicatesList = String.join(";", predicates) + ";" + String.join(";", environmentConditions) + ";";
		String blockname1;
		String blockname2;
		String armname;
		switch(PredicateHelper.findType(predicate)) {
		case CLEAR:
			blockname1 = predicate.substring(6, 7);
			if(Pattern.matches(".*ON\\(.," + blockname1 + "\\);.*", predicatesList)) {
				stateExplanation = "Contradictory states: CLEAR("+ blockname1 + ") is incompatible with ON(y," + blockname1 + ")";
				return true;
			}
			break;
		case EMPTY_ARM:
			armname = predicate.substring(10, 11);
			if(Pattern.matches(".*HOLDING\\(.," + armname + "\\);.*", predicatesList)) {
				stateExplanation = "Contradictory states: EMPTY-ARM("+ armname +") is incompatible with HOLDING(x," + armname + ")";
				return true;
			}
			break;
		case HOLDING:
			blockname1 = predicate.substring(8, 9);
			armname = predicate.substring(10, 11);
			if(Pattern.matches(".*HOLDING\\([^" + blockname1 + "]," + armname + "\\);.*", predicatesList)) {
				stateExplanation = "Contradictory states: HOLDING(" + blockname1 + "," + armname + ") is incompatible with HOLDING(y," + armname + ")";
				return true;
			}else if(Pattern.matches(".*HOLDING\\(" + blockname1 + ",[^" + armname + "]\\);.*", predicatesList)) {
				stateExplanation = "Contradictory states: HOLDING(" + blockname1 + "," + armname + ") is incompatible with HOLDING(" + blockname1 + ", with the other arm)";
				return true;
			}else if(armname.equals("L") && (!Pattern.matches(".*LIGHT-BLOCK\\(" + blockname1 + "\\);.*", predicatesList))) {
				stateExplanation = "Contradictory states: HOLDING(" + blockname1 + ",L) is only possible if there is the predicate LIGHT-BLOCK(" + blockname1 + ")";
				return true;
			}
			break;
		case ON:
			blockname1 = predicate.substring(3, 4);
			blockname2 = predicate.substring(5, 6);
			if(Pattern.matches(".*HOLDING\\(" + blockname1 + ",.*", predicatesList)) {
				stateExplanation = "Contradictory states: ON(" + blockname1 + ",y) is incompatible with HOLDING(" + blockname1 + ",Arm)";
				return true;
			}else if(Pattern.matches(".*ON-TABLE\\(" + blockname1 + "\\);.*", predicatesList)) {
				stateExplanation = "Contradictory states: ON(" + blockname1 + ",y) is incompatible with ON-TABLE(" + blockname1 + ")";
				return true;
			}else if(!Pattern.matches(".*HEAVIER\\(" + blockname2 + "," + blockname1 + "\\);.*", predicatesList)) {
				stateExplanation = "Contradictory states: if ON(" + blockname1 + "," + blockname2 + ") there must be the predicate HEAVIER(" + blockname2 + "," + blockname1 + ")";
				return true;
			}else if(Pattern.matches(".*ON\\(" + blockname2 + "," + blockname1 + "\\);.*", predicatesList)) {
				stateExplanation = "Contradictory states: ON(" + blockname1 + "," + blockname2 + ") is incompatible with ON(" + blockname2 + "," + blockname1 + ")";
				return true;
			}else if(!Pattern.matches(".*ON-TABLE\\(" + blockname2 + "\\);.*", predicatesList) && !Pattern.matches(".*ON\\(" + blockname2 + ",.\\);.*", predicatesList)) {
				stateExplanation = "Contradictory states: if ON(" + blockname1 + "," + blockname2 + ") there must be the predicate ON-TABLE(" + blockname2 + ") or ON(" + blockname2 + ",z)";
				return true;
			}
			break;
		case ON_TABLE:
			blockname1 = predicate.substring(9, 10);
			if(Pattern.matches(".*HOLDING\\(" + blockname1 + ",.*", predicatesList)) {
				stateExplanation = "Contradictory states: ON-TABLE(" + blockname1 + ") is incompatible with HOLDING(" + blockname1 + ",Arm)";
				return true;
			}else if(Pattern.matches(".*ON\\(" + blockname1 + ",.\\);.*", predicatesList)) {
				stateExplanation = "Contradictory states: ON-TABLE(" + blockname1 + ") is incompatible with ON(" + blockname1 + ",y)";
				return true;
			}
			break;
		default:
			return false;
		}		
		return false;
	}
	
	/**
	 * Method used internally to calculate the available space on the table in the current state
	 */
	private int calculateAvailableSpace() {
		int available_space = maxColumns;
		for(String predicate : predicates) {
			if(PredicateHelper.findType(predicate) == PredicateType.ON_TABLE) available_space -= 1;
		}
		return available_space;
	}
	
	/**
	 * Checks if the given predicate is one of the special ones that should not be added. e.g USED_COLS_NUM(n+1)
	 * @param predicate
	 */
	private boolean isSpecialPredicate(String predicate) {
		if((PredicateHelper.findType(predicate) == PredicateType.USED_COLS_NUM_INC) 
				|| (PredicateHelper.findType(predicate) == PredicateType.USED_COLS_NUM_DEC)
				|| (PredicateHelper.findType(predicate) == PredicateType.USED_COLS_NUM_OK)
				|| (PredicateHelper.findType(predicate) == PredicateType.HEAVIER)
				|| (PredicateHelper.findType(predicate) == PredicateType.LIGHT_BLOCK)
				|| (PredicateHelper.findType(predicate) == null)) {
			return true;
		}else {
			return false;
		}
	}
	
	
	@Override
	public boolean equals(Object other) {
		if (!(other instanceof State)) {
	        return false;
	    }
		
		State state = (State) other;
		
		ArrayList<String> predicates1 = new ArrayList<String>(this.predicates);
		ArrayList<String> predicates2 = new ArrayList<String>(state.predicates);
		predicates1.sort(null);
		predicates2.sort(null);
		
		//Custom equality check
		return predicates1.equals(predicates2);
	}
}

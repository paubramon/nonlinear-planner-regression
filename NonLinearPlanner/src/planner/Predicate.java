package planner;

import planner.Planner.Arm;

public class Predicate {
	public static enum PredicateType{
		ON_TABLE, ON, CLEAR, EMPTY_ARM, HOLDING, USED_COLS_NUM, HEAVIER, LIGHT_BLOCK
	}
	public PredicateType type;
	public String rootString; //String used to define the predicate
	public int numBlockArguments; //Number of block arguments when defining the predicate. e.g HEAVIER(x,y) has 2 block arguments: x and y.
	public int numArmArguments; //Number of arm arguments when defining the predicate. e.g HOLDING(x,a) has 1 block argument (x) and 1 arm argument(a)
	
	/*
	 * Predicate Constructor
	 */
	public Predicate(PredicateType type){
		this.type = type;
		switch (type) {
			case ON_TABLE:
				rootString = "ON_TABLE";
				numBlockArguments = 1;
				numArmArguments = 0;
				break;
				
			case ON:
				rootString = "ON";
				numBlockArguments = 2;
				numArmArguments = 0;
				break;
				
			case CLEAR:
				rootString = "CLEAR";
				numBlockArguments = 1;
				numArmArguments = 0;
				break;

			case EMPTY_ARM:
				rootString = "EMPTY_ARM";
				numBlockArguments = 0;
				numArmArguments = 1;
				break;
				
			case HOLDING:
				rootString = "HOLDING";
				numBlockArguments = 1;
				numArmArguments = 1;
				break;
				
			case USED_COLS_NUM:
				rootString = "USED_COLS_NUM";
				numBlockArguments = 0;
				numArmArguments = 0;
				break;
			
			case HEAVIER:
				rootString = "HEAVIER";
				numBlockArguments = 2;
				numArmArguments = 0;
				break;
				
			case LIGHT_BLOCK:
				rootString = "LIGHT_BLOCK";
				numBlockArguments = 1;
				numArmArguments = 0;
				break;		
		}
	}
	
	/*
	 * This method generates the predicate with the given arguments. For example, if the predicate type
	 * is HOLDING, and the given arguments are Block A, block B, arm R and available_space 1, it will generate
	 * an string like "HOLDING(A,R)", ignoring the arguments that are not needed.
	 * When invoking this method, it is very important to put the arguments in the following order: Blocks, Arm, Spaces
	 */
	public String generatePredicate(Block block1, Block block2, Arm arm, Integer available_space) {
		String resultString = rootString; 
		if (numBlockArguments == 1) {
			resultString = resultString + "("+ block1.name;
		}else if(numBlockArguments == 2) {
			resultString = resultString + "(" + block1.name + "," + block2.name;
		}else {
			resultString = resultString + "(";
		}
		
		if (numArmArguments > 0) {
			if (numBlockArguments > 0) {
				resultString = resultString + ",";
			}
			resultString = resultString + arm.toString();
		}
		
		if (type == PredicateType.USED_COLS_NUM) {
			resultString = resultString + available_space.toString();
		}
		
		resultString = resultString + ")";
		return resultString;
	}
}

package elements;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import main.Planner.Arm;

/**
 * This class will create generic predicates objects. These can be used together with the GenericOperators
 * class in order to define our system.
 * @author paubr
 *
 */
public class GenericPredicate {
	public static enum PredicateType{
		ON_TABLE, ON, CLEAR, EMPTY_ARM, HOLDING, HEAVIER, LIGHT_BLOCK, USED_COLS_NUM_OK,  USED_COLS_NUM_INC,  USED_COLS_NUM_DEC
	}
	public PredicateType type;
	public String rootString; //String used to define the predicate
	public int numBlockArguments; //Number of block arguments when defining the predicate. e.g HEAVIER(x,y) has 2 block arguments: x and y.
	public int numArmArguments; //Number of arm arguments when defining the predicate. e.g HOLDING(x,a) has 1 block argument (x) and 1 arm argument(a)
	
	/*
	 * Predicate Constructor
	 */
	public GenericPredicate(PredicateType type){
		this.type = type;
		switch (type) {
			case ON_TABLE:
				rootString = "ON-TABLE";
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
				rootString = "EMPTY-ARM";
				numBlockArguments = 0;
				numArmArguments = 1;
				break;
				
			case HOLDING:
				rootString = "HOLDING";
				numBlockArguments = 1;
				numArmArguments = 1;
				break;
				
			case HEAVIER:
				rootString = "HEAVIER";
				numBlockArguments = 2;
				numArmArguments = 0;
				break;
				
			case LIGHT_BLOCK:
				rootString = "LIGHT-BLOCK";
				numBlockArguments = 1;
				numArmArguments = 0;
				break;
				
			case USED_COLS_NUM_OK:
				rootString = "USED-COLS-NUM";
				numBlockArguments = 0;
				numArmArguments = 0;
				break;
				
			case USED_COLS_NUM_INC:
				rootString = "USED-COLS-NUM";
				numBlockArguments = 0;
				numArmArguments = 0;
				break;
				
			case USED_COLS_NUM_DEC:
				rootString = "USED-COLS-NUM";
				numBlockArguments = 0;
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
	public String generatePredicate(Block block1, Block block2, Arm arm) {
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
		
		if (type == PredicateType.USED_COLS_NUM_OK) {
			resultString = resultString + "n) n>0";
		}else if (type == PredicateType.USED_COLS_NUM_INC) {
			resultString = resultString + "n+1)";
		}else if (type == PredicateType.USED_COLS_NUM_DEC) {
			resultString = resultString + "n-1)";
		}else {
			resultString = resultString + ")";
		}
		return resultString;
	}
	
	public static PredicateType findType(String predicate) {
		Matcher matcher = Pattern.compile("(.*)[(].*").matcher(predicate);
		if(matcher.matches()) {
			String temp = matcher.group(1);
			switch(temp) {
			case "ON-TABLE":
				return PredicateType.ON_TABLE;
			case "ON":
				return PredicateType.ON;
			case "CLEAR":
				return PredicateType.CLEAR;
			case "EMPTY-ARM":
				return PredicateType.EMPTY_ARM;
			case "HOLDING":
				return PredicateType.HOLDING;
			case "HEAVIER":
				return PredicateType.HEAVIER;
			case "LIGHT-BLOCK":
				return PredicateType.LIGHT_BLOCK;
			case "USED-COLS-NUM":
				if(predicate == "USED-COLS-NUM(n) n>0") {
					return PredicateType.USED_COLS_NUM_OK;
				}else if(predicate == "USED-COLS-NUM(n+1)") {
					return PredicateType.USED_COLS_NUM_INC;
				}else if(predicate == "USED-COLS-NUM(n-1)") {
					return PredicateType.USED_COLS_NUM_DEC;
				}
			}
		}
		return null;
	}	
}

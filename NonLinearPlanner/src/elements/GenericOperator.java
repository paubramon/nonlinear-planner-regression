package elements;

import java.util.ArrayList;

import elements.Predicate.PredicateType;

/**
 * This class create a generic operator, where all the predicates in preconditions, addlist 
 * and deletelist are generic. This class will allow us to define the different type of operators without
 * setting the blocks. A method of this class will create operators that can be applied to a given state 
 * to obtain all the conditions. 
 * @author paubr
 */

public class GenericOperator {
	public static enum OperatorType{
		PICK_UP_RIGHT, PICK_UP_LEFT, UNSTACK_RIGHT, UNSTACK_LEFT, STACK_RIGHT,STACK_LEFT, LEAVE_RIGHT,LEAVE_LEFT
	}
	public static enum Arm{
		R,L
	}
	
	private ArrayList<String> preConditions = new ArrayList<String>();
	private ArrayList<String> addedConditions = new ArrayList<String>();
	private ArrayList<String> deletedConditions = new ArrayList<String>();
	private Arm arm = Arm.R;
	private OperatorType type; 
	private int numOfBlocks = 0; //This is the number of blocks involved in this operation
	
	public GenericOperator(OperatorType type) {
		this.type = type; 
		
		/*
		 * Definition of the preconditions, add list and delete list for each type of operators
		 */
		switch(type) {
		case PICK_UP_RIGHT:
			arm = Arm.R;
			numOfBlocks = 1;
			preConditions.add("ON-TABLE(?x)");
			preConditions.add("CLEAR(?x)");
			preConditions.add("EMPTY-ARM(?a)");
			deletedConditions.add("ON-TABLE(?x)");
			deletedConditions.add("EMPTY-ARM(?a)");
			addedConditions.add("HOLDING(?x,?a)");
			addedConditions.add("USED-COLS-NUM(n+1)");
			break;
			
		case PICK_UP_LEFT:
			arm = Arm.L;
			numOfBlocks = 1;
			preConditions.add("ON-TABLE(?x)");
			preConditions.add("CLEAR(?x)");
			preConditions.add("EMPTY-ARM(?a)");
			preConditions.add("LIGHT-BLOCK(?x)");
			deletedConditions.add("ON-TABLE(?x)");
			deletedConditions.add("EMPTY-ARM(?a)");
			addedConditions.add("HOLDING(?x,?a)");
			addedConditions.add("USED-COLS-NUM(n+1)");
			break;
			
		case UNSTACK_RIGHT:
			arm = Arm.R;
			numOfBlocks = 2;
			preConditions.add("ON(?x,?y)");
			preConditions.add("CLEAR(?x)");
			preConditions.add("EMPTY-ARM(?a)");
			deletedConditions.add("ON(?x,?y)");
			deletedConditions.add("EMPTY-ARM(?a)");
			addedConditions.add("HOLDING(?x,?a)");
			addedConditions.add("CLEAR(?y)");
			break;
			
		case UNSTACK_LEFT:
			arm = Arm.L;
			numOfBlocks = 2;
			preConditions.add("ON(?x,?y)");
			preConditions.add("CLEAR(?x)");
			preConditions.add("EMPTY-ARM(?a)");
			preConditions.add("LIGHT-BLOCK(?x)");
			deletedConditions.add("ON(?x,?y)");
			deletedConditions.add("EMPTY-ARM(?a)");
			addedConditions.add("HOLDING(?x,?a)");
			addedConditions.add("CLEAR(?y)");
			break;
			
		case STACK_RIGHT:
			arm = Arm.R;
			numOfBlocks = 2;
			preConditions.add("HOLDING(?x,?a)");
			preConditions.add("CLEAR(?y)");
			preConditions.add("HEAVIER(?x,?y)");
			deletedConditions.add("CLEAR(?y)");
			deletedConditions.add("HOLDING(?x,?a)");
			addedConditions.add("ON(?x,?y)");
			addedConditions.add("EMPTY-ARM(?a)");
			break; 
			
		case STACK_LEFT:
			arm = Arm.L;
			numOfBlocks = 2;
			preConditions.add("HOLDING(?x,?a)");
			preConditions.add("CLEAR(?y)");
			preConditions.add("HEAVIER(?x,?y)");
			deletedConditions.add("CLEAR(?y)");
			deletedConditions.add("HOLDING(?x,?a)");
			addedConditions.add("ON(?x,?y)");
			addedConditions.add("EMPTY-ARM(?a)");
			break; 
			
		case LEAVE_RIGHT:
			arm = Arm.R;
			numOfBlocks = 1;
			preConditions.add("HOLDING(?x,?a)");
			preConditions.add("USED-COLS-NUM(n) n>0");
			deletedConditions.add("HOLDING(?x,?a)");
			addedConditions.add("ON-TABLE(?x)");
			addedConditions.add("EMPTY-ARM(?a)");
			addedConditions.add("USED-COLS-NUM(n-1)");
			break;
			
		case LEAVE_LEFT:
			arm = Arm.L;
			numOfBlocks = 1;
			preConditions.add("HOLDING(?x,?a)");
			preConditions.add("USED-COLS-NUM(n) n>0");
			deletedConditions.add("HOLDING(?x,?a)");
			addedConditions.add("ON-TABLE(?x)");
			addedConditions.add("EMPTY-ARM(?a)");
			addedConditions.add("USED-COLS-NUM(n-1)");
			break;
		}
	}
	
	/**
	 * This method creates an operator object given one or two parameters
	 * @param block1 : first block of the operator
	 * @param block2 : second block of the operator (null if the operator involves only one block)
	 * @return
	 */
	public Operator getOperator(Block block1,Block block2) {
		ArrayList<String> foundPreConditions = new ArrayList<String>();
		ArrayList<String> foundAddedConditions = new ArrayList<String>();
		ArrayList<String> foundDeletedConditions = new ArrayList<String>();
		
		for(String predicate : preConditions) {
			predicate = predicate.replace("?x", block1.name);
			predicate = predicate.replace("?y", block2.name);
			predicate = predicate.replace("?a", arm.toString());
			foundPreConditions.add(predicate);
		}
		for(String predicate : addedConditions) {
			predicate = predicate.replace("?x", block1.name);
			predicate = predicate.replace("?y", block2.name);
			predicate = predicate.replace("?a", arm.toString());
			foundAddedConditions.add(predicate);
		}
		for(String predicate : deletedConditions) {
			predicate = predicate.replace("?x", block1.name);
			predicate = predicate.replace("?y", block2.name);
			predicate = predicate.replace("?a", arm.toString());
			foundDeletedConditions.add(predicate);
		}
		if(numOfBlocks == 1) {
			return new Operator(type, numOfBlocks, foundPreConditions, foundAddedConditions,foundDeletedConditions, arm, block1, null);
		}else if(numOfBlocks == 2) {
			return new Operator(type, numOfBlocks, foundPreConditions, foundAddedConditions,foundDeletedConditions, arm, block1, block2);
		}
		return new Operator(type, numOfBlocks, foundPreConditions, foundAddedConditions,foundDeletedConditions, arm, block1, block2);
	}
	
	/**
	 * This method returns a list of possible operators that would add the condition with the provided blocks
	 * @return ArrayList<Operators>, will return an empty list if there aren't possible combinations with the given parameters.
	 */
	public ArrayList<Operator> findCombinations(ArrayList<Block> blocks, String condition){
		ArrayList<Operator> possibleOperators = new ArrayList<Operator>();
		PredicateType searchedType = Predicate.findType(condition);
		
		/*
		 * First, we check if the condition is in the add list of this operator
		 */
		boolean existAdd = false;
		for(String addCondition : addedConditions) {
			if(Predicate.findType(addCondition) == searchedType) existAdd = true;
		}
		
		//If this method add a condition of the same type as input:condition
		if(existAdd) {
			Operator tempOp;
			//We search for all the possible combinations of this operator with the given blocks
			for(Block block1 : blocks) {
				for(Block block2 : blocks) {
					if(!block1.equals(block2)) {
						tempOp = getOperator(block1,block2);
						if(tempOp.isInAddList(condition) && !possibleOperators.contains(tempOp)) possibleOperators.add(tempOp);
					}
				}
			}
		}	
		//This list have all the possible operators (without taking into account any of the state preconditions and environment conditions)
		return possibleOperators;
	}
}

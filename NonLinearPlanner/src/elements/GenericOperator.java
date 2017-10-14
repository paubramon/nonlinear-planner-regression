package elements;

import java.util.ArrayList;

import elements.GenericOperator.OperatorType;
import elements.GenericPredicate.PredicateType;
import main.Planner.Arm;

/**
 * This class create a generic operator, where all the predicates in preconditions, addlist 
 * and deletelist are generic. This class will allow us to define the different type of operators without
 * setting the blocks. A method of this class will create operators that can be applied to a given state 
 * to obtain all the conditions. 
 * @author paubr
 */

public class GenericOperator {
	public static enum OperatorType{
		PICK_UP_RIGHT, PICK_UP_LEFT, UNSTACK_RIGHT, UNSTACK_LEFT, STACK, LEAVE
	}
	
	private ArrayList<GenericPredicate> preConditions = new ArrayList<GenericPredicate>();
	private ArrayList<GenericPredicate> addedConditions = new ArrayList<GenericPredicate>();
	private ArrayList<GenericPredicate> deletedConditions = new ArrayList<GenericPredicate>();
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
			preConditions.add(new GenericPredicate(PredicateType.ON_TABLE));
			preConditions.add(new GenericPredicate(PredicateType.CLEAR));
			preConditions.add(new GenericPredicate(PredicateType.EMPTY_ARM));
			deletedConditions.add(new GenericPredicate(PredicateType.ON_TABLE));
			deletedConditions.add(new GenericPredicate(PredicateType.EMPTY_ARM));
			addedConditions.add(new GenericPredicate(PredicateType.HOLDING));
			addedConditions.add(new GenericPredicate(PredicateType.USED_COLS_NUM_INC));
			break;
			
		case PICK_UP_LEFT:
			arm = Arm.L;
			numOfBlocks = 1;
			preConditions.add(new GenericPredicate(PredicateType.ON_TABLE));
			preConditions.add(new GenericPredicate(PredicateType.CLEAR));
			preConditions.add(new GenericPredicate(PredicateType.EMPTY_ARM));
			preConditions.add(new GenericPredicate(PredicateType.LIGHT_BLOCK));
			deletedConditions.add(new GenericPredicate(PredicateType.ON_TABLE));
			deletedConditions.add(new GenericPredicate(PredicateType.EMPTY_ARM));
			addedConditions.add(new GenericPredicate(PredicateType.HOLDING));
			addedConditions.add(new GenericPredicate(PredicateType.USED_COLS_NUM_INC));
			break;
			
		case UNSTACK_RIGHT:
			arm = Arm.R;
			numOfBlocks = 2;
			preConditions.add(new GenericPredicate(PredicateType.ON));
			preConditions.add(new GenericPredicate(PredicateType.CLEAR));
			preConditions.add(new GenericPredicate(PredicateType.EMPTY_ARM));
			deletedConditions.add(new GenericPredicate(PredicateType.ON));
			deletedConditions.add(new GenericPredicate(PredicateType.EMPTY_ARM));
			addedConditions.add(new GenericPredicate(PredicateType.HOLDING));
			addedConditions.add(new GenericPredicate(PredicateType.CLEAR));
			break;
			
		case UNSTACK_LEFT:
			arm = Arm.L;
			numOfBlocks = 2;
			preConditions.add(new GenericPredicate(PredicateType.ON));
			preConditions.add(new GenericPredicate(PredicateType.CLEAR));
			preConditions.add(new GenericPredicate(PredicateType.EMPTY_ARM));
			preConditions.add(new GenericPredicate(PredicateType.LIGHT_BLOCK));
			deletedConditions.add(new GenericPredicate(PredicateType.ON));
			deletedConditions.add(new GenericPredicate(PredicateType.EMPTY_ARM));
			addedConditions.add(new GenericPredicate(PredicateType.HOLDING));
			addedConditions.add(new GenericPredicate(PredicateType.CLEAR));
			break;
			
		case STACK:
			numOfBlocks = 2;
			preConditions.add(new GenericPredicate(PredicateType.HOLDING));
			preConditions.add(new GenericPredicate(PredicateType.CLEAR));
			preConditions.add(new GenericPredicate(PredicateType.HEAVIER));
			deletedConditions.add(new GenericPredicate(PredicateType.CLEAR));
			deletedConditions.add(new GenericPredicate(PredicateType.HOLDING));
			addedConditions.add(new GenericPredicate(PredicateType.ON));
			addedConditions.add(new GenericPredicate(PredicateType.EMPTY_ARM));
			break; 
			
		case LEAVE:
			numOfBlocks = 1;
			preConditions.add(new GenericPredicate(PredicateType.HOLDING));
			preConditions.add(new GenericPredicate(PredicateType.USED_COLS_NUM_OK));
			deletedConditions.add(new GenericPredicate(PredicateType.HOLDING));
			addedConditions.add(new GenericPredicate(PredicateType.ON_TABLE));
			addedConditions.add(new GenericPredicate(PredicateType.EMPTY_ARM));
			addedConditions.add(new GenericPredicate(PredicateType.USED_COLS_NUM_DEC));
			break;
		}
	}
	
	/**
	 * This method prints out the Preconditions, the Add list and Delete list for the method given the following parameters:
	 * @param block1 : first block of the operator  
	 * @param block2 : second block of the operator (null if it does not exist)
	 * @param arm : arm of the robot that does the operation. 
	 * @param availableSpace : number of space available
	 */
	public void printOperator(Block block1, Block block2) {
		System.out.print(String.format("\n\n%s", type.toString()));
		System.out.print("\n-------------------------------------");
		System.out.print("\nPRECONDITIONS:\n");
		System.out.print("-------------------------------------\n");
		for(GenericPredicate prec:preConditions){
			System.out.println(prec.generatePredicate(block1, block2, arm));
		}
		System.out.print("\n-------------------------------------");
		System.out.print("\nADD List:\n");
		System.out.print("-------------------------------------\n");
		for(GenericPredicate acond:addedConditions){
			System.out.println(acond.generatePredicate(block1, block2, arm));
		}
		System.out.print("\n-------------------------------------");
		System.out.print("\nDELETE List:\n");
		System.out.print("-------------------------------------\n");
		for(GenericPredicate dcond:deletedConditions){
			System.out.println(dcond.generatePredicate(block1, block2, arm));
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
		for(GenericPredicate predicate : preConditions) {
			foundPreConditions.add(predicate.generatePredicate(block1, block2, arm));
		}
		for(GenericPredicate predicate : addedConditions) {
			foundAddedConditions.add(predicate.generatePredicate(block1, block2, arm));
		}
		for(GenericPredicate predicate : deletedConditions) {
			foundDeletedConditions.add(predicate.generatePredicate(block1, block2, arm));
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
		PredicateType searchedType = GenericPredicate.findType(condition);
		
		/*
		 * First, we look into the addConditions list to see if any of the conditions that this method
		 * adds, is one of the same type as the one we are looking for
		 */
		boolean existAdd = false;
		for(GenericPredicate addCondition : addedConditions) {
			if(addCondition.type == searchedType) existAdd = true;
		}
		
		//If this method add a condition of the same type as input:condition
		if(existAdd) {
			Operator tempOp;
			//We search for all the possible combinations of this operator with the given blocks
			for(Block block1 : blocks) {
				for(Block block2 : blocks) {
					if(block1 != block2) {
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

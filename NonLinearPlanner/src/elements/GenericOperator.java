package elements;

import java.util.ArrayList;

import elements.GenericPredicate.PredicateType;
import main.Planner.Arm;

/**
 * This class create a generic operator, where all the predicates in preconditions, addlist 
 * and deletelist are generic. This class will allow us to define the different type of operators without
 * setting the blocks. A method of this class will create operators that can be applied to a given state 
 * to obtain some conditions. 
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
	
	public GenericOperator(OperatorType type) {
		this.type = type; 
		
		/*
		 * Definition of the preconditions, add list and delete list for each type of operators
		 */
		switch(type) {
			case PICK_UP_RIGHT:
				arm = Arm.R;
				preConditions.add(new GenericPredicate(PredicateType.ON_TABLE));
				preConditions.add(new GenericPredicate(PredicateType.CLEAR));
				preConditions.add(new GenericPredicate(PredicateType.EMPTY_ARM));
				deletedConditions.add(new GenericPredicate(PredicateType.ON_TABLE));
				deletedConditions.add(new GenericPredicate(PredicateType.EMPTY_ARM));
				addedConditions.add(new GenericPredicate(PredicateType.HOLDING));
				break;
				
			case PICK_UP_LEFT:
				arm = Arm.L;
				preConditions.add(new GenericPredicate(PredicateType.ON_TABLE));
				preConditions.add(new GenericPredicate(PredicateType.CLEAR));
				preConditions.add(new GenericPredicate(PredicateType.EMPTY_ARM));
				preConditions.add(new GenericPredicate(PredicateType.LIGHT_BLOCK));
				deletedConditions.add(new GenericPredicate(PredicateType.ON_TABLE));
				deletedConditions.add(new GenericPredicate(PredicateType.EMPTY_ARM));
				addedConditions.add(new GenericPredicate(PredicateType.HOLDING));
				break;
				
			case UNSTACK_RIGHT:
				arm = Arm.R;
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
				preConditions.add(new GenericPredicate(PredicateType.HOLDING));
				preConditions.add(new GenericPredicate(PredicateType.CLEAR));
				preConditions.add(new GenericPredicate(PredicateType.HEAVIER));
				deletedConditions.add(new GenericPredicate(PredicateType.CLEAR));
				deletedConditions.add(new GenericPredicate(PredicateType.HOLDING));
				addedConditions.add(new GenericPredicate(PredicateType.ON));
				addedConditions.add(new GenericPredicate(PredicateType.EMPTY_ARM));
				break; 
				
			case LEAVE:
				preConditions.add(new GenericPredicate(PredicateType.HOLDING));
				preConditions.add(new GenericPredicate(PredicateType.USED_COLS_NUM));
				deletedConditions.add(new GenericPredicate(PredicateType.HOLDING));
				addedConditions.add(new GenericPredicate(PredicateType.ON_TABLE));
				addedConditions.add(new GenericPredicate(PredicateType.EMPTY_ARM));
				addedConditions.add(new GenericPredicate(PredicateType.USED_COLS_NUM));
				break;
		}
	}
	
	/**
	 * This method prints out the Preconditions, the Add list and Delete list for the method given the following parameters:
	 * @param block1 : first block of the operator  
	 * @param block2 : second block of the operator (null if it does not exist)
	 * @param arm : arm of the robot that does the operator. 
	 * @param availableSpace : number of space available
	 */
	public void printOperator(Block block1, Block block2, Integer availableSpace) {
		System.out.print(String.format("\n\n%s", type.toString()));
		System.out.print("\n-------------------------------------");
		System.out.print("\nPRECONDITIONS:\n");
		System.out.print("-------------------------------------\n");
		for(GenericPredicate prec:preConditions){
			System.out.println(prec.generatePredicate(block1, block2, arm, 2));
		}
		System.out.print("\n-------------------------------------");
		System.out.print("\nADD List:\n");
		System.out.print("-------------------------------------\n");
		for(GenericPredicate acond:addedConditions){
			System.out.println(acond.generatePredicate(block1, block2, arm, 2));
		}
		System.out.print("\n-------------------------------------");
		System.out.print("\nDELETE List:\n");
		System.out.print("-------------------------------------\n");
		for(GenericPredicate dcond:deletedConditions){
			System.out.println(dcond.generatePredicate(block1, block2, arm, 2));
		}
		
	}
}

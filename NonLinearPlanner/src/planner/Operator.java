package planner;

import java.util.ArrayList;

import planner.Planner.Arm;
import planner.Predicate.PredicateType;

public class Operator {
	public static enum OperatorType{
		PICK_UP_RIGHT, PICK_UP_LEFT, UNSTACK_RIGHT, UNSTACK_LEFT, STACK, LEAVE
	}
	
	private ArrayList<Predicate> preConditions = new ArrayList<Predicate>();
	private ArrayList<Predicate> addedConditions = new ArrayList<Predicate>();
	private ArrayList<Predicate> deletedConditions = new ArrayList<Predicate>();
	private Arm arm = Arm.R;
	private OperatorType type; 
	
	public Operator(OperatorType type) {
		this.type = type; 
		
		/*
		 * Definition of the preconditions, add list and delete list for each type of operators
		 */
		switch(type) {
			case PICK_UP_RIGHT:
				arm = Arm.R;
				preConditions.add(new Predicate(PredicateType.ON_TABLE));
				preConditions.add(new Predicate(PredicateType.CLEAR));
				preConditions.add(new Predicate(PredicateType.EMPTY_ARM));
				deletedConditions.add(new Predicate(PredicateType.ON_TABLE));
				deletedConditions.add(new Predicate(PredicateType.EMPTY_ARM));
				addedConditions.add(new Predicate(PredicateType.HOLDING));
				break;
				
			case PICK_UP_LEFT:
				arm = Arm.L;
				preConditions.add(new Predicate(PredicateType.ON_TABLE));
				preConditions.add(new Predicate(PredicateType.CLEAR));
				preConditions.add(new Predicate(PredicateType.EMPTY_ARM));
				preConditions.add(new Predicate(PredicateType.LIGHT_BLOCK));
				deletedConditions.add(new Predicate(PredicateType.ON_TABLE));
				deletedConditions.add(new Predicate(PredicateType.EMPTY_ARM));
				addedConditions.add(new Predicate(PredicateType.HOLDING));
				break;
				
			case UNSTACK_RIGHT:
				arm = Arm.R;
				preConditions.add(new Predicate(PredicateType.ON));
				preConditions.add(new Predicate(PredicateType.CLEAR));
				preConditions.add(new Predicate(PredicateType.EMPTY_ARM));
				deletedConditions.add(new Predicate(PredicateType.ON));
				deletedConditions.add(new Predicate(PredicateType.EMPTY_ARM));
				addedConditions.add(new Predicate(PredicateType.HOLDING));
				addedConditions.add(new Predicate(PredicateType.CLEAR));
				break;
				
			case UNSTACK_LEFT:
				arm = Arm.L;
				preConditions.add(new Predicate(PredicateType.ON));
				preConditions.add(new Predicate(PredicateType.CLEAR));
				preConditions.add(new Predicate(PredicateType.EMPTY_ARM));
				preConditions.add(new Predicate(PredicateType.LIGHT_BLOCK));
				deletedConditions.add(new Predicate(PredicateType.ON));
				deletedConditions.add(new Predicate(PredicateType.EMPTY_ARM));
				addedConditions.add(new Predicate(PredicateType.HOLDING));
				addedConditions.add(new Predicate(PredicateType.CLEAR));
				break;
				
			case STACK:
				preConditions.add(new Predicate(PredicateType.HOLDING));
				preConditions.add(new Predicate(PredicateType.CLEAR));
				preConditions.add(new Predicate(PredicateType.HEAVIER));
				deletedConditions.add(new Predicate(PredicateType.CLEAR));
				deletedConditions.add(new Predicate(PredicateType.HOLDING));
				addedConditions.add(new Predicate(PredicateType.ON));
				addedConditions.add(new Predicate(PredicateType.EMPTY_ARM));
				break; 
				
			case LEAVE:
				preConditions.add(new Predicate(PredicateType.HOLDING));
				preConditions.add(new Predicate(PredicateType.USED_COLS_NUM));
				deletedConditions.add(new Predicate(PredicateType.HOLDING));
				addedConditions.add(new Predicate(PredicateType.ON_TABLE));
				addedConditions.add(new Predicate(PredicateType.EMPTY_ARM));
				addedConditions.add(new Predicate(PredicateType.USED_COLS_NUM));
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
		for(Predicate prec:preConditions){
			System.out.println(prec.generatePredicate(block1, block2, arm, 2));
		}
		System.out.print("\n-------------------------------------");
		System.out.print("\nADD List:\n");
		System.out.print("-------------------------------------\n");
		for(Predicate acond:addedConditions){
			System.out.println(acond.generatePredicate(block1, block2, arm, 2));
		}
		System.out.print("\n-------------------------------------");
		System.out.print("\nDELETE List:\n");
		System.out.print("-------------------------------------\n");
		for(Predicate dcond:deletedConditions){
			System.out.println(dcond.generatePredicate(block1, block2, arm, 2));
		}
		
	}
}

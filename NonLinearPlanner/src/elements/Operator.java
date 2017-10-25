package elements;

import java.util.ArrayList;
import elements.GenericOperator.Arm;
import elements.GenericOperator.OperatorType;

public class Operator {
	private static final String[] EXCLUDED_CONDITIONS = {"USED-COLS-NUM(n) n>0","USED-COLS-NUM(n+1)","USED-COLS-NUM(n-1)"};
	private ArrayList<String> preConditions = new ArrayList<String>();
	private ArrayList<String> addedConditions = new ArrayList<String>();
	private ArrayList<String> deletedConditions = new ArrayList<String>();
	private Arm arm;
	private OperatorType type; 
	private String rootString;
	private Block block1 = null;
	private Block block2 = null; 
	private int numOfBlocks = 0; //Number of blocks involved in this operation 
	
	public Operator(OperatorType type, int numOfBlocks, ArrayList<String> preConditions, ArrayList<String> addedConditions,
			ArrayList<String> deletedConditions, Arm arm, Block block1, Block block2) {
		this.type = type;
		this.numOfBlocks = numOfBlocks;
		this.arm = arm; 
		this.preConditions = preConditions;
		this.addedConditions = addedConditions;
		this.deletedConditions = deletedConditions;
		this.block1 = block1;
		if(numOfBlocks > 1) {
			this.block2 = block2;
		}else {
			this.block2 = null;
		}
		setRootString(type);
	}

	/*
	 * Method used to create the rootString with the proper name of the method
	 */
	private void setRootString(OperatorType type) {
		switch(type) {
		case PICK_UP_RIGHT:
			rootString = "PICK-UP-RIGHT";
			break;
		case PICK_UP_LEFT:
			rootString = "PICK-UP-LEFT";
			break;
		case UNSTACK_RIGHT:
			rootString = "UNSTACK-RIGHT";
			break;
		case UNSTACK_LEFT:
			rootString = "UNSTACK-LEFT";
			break;
		case STACK_RIGHT:
			rootString = "STACK-RIGHT";
			break; 
		case STACK_LEFT:
			rootString = "STACK-LEFT";
			break; 
		case LEAVE_RIGHT:
			rootString = "LEAVE-RIGHT";
			break;
		case LEAVE_LEFT:
			rootString = "LEAVE-LEFT";
			break;
		}
	}
	
	/*
	 * Getters and Setters
	 */
	public ArrayList<String> getPreConditions() {
		return preConditions;
	}

	public void setPreConditions(ArrayList<String> preConditions) {
		this.preConditions = preConditions;
	}

	public ArrayList<String> getAddedConditions() {
		return addedConditions;
	}

	public void setAddedConditions(ArrayList<String> addedConditions) {
		this.addedConditions = addedConditions;
	}

	public ArrayList<String> getDeletedConditions() {
		return deletedConditions;
	}

	public void setDeletedConditions(ArrayList<String> deletedConditions) {
		this.deletedConditions = deletedConditions;
	}
	
	/*
	 * Method to print the operator
	 */
	public String printOperator() {
		String result = rootString + "(";
		result = result + block1.name;
		if(numOfBlocks>1 && block2 !=null) {
			result = result + "," + block2.name;
		}
		result = result + ")";
		return result;
	}
	
	/*
	 * Method to print all the information of the operator. Just for testing
	 */
	public void printAll() {
		System.out.println(String.format("\n\nOperator: %s",printOperator()));
		System.out.println("----------------------");
		System.out.println("Preconditions:");
		System.out.println("----------------------");
		for (String predicate : preConditions) {
			System.out.println(predicate);
		}
		System.out.println("----------------------");
		System.out.println("Add:");
		System.out.println("----------------------");
		for (String predicate : addedConditions) {
			System.out.println(predicate);
		}
		System.out.println("----------------------");
		System.out.println("Delete:");
		System.out.println("----------------------");
		for (String predicate : deletedConditions) {
			System.out.println(predicate);
		}
	}
	
	/**
	 * This method checks if the condition is in the add list.
	 * @param condition
	 */
	public boolean isInAddList(String condition) {
		if(addedConditions.contains(condition)) {
			return true;
		}else {
			return false;	
		}
	}
	
	/**
	 * This method checks if the condition is in the delete list.
	 * @param condition
	 */
	public boolean isInDelList(String condition) {
		if(deletedConditions.contains(condition)) {
			return true;
		}else {
			return false;	
		}
	}
	
	public boolean matchAddedConditions(ArrayList<String> predicates) {
		ArrayList<String> tempAddList = new ArrayList<String>(addedConditions);
		for(String exCond : EXCLUDED_CONDITIONS) {
			tempAddList.remove(exCond);
		}
		return predicates.containsAll(tempAddList);
	}
	
	@Override
	public boolean equals(Object other) {
		if (!(other instanceof Operator)) {
	        return false;
	    }
		
		Operator op = (Operator) other;
		
		//Custom equality check
		return this.addedConditions.equals(op.addedConditions)
				&& this.deletedConditions.equals(op.deletedConditions)
				&& this.preConditions.equals(op.preConditions)
				&& this.arm == op.arm
				&& this.block1 == op.block1
				&& this.block2 == op.block2
				&& this.numOfBlocks == op.numOfBlocks
				&& this.type == op.type
				&& this.rootString == op.rootString;
	}
	
	
}

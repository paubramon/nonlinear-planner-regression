package elements;

import java.util.ArrayList;

import elements.GenericOperator.OperatorType;
import main.Planner.Arm;

public class Operator {
	private ArrayList<Predicate> preConditions = new ArrayList<Predicate>();
	private ArrayList<Predicate> addedConditions = new ArrayList<Predicate>();
	private ArrayList<Predicate> deletedConditions = new ArrayList<Predicate>();
	private Arm arm;
	private OperatorType type; 
	private Block block1 = null;
	private Block block2 = null; 
	
	public Operator(OperatorType type, ArrayList<Predicate> preConditions, ArrayList<Predicate> addedConditions,
			ArrayList<Predicate> deletedConditions, Arm arm, Block block1, Block block2) {
		this.type = type;
		this.arm = arm; 
		this.preConditions = preConditions;
		this.addedConditions = addedConditions;
		this.deletedConditions = deletedConditions;
		this.block1 = block1;
		this.block2 = block2;
	}
	
}

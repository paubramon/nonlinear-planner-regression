package planner;

import java.util.ArrayList;

import elements.Block;
import elements.GenericOperator;
import elements.Operator;
import elements.State;

public class NonLinearPlannerRegression {
	private ArrayList<GenericOperator> operators = new ArrayList<GenericOperator>();
	private State finalState;
	private State initialState;
	private ArrayList<Block> blocks = new ArrayList<Block>();
	private ArrayList<String> environmentConditions = new ArrayList<String>();
	private ArrayList<State> stateLevel = new ArrayList<State>();
	private ArrayList<State> visitedStates = new ArrayList<State>();
	
	public NonLinearPlannerRegression(ArrayList<GenericOperator> operators, State initialState, 
			State finalState, ArrayList<Block> blocks) {
		this.operators = operators;
		this.initialState = initialState;
		this.finalState = finalState; 
		this.environmentConditions = environmentConditions;
		this.blocks = blocks; 
		stateLevel.add(finalState);
	}
	
	public ArrayList<String> runPlanner(){
		
		findChilds(finalState);
		
		return null;
	}
	
	/**
	 * This method finds all possible child states for a given goal state.
	 */
	private void findChilds(State goalState) {
		ArrayList<String> finalConditions = finalState.getPredicates();
		ArrayList<Operator> possibleOperators = new ArrayList<Operator>();
		for(String condition : finalConditions) {
			for(GenericOperator operator : operators) {
				possibleOperators.addAll(operator.findCombinations(blocks, condition));	
			}
		}
		for(Operator operator : possibleOperators) {
			System.out.println(operator.printOperator());
		}
	}
	
}

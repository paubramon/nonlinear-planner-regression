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
		ArrayList<State> childs = new ArrayList<State>();
		ArrayList<String> finalConditions = finalState.getPredicates();
		ArrayList<Operator> possibleOperators = new ArrayList<Operator>();
		ArrayList<Operator> validOperators = new ArrayList<Operator>();
		
		//This loop searches all the possible operators that could add one of the conditions in the final state.
		for(String condition : finalConditions) {
			for(GenericOperator operator : operators) {
				possibleOperators.addAll(operator.findCombinations(blocks, condition));	
			}
		}
		
		//These loops will discard the operators that don't pass the Regression function from the list of possible operators 
		State possibleState;
		int resultRegression = 0;
		boolean stateValid = true;
		for(Operator pOperator : possibleOperators) {
			stateValid = true;
			possibleState = new State();
			possibleState.setUsedOperators(goalState.getUsedOperators());
			possibleState.addAllPredicate(pOperator.getPreConditions());
			possibleState.addOperator(pOperator.printOperator());
			
			//Lets check with the regression function if all the conditions are achievable
			for(String condition : finalConditions) {
				resultRegression = RegressionFunc(condition, pOperator);
				if(resultRegression == 0) {
					//condition not achievable (it's in the delete list of the operator)
					stateValid = false;
					break;
				}else if(resultRegression == 2){
					//condition must be already in this state
					possibleState.addPredicate(condition);
				}
				//Otherwise, the condition is achieved with the operator
			}
			if(stateValid) {
				childs.add(possibleState);
				validOperators.add(pOperator);
			}
		}
		
		System.out.println(String.format("The number of possible operators was %d, while the final valid operators is %d",possibleOperators.size(),validOperators.size()));
		
		for(Operator operator : validOperators) {
			System.out.println(operator.printOperator());
		}
	}
	
	/**
	 * This is the regression function of a NonLinear Planner with Regression. When this function returns 1,
	 * the condition is achievable with the operator, so the operator is still valid and we can 
	 * check another condition. When this function returns 0, we must discard the operation, since 
	 * the condition is incompatible with the operator. Finally, when this function returns 2, we
	 * must add this condition to the new state. 
	 * @param condition : Condition to be checked
	 * @param operator : Operator to be checked
	 * @return : It will return an int with three possible values: 0, 1, 2.
	 */
	private int RegressionFunc(String condition, Operator operator) {
		if(operator.isInAddList(condition)) return 1;
		if(operator.isInDelList(condition)) return 0;
		return 2;
	}
	
}

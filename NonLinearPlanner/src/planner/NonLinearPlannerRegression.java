package planner;

import java.util.ArrayList;

public class NonLinearPlannerRegression {
	private ArrayList<Operator> operators = new ArrayList<Operator>();
	private State finalState;
	private State initialState;
	private ArrayList<State> stateLevel = new ArrayList<State>();
	private ArrayList<State> visitedStates = new ArrayList<State>();
	
	public NonLinearPlannerRegression(ArrayList<Operator> operators, State InitialState, State FinalState) {
		this.operators = operators;
		this.initialState = initialState;
		this.finalState = finalState; 
		stateLevel.add(finalState);
	}
	
	public ArrayList<String> runPlanner(){
		
		for(Operator operators:operators) {
			
		}
		
		return null;
	}
	
}

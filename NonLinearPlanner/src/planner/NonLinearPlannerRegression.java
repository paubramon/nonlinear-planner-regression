package planner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import display.MultipleStateDisplay;
import display.SimpleGraph;
import elements.Block;
import elements.GenericOperator;
import elements.Operator;
import elements.PredicateHelper;
import elements.State;

public class NonLinearPlannerRegression {

	public static final int NUM_CANCELLED_STATES = 100;
	public static final String TEXT_SEPARATOR = "-------------------------------------------\n";
	public static final int MAX_NUMBER_OF_OPERATORS = 100000;
	public static final boolean SIMILARITY_CONSTRAIN = true;
	public static final boolean SAVE_GRAPH = true;

	private ArrayList<GenericOperator> operators = new ArrayList<GenericOperator>();
	private State finalState;
	private State initialState;
	private ArrayList<Block> blocks = new ArrayList<Block>();
	private ArrayList<State> stateQueue = new ArrayList<State>();
	private ArrayList<State> visitedStates = new ArrayList<State>();
	private int totalNumStates = 0;
	private String textCancelledStates = "";
	private int storedCancelledStates = 0;
	private String textCorrectPlan = "";
	private int totalOperations = 0;
	private int algorithmIterations = 0;
	private int similarityGoal = 0;
	private int similarityNew = 0;
	ArrayList<String> common;
	private long startTime = 0;
	private long stopTime = 0;

	public NonLinearPlannerRegression(ArrayList<GenericOperator> operators, State initialState, State finalState,
			ArrayList<Block> blocks) {
		this.operators = operators;
		this.initialState = initialState;
		this.finalState = finalState;
		this.blocks = blocks;
	}

	/*
	 * Getters
	 */
	public String getTextCancelledStates() {
		return textCancelledStates;
	}

	public String getTextCorrectPlan() {
		return textCorrectPlan;
	}

	public boolean runPlanner(boolean printOutput) {
		startTime = System.currentTimeMillis();

		// Initialization of variables
		textCancelledStates = "Details of the states that were cancelled:\n"; // Variables for storing text of the
																				// cancelled states
		textCancelledStates = textCancelledStates + TEXT_SEPARATOR;
		textCorrectPlan = "";
		storedCancelledStates = 0; // Variables for counting the states that were cancelled
		totalNumStates = 1; // Number of visited states (repeated or not)
		stateQueue = new ArrayList<State>();
		visitedStates = new ArrayList<State>();
		similarityGoal = similarityFunction(initialState.getPredicates(), finalState.getPredicates());

		// Run the algorithm
		stateQueue.add(finalState);
		State tempState = new State();
		boolean solved = false;
		algorithmIterations = 0;
		while (!solved && !stateQueue.isEmpty() && algorithmIterations < MAX_NUMBER_OF_OPERATORS) {
			// System.out.println("Analizing...");
			if (printOutput && algorithmIterations % 1000 == 0) {
				System.out.println("Analizing... Number of iterations = " + algorithmIterations + ";");
			}
			tempState = stateQueue.get(0);
			if (tempState.equals(initialState)) {
				solved = true;
				break;
			} else if (!visitedStates.contains(tempState)) {
				stateQueue.addAll(findChilds(tempState));
				visitedStates.add(tempState);
			}
			stateQueue.remove(0);
			algorithmIterations++;
		}
		stopTime = System.currentTimeMillis();
		if (solved && !tempState.getUsedOperators().isEmpty()) {
			totalOperations = tempState.getUsedOperators().size();
			// textCorrectPlan = "Solved!!\n\n";
			textCorrectPlan = textCorrectPlan + "Number of operators of the plan: " + totalOperations + "\n";
			textCorrectPlan = textCorrectPlan + "Number of states generated to solve the problem: "
					+ visitedStates.size() + "\n";
			// textCorrectPlan = textCorrectPlan + "Total number of correct and incorrect
			// states generated to solve the problem: " + totalNumStates + "\n";
			textCorrectPlan = textCorrectPlan + "Plan: " + String.join(",", tempState.getUsedOperators());

			if (printOutput) {
				System.out.println("Solved!!");
				System.out.println("Number of operators of the plan: " + totalOperations);
				System.out.println("Number of states generated to solve the problem: " + visitedStates.size());
				System.out.println("Total number of correct and incorrect states generated to solve the problem: "
						+ totalNumStates);
				System.out.println("Plan: " + String.join(",", tempState.getUsedOperators()));

				System.out.println(String.format("\nFirst %d cancelled States", NUM_CANCELLED_STATES));
				System.out.println(textCancelledStates);
				MultipleStateDisplay.printStates(tempState, totalOperations, blocks);
			}
			return true;
		} else {
			totalOperations = 0;
			textCorrectPlan = "Unable to find a plan...";
			if (printOutput) {
				System.out.println("Unable to find a plan...");
			}
			return false;
		}
	}

	/**
	 * This method finds all possible child states for a given goal state.
	 */
	private ArrayList<State> findChilds(State goalState) {
		ArrayList<State> childs = new ArrayList<State>();
		ArrayList<String> finalConditions = goalState.getPredicates();
		ArrayList<Operator> possibleOperators = new ArrayList<Operator>();
		ArrayList<Operator> validOperators = new ArrayList<Operator>();

		similarityNew = 0;

		// This loop searches all the possible operators that could add one of the
		// conditions in the final state.
		for (String condition : finalConditions) {
			for (GenericOperator operator : operators) {
				for (Operator opp : operator.findCombinations(blocks, condition, goalState.getPredicates())) {
					if (!possibleOperators.contains(opp))
						possibleOperators.add(opp);
				}
			}
		}

		// These loops will discard the operators that don't pass the Regression
		// function from the list of possible operators
		State possibleState;
		int resultRegression = 0;
		boolean stateValid = true;
		// System.out.println("\n\n-------------------------------------------------");
		for (Operator pOperator : possibleOperators) {
			stateValid = true;
			possibleState = new State();
			possibleState.setUsedOperators(goalState.getUsedOperators());
			possibleState.addAllPredicate(pOperator.getPreConditions());
			possibleState.addOperator(pOperator.printOperator());
			possibleState.parentState = goalState;
			totalNumStates += 1;

			// Lets check with the regression function if all the conditions are achievable
			for (String condition : finalConditions) {
				resultRegression = RegressionFunc(condition, pOperator);
				if (resultRegression == 0) {
					// condition not achievable (it's in the delete list of the operator)
					stateValid = false;
					break;
				} else if (resultRegression == 2) {
					// condition must be already in this state
					possibleState.addPredicate(condition);
				}
				// Otherwise, the condition is achieved with the operator
			}
			if (stateValid) {
				// Check if the state has any contradictory predicates
				if (possibleState.isStateValid()) {
					similarityNew = similarityFunction(initialState.getPredicates(), possibleState.getPredicates());
					if (SIMILARITY_CONSTRAIN) {
						if (similarityNew >= (similarityGoal - 2)) {
							if (similarityNew > similarityGoal) {
								similarityGoal = similarityNew;
							}
							childs.add(possibleState);
							validOperators.add(pOperator);
						} else {
							addCancelExplanation(possibleState,
									"This state was cancelled because it wasn't goal oriented.");
						}
					} else {
						childs.add(possibleState);
						validOperators.add(pOperator);
					}
				} else {
					addCancelExplanation(possibleState, possibleState.stateExplanation);
				}
			}
		}

		return childs;
	}

	/**
	 * This method adds explanation for cancelling state to the textCancelledStates
	 * variable.
	 * 
	 * @param possibleState
	 * @param explanation
	 */
	private void addCancelExplanation(State possibleState, String explanation) {
		if (storedCancelledStates < NUM_CANCELLED_STATES) {
			storedCancelledStates += 1;
			textCancelledStates = textCancelledStates + Integer.toString(storedCancelledStates) + "\n";
			textCancelledStates = textCancelledStates + "Predicates -> "
					+ String.join(",", possibleState.getPredicates()) + "\n";
			textCancelledStates = textCancelledStates + "Operators Used -> "
					+ String.join(",", possibleState.getUsedOperators()) + "\n";
			textCancelledStates = textCancelledStates + "Reason for cancelling the exploration -> " + explanation
					+ "\n";
			textCancelledStates = textCancelledStates + TEXT_SEPARATOR;
		}
	}

	/**
	 * This is the regression function of a NonLinear Planner with Regression. When
	 * this function returns 1, the condition is achievable with the operator, so
	 * the operator is still valid and we can check another condition. When this
	 * function returns 0, we must discard the operation, since the condition is
	 * incompatible with the operator. Finally, when this function returns 2, we
	 * must add this condition to the new state.
	 * 
	 * @param condition
	 *            : Condition to be checked
	 * @param operator
	 *            : Operator to be checked
	 * @return : It will return an int with three possible values: 0, 1, 2.
	 */
	private int RegressionFunc(String condition, Operator operator) {
		if (operator.isInAddList(condition))
			return 1;
		if (operator.isInDelList(condition))
			return 0;
		return 2;
	}

	/**
	 * Given two arrays of predicates, this method returns a value of similarity.
	 * This value of similarity gives more importance to ON_TABLE predicates and
	 * ignores others not so important
	 * 
	 * @param initial
	 * @param possible
	 * @return
	 */
	private int similarityFunction(ArrayList<String> initial, ArrayList<String> possible) {
		int similarity = 0;
		String blockname1;
		String blockname2;
		for (String predicate : possible) {
			switch (PredicateHelper.findType(predicate)) {
			case ON_TABLE:
				if (initial.contains(predicate)) {
					similarity = similarity + 1;
				}
				break;
			case ON:
				blockname1 = predicate.substring(3, 4);
				blockname2 = predicate.substring(5, 6);
				if (initial.contains(predicate) && initial.contains("ON-TABLE(" + blockname2 + ")")
						&& possible.contains("ON-TABLE(" + blockname2 + ")")) {
					// Checks if we have already two blocks correctly stack
					similarity = similarity + 1;
				}
				break;
			case HOLDING:
				blockname1 = predicate.substring(8, 9);
				if (initial.contains("CLEAR(" + blockname1 + ")")) {
					similarity = similarity + 1;
				}
			default:
				break;
			}
		}
		return similarity;
	}

	/**
	 * It runs the runPlanner method for the parameters of the input file changing
	 * the value of the maxColumns number. Then it plots the statistics.
	 */
	public void runGraphForColumns(int type, String filename) {
		int number_of_blocks = blocks.size();
		long[] available_space = new long[number_of_blocks + 1];
		long[] operations = new long[number_of_blocks + 1];
		int first_solvable = 0;
		String titlegraph = "";
		String xLabel = "Max number of columns";
		String yLabel = "";
		String prefixFile = "";

		for (int i = 0; i <= number_of_blocks; i++) {
			if (i < finalState.getUsedSpace()) {
				// for a final state using more space than the available, the plan is impossible
				System.out.println(i);
				available_space[i] = i;
				operations[i] = 0;
			} else {
				System.out.println(i);
				available_space[i] = i;
				State.maxColumns = i;
				if (runPlanner(false)) {
					if (first_solvable == 0) {
						first_solvable = i;
					}
					if (type == 0) {
						// Number of iterations
						titlegraph = "Algorithm iterations vs MaxColumns";
						yLabel = "Algorithm iterations";
						operations[i] = algorithmIterations;
						prefixFile = "iterations";
					} else if (type == 1) {
						// Number of operations
						titlegraph = "Operations vs MaxColumns";
						yLabel = "Operations";
						operations[i] = totalOperations;
						prefixFile = "operations";
					} else if (type == 2) {
						// Time
						titlegraph = "Time vs MaxColumns";
						yLabel = "Time (ms)";
						operations[i] = stopTime - startTime;
						prefixFile = "time";
					} else if (type == 3) {
						// Time
						titlegraph = "Visited states vs MaxColumns";
						yLabel = "Number of visited states";
						operations[i] = visitedStates.size();
						prefixFile = "states";
					} else {
						// Number of operations
						titlegraph = "Operations vs MaxColumns";
						yLabel = "Operations";
						operations[i] = totalOperations;
						prefixFile = "operations";
					}
				} else {
					operations[i] = 0;
				}
			}
		}

		long[] x = new long[number_of_blocks - first_solvable + 1];
		long[] y = new long[number_of_blocks - first_solvable + 1];

		for (int i = 0; i <= number_of_blocks - first_solvable; i++) {
			x[i] = available_space[i + first_solvable];
			y[i] = operations[i + first_solvable];
		}
		System.out.println("x axis: " + Arrays.toString(x));
		System.out.println("y axis: " + Arrays.toString(y));
		SimpleGraph chart2 = new SimpleGraph(titlegraph, xLabel, yLabel, x, y);
		chart2.displayPlot();
		if (SAVE_GRAPH) {
			try {
				chart2.saveJPEG(prefixFile + "_" + filename);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}

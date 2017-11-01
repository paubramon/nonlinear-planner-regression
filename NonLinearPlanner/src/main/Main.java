package main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import elements.Block;
import elements.GenericOperator;
import elements.State;
import planner.NonLinearPlannerRegression;
import elements.GenericOperator.OperatorType;

public class Main {

	// Options of the planner
	public static final String INPUT_FILE = "testing1";
	public static final boolean FIND_GRAPHS = false; // true -> tries to solve the system with different number of
													 // columns and plots graphs of the results
	public static final int TYPE_OF_GRAPH = 2; // 0-> Algorithm iterations, 1-> Operations, 2-> Time, 3-> Visited States
	public static final boolean PRINT_ALL = true; // true -> prints and draws all the results of the planner.

	// Constants
	public static final String FILE_EXTENSION = ".txt";
	public static final String INPUT_PATH = "files/";
	public static final String OUTPUT_PATH = "output/";

	// Variables of the planner
	public static ArrayList<GenericOperator> operators = new ArrayList<GenericOperator>();
	public static ArrayList<Block> blocks = new ArrayList<Block>();
	public static State initialState;
	public static State finalState;
	public static NonLinearPlannerRegression nonLinearPlanner;

	public static void main(String[] args) {
		// Execute setup method
		setupPlanner();
		// Read input file
		readInputFile();
		// Get environment conditions
		findEnvironmentConditions();

		// Check if the final and initial states are possible
		if (initialState.isStateValid()) {
			System.out.println("Initial State valid");
		} else {
			System.out.println("Initial State NOT valid");
			System.out.println(initialState.stateExplanation);
		}
		if (finalState.isStateValid()) {
			System.out.println("Final State valid");
		} else {
			System.out.println("Final State NOT valid");
			System.out.println(finalState.stateExplanation);
		}

		runPlanner();

		createOutputFile();

	}

	/**
	 * This method runs the planner with nonLinearPlanner
	 */
	private static void runPlanner() {
		// Run the planner once
		nonLinearPlanner = new NonLinearPlannerRegression(operators, initialState, finalState, blocks);
		nonLinearPlanner.runPlanner(PRINT_ALL);

		// Plot time solution with different maxColumns
		if (FIND_GRAPHS) {
			nonLinearPlanner.runGraphForColumns(TYPE_OF_GRAPH, INPUT_FILE);
		}
	}

	/**
	 * This method creates an output file with the information of the
	 * non_linear_planner
	 */
	private static void createOutputFile() {
		// Create the output file
		String text_to_print = nonLinearPlanner.getTextCorrectPlan();
		text_to_print = text_to_print
				+ String.format("\n\n\nFirst %d cancelled States", NonLinearPlannerRegression.NUM_CANCELLED_STATES);
		text_to_print = text_to_print + nonLinearPlanner.getTextCancelledStates();
		printOutputFile(text_to_print, OUTPUT_PATH + "Solved_" + INPUT_FILE + FILE_EXTENSION);
	}

	/**
	 * This method sets up all parameters necessaries for the planner.
	 */
	private static void setupPlanner() {
		// Create operators list
		operators.add(new GenericOperator(OperatorType.LEAVE_RIGHT));
		operators.add(new GenericOperator(OperatorType.LEAVE_LEFT));
		operators.add(new GenericOperator(OperatorType.PICK_UP_LEFT));
		operators.add(new GenericOperator(OperatorType.PICK_UP_RIGHT));
		operators.add(new GenericOperator(OperatorType.STACK_RIGHT));
		operators.add(new GenericOperator(OperatorType.STACK_LEFT));
		operators.add(new GenericOperator(OperatorType.UNSTACK_LEFT));
		operators.add(new GenericOperator(OperatorType.UNSTACK_RIGHT));

		// This is used to check if the operators are correctly defined.
		/*
		 * Block a = new Block("A",2); Block b = new Block("B",2); for(GenericOperator
		 * op : operators) { op.getOperator(a, b).printAll(); }
		 */

	}

	/**
	 * This method reads the input file and store all the information in different
	 * objects.
	 */
	private static void readInputFile() {
		// Read input file
		String filename = INPUT_PATH + INPUT_FILE + FILE_EXTENSION;
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(filename));

			// Get MaxColumns
			String line = br.readLine();
			Matcher matcher = Pattern.compile("MaxColumns=(\\d*);").matcher(line);
			if (matcher.matches()) {
				State.maxColumns = Integer.parseInt(matcher.group(1));
			}

			// Get Blocks
			line = br.readLine();
			matcher = Pattern.compile("Blocks=(.*);").matcher(line);
			if (matcher.matches()) {
				List<String> blist = Arrays.asList(matcher.group(1).split("\\."));
				Block tempBlock = null;
				String str = null;
				for (String strBlock : blist) {
					str = strBlock.trim();
					tempBlock = new Block(str.substring(0, 1), str.length() - 1);
					blocks.add(tempBlock);
				}
			}

			// Get Initial State
			line = br.readLine();
			matcher = Pattern.compile("InitialState=(.*);").matcher(line);
			if (matcher.matches()) {
				ArrayList<String> initialStateList = new ArrayList<String>();
				List<String> tempList = Arrays.asList(matcher.group(1).split("\\."));
				for (String predicate : tempList) {
					// removes spaces before and after the strings and adds a ")" at the end
					predicate = predicate.trim();
					if (!predicate.endsWith(")"))
						predicate = predicate + ")";
					initialStateList.add(predicate);
				}
				initialState = new State(initialStateList);
				System.out.println("Initial State");
				initialState.printState();
			}

			// Get GoalState
			line = br.readLine();
			matcher = Pattern.compile("GoalState=(.*);").matcher(line);
			if (matcher.matches()) {
				ArrayList<String> finalStateList = new ArrayList<String>();
				List<String> tempList = Arrays.asList(matcher.group(1).split("\\."));
				for (String predicate : tempList) {
					// removes spaces before and after the strings and adds a ")" at the end
					predicate = predicate.trim();
					if (!predicate.endsWith(")"))
						predicate = predicate + ")";
					finalStateList.add(predicate);
				}
				finalState = new State(new ArrayList<String>(finalStateList));
				System.out.println("Final State");
				finalState.printState();
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null) {
					br.close();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}

	}

	public static void printOutputFile(String text, String filename) {
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(new File(filename)));
			writer.write(text);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				// Close the writer
				writer.close();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
	}

	/**
	 * Looking at the different blocks and with the knowledge we have of the
	 * environment, we can add some predicates that will define our environment.
	 * This will provide the knowledge of the environment.
	 */
	public static void findEnvironmentConditions() {

		ArrayList<String> environmentPredicates = new ArrayList<String>();

		/*
		 * With the knowledge of the different block we can specify the weight
		 * relations. With this loop we will add predicates like HEAVIER(A,B) or
		 * LIGHT-BLOCK(B)
		 */
		for (Block block1 : blocks) {
			if (block1.weight == 1)
				environmentPredicates.add("LIGHT-BLOCK(" + block1.name + ")");
			for (Block block2 : blocks) {
				if (block1 != block2) {
					if (block1.weight >= block2.weight)
						environmentPredicates.add("HEAVIER(" + block1.name + "," + block2.name + ")");
				}
			}
		}

		State.environmentConditions.addAll(environmentPredicates);

	}
}

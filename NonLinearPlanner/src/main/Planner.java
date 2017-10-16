package main;

import java.io.BufferedReader;
import java.io.FileReader;
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

public class Planner {
	
	public static ArrayList<GenericOperator> operators = new ArrayList<GenericOperator>();
	public static ArrayList<State> stateLevel = new ArrayList<State>();
	public static ArrayList<Block> blocks = new ArrayList<Block>();
	public static State initialState;
	public static State finalState;
	
	public static void main(String[] args) {
		//Execute setup method
		setupPlanner(); 
		readInputFile();
		findEnvironmentConditions();
		NonLinearPlannerRegression planner = new NonLinearPlannerRegression(operators, initialState, finalState, blocks);
		planner.runPlanner();
		
		/* Block for testing State properties
		State prova = new State();
		prova.addPredicate("HOLDING(A,R)");
		prova.addPredicate("EMPTY-ARM(R)");
		System.out.println(String.format("State is: %b\nExplanation is: %s", prova.isStateValid(),prova.stateExplanation));
		*/
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
		
		
		//This is used to check if the operators are correctly defined.
		/*Block a = new Block("A",2);
		Block b = new Block("B",2);
		for(GenericOperator op : operators) {
			op.getOperator(a, b).printAll();
		}*/
		
	}
	
	/**
	 * This method reads the input file and store all the information in different objects.  
	 */
	private static void readInputFile() {
		//Read input file
		String filename = "files/testing1.txt";
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(filename));
			
			//Get MaxColumns
			String line = br.readLine();
			Matcher matcher = Pattern.compile("MaxColumns=(\\d*);").matcher(line);
			if(matcher.matches()) {
				State.MaxColumns = Integer.parseInt(matcher.group(1));
			}
			
			//Get Blocks
			line = br.readLine();
			matcher = Pattern.compile("Blocks=(.*);").matcher(line);
			if(matcher.matches()) {
				List<String> blist = Arrays.asList(matcher.group(1).split("\\."));
				Block tempBlock = null;
				String str = null;
				for(String strBlock : blist) {
					str = strBlock.trim();
					tempBlock = new Block(str.substring(0, 1),str.length()-1);
					blocks.add(tempBlock);
				}
			}
			
			//Get Initial State
			line = br.readLine();
			matcher = Pattern.compile("InitialState=(.*);").matcher(line);
			if(matcher.matches()) {
				ArrayList<String> initialStateList = new ArrayList<String>();
				List<String> tempList = Arrays.asList(matcher.group(1).split("\\."));
				for(String predicate : tempList) {
					//removes spaces before and after the strings and adds a ")" at the end
					predicate = predicate.trim();
					if (!predicate.endsWith(")")) predicate = predicate + ")";
					initialStateList.add(predicate); 
				}
				initialState = new State(initialStateList);
				System.out.println("Initial State");
				initialState.printState();
			}
			
			//Get GoalState
			line = br.readLine();
			matcher = Pattern.compile("GoalState=(.*);").matcher(line);
			if(matcher.matches()) {
				ArrayList<String> finalStateList = new ArrayList<String>();
				List<String> tempList = Arrays.asList(matcher.group(1).split("\\."));
				for(String predicate : tempList) {
					//removes spaces before and after the strings and adds a ")" at the end
					predicate = predicate.trim();
					if (!predicate.endsWith(")")) predicate = predicate + ")";
					finalStateList.add(predicate); 
				}
				finalState = new State(new ArrayList<String>(finalStateList));
				System.out.println("Final State");
				finalState.printState();
			}
			
		}catch (IOException e) {
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
	
	/**
	 * Looking at the different blocks and with the knowledge we have of the environment, 
	 * we can add some predicates that will define our environment. This will provide the knowledge of the environment.
	 */
	public static void findEnvironmentConditions() {
		
		ArrayList<String> environmentPredicates = new ArrayList<String>();
		
		/*
		 * With the knowledge of the different block we can specify the weight relations. 
		 * With this loop we will add predicates like HEAVIER(A,B) or LIGHT-BLOCK(B)
		 */
		for(Block block1 : blocks) {
			if(block1.weight == 1) environmentPredicates.add("LIGHT-BLOCK(" + block1.name + ")");
			for(Block block2 : blocks) {
				if(block1 != block2) {
					if(block1.weight >= block2.weight) environmentPredicates.add("HEAVIER(" + block1.name + "," + block2.name + ")");
				}
			}
		}
		
		State.environmentConditions.addAll(environmentPredicates);
		
		
	}
}

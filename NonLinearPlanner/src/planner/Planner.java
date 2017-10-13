package planner;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import planner.Operator.OperatorType;

public class Planner {
	public static enum Arm{
		R,L
	}
	public static ArrayList<Operator> operators = new ArrayList<Operator>();
	public static ArrayList<State> stateLevel = new ArrayList<State>();
	public static ArrayList<Block> blocks = new ArrayList<Block>();
	public static State InitialState = new State();
	public static State FinalState = new State();
	public static int MaxColumns;
	
	public static void main(String[] args) {
		//Execute setup method
		setupPlanner(); 
		readInputFile();
	}
	
	/*
	 * This method sets up all parameters necessaries for the planner. 
	 */
	private static void setupPlanner() {
		// Create operators list
		operators.add(new Operator(OperatorType.LEAVE));
		operators.add(new Operator(OperatorType.PICK_UP_LEFT));
		operators.add(new Operator(OperatorType.PICK_UP_RIGHT));
		operators.add(new Operator(OperatorType.STACK));
		operators.add(new Operator(OperatorType.UNSTACK_LEFT));
		operators.add(new Operator(OperatorType.UNSTACK_RIGHT));
	}
	
	private static void readInputFile() {
		//Read input file
		String filename = "C:\\Pau\\eclipse_workspace\\test.txt";
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(filename));
			
			//Get MaxColumns
			String line = br.readLine();
			Matcher matcher = Pattern.compile("MaxColumns=(\\d*)").matcher(line);
			if(matcher.matches()) {
				MaxColumns = Integer.parseInt(matcher.group(1));
			}
			
			//Get Blocks
			line = br.readLine();
			matcher = Pattern.compile("Blocks=(.*);").matcher(line);
			if(matcher.matches()) {
				List<String> blist = Arrays.asList(matcher.group(1).split(","));
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
				List<String> initialStateList = Arrays.asList(matcher.group(1).split(","));
				InitialState.setPredicates(new ArrayList<String>(initialStateList));
			}
			
			//Get GoalState
			line = br.readLine();
			matcher = Pattern.compile("GoalState=(.*);").matcher(line);
			if(matcher.matches()) {
				List<String> finalStateList = Arrays.asList(matcher.group(1).split(","));
				FinalState.setPredicates(new ArrayList<String>(finalStateList));
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
}
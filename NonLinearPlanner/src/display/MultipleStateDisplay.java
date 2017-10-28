package display;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.commons.io.FileUtils;

import elements.PredicateHelper;
import elements.State;
import elements.PredicateHelper.PredicateType;
import main.Main;

public class MultipleStateDisplay {
	private String[] bestColumns = new String[20];
	public boolean free;

	public MultipleStateDisplay() {
		clearBestColumns();
		free = true;
	}
	public static void printStates(State firstState, int totalOperations) {
		// DISPLAY STATES
		State displayedState = firstState;
		MultipleStateDisplay multiDisplay = new MultipleStateDisplay();
		int jj = 0;
		String title;

		// Create Folder to store images
		String directory = "./images/" + Main.INPUT_FILE;
		try {
			File dir = new File(directory);
			dir.mkdir();
			FileUtils.cleanDirectory(dir);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		while (displayedState != null) {
			if (jj == 0) {
				title = "Initial_State";
			} else if (jj == totalOperations) {
				title = "Final_State";
			} else {
				title = "State_" + Integer.toString(jj);
			}
			multiDisplay.printNewState(displayedState, title, directory);
			displayedState = displayedState.parentState;
			jj++;
		}
	}
	public void printNewState(State state, String figTitle, String directory) {
		free = false;
		// Variables to print the state
		String[] columnLastElement = new String[State.maxColumns];
		int[] columnHeight = new int[State.maxColumns];
		String[] tempLast = new String[State.maxColumns];
		int[] tempHeight = new int[State.maxColumns];
		
		//initialize
		for (int i = 1; i <= State.maxColumns; i++) {
			columnLastElement[i - 1] = "";
			columnHeight[i - 1] = 0;
			tempLast[i-1] = "";
			tempHeight[i-1] = 0;
		}

		// Create array of predicates
		ArrayList<String> predicates = new ArrayList<String>(state.getPredicates());

		// Lets print on table
		int jj = 1;
		for (String predicate : predicates) {
			if (PredicateHelper.findType(predicate) == PredicateType.ON_TABLE) {
				columnLastElement[jj - 1] = predicate.substring(9, 10);
				columnHeight[jj - 1] = CreateStateImage.GROUND - CreateStateImage.BLOCK_HEIGHT;
				jj++;
			}
		}

		for (int i = 0; i < columnLastElement.length; i++) {
			for (int i_sub = 0; i_sub < bestColumns.length; i_sub++) {
				if (bestColumns[i_sub].equals(columnLastElement[i]) && !columnLastElement[i].equals("")) {
					tempLast[i_sub] = columnLastElement[i];
					tempHeight[i_sub] = columnHeight[i];
					break;
				}
			}
		}
		for (int i = 0; i < columnLastElement.length; i++) {
			if (findElementInColumn(tempLast, columnLastElement[i]) == 0) {
				for (int j = 0; j < columnLastElement.length; j++) {
					if (tempLast[j].isEmpty()) {
						tempLast[j] = columnLastElement[i];
						tempHeight[j] = columnHeight[i];
						break;
					}
				}
			}
		}
		columnLastElement = tempLast;
		columnHeight = tempHeight;

		//Rewrite bestColumns again.
		clearBestColumns();
		for (int i = 1; i <= columnLastElement.length; i++) {
			this.bestColumns[i - 1] = columnLastElement[i - 1];
		}
		
		CreateStateImage stateImage = new CreateStateImage(state, figTitle, columnLastElement, columnHeight);
		stateImage.createGrafic(directory);
	}
	
	private int findElementInColumn(String[] columns, String target) {
		int isThere = 0;
		for (int i = 0; i < columns.length; i++) {
			if (columns[i] != null){
				if (columns[i].equals(target)) {
					isThere = i + 1;
				}
			}
		}
		return isThere;
	}
	
	public void clearBestColumns() {
		for (int i = 0; i < 20; i++) {
			bestColumns[i] = "";
		}
	}
}

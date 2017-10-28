package display;

import java.util.ArrayList;

import elements.PredicateHelper;
import elements.State;
import elements.PredicateHelper.PredicateType;

public class MultipleStateDisplay {
	private String[] bestColumns = new String[20];
	public boolean free;

	public MultipleStateDisplay() {
		clearBestColumns();
		free = true;
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

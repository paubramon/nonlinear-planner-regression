package main;

import java.awt.*;
import java.util.ArrayList;

import javax.swing.*;
import elements.*;
import elements.Predicate.PredicateType;

public class StateDisplay extends JPanel {

	public static final int SIZE_Y = 600;
	public static final int SIZE_X = 800;
	public static final int MARGIN = 10;
	public static final int MARGIN_TITLE = 40;
	public static final int BLOCK_HEIGHT = 30;

	private State state;
	private String figTitle;
	private static String[] bestColumns = new String[20];
	private String[] firstRow;
	private int[] firstRowHeight;
	private boolean predefinedFirstRow = false;

	public StateDisplay(State state, String figTitle) {
		this.state = state;
		this.figTitle = figTitle;
		predefinedFirstRow = false;
	}

	public StateDisplay(State state, String figTitle, String[] firstRow, int[] firstRowHeight) {
		this.state = state;
		this.figTitle = figTitle;
		this.firstRow = firstRow;
		this.firstRowHeight = firstRowHeight;
		predefinedFirstRow = true;
	}

	@Override
	public void paintComponent(Graphics g) {

		// White background
		Graphics2D background = (Graphics2D) g;
		background.setColor(Color.WHITE);
		background.setBackground(Color.WHITE);
		background.fillRect(MARGIN, MARGIN + MARGIN_TITLE, SIZE_X - (4 * MARGIN), SIZE_Y - (7 * MARGIN) - MARGIN_TITLE);
		background.setColor(Color.BLACK);
		background.setFont(new Font("default", Font.BOLD, 20));
		background.drawString(figTitle, SIZE_X / 2 - figTitle.length() * 5, 35);

		// Ground Line
		Graphics2D ground = (Graphics2D) g;
		int ground_value = SIZE_Y - (9 * MARGIN);
		ground.setColor(Color.BLACK);
		ground.setStroke(new BasicStroke(2.0f));
		ground.drawLine(MARGIN, ground_value, SIZE_X - (3 * MARGIN), ground_value);

		// Variables to print the state
		int step_size = SIZE_X / (state.maxColumns + 1);
		int block_size = step_size - 30;
		String[] columnLastElement = new String[state.maxColumns];
		int[] columnHeight = new int[state.maxColumns];

		// Print Hands
		Graphics2D Hand = (Graphics2D) g;
		// Right
		int handPos_x = SIZE_X / 3;
		int handPos_y = MARGIN + MARGIN_TITLE;
		int[] posRightHand = { handPos_x - block_size / 2, handPos_y + 30 };
		Hand.setColor(Color.BLACK);
		Hand.setFont(new Font("default", Font.BOLD, 16));

		Hand.drawLine(handPos_x, handPos_y, handPos_x, handPos_y + 30);
		Hand.drawString("R", handPos_x - 20, handPos_y + 15);
		Hand.drawLine(handPos_x - block_size / 2, handPos_y + 30, handPos_x + block_size / 2, handPos_y + 30);
		Hand.drawLine(handPos_x - block_size / 2, handPos_y + 30, handPos_x - block_size / 2, handPos_y + 40);
		Hand.drawLine(handPos_x + block_size / 2, handPos_y + 30, handPos_x + block_size / 2, handPos_y + 40);
		// Left
		handPos_x = 2 * SIZE_X / 3;
		int[] posLeftHand = { handPos_x - block_size / 2, handPos_y + 30 };
		Hand.drawLine(handPos_x, handPos_y, handPos_x, handPos_y + 30);
		Hand.drawString("L", handPos_x - 20, handPos_y + 15);
		Hand.drawLine(handPos_x - block_size / 2, handPos_y + 30, handPos_x + block_size / 2, handPos_y + 30);
		Hand.drawLine(handPos_x - block_size / 2, handPos_y + 30, handPos_x - block_size / 2, handPos_y + 40);
		Hand.drawLine(handPos_x + block_size / 2, handPos_y + 30, handPos_x + block_size / 2, handPos_y + 40);

		// Column numbers
		for (int i = 1; i <= state.maxColumns; i++) {
			columnLastElement[i - 1] = "";
			columnHeight[i - 1] = 0;
			Graphics2D column = (Graphics2D) g;
			column.drawString(Integer.toString(i), step_size * i, SIZE_Y - (7 * MARGIN));
		}

		// Create array of predicates
		ArrayList<String> predicates = new ArrayList<String>(state.getPredicates());

		// Lets print on table
		if (predefinedFirstRow) {
			columnLastElement = firstRow;
			columnHeight = firstRowHeight;
		} else {
			int jj = 1;
			for (String predicate : predicates) {
				if (Predicate.findType(predicate) == PredicateType.ON_TABLE) {
					columnLastElement[jj - 1] = predicate.substring(9, 10);
					columnHeight[jj - 1] = ground_value - BLOCK_HEIGHT;
					jj++;
				}
			}
		}

		for (int i = 1; i <= columnLastElement.length; i++) {
			if (!columnLastElement[i - 1].isEmpty()) {
				drawBlock(g, ground_value, step_size * i, block_size, columnLastElement[i - 1]);
			}
		}

		// Lets print on()
		int jj;
		while (!predicates.isEmpty()) {
			String predicate = predicates.get(0);
			if (Predicate.findType(predicate) == PredicateType.ON) {
				String blockname1 = predicate.substring(3, 4);
				String blockname2 = predicate.substring(5, 6);
				jj = findElementInColumn(columnLastElement, blockname2);
				if (jj != 0) {
					drawBlock(g, columnHeight[jj - 1], step_size * jj, block_size, blockname1);
					columnLastElement[jj - 1] = blockname1;
					columnHeight[jj - 1] = columnHeight[jj - 1] - BLOCK_HEIGHT;
					predicates.remove(0);
				} else {
					predicates.remove(0);
					predicates.add(predicate);
				}
			} else if (Predicate.findType(predicate) == PredicateType.HOLDING) {
				String blockname1 = predicate.substring(8, 9);
				String armname = predicate.substring(10, 11);
				if (armname.equals("R")) {
					drawBlock(g, posRightHand[1] + BLOCK_HEIGHT, posRightHand[0] + block_size / 2, block_size,
							blockname1);
				} else {
					drawBlock(g, posLeftHand[1] + BLOCK_HEIGHT, posLeftHand[0] + block_size / 2, block_size,
							blockname1);
				}
				predicates.remove(0);
			} else {
				predicates.remove(0);
			}
		}
	}

	private int findElementInColumn(String[] columns, String target) {
		int isThere = 0;
		for (int i = 0; i < columns.length; i++) {
			if (columns[i] != null) {
				if (columns[i].equals(target)) {
					isThere = i + 1;
				}
			}
		}
		return isThere;
	}

	/**
	 * Draws a block at the specified position with the specified size
	 * 
	 * @param g
	 * @param ground_value
	 * @param centerBlock
	 * @param block_size
	 * @param predicate
	 */
	private void drawBlock(Graphics g, int ground_value, int centerBlock, int block_size, String blockname) {
		Graphics2D block = (Graphics2D) g;
		block.setColor(Color.ORANGE);
		block.setBackground(Color.ORANGE);
		block.fillRect(centerBlock - block_size / 2, ground_value - BLOCK_HEIGHT, block_size, BLOCK_HEIGHT);
		block.setColor(Color.BLACK);
		block.drawRect(centerBlock - block_size / 2, ground_value - BLOCK_HEIGHT, block_size, BLOCK_HEIGHT);
		block.setFont(new Font("default", Font.BOLD, 20));
		block.drawString(blockname, centerBlock - 5, ground_value + 6 - BLOCK_HEIGHT / 2);
	}

	public static void createStateDrawing(State state, String stateName) {
		JFrame frame = new JFrame();
		frame.setSize(SIZE_X, SIZE_Y);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setTitle(stateName);

		StateDisplay stateDisplay = new StateDisplay(state, stateName);
		frame.setContentPane(stateDisplay);

		frame.setVisible(true);
		frame.invalidate();
	}

	public static void createMultipleState(State state, String stateName, String[] firstRow, int[] firstRowHeight) {
		JFrame frame = new JFrame();
		frame.setSize(SIZE_X, SIZE_Y);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setTitle(stateName);

		StateDisplay stateDisplay = new StateDisplay(state, stateName, firstRow,firstRowHeight);
		frame.setContentPane(stateDisplay);

		frame.setVisible(true);
		frame.invalidate();
	}
}
/*
 * public class StateDisplay extends JPanel { public void paint(Graphics g) {
 * g.setFont(new Font("", 0, 100)); FontMetrics fm = getFontMetrics(new Font("",
 * 0, 100)); String s = "message"; int x = 5; int y = 5;
 * 
 * for (int i = 0; i < s.length(); i++) { char c = s.charAt(i); int h =
 * fm.getHeight(); int w = fm.charWidth(c);
 * 
 * g.drawRect(x, y, w, h); g.drawString(String.valueOf(c), x, y + h); x = x + w;
 * } }
 * 
 * public static void main(String[] args) { JFrame frame = new JFrame();
 * frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
 * frame.setContentPane(new StateDisplay()); frame.setSize(500, 700);
 * frame.setVisible(true); } }
 */
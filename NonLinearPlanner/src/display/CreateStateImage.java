package display;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import elements.Block;
import elements.PredicateHelper;
import elements.State;
import elements.PredicateHelper.PredicateType;

public class CreateStateImage {
	public static final int SIZE_Y = 500;
	public static final int SIZE_X = 800;
	public static final int MARGIN = 10;
	public static final int MARGIN_TITLE = 40;
	public static final int GROUND = SIZE_Y - (4 * MARGIN);
	public static final int BLOCK_HEIGHT = 30;

	private State state;
	private String figTitle;
	private String[] firstRow;
	private int[] firstRowHeight;
	private boolean predefinedFirstRow = false;
	private Graphics2D im;
	private ArrayList<Block> blocks;

	public CreateStateImage(State state, String figTitle, String[] firstRow, int[] firstRowHeight, ArrayList<Block> blocks) {
		this.state = state;
		this.figTitle = figTitle;
		this.firstRow = firstRow;
		this.firstRowHeight = firstRowHeight;
		predefinedFirstRow = true;	
		this.blocks = blocks;
	}

	public Graphics2D createGrafic(String filePath) {
		BufferedImage state_image = new BufferedImage(SIZE_X, SIZE_Y, BufferedImage.TYPE_INT_RGB);
		im = state_image.createGraphics();
		im.setColor(Color.WHITE);
		im.setBackground(Color.WHITE);
		im.fillRect(0,0,SIZE_X,SIZE_Y);

		// White background
		im.setColor(Color.WHITE);
		im.setBackground(Color.WHITE);
		im.fillRect(MARGIN, MARGIN + MARGIN_TITLE, SIZE_X - (2 * MARGIN), SIZE_Y - (2 * MARGIN) - MARGIN_TITLE);
		im.setColor(Color.BLACK);
		im.setFont(new Font("default", Font.BOLD, 26));
		im.drawString(figTitle, SIZE_X / 2 - figTitle.length() * 6, 35);

		// Ground Line
		im.setColor(Color.LIGHT_GRAY);
		im.fillRect(MARGIN, GROUND, SIZE_X - 2*MARGIN, 3*MARGIN);
		im.setColor(Color.BLACK);
		im.setStroke(new BasicStroke(2.0f));
		im.drawLine(MARGIN, GROUND, SIZE_X - MARGIN, GROUND);
		im.setColor(Color.BLACK);
		im.setStroke(new BasicStroke(2.0f));
		im.drawRect(MARGIN, MARGIN + MARGIN_TITLE, SIZE_X - (2 * MARGIN), SIZE_Y - (2 * MARGIN) - MARGIN_TITLE);
		

		// Variables to print the state
		int step_size = SIZE_X / (State.maxColumns + 1);
		int block_size = step_size - 30;
		String[] columnLastElement = new String[State.maxColumns];
		int[] columnHeight = new int[State.maxColumns];

		// Print Hands
		// Right
		int handPos_x = SIZE_X / 3;
		int handPos_y = MARGIN + MARGIN_TITLE;
		int[] posRightHand = { handPos_x - block_size / 2, handPos_y + 30 };
		im.setColor(Color.BLACK);
		im.setFont(new Font("default", Font.BOLD, 16));

		im.drawLine(handPos_x, handPos_y, handPos_x, handPos_y + 30);
		im.drawString("R", handPos_x - 20, handPos_y + 20);
		im.drawLine(handPos_x - block_size / 2, handPos_y + 30, handPos_x + block_size / 2, handPos_y + 30);
		im.drawLine(handPos_x - block_size / 2, handPos_y + 30, handPos_x - block_size / 2, handPos_y + 40);
		im.drawLine(handPos_x + block_size / 2, handPos_y + 30, handPos_x + block_size / 2, handPos_y + 40);
		// Left
		handPos_x = 2 * SIZE_X / 3;
		int[] posLeftHand = { handPos_x - block_size / 2, handPos_y + 30 };
		im.drawLine(handPos_x, handPos_y, handPos_x, handPos_y + 30);
		im.drawString("L", handPos_x - 20, handPos_y + 20);
		im.drawLine(handPos_x - block_size / 2, handPos_y + 30, handPos_x + block_size / 2, handPos_y + 30);
		im.drawLine(handPos_x - block_size / 2, handPos_y + 30, handPos_x - block_size / 2, handPos_y + 40);
		im.drawLine(handPos_x + block_size / 2, handPos_y + 30, handPos_x + block_size / 2, handPos_y + 40);

		// Column numbers
		for (int i = 1; i <= State.maxColumns; i++) {
			columnLastElement[i - 1] = "";
			columnHeight[i - 1] = 0;
			im.drawString(Integer.toString(i), step_size * i, GROUND + (2 * MARGIN));
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
				if (PredicateHelper.findType(predicate) == PredicateType.ON_TABLE) {
					columnLastElement[jj - 1] = predicate.substring(9, 10);
					columnHeight[jj - 1] = GROUND - BLOCK_HEIGHT;
					jj++;
				}
			}
		}

		for (int i = 1; i <= columnLastElement.length; i++) {
			if (!columnLastElement[i - 1].isEmpty()) {
				drawBlock(GROUND, step_size * i, block_size, columnLastElement[i - 1]);
			}
		}

		// Lets print on()
		int jj;
		while (!predicates.isEmpty()) {
			String predicate = predicates.get(0);
			if (PredicateHelper.findType(predicate) == PredicateType.ON) {
				String blockname1 = predicate.substring(3, 4);
				String blockname2 = predicate.substring(5, 6);
				jj = findElementInColumn(columnLastElement, blockname2);
				if (jj != 0) {
					drawBlock(columnHeight[jj - 1], step_size * jj, block_size, blockname1);
					columnLastElement[jj - 1] = blockname1;
					columnHeight[jj - 1] = columnHeight[jj - 1] - BLOCK_HEIGHT;
					predicates.remove(0);
				} else {
					predicates.remove(0);
					predicates.add(predicate);
				}
			} else if (PredicateHelper.findType(predicate) == PredicateType.HOLDING) {
				String blockname1 = predicate.substring(8, 9);
				String armname = predicate.substring(10, 11);
				if (armname.equals("R")) {
					drawBlock(posRightHand[1] + BLOCK_HEIGHT, posRightHand[0] + block_size / 2, block_size,
							blockname1);
				} else {
					drawBlock(posLeftHand[1] + BLOCK_HEIGHT, posLeftHand[0] + block_size / 2, block_size,
							blockname1);
				}
				predicates.remove(0);
			} else {
				predicates.remove(0);
			}
		}

		try {
			ImageIO.write(state_image, "JPEG", new File(filePath + "/" + figTitle + ".jpeg"));
		} catch (IOException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
		
		return im;

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
	private void drawBlock(int ground_value, int centerBlock, int block_size, String blockname) {
		im.setColor(Color.ORANGE);
		im.setBackground(Color.ORANGE);
		im.fillRect(centerBlock - block_size / 2, ground_value - BLOCK_HEIGHT, block_size, BLOCK_HEIGHT);
		im.setColor(Color.BLACK);
		im.drawRect(centerBlock - block_size / 2, ground_value - BLOCK_HEIGHT, block_size, BLOCK_HEIGHT);
		im.setFont(new Font("default", Font.BOLD, 20));
		int extraShift = 0;
		if(State.maxColumns>4) {extraShift = 10;}
		im.drawString(findBlockText(blockname), centerBlock - 5 - extraShift, ground_value + 6 - BLOCK_HEIGHT / 2);
	}
	
	private String findBlockText(String blockname) {
		String blockText = blockname + " ";
		for(Block b: blocks) {
			if (b.name.equals(blockname)) {
				blockText = blockText + String.format("%0" + b.weight + "d", 0).replace("0","*");
			}
		}
		return blockText;
	}
}

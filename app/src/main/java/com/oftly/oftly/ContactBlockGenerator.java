package com.oftly.oftly;

import java.util.Arrays;
import java.util.Random;

import android.util.Log;

public class ContactBlockGenerator {
	public int numItems;
	public static final int MAX_Y = 5;
	public static final int MAX_X = 4;
	public static final int MAX_ITEMS = MAX_X * MAX_Y;
	
	public static final int NUM_TILE_TYPES = 4;
	public static final int INVALID = -1;
	public static final int TWO_BY_TWO = 0;
	public static final int TWO_BY_ONE = 1;
	public static final int ONE_BY_TWO = 2;
	public static final int ONE_BY_ONE = 3;

	boolean matrix[][] = new boolean[MAX_X][MAX_Y];
	int curX = 0, curY = 0;
	Tile item2Tile[] = new Tile[MAX_ITEMS];
	int constantSize;
	
	final int NUM_CONTIGUOUS_ONE_BY_ONE = 2;
	int[] lastFewSizes = new int[NUM_CONTIGUOUS_ONE_BY_ONE];
	Random random;
	int seed;
	
	public ContactBlockGenerator(int constantSize, int seed, SearchAdapter adapter, int start_idx) {
		this.seed = seed;
		this.constantSize = constantSize;
		this.numItems = 0;
		boolean possible[] = new boolean[MAX_ITEMS];
		random = new Random(seed);

		for (int i = 0; i < MAX_X; i++) {
			for (int j = 0; j < MAX_Y; j++) {
				matrix[i][j] = false;
			}
		}
		for (int i = 0; i < NUM_CONTIGUOUS_ONE_BY_ONE; i++) {
			lastFewSizes[i] = INVALID;
		}
		
		Arrays.fill(possible, true);
		int width, height;
		do {
			if (start_idx + numItems >= adapter.contacts.size()) {
				break;
			}
			int tileIndex = selectTileIndex(possible, adapter.contacts.get(start_idx + numItems));
			updateLastFewSizes(tileIndex);
			width = getTileWidth(tileIndex);
			height = getTileHeight(tileIndex);
			item2Tile[numItems] = new Tile(curX, curY, width, height);
			for (int m = curX; m < curX + width; m++) {
				for (int n = curY; n < curY + height; n++) {
					matrix[m][n] = true;
				}
			}
			updateCurXY();
			numItems++;
			possible = getPossibleTiles();
		} while (checkIfSomeTileIsPossible(possible));
	}
	public Tile getTile(int item) {
		return item2Tile[item];
	}
	private int selectTileIndex(boolean[] possible, Contact contact) {
		int rank = contact.numCallRank;
		int srank = contact.sustainabilityRank;
		long timeDiff = contact.lastActualCallTime - contact.firstActualCallTime;
		/* If you have not talked to the contact across more than
		 * 24 hours, we will give it smallest area.
		 */
		long threshold = 24 * 60 * 60 * 1000;
		if (timeDiff <= threshold && !lastFewAreOneByOne()) {
			return ONE_BY_ONE;
		}
		if ((rank < 10 || srank < 10) && possible[TWO_BY_TWO]) {
			return TWO_BY_TWO;
		}
		if ((rank < 30 || srank < 30) && possible[ONE_BY_TWO] && possible[TWO_BY_ONE]) {
			if (random.nextInt(1) == 0) {
				return ONE_BY_TWO;
			} else {
				return TWO_BY_ONE;
			}
		}
		if ((rank < 30 || srank < 30) && possible[ONE_BY_TWO]) {
			return ONE_BY_TWO;
		}
		if ((rank < 30 || srank < 30) && possible[TWO_BY_ONE]) {
			return TWO_BY_ONE;
		}
		/* If contact does not have name, and rank is also 
		 * low then we cannot give it the largest area.
		 */
		if (contact.getName() == null) {
			possible[TWO_BY_TWO] = false;
		}
		/* Count number of possibilities */
		int count = 0;
		for (int i = 0; i < NUM_TILE_TYPES; i++) {
			if (possible[i]) {
				count++;
			}
		}
		
		/* Generate a random number between 0 and count - 1 */
		int r;
		if (constantSize == -1) {
			r = random.nextInt(count);
		} else {
			r = constantSize;
		}

		/* Skip first r - 1 possibilities, and return the rth possibility */
		int index = 0;
		for (int i = 0; i < NUM_TILE_TYPES; i++) {
			if (index == r && possible[i]) {
				return i;
			}
			if (possible[i]) {
				index++;
			}
		}
		return NUM_TILE_TYPES - 1;
	}
	private void updateCurXY() {
		for (int y = curY; y < MAX_Y; y++) {
			for (int x = 0; x < MAX_X; x++) {
				if (matrix[x][y] == false) {
					curX = x;
					curY = y;
					return;
				}
			}
		}
	}
	private boolean[] getPossibleTiles() {
		boolean[] possible = new boolean[NUM_TILE_TYPES];
		for (int i = 0; i < NUM_TILE_TYPES; i++) {
			possible[i] = false;
		}
		try {
			if (!matrix[curX][curY] && !matrix[curX][curY + 1] &&
					!matrix[curX + 1][curY] && !matrix[curX + 1][curY + 1]) {
				possible[TWO_BY_TWO] = true;
			}
		} catch (ArrayIndexOutOfBoundsException e) {
		}
		
		try {
			if (!matrix[curX][curY] && !matrix[curX + 1][curY]) {
				possible[TWO_BY_ONE] = true;
			}
		} catch (ArrayIndexOutOfBoundsException e) {
		}
		
		try {
			if (!matrix[curX][curY] && !matrix[curX][curY + 1]) {
				possible[ONE_BY_TWO] = true;
			}
		} catch (ArrayIndexOutOfBoundsException e) {
		}
		
		try {
			if (!matrix[curX][curY]) {
				possible[ONE_BY_ONE] = true;
			}
		} catch (ArrayIndexOutOfBoundsException e) {
		}
		return possible;
	}
	private int getTileWidth(int tileIndex) {
		if (tileIndex == ONE_BY_TWO || tileIndex == ONE_BY_ONE) {
			return 1;
		}
		return 2;
	}
	private int getTileHeight(int tileIndex) {
		if (tileIndex == ONE_BY_ONE || tileIndex == TWO_BY_ONE) {
			return 1;
		}
		return 2;
	}
	
	private boolean checkIfSomeTileIsPossible(boolean[] possible) {
		for (int i = 0; i < possible.length; i++) {
			if (possible[i] == true) {
				return true;
			}
		}
		return false;
	}
	private void updateLastFewSizes(int newSize) {
		for (int i = NUM_CONTIGUOUS_ONE_BY_ONE - 1; i > 0; i--) {
			lastFewSizes[i] = lastFewSizes[i - 1];
		}
		lastFewSizes[0] = newSize;
	}
	private boolean lastFewAreOneByOne() {
		for (int i = 0; i < NUM_CONTIGUOUS_ONE_BY_ONE; i++) {
			if (lastFewSizes[i] != ONE_BY_ONE) {
				return false;
			}
		}
		return true;
	}
}

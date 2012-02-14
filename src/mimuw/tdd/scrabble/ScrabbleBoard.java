package mimuw.tdd.scrabble;

import java.util.ArrayList;
import java.util.Collections;

public class ScrabbleBoard {

	private static final int LIMIT_FOR_EXTRA_POINTS = 7;
	private static final int EXTRA_POINTS = 50;
	private static final int BOARD_SIZE = 15;
	private Character[][] temp;
	private Character[][] fixed;
	private int[][] wordMultiplier;
	private int[][] letterMultiplier;
	private LetterScorer scorer;
	private ScrabbleDictionary dictionary;
	
	public ScrabbleBoard(LetterScorer scorer, ScrabbleDictionary dictionary) {
		temp = new Character[BOARD_SIZE][BOARD_SIZE];
		fixed = new Character[BOARD_SIZE][BOARD_SIZE];
		wordMultiplier = new int[BOARD_SIZE][BOARD_SIZE];
		letterMultiplier = new int[BOARD_SIZE][BOARD_SIZE];
		for (int row = 0; row < BOARD_SIZE; ++row) {
			for (int col = 0; col < BOARD_SIZE; ++col)
				wordMultiplier[row][col] = letterMultiplier[row][col] = 1;
		}
		this.scorer = scorer;
		this.dictionary = dictionary;
	}

	public void addLetter(int row, int col, char letter) throws Exception {
		checkBounds(row, col);
		temp[row][col] = letter;
	}

	private void checkBounds(int row, int col) throws InvalidBoardCoordsException {
		if (row < 0 || row >= BOARD_SIZE || col < 0 || col >= BOARD_SIZE)
			throw new InvalidBoardCoordsException();
	}

	public Character getLetter(int row, int col) throws InvalidBoardCoordsException {
		checkBounds(row, col);
		if (temp[row][col] == null)
			return fixed[row][col];
		return temp[row][col];
	}

	public void deleteLetter(int row, int col) throws InvalidBoardCoordsException {
		checkBounds(row, col);
		if (temp[row][col] == null)
			throw new InvalidBoardCoordsException();
		temp[row][col] = null;
	}

	public void setWordMultiplier(int row, int col, int multi) throws InvalidBoardCoordsException {
		checkBounds(row, col);
		wordMultiplier[row][col] = multi;
	}

	public int getWordMultiplier(int row, int col) throws InvalidBoardCoordsException {
		checkBounds(row, col);
		return wordMultiplier[row][col];
	}

	public int getLetterMultiplier(int row, int col) throws InvalidBoardCoordsException {
		checkBounds(row, col);
		return letterMultiplier[row][col];
	}

	public void setLetterMultiplier(int row, int col, int multi) throws InvalidBoardCoordsException {
		checkBounds(row, col);
		letterMultiplier[row][col] = multi;
	}

	public int commitMove(boolean verify) throws MoveNotAllowedException {
		if (!moveAllowed())
			throw new MoveNotAllowedException();
		ScrabbleWord[] words = getNewWords();
		if (verify)
			verifyWords(words);
		int sumScore = sumPoint(words);
		if (countTemp() >= LIMIT_FOR_EXTRA_POINTS)
			sumScore += EXTRA_POINTS;
		copyFromTempToFixed();
		return sumScore;
	}

	private void verifyWords(ScrabbleWord[] words)
			throws MoveNotAllowedException {
		for (ScrabbleWord word : words) {
			if (!dictionary.contains(word.getWord()))
				throw new MoveNotAllowedException();
		}
	}

	private void copyFromTempToFixed() {
		for (int row = 0; row < BOARD_SIZE; ++row) {
			for (int col = 0; col < BOARD_SIZE; ++col) {
				if (temp[row][col] != null) {
					fixed[row][col] = temp[row][col];
					temp[row][col] = null;
				}
			}
		}
	}

	private int sumPoint(ScrabbleWord[] words) {
		int sumScore = 0;
		for (ScrabbleWord word : words)
			sumScore += word.getScore();
		return sumScore;
	}

	public boolean moveAllowed() {
		if (isEmptyMove())
			return true;
		int minRow = Integer.MAX_VALUE, maxRow = Integer.MIN_VALUE;
		int minCol = Integer.MAX_VALUE, maxCol = Integer.MIN_VALUE;
		for (int row = 0; row < BOARD_SIZE; ++row) {
			for (int col = 0; col < BOARD_SIZE; ++col) {
				if (temp[row][col] != null) {
					minRow = Math.min(minRow, row);
					maxRow = Math.max(maxRow, row);
					minCol = Math.min(minCol, col);
					maxCol = Math.max(maxCol, col);
				}
			}
		}
		if(minRow != maxRow && minCol != maxCol)
			return false;
		if (!checkIfRectangleIsNotSeparated(minRow, maxRow, minCol, maxCol))
			return false;
		if (isFirstMove())
			return checkIfFirstMoveIsValid();
		return checkIfNewLetterTouchesFixed();
	}

	private boolean checkIfFirstMoveIsValid() {
		if (temp[7][7] == null)
			return false;
		if (countTemp() == 1)
			return false;
		return true;
	}

	private boolean checkIfNewLetterTouchesFixed() {
		for (int row = 0; row < BOARD_SIZE; ++row) {
			for (int col = 0; col < BOARD_SIZE; ++col) {
				if (temp[row][col] != null && touchesFixed(row, col))
					return true;
			}
		}
		return false;
	}

	private boolean touchesFixed(int row, int col) {
		int drow[] = {-1, 0, 1, 0}, dcol[] = {0, 1, 0, -1};
		for (int dir = 0; dir < 4; ++dir) {
			int rt = row + drow[dir];
			int ct = col + dcol[dir];
			try {
				checkBounds(rt, ct);
				if (fixed[rt][ct] != null)
					return true;
			}
			catch(InvalidBoardCoordsException e) {}
		}
		return false;
	}

	private boolean checkIfRectangleIsNotSeparated(int minRow, int maxRow,
			int minCol, int maxCol) {
		for (int row = minRow; row <= maxRow; ++row) {
			for (int col = minCol; col <= maxCol; ++col) {
				if (temp[row][col] == null && fixed[row][col] == null)
					return false;
			}
		}
		return true;
	}

	private boolean isEmptyMove() {
		return countTemp() == 0;
	}

	private boolean isFirstMove() {
		return countFixed() == 0;
	}

	private int countTemp() {
		int tempCount = 0;
		for (int row = 0; row < BOARD_SIZE; ++row) {
			for (int col = 0; col < BOARD_SIZE; ++col) {
				if (temp[row][col] != null)
					++tempCount;
			}
		}
		return tempCount;
	}

	private int countFixed() {
		int fixedCount = 0;
		for (int row = 0; row < BOARD_SIZE; ++row) {
			for (int col = 0; col < BOARD_SIZE; ++col) {
				if (fixed[row][col] != null)
					++fixedCount;
			}
		}
		return fixedCount;
	}

	public ScrabbleWord[] getNewWords() {
		ArrayList<ScrabbleWord> words = new ArrayList<ScrabbleWord>();
		for (int row = 0; row < BOARD_SIZE; ++row) {
			for (int col = 0; col < BOARD_SIZE; ++col) {
				try {
					if (getLetter(row, col - 1) == null) {
						ScrabbleWord word = getNewWordFromRectangle(row, row, col, BOARD_SIZE - 1);
						if (word != null)
							words.add(word);
					}
				} catch (InvalidBoardCoordsException e) {}
				try {
					if (getLetter(row - 1, col) == null) {
						ScrabbleWord word = getNewWordFromRectangle(row, BOARD_SIZE - 1, col, col);
						if (word != null)
							words.add(word);
					}
				} catch (InvalidBoardCoordsException e) {}
			}
		}
		Collections.sort(words);
		return words.toArray(new ScrabbleWord[words.size()]);
	}

	private ScrabbleWord getNewWordFromRectangle(int minRow, int maxRow, int minCol,
			int maxCol) {
		String word = new String();
		boolean newLetter = false;
		int sumLetters = 0;
		int totalMulti = 1;
		search:
		for (int row = minRow; row <= maxRow; ++row) {
			for (int col = minCol; col <= maxCol; ++col) {
				if (temp[row][col] != null) {
					newLetter = true;
					word += temp[row][col];
					sumLetters += scorer.scoreLetter(temp[row][col]) * letterMultiplier[row][col];
					totalMulti *= wordMultiplier[row][col];
				}
				else if (fixed[row][col] != null) {
					sumLetters += scorer.scoreLetter(fixed[row][col]);
					word += fixed[row][col];
				}
				else
					break search;
			}
		}
		if (newLetter && word.length() > 1)
			return new ScrabbleWord(word, sumLetters * totalMulti);
		return null;
	}

}

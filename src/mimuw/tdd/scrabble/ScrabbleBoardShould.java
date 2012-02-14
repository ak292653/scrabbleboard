package mimuw.tdd.scrabble;

import static org.junit.Assert.*;

import org.junit.Test;
import static org.mockito.Mockito.*;

public class ScrabbleBoardShould {

	private LetterScorer defaultScorer;
	private ScrabbleDictionary defaultDictionary;
	
	public ScrabbleBoardShould() {
		defaultScorer = mock(LetterScorer.class);
		when(defaultScorer.scoreLetter(anyChar())).thenReturn(1);
		defaultDictionary = mock(ScrabbleDictionary.class);
		when(defaultDictionary.contains(anyString())).thenReturn(true);
	}
	
	public ScrabbleBoard getBoardWithMockedParameters() {
		return new ScrabbleBoard(defaultScorer, defaultDictionary); 
	}
	
	@Test
	public void allowToPlaceFirstTileAtTheCenter() throws Exception {
		ScrabbleBoard board = getBoardWithMockedParameters();
		board.addLetter(7, 7, 'a');
	}
	
	@Test (expected = InvalidBoardCoordsException.class)
	public void notAllowToPlaceTileOutsideTheBoard() throws Exception {
		ScrabbleBoard board = getBoardWithMockedParameters();
		board.addLetter(7, -1, 'a');
	}
	
	@Test
	public void returnCorrectLetterAtPosition() throws Exception {
		ScrabbleBoard board = getBoardWithMockedParameters();
		board.addLetter(7, 7, 'z');
		assertEquals(new Character('z'), board.getLetter(7, 7));
	}
	
	@Test
	public void allowToDeleteLetterAtPosition() throws Exception {
		ScrabbleBoard board = getBoardWithMockedParameters();
		board.addLetter(7, 7, 'a');
		board.deleteLetter(7, 7);
	}
	
	@Test (expected = InvalidBoardCoordsException.class)
	public void notAllowToDeleteLetterThatDoesNotExist() throws Exception {
		ScrabbleBoard board = getBoardWithMockedParameters();
		board.deleteLetter(7, 7);
	}
	
	@Test (expected = InvalidBoardCoordsException.class)
	public void notAllowToDeleteLetterFromOutsideTheBoard() throws Exception {
		ScrabbleBoard board = getBoardWithMockedParameters();
		board.deleteLetter(-1, 1);
	}
	
	@Test
	public void notAllowToDeleteFixedLetter() throws Exception {
		ScrabbleBoard board = getBoardWithMockedParameters();
		board.addLetter(7, 7, 'a');
		board.addLetter(7, 8, 'a');
		board.commitMove(false);
		try {
			board.deleteLetter(7, 7);
			fail();
		}
		catch (InvalidBoardCoordsException e) {
			assertEquals(new Character('a'), board.getLetter(7, 7));
		}
	}
	
	@Test
	public void returnCorrectWordMultiplier() throws Exception {
		ScrabbleBoard board = getBoardWithMockedParameters();
		board.setWordMultiplier(7, 7, 5);
		assertEquals(5, board.getWordMultiplier(7, 7));
	}
	
	@Test (expected = InvalidBoardCoordsException.class)
	public void throwWhenTryingToSetLetterMultiplierFromOutsideTheBoard() throws Exception {
		ScrabbleBoard board = getBoardWithMockedParameters();
		board.setLetterMultiplier(-1, 1, 5);
	}
	
	@Test
	public void returnCorrectLetterMultiplier() throws Exception {
		ScrabbleBoard board = getBoardWithMockedParameters();
		board.setLetterMultiplier(7, 7, 6);
		assertEquals(6, board.getLetterMultiplier(7, 7));
	}
	
	@Test (expected = InvalidBoardCoordsException.class)
	public void throwWhenTryingToSetWordMultiplierFromOutsideTheBoard() throws Exception {
		ScrabbleBoard board = getBoardWithMockedParameters();
		board.setWordMultiplier(16, 5, 7);
	}
	
	@Test
	public void returnCorrectDefaultMultipliers() throws Exception {
		ScrabbleBoard board = getBoardWithMockedParameters();
		assertEquals(1, board.getWordMultiplier(5, 5));
		assertEquals(1, board.getLetterMultiplier(10, 10));
	}

	@Test
	public void allowMoveIfLettersAreInOneLine() throws Exception {
		ScrabbleBoard board = getBoardWithMockedParameters();
		board.addLetter(7, 7, 'a');
		board.addLetter(7, 8, 'b');
		assertTrue(board.moveAllowed());
	}
	
	@Test
	public void notAllowMoveIfLettersAreNotInOneLine() throws Exception {
		ScrabbleBoard board = getBoardWithMockedParameters();
		board.addLetter(7, 7, 'a');
		board.addLetter(8, 8, 'b');
		assertFalse(board.moveAllowed());
	}
	
	@Test (expected = MoveNotAllowedException.class)
	public void notAllowToCommitIfItsNotAllowed() throws Exception {
		ScrabbleBoard board = getBoardWithMockedParameters();
		board.addLetter(7, 7, 'a');
		board.addLetter(1, 1, 'b');
		board.commitMove(false);
	}
	
	@Test
	public void allowMoveWithZeroLetters() throws Exception {
		ScrabbleBoard board = getBoardWithMockedParameters();
		assertTrue(board.moveAllowed());
	}
	
	@Test
	public void notAllowFirstMoveIfItDoesNotTouchCenter() throws Exception {
		ScrabbleBoard board = getBoardWithMockedParameters();
		board.addLetter(1, 1, 'a');
		board.addLetter(1, 2, 'b');
		assertFalse(board.moveAllowed());
	}
	
	@Test
	public void notAllowFirstMoveIfItAddsOnlyOneLetter() throws Exception {
		ScrabbleBoard board = getBoardWithMockedParameters();
		board.addLetter(7, 7, 'a');
		assertFalse(board.moveAllowed());
	}
	
	@Test
	public void notAllowMoveIfNewLettersAreSeparatedHorizontally() throws Exception {
		ScrabbleBoard board = getBoardWithMockedParameters();
		board.addLetter(7, 7, 'a');
		board.addLetter(7, 9, 'b');
		assertFalse(board.moveAllowed());
	}
	
	@Test
	public void notAllowMoveIfNewLettersAreSeparatedVertically() throws Exception {
		ScrabbleBoard board = getBoardWithMockedParameters();
		board.addLetter(7, 7, 'a');
		board.addLetter(9, 7, 'b');
		assertFalse(board.moveAllowed());
	}
	
	@Test
	public void notAllowFurtherMoveIfItDoesNotTouchPreviousFixed() throws Exception {
		ScrabbleBoard board = getBoardWithMockedParameters();
		board.addLetter(7, 7, 'a');
		board.addLetter(7, 8, 'b');
		board.commitMove(false);
		board.addLetter(0, 0, 'a');
		board.addLetter(0, 1, 'b');
		assertFalse(board.moveAllowed());
	}
	
	public void addWordVertically(ScrabbleBoard board, int row, int col, String word) throws Exception {
		for (int i = 0; i < word.length(); ++i)
			board.addLetter(row + i, col, word.charAt(i));
	}
	
	@Test
	public void recognizeNewWordsCorrectly() throws Exception {
		ScrabbleBoard board = getBoardWithMockedParameters();
		addWordVertically(board, 7, 7, "abc");
		board.commitMove(false);
		addWordVertically(board, 6, 8, "abcd");
		String[] expected = {"ab", "abcd", "bc", "cd"};
		ScrabbleWord[] words = board.getNewWords();
		String[] actual = new String[words.length];
		for (int i = 0; i < words.length; ++i)
			actual[i] = words[i].getWord();
		assertArrayEquals(expected, actual);
	}
	
	@Test
	public void scoreWordsCorrectly() throws Exception {
		LetterScorer scorer = mock(LetterScorer.class);
		when(scorer.scoreLetter(anyChar())).thenReturn(1);
		when(scorer.scoreLetter('b')).thenReturn(3);
		ScrabbleBoard board = new ScrabbleBoard(scorer, defaultDictionary);
		board.setWordMultiplier(7, 8, 3);
		board.setLetterMultiplier(9, 8, 2);
		addWordVertically(board, 7, 7, "ab");
		board.commitMove(false);
		addWordVertically(board, 7, 8, "abc");
		ScrabbleWord[] expected = {
				new ScrabbleWord("aa", 3 * (1 + 1)),
				new ScrabbleWord("abc", 3 * (1 + 3 + 1 * 2)),
				new ScrabbleWord("bb", 3 + 3)
		};
		assertArrayEquals(expected, board.getNewWords());		
	}
	
	@Test
	public void sumWordsCorrectly() throws Exception {
		ScrabbleBoard board = getBoardWithMockedParameters();
		addWordVertically(board, 7, 7, "abcd");
		board.commitMove(false);
		addWordVertically(board, 7, 8, "qwer");
		assertEquals(4 * 2 + 4, board.commitMove(false));
	}
	
	@Test
	public void add50PointsIf7LettersUsed() throws Exception {
		ScrabbleBoard board = getBoardWithMockedParameters();
		addWordVertically(board, 7, 7, "qwertyu");
		assertEquals(7 + 50, board.commitMove(false));
	}
	
	@Test
	public void performDictionaryChecksIfNeeded() throws Exception {
		ScrabbleDictionary dictionary = mock(ScrabbleDictionary.class);
		when(dictionary.contains(anyString())).thenReturn(true);
		ScrabbleBoard board = new ScrabbleBoard(defaultScorer, dictionary);
		addWordVertically(board, 7, 7, "ab");
		board.commitMove(false);
		verify(dictionary, never()).contains(anyString());
		board.addLetter(7, 8, 'b');
		board.commitMove(true);
		verify(dictionary, times(1)).contains(anyString());
		verify(dictionary, times(1)).contains("ab");
	}
	
	@Test (expected = MoveNotAllowedException.class)
	public void throwWhenDictionaryCheckFails() throws Exception {
		ScrabbleDictionary dictionary = mock(ScrabbleDictionary.class);
		when(dictionary.contains(anyString())).thenReturn(false);
		ScrabbleBoard board = new ScrabbleBoard(defaultScorer, dictionary);
		addWordVertically(board, 7, 7, "aba");
		board.commitMove(true);
	}
}

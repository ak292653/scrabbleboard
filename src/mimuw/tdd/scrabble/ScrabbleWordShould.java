package mimuw.tdd.scrabble;

import static org.junit.Assert.*;

import org.junit.Test;

public class ScrabbleWordShould {

	@Test
	public void shouldStoreWordCorrectly() throws Exception {
		ScrabbleWord word = new ScrabbleWord("adam", 2);
		assertEquals("adam", word.getWord());
	}
	
	@Test
	public void shouldStoreScoreCorrectly() throws Exception {
		ScrabbleWord word = new ScrabbleWord("adam", 5);
		assertEquals(5, word.getScore());
	}
	
	@Test
	public void compareByWordCorrectly() throws Exception {
		ScrabbleWord w1 = new ScrabbleWord("a", 1);
		ScrabbleWord w2 = new ScrabbleWord("b", 0);
		assertTrue(w1.compareTo(w2) < 0 && w2.compareTo(w1) > 0);
	}
	
	@Test
	public void compareByScoreCorrectly() throws Exception {
		ScrabbleWord w1 = new ScrabbleWord("a", 1);
		ScrabbleWord w2 = new ScrabbleWord("a", 2);
		assertTrue(w1.compareTo(w2) < 0 && w2.compareTo(w1) > 0 && w1.compareTo(w1) == 0);
	}
}

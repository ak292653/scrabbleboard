package mimuw.tdd.scrabble;

public class ScrabbleWord implements Comparable<ScrabbleWord> {

	private String word;
	private int score;
	
	public ScrabbleWord(String word, int score) {
		this.word = word;
		this.score = score;
	}

	public String getWord() {
		return word;
	}

	public int getScore() {
		return score;
	}

	public int compareTo(ScrabbleWord sWord) {
		if (word.compareTo(sWord.word) == 0)
			return (new Integer(score)).compareTo(sWord.score);
		return word.compareTo(sWord.word);
	}
	
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof ScrabbleWord))
			return false;
		ScrabbleWord sWord = (ScrabbleWord)obj;
		return this.compareTo(sWord) == 0;
	}
	
}

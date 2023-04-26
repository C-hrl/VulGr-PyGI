
package org.apache.struts2.showcase.hangman;

public class HangmanService {

	public VocabSource vocabSource;

	public HangmanService(VocabSource vocabSource) {
		this.vocabSource = vocabSource;
	}

	public Hangman startNewGame() {
		return new Hangman(vocabSource.getRandomVocab());
	}
}

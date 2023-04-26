
package org.apache.struts2.showcase.hangman;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class PropertiesVocabSource implements VocabSource {

	private Properties prop;
	private List<Vocab> vocabs;

	public PropertiesVocabSource() {
	}

	public PropertiesVocabSource(Properties prop) {
		assert (prop != null);
		this.prop = prop;
		vocabs = readVocab(prop);
	}

	public void setVocabProperties(Properties prop) {
		assert (prop != null);
		this.prop = prop;
		vocabs = readVocab(prop);
	}

	public Vocab getRandomVocab() {
		if (vocabs == null) {
			throw new HangmanException(HangmanException.Type.valueOf("NO_VOCAB_SOURCE"), "No vocab source");
		}
		if (vocabs.size() <= 0) {
			throw new HangmanException(HangmanException.Type.valueOf("NO_VOCAB"), "No vocab");
		}
		long vocabIndex = Math.round((Math.random() * (double) prop.size()));
		vocabIndex = vocabIndex == vocabs.size() ? vocabs.size() - 1 : vocabIndex;
		return vocabs.get((int) vocabIndex);
	}

	protected List<Vocab> readVocab(Properties prop) {
		List<Vocab> vocabList = new ArrayList<Vocab>();

		for (Map.Entry e : prop.entrySet()) {
			String vocab = (String) e.getKey();
			String hint = (String) e.getValue();

			vocabList.add(new Vocab(vocab, hint));
		}
		return vocabList;
	}
}

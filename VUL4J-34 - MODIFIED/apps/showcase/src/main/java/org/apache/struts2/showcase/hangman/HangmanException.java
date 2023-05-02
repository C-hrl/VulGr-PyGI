
package org.apache.struts2.showcase.hangman;

public class HangmanException extends RuntimeException {

	private static final long serialVersionUID = -8500292863595941335L;

	enum Type {
		GAME_ENDED,
		NO_VOCAB,
		NO_VOCAB_SOURCE;
	}


	private Type type;

	public HangmanException(Type type, String reason) {
		super(reason);
		this.type = type;
	}

	public Type getType() {
		return type;
	}
}

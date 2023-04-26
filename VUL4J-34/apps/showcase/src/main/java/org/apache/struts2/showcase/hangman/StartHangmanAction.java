
package org.apache.struts2.showcase.hangman;

import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.interceptor.SessionAware;

import java.util.Map;

import static org.apache.struts2.showcase.hangman.HangmanConstants.HANGMAN_SESSION_KEY;

public class StartHangmanAction extends ActionSupport implements SessionAware {

	private static final long serialVersionUID = 2333463075324892521L;

	private HangmanService service;
	private Hangman hangman;
	private Map session;


	public StartHangmanAction(HangmanService service) {
		this.service = service;
	}

	public String execute() throws Exception {

		hangman = service.startNewGame();
		session.put(HANGMAN_SESSION_KEY, hangman);

		return SUCCESS;
	}

	public Hangman getHangman() {
		return hangman;
	}


	
	public void setSession(Map session) {
		this.session = session;
	}
}

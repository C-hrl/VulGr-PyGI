
package org.apache.struts2.showcase.action;

import com.opensymphony.xwork2.ActionSupport;

import java.util.*;

public class ExampleAction extends ActionSupport {
	public static final String CONSTANT = "Struts Rocks!";

	public static Date getCurrentDate() {
		return new Date();
	}

	public String getName() {
		return "John Galt";
	}

	public String[] getBands() {
		return new String[]{"Pink Floyd", "Metallica", "Guns & Roses"};
	}

	public List<String> getMovies() {
		return Arrays.asList("Lord of the Rings", "Matrix");
	}

	public Book getBook() {
		return new Book("Iliad", "Homer");
	}

	public Map<String, Book> getBooks() {
		Map<String, Book> books = new HashMap<String, Book>();
		books.put("Iliad", new Book("Iliad", "Homer"));
		books.put("The Republic", new Book("The Replublic", "Plato"));
		books.put("Thus Spake Zarathustra", new Book("Thus Spake Zarathustra",
				"Friedrich Nietzsche"));
		return books;
	}
}

class Book {
	private String title;
	private String author;

	public Book(String title, String author) {
		this.title = title;
		this.author = author;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}
}
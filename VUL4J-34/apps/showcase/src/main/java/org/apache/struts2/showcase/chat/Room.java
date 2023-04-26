
package org.apache.struts2.showcase.chat;

import java.util.*;

public class Room {

	private static final int MAX_CHAT_MESSAGES = 10;

	private String name;
	private String description;
	private Date creationDate;

	private List<ChatMessage> messages = new ArrayList<ChatMessage>();

	private Map<String, User> members = new LinkedHashMap<String, User>();

	public Room(String name, String description) {
		this.name = name;
		this.description = description;
		this.creationDate = new Date(System.currentTimeMillis());
	}


	
	public Date getCreationDate() {
		return creationDate;
	}

	public String getDescription() {
		return description;
	}

	public String getName() {
		return name;
	}


	
	public List<User> getMembers() {
		return new ArrayList<User>(members.values());
	}

	public User findMember(String name) {
		assert (name != null);
		return members.get(name);
	}

	public boolean hasMember(String name) {
		assert (name != null);
		return members.containsKey(name);
	}

	public void memberEnter(User member) {
		assert (member != null);
		if (!hasMember(member.getName())) {
			members.put(member.getName(), member);
		}
	}

	public void memberExit(String memberName) {
		assert (memberName != null);
		assert (memberName.trim().length() > 0);
		members.remove(memberName);
	}


	
	public void addMessage(ChatMessage chatMessage) {
		if (messages.size() > MAX_CHAT_MESSAGES) {
			
			messages.remove(0);
		}
		messages.add(chatMessage);
	}

	public List<ChatMessage> getChatMessages() {
		return new ArrayList<ChatMessage>(messages);
	}

}

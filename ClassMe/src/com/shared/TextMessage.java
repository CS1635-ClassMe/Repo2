package com.shared;

import java.util.ArrayList;

/**
 * Created by Robert McDermot on 3/27/14.
 */
public class TextMessage
{
	private String text, from ,timestamp, conversationId;
	private ArrayList<String> usernames;

	public TextMessage(String text, String from, String timestamp, String conversationId, ArrayList<String> usernames)
	{
		this.text = text;
		this.from = from;
		this.timestamp = timestamp;
		this.conversationId = conversationId;
		this.usernames = usernames;
	}

	public String getTimestamp()
	{
		return timestamp;
	}

	public void setTimestamp(String timestamp)
	{
		this.timestamp = timestamp;
	}

	public String getFrom()
	{
		return from;
	}

	public void setFrom(String from)
	{
		this.from = from;
	}

	public String getText()
	{
		return text;
	}

	public void setText(String text)
	{
		this.text = text;
	}

	public String getConversationId()
	{
		return conversationId;
	}

	public void setConversationId(String conversationId)
	{
		this.conversationId = conversationId;
	}

	public ArrayList<String> getUsernames()
	{
		return usernames;
	}

	public void setUsernames(ArrayList<String> usernames)
	{
		this.usernames = usernames;
	}
}

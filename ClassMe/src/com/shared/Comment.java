package com.shared;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class Comment implements Serializable, Comparable<Comment>
{
	private static final long serialVersionUID = 3763260169521173359L;
	
	private String username, content, commentKey;
	private Date commentTime, lastEdit;
	private int plusOnes;
	private boolean plusOned, isAccepted;
	private ArrayList<String> attachmentKeys = new ArrayList<String>();
	private ArrayList<String> attachmentNames = new ArrayList<String>();
	
	public boolean isPlusOned()
	{
		return plusOned;
	}
	
	public void setPlusOned(boolean plusOned)
	{
		this.plusOned = plusOned;
	}
	
	public int getPlusOnes()
	{
		return plusOnes;
	}
	
	public void setPlusOnes(int plusOnes)
	{
		this.plusOnes = plusOnes;
	}
	
	public Comment(){}
	
	public Comment(String username, String content)
	{
		this.username = username;
		this.content = content;
		commentTime = new Date();
	}
	
	public String getUsername()
	{
		return username;
	}
	
	public void setUsername(String username)
	{
		this.username = username;
	}
	
	public String getContent()
	{
		return content;
	}
	
	public void setContent(String content)
	{
		this.content = content;
	}
	
	public Date getCommentTime()
	{
		return commentTime;
	}
	
	public void setCommentTime(Date commentTime)
	{
		this.commentTime = commentTime;
	}
	
	@Override
	public int compareTo(Comment other)
	{
		return commentTime.compareTo(other.commentTime);
	}
	
	public String getCommentKey()
	{
		return commentKey;
	}
	
	public void setCommentKey(String commentKey)
	{
		this.commentKey = commentKey;
	}
	
	public Date getLastEdit()
	{
		return lastEdit;
	}
	
	public void setLastEdit(Date lastEdit)
	{
		this.lastEdit = lastEdit;
	}

	public boolean isAccepted()
	{
		return isAccepted;
	}

	public void setAccepted(boolean isAccepted)
	{
		this.isAccepted = isAccepted;
	}

	public ArrayList<String> getAttachmentKeys()
	{
		return attachmentKeys;
	}

	public void setAttachmentKeys(ArrayList<String> attachmentKeys)
	{
		this.attachmentKeys = attachmentKeys;
	}

	public ArrayList<String> getAttachmentNames()
	{
		return attachmentNames;
	}

	public void setAttachmentNames(ArrayList<String> attachmentNames)
	{
		this.attachmentNames = attachmentNames;
	}
}

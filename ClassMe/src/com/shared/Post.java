package com.shared;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;

public class Post implements Serializable
{
	private static final long serialVersionUID = -6112430428377944014L;

	private String postTitle, postContent, username, classId, postKey, reportReason, type;
	private Date postTime, lastEdit;
	private int upvotes, downvotes;
	private double score;
	private ArrayList<Comment> comments = new ArrayList<Comment>();
	private ArrayList<String> attachmentKeys = new ArrayList<String>();
	private ArrayList<String> attachmentNames = new ArrayList<String>();
	private boolean upvoted, downvoted, reported;

	public boolean isUpvoted()
	{
		return upvoted;
	}

	public void setUpvoted(boolean upvoted)
	{
		this.upvoted = upvoted;
	}

	public boolean isDownvoted()
	{
		return downvoted;
	}

	public void setDownvoted(boolean downvoted)
	{
		this.downvoted = downvoted;
	}

	public static Comparator<Post> PostTimeComparator = new Comparator<Post>()
	{
		@Override
		public int compare(Post post1, Post post2)
		{
			return(post2.getPostTime().compareTo(post1.getPostTime()));
		}
	};

	public static Comparator<Post> PostScoreComparator = new Comparator<Post>()
	{
		@Override
		public int compare(Post post1, Post post2)
		{
			return (int) (post2.getScore()*10000-post1.getScore()*10000);
		}
	};

	public ArrayList<Comment> getComments()
	{
		return comments;
	}

	public void setComments(ArrayList<Comment> comments)
	{
		this.comments = comments;
	}

	public String getPostContent()
	{
		return postContent;
	}

	public int getUpvotes()
	{
		return upvotes;
	}

	public void setUpvotes(int upvotes)
	{
		this.upvotes = upvotes;
	}

	public int getDownvotes()
	{
		return downvotes;
	}

	public void setDownvotes(int downvotes)
	{
		this.downvotes = downvotes;
	}

	public void setPostContent(String postContent)
	{
		this.postContent = postContent;
	}

	public String getUsername()
	{
		return username;
	}

	public void setUsername(String username)
	{
		this.username = username;
	}

	public Date getPostTime()
	{
		return postTime;
	}

	public void setPostTime(Date postTime)
	{
		this.postTime = postTime;
	}

	public String getClassId()
	{
		return classId;
	}

	public void setClassId(String classId)
	{
		this.classId = classId;
	}

	public double getScore()
	{
		return score;
	}

	public void setScore(double score)
	{
		this.score = score;
	}

	public String getPostKey()
	{
		return postKey;
	}

	public void setPostKey(String postKey)
	{
		this.postKey = postKey;
	}

	public Date getLastEdit()
	{
		return lastEdit;
	}

	public void setLastEdit(Date lastEdit)
	{
		this.lastEdit = lastEdit;
	}

	public String getReportReason()
	{
		return reportReason;
	}

	public void setReportReason(String reportReason)
	{
		this.reportReason = reportReason;
	}

	public boolean isReported()
	{
		return reported;
	}

	public void setReported(boolean reported)
	{
		this.reported = reported;
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

	public String getPostTitle()
	{
		return postTitle;
	}

	public void setPostTitle(String postTitle)
	{
		this.postTitle = postTitle;
	}

	public String getType()
	{
		return type;
	}

	public void setType(String type)
	{
		this.type = type;
	}
}

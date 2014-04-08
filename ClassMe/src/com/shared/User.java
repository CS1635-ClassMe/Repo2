package com.shared;

import java.io.Serializable;
import java.util.ArrayList;

public class User implements Serializable
{

	private static final long serialVersionUID = 3724822216046123105L;
	private String username, firstName, lastName;
	private ArrayList<String> courseList;

	public User(){}

	public User(String u)
	{
		setUsername(u);
	}

	public String getUsername()
	{
		return username;
	}

	public void setUsername(String username)
	{
		this.username = username;
	}

	public String getFirstName()
	{
		return firstName;
	}

	public void setFirstName(String firstName)
	{
		this.firstName = firstName;
	}

	public String getLastName()
	{
		return lastName;
	}

	public void setLastName(String lastName)
	{
		this.lastName = lastName;
	}

	public ArrayList<String> getCourseList()
	{
		return courseList;
	}

	public void setCourseList(ArrayList<String> courseList)
	{
		this.courseList = courseList;
	}
}

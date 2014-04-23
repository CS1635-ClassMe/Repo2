package com.shared;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Course implements Serializable
{
	private String courseId, instructor, department;

	public String getCourseId()
	{
		return courseId;
	}

	public void setCourseId(String courseId)
	{
		this.courseId = courseId;
	}

	public String getInstructor()
	{
		return instructor;
	}

	public void setInstructor(String instructor)
	{
		this.instructor = instructor;
	}

	public String getDepartment()
	{
		return department;
	}

	public void setDepartment(String department)
	{
		this.department = department;
	}
}

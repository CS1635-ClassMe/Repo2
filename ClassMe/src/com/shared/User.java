package com.shared;

import java.io.Serializable;

// TODO: Auto-generated Javadoc
/**
 * The Class User.
 */
public class User implements Serializable
{

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 3724822216046123105L;

	/** The username. */
	private String username;

	/** The first name. */
	private String firstName;

	/** The last name. */
	private String lastName;

	/**
	 * Instantiates a new user.
	 */
	public User(){}

	/**
	 * Instantiates a new user.
	 *
	 * @param u the u
	 */
	public User(String u)
	{
		setUsername(u);
	}

	/**
	 * Gets the username.
	 *
	 * @return the username
	 */
	public String getUsername()
	{
		return username;
	}

	/**
	 * Sets the username.
	 *
	 * @param username the new username
	 */
	public void setUsername(String username)
	{
		this.username = username;
	}

	/**
	 * Gets the first name.
	 *
	 * @return the first name
	 */
	public String getFirstName()
	{
		return firstName;
	}

	/**
	 * Sets the first name.
	 *
	 * @param firstName the new first name
	 */
	public void setFirstName(String firstName)
	{
		this.firstName = firstName;
	}

	/**
	 * Gets the last name.
	 *
	 * @return the last name
	 */
	public String getLastName()
	{
		return lastName;
	}

	/**
	 * Sets the last name.
	 *
	 * @param lastName the new last name
	 */
	public void setLastName(String lastName)
	{
		this.lastName = lastName;
	}
}

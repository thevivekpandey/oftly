package com.oftly.oftly;

public class Contact {
	String name;
	String phone;
	String photoURI;
	
	public int numCalls = 0;
	public long lastCallTime = Long.MIN_VALUE;
	public long firstCallTime = Long.MAX_VALUE;
	
	/* Actual call times consider calls which are of
	 * non zero duration.
	 */
	public long lastActualCallTime = Long.MIN_VALUE;
	public long firstActualCallTime = Long.MAX_VALUE;
	
	/* Rank 0 is for the contact with whom you have had
	 * most calls, rank 1 is for the contact with whom
	 * you have had second highest number of calls, and
	 * so on.
	 */
	public int numCallRank = 99999;
	public int sustainabilityRank = 99999;
	
	public Contact(String phone) {
		this.phone = phone;
		this.name = null;
	}
	
	public Contact(String name, String phone) {
		this.name = name;
		this.phone = phone;
	}
	
	/* First, all the set functions. */
	public void setName(String name) {
		this.name = name;
	}
	public void setPhotoURI(String photoURI) {
		this.photoURI = photoURI;
	}
	/* Then, all the get functions. */
	public String getName() {
		return this.name;
	}
	public String getPhone() {
		return this.phone;
	}
	public String getPhotoURI() {
		return this.photoURI;
	}
}

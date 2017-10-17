package com.oftly.oftly;

import java.sql.Date;

public class UploadedPhotoEntry {
	int id;
	Date dateTime;
	String phoneNumber;
	
	public UploadedPhotoEntry(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	public int getId() {
		return this.id;
	}
	public Date getDateTime() {
		return this.dateTime;
	}
	public String getPhoneNumber() {
		return this.phoneNumber;
	}
}

package com.oftly.oftly;

import java.util.ArrayList;
import java.util.HashMap;

public class SearchAdapter {
	public HashMap<String, Contact>contactDetails = new HashMap<String, Contact>();
	public ArrayList<Contact>contacts = new ArrayList<Contact>();
	public ArrayList<Contact>all_contacts = new ArrayList<Contact>();
	String searchText;

	public SearchAdapter(String searchText) {
		this.searchText = searchText;
	}
	public SearchAdapter(ArrayList<Contact> all_contacts) {
		this.all_contacts = all_contacts;
	}
	public void addContactToContacts(Contact contact) {
		contacts.add(contact);
		contactDetails.put(Util.getCanonicalPhoneNumber(contact.phone), contact);
	}
	public void addContactToAllContacts(Contact contact) {
		all_contacts.add(contact);
	}
}
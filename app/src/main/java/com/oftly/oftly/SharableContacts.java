package com.oftly.oftly;

import java.util.ArrayList;
import java.util.Set;
import java.util.TreeMap;

public class SharableContacts {
	/* Key is dst and value is array of targets */
	TreeMap<String, ArrayList<Contact>> map;
	public SharableContacts() {
		map = new TreeMap<String, ArrayList<Contact>>();
	}
	private boolean checkDstExists(String dst) {
		if (map.containsKey(dst)) {
			return true;
		} else {
			return false;
		}
	}
	private void addDst(String dst) {
		assert(!map.containsKey(dst));
		map.put(dst, new ArrayList<Contact>());
	}
	public void addTarget(String dst, Contact target) {
		if (!checkDstExists(dst)) {
			addDst(dst);
		}
		map.get(dst).add(target);
	}
	public ArrayList<Contact> getTargets(String dst) {
		return map.get(dst);
	}
	public int getDstCount() {
		return map.size();
	}
	public Set getDsts() {
		return map.keySet();
	}
}

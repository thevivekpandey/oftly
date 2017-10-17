package com.oftly.oftly;

import java.util.ArrayList;
import java.util.Set;
import java.util.TreeMap;

public class SharedContactsWithMe {
	TreeMap<String, ArrayList<Contact>> map;
	public SharedContactsWithMe() {
		map = new TreeMap<String, ArrayList<Contact>>();
	}
	private boolean checkSrcExists(String src) {
		if (map.containsKey(src)) {
			return true;
		} else {
			return false;
		}
	}
	private void addSrc(String src) {
		assert(!map.containsKey(src));
		map.put(src, new ArrayList<Contact>());
	}
	public void addTarget(String src, Contact target) {
		if (!checkSrcExists(src)) {
			addSrc(src);
		}
		map.get(src).add(target);
	}
	public ArrayList<Contact> getTargets(String src) {
		return map.get(src);
	}
	public int getSrcCount() {
		return map.size();
	}
	public Set getSrcs() {
		return map.keySet();
	}
}

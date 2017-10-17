package com.oftly.oftly;

import android.app.Fragment;
import android.app.FragmentManager;

public class BackButton {
	private static BackButton backButton;
	final String NONE = "none";
	private String[] stack = {NONE, NONE}; // element 0 is top of stack
    
	FragmentManager fragmentManager = null;
    
	DialpadFragment dialpadFragment;
	CallLogFragment callLogFragment;
	Fragment contactDetailFragment;
	ContactBlockFragment contactBlockFragment;
	TransparentFragment transparentFragment;
		
	private BackButton(FragmentManager fragmentManager) {
		this.fragmentManager = fragmentManager;
	}
	/* It is caller's reponsibility to call this getter only if 
	 * backButton has been initialized by a call to another getter.
	 */
	public static BackButton getBackButtonInstance() {
		return backButton;
	}
	public static BackButton getBackButtonInstance(FragmentManager fragmentManager) {
		if (backButton == null) {
			backButton = new BackButton(fragmentManager);
		} 
		return backButton;
	}
    public void addToStack(String fragmentName) {
    	if (stack[0].equals(NONE)) {
    		stack[0] = fragmentName;
    	} else if (stack[1].equals(NONE)){
    		stack[1] = stack[0];
    		stack[0] = fragmentName;
    	} else {
    		
    	}
    }
    public void removeFromStack(String fragmentName) {
    	if (stack[1].equals(fragmentName)) {
    		stack[1] = NONE;
    	} else if (stack[0].equals(fragmentName)) {
    		stack[0] = stack[1];
    		stack[1] = NONE;
    	}
    }
    
    public String handleBackButton() {
		if (stack[0].equals(NONE)) {
			return "system";
		}
		if (stack[0].equals("dialpad")) {
	    	removeFromStack("dialpad");
	    	return "dialpad";
		}
		if (stack[0].equals("calllog")) {
    		removeFromStack("calllog");
    		return "calllog";
		}
		if (stack[0].equals("contactdetail")) {
			removeFromStack("contactdetail");
			return "contactdetail";
		}
		return null;
    }
    public String handleBackButton(String buttonPressed) {
    	if (buttonPressed.equals("transparentLayer")) {
    		if (stack[0].equals("dialpad") || stack[1].equals("dialpad")) {
    			removeFromStack("dialpad");
    			return "dialpad";
    		}
    	}
    	return null;
    }
}

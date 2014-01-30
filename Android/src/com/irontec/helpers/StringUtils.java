package com.irontec.helpers;

public class StringUtils {

	public final static boolean isValidEmail(CharSequence target) {
	    if (target == null) {
	        return false;
	    } else {
	        return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
	    }
	}
	
	public final static boolean isEmptyString(String target) {
		if (target == null || target.trim().equals("") || target.trim().equals("null")) {
			return true;
		} else {
			return false;
		}
	}
	
}

package de.db0x.eslog;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Utils {
	public static String getHost() {
		try {
			String result = InetAddress.getLocalHost().getHostName();
			if (result != null && result.length() > 0) {
				return result;
			}
		} catch (UnknownHostException e) { }

		String host = System.getenv("COMPUTERNAME");
		if (host != null) {
			return host;
		}
		host = System.getenv("HOSTNAME");
		if (host != null) {
			return host;
		}
		return "";
	}

	public static boolean indexNameMatch(String index, String pattern) {
		if ( pattern.contains("%date{")) {
			String[] tempPattern = pattern.replace("%", "").split("date");
			if ( tempPattern.length != 2 ) {
				return false;
			} else {
				if ( !index.startsWith(tempPattern[0])) {
					return false;
				}
				String temp = index.replace(tempPattern[0], "");
				String dateformat = tempPattern[1].replace("{","").replace("}","");
				if ( temp.length() != dateformat.length()) {
					return false;
				}
				try {					
					SimpleDateFormat sdf = new SimpleDateFormat(dateformat);
					sdf.parse(temp);
					return true;
				} catch ( Exception e) {
					return false;
				}
			}				
		} else {
			if (index.equalsIgnoreCase(pattern)) {   
				return true;
			}
		}	
		return false;
	}

	public static Date addDays(Date date, int days) {
		if (date == null)  {
			date = new Date();
		}
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.DATE, days); 
		return cal.getTime();
	}
	
	public static Date addMinutes(Date date, int minutes) {
		if (date == null)  {
			date = new Date();
		}
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.MINUTE, minutes); 
		return cal.getTime();
	}
	
}

package de.db0x.eslog;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Calendar;
import java.util.Date;

public class Utils {
	public static String getHost() {
		try {
		    String result = InetAddress.getLocalHost().getHostName();
		    if ( result != null && result.length() > 0 )
		        return result;
		} catch (UnknownHostException e) {}

		String host = System.getenv("COMPUTERNAME");
		if (host != null)
		    return host;
		host = System.getenv("HOSTNAME");
		if (host != null)
		    return host;

		return "";
	}
	
	public static boolean indexNameMatch( String index, String pattern ) {
		if ( index.startsWith("log-"))
			return true;
		return false;
	}
    public static Date addDays(Date date, int days) {
    	if ( date == null)
    		date = new Date();
    	
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, days); //minus number would decrement the days
        return cal.getTime();
    }
}

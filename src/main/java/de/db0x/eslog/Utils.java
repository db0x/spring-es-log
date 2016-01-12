package de.db0x.eslog;

import java.net.InetAddress;
import java.net.UnknownHostException;

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

}

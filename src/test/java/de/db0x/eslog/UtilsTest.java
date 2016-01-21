package de.db0x.eslog;

import java.util.Date;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import junit.framework.Assert;

public class UtilsTest {

	private final static Logger LOG = LoggerFactory.getLogger(UtilsTest.class);
	@Test
	public void testUtils() {
		
		Date now = new Date();
		
		Date plus1 = Utils.addDays(now, 1);
		
		Assert.assertEquals("expected +1 days", now.getTime()+( 1000 * 60 * 60 * 24 ) , plus1.getTime());
		LOG.info("passed date+1");

		Date minus1 = Utils.addDays(now, -1);

		Assert.assertEquals("expected -1 days", now.getTime()-( 1000 * 60 * 60 * 24 ) , minus1.getTime());
		LOG.info("passed date-1");

		Date plus1m = Utils.addMinutes(now, 1);

		Assert.assertEquals("expected +1 minutes", now.getTime()+( 1000 * 60  ) , plus1m.getTime());
		LOG.info("passed date+1m");

		Date minus1m = Utils.addMinutes(now, -1);

		Assert.assertEquals("expected -1 days", now.getTime()-( 1000 * 60  ) , minus1m.getTime());
		LOG.info("passed date-1m");
		
	}
}

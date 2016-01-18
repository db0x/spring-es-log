package de.db0x.eslog;

import org.junit.Test;

import junit.framework.Assert;

public class ElasticsearchAppenderTest {

	@Test
	public void testAddParameter() {
		
		ElasticsearchAppender esa = new ElasticsearchAppender();
		
		esa.addParameter("test1", "", "test");
				
		Assert.assertEquals(esa.getEsProperties().getProperties().size(), 1);
	}
}

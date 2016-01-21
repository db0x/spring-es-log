package de.db0x.eslog;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import junit.framework.Assert;

public class ElasticsearchAppenderTest {

	private final static Logger LOG = LoggerFactory.getLogger(ElasticsearchAppenderTest.class);
	@Test
	public void testAddParameter() {
		
		ElasticsearchAppender esa = new ElasticsearchAppender();
		
		esa.addParameter("name", "", "default");
		LOG.info("added name//default");		
		
		Assert.assertEquals("expected one",1,esa.getEsProperties().getProperties().size());
		LOG.info("passed size = 1");
		
		Assert.assertEquals("expected name","name",esa.getEsProperties().getProperties().get(0).getName());
		LOG.info("passed name = name");
		
		Assert.assertEquals("expected value","default",esa.getEsProperties().getProperties().get(0).getValue());
		LOG.info("passed value = default");
		
		esa.addParameter("test2", "2", "nothing");
		LOG.info("added test2/2/nothing");		

		Assert.assertEquals("expected two",2,esa.getEsProperties().getProperties().size());
		LOG.info("passed size = 2");
		
		Assert.assertEquals("expected name","test2",esa.getEsProperties().getProperties().get(1).getName());
		LOG.info("passed name = test2");
		
		Assert.assertEquals("expected value","2",esa.getEsProperties().getProperties().get(1).getValue());
		LOG.info("passed value = 2");
		
		Assert.assertEquals("expected default-clean", 5,esa.getLogPropertied().getClean().intValue());
		LOG.info("passed default (clean) = 5");

		Assert.assertEquals("expected default-clean-numberOfDocuments", 10000,esa.getLogPropertied().getCleanNumberOfDocuments().intValue());
		LOG.info("passed default (clean-numberOfDocuments) = 10000");

	}
}

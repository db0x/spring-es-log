package de.db0x.eslog;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ ElasticsearchAppenderTest.class, UtilsTest.class })
public class AllTests {

}

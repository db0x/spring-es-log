package de.db0x.eslog;

import java.io.IOException;
import java.net.URL;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.internetitem.logback.elasticsearch.AbstractElasticsearchAppender;
import com.internetitem.logback.elasticsearch.ClassicElasticsearchPublisher;
import com.internetitem.logback.elasticsearch.config.ElasticsearchProperties;
import com.internetitem.logback.elasticsearch.config.Property;
import com.internetitem.logback.elasticsearch.config.Settings;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;

@Component
public class ElasticsearchAppender extends AbstractElasticsearchAppender<ILoggingEvent> {

	private final static Logger LOG = LoggerFactory.getLogger(ElasticsearchAppender.class);
	
	@Autowired
	Environment env;
	
	@Value(value="${server.port}")
	private String serverPort;

	@Autowired
	private LogProperties properties;
	
	@PostConstruct
	private void init() {

		try {
			
			if ( env.getProperty("spring.application.log.enable-es-log") == null || env.getProperty("spring.application.log.enable-es-log").equalsIgnoreCase("false") ) {
				LOG.info("ElasticsearchAppender is disabled");
				return;
			}
			
			if ( properties.getParameters() != null ) {
				for ( Object key : properties.getParameters().keySet() ) {
					LOG.info("   -> " + key.toString());;
				}
			} else {
				LOG.info("---------------- ES PROPS is null ----------------");
			}
			MDC.put("host",Utils.getHost());
			MDC.put("port",serverPort);
			MDC.put("spring.application.name", env.getProperty("spring.application.name"));

			ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) LoggerFactory
					.getLogger(Logger.ROOT_LOGGER_NAME);
			
			LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
			
			Settings settings = new Settings();
			settings.setIndex("log-%date{yyyy-MM-dd}");
			settings.setLoggerName("es-logger");
			settings.setUrl(new URL(env.getProperty("spring.application.log.es-log-url")));
			settings.setType("ic-log");

			ElasticsearchAppender ea = new ElasticsearchAppender(settings);
			ElasticsearchProperties ep = new ElasticsearchProperties();
			Property pHost = new Property();
			pHost.setName("host");
			pHost.setValue("%X{host}");
			pHost.setAllowEmpty(false);
			ep.addProperty(pHost);

			Property pPort = new Property();
			pPort.setName("port");
			pPort.setValue("%X{port}");
			pPort.setAllowEmpty(false);
			ep.addProperty(pPort);

			Property pSeverity = new Property();
			pSeverity.setName("severity");
			pSeverity.setValue("%level");
			ep.addProperty(pSeverity);

			Property pThread = new Property();
			pThread.setName("thread");
			pThread.setValue("%thread");
			ep.addProperty(pThread);

			Property pStracktrace = new Property();
			pStracktrace.setName("stacktrace");
			pStracktrace.setValue("%ex");
			ep.addProperty(pStracktrace);

			Property pLogger = new Property();
			pLogger.setName("logger");
			pLogger.setValue("%logger");
			ep.addProperty(pLogger);

			Property pMethode = new Property();
			pMethode.setName("application");
			pMethode.setValue("%X{spring.application.name}");
			ep.addProperty(pMethode);

			ea.setProperties(ep);
			ea.setContext(lc);

			ea.start();

			root.addAppender(ea);
			ch.qos.logback.classic.Logger eslog = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger("es-logger");
			eslog.setAdditive(false);
			
			LOG.info("ElasticsearchAppender added ["+env.getProperty("spring.application.log.es-log-url")+"]");
		} catch (Exception e) {
			LOG.error(e.getMessage());
		}
	}

	public ElasticsearchAppender() {
	}

	public ElasticsearchAppender(Settings settings) {
		super(settings);
	}

	@Override
	protected void appendInternal(ILoggingEvent eventObject) {

		String targetLogger = eventObject.getLoggerName();

		String loggerName = settings.getLoggerName();
		if (loggerName != null && loggerName.equals(targetLogger)) {
			return;
		}

		String errorLoggerName = settings.getErrorLoggerName();
		if (errorLoggerName != null && errorLoggerName.equals(targetLogger)) {
			return;
		}

		eventObject.prepareForDeferredProcessing();
		if (settings.isIncludeCallerData()) {
			eventObject.getCallerData();
		}

		publishEvent(eventObject);
	}

	protected ClassicElasticsearchPublisher buildElasticsearchPublisher() throws IOException {
		return new ClassicElasticsearchPublisher(getContext(), errorReporter, settings, elasticsearchProperties);
	}

}

package de.db0x.eslog;

import java.io.IOException;
import java.net.URL;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

	@Value(value = "${server.port}")
	private String serverPort;

	@Value(value = "${spring.application.name}")
	private String appName;

	@Value(value = "${spring.application.log.enable-es-log}")
	private Boolean enabled;

	@Autowired
	private LogProperties properties = new LogProperties();

	private ElasticsearchProperties esProperties = new ElasticsearchProperties();

	public ElasticsearchAppender() {
	}

	@PostConstruct
	private void init() {

		try {

			if (enabled != null && enabled.booleanValue() == false) {
				LOG.info("ElasticsearchAppender is disabled");
				return;
			}

			MDC.put("host", Utils.getHost());

			if (serverPort != null) {
				MDC.put("port", serverPort);
			} else {
				MDC.put("port", "-UNKNOWN-");
			}

			if (appName != null) {
				MDC.put("spring.application.name", appName);
			} else {
				MDC.put("spring.application.name", "-UNKNOWN-");
			}

			ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) LoggerFactory
					.getLogger(Logger.ROOT_LOGGER_NAME);

			LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();

			Settings settings = new Settings();

			if (properties.getIndexName() == null || properties.getIndexName().length() == 0)
				properties.setIndexName("log-%date{yyyy-MM-dd}");
			settings.setIndex(properties.getIndexName());

			settings.setLoggerName("es-logger");

			String url;
			if (properties.getHost() == null || properties.getHost().length() == 0) {
				url = "http://localhost:";
			} else {
				url = "http://" + properties.getHost() + ":";
			}

			if (properties.getPorts() == null || properties.getPorts().size() == 0) {
				url = url + "9200";
			} else {
				url = url + properties.getPorts().get(0);
			}

			url = url + "/_bulk";
			settings.setUrl(new URL(url));

			if (properties.getType() == null || properties.getType().length() == 0)
				properties.setType("eslog");
			settings.setType(properties.getType());

			ElasticsearchAppender ea = new ElasticsearchAppender(settings);
			
			addDefaultParameters();
			
			if (properties.getParameters() != null) {
				for (String key : properties.getParameters().keySet()) {
					LOG.info("   -> " + key.toString() + " - " + properties.getParameters().get(key));
					if (!"logger.thread.severity.stacktrace".contains(key)) {
						addParameter( key, properties.getParameters().get(key), properties.getParameters().get(key) );
					}
				}
			}

			ea.setProperties(esProperties);
			ea.setContext(lc);
			ea.start();

			root.addAppender(ea);

			ch.qos.logback.classic.Logger eslog = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger("es-logger");
			eslog.setAdditive(false);

			LOG.info("ElasticsearchAppender added [" + url + "]");
		} catch (Exception e) {
			LOG.error(e.getMessage());
		}
	}
    
	public void addDefaultParameters() {
		addParameter( "logger", properties.getParameters().get("logger"), "%logger" );
		addParameter( "thread", properties.getParameters().get("thread"), "%thread" );
		addParameter( "severity", properties.getParameters().get("severity"), "%level" );
		addParameter( "stacktrace", properties.getParameters().get("stacktrace"), "%ex" );
	}
	
	public void addParameter( String parameter, String value, String defaultValue ) {
		Property property = new Property();
		property.setName(parameter);
		if (value == null || value.length() == 0) {
			value = defaultValue;
		}
		
		property.setValue(value);
		esProperties.addProperty(property);
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

	public ElasticsearchProperties getEsProperties() {
		return esProperties;
	}
	
	public LogProperties getLogPropertied() {
		return properties;
	}
}

package de.db0x.eslog;

import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(value = "spring.application.log", merge = true)
@Component
public class LogProperties {
	
	private boolean enableEsLog;
	
	private String esLogUrl;
	
	private Map<String,String> parameters;
	
	private String indexName;
	
	private String type;

	public boolean isEnableEsLog() {
		return enableEsLog;
	}

	public void setEnableEsLog(boolean enableEsLog) {
		this.enableEsLog = enableEsLog;
	}

	public String getEsLogUrl() {
		return esLogUrl;
	}

	public void setEsLogUrl(String esLogUrl) {
		this.esLogUrl = esLogUrl;
	}

	public String getIndexName() {
		return indexName;
	}

	public void setIndexName(String indexName) {
		this.indexName = indexName;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Map<String,String> getParameters() {
		return parameters;
	}

	public void setParameters(Map<String,String> parameters) {
		this.parameters = parameters;
	}
	
}
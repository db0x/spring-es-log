package de.db0x.eslog;

import java.util.List;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(value = "spring.application.log", merge = true)
@Component
public class LogProperties {

	private boolean enableEsLog;

	private Map<String, String> parameters;

	private String indexName;

	private String type;

	private String host;

	private List<Integer> ports;

	public boolean isEnableEsLog() {
		return enableEsLog;
	}

	public void setEnableEsLog(boolean enableEsLog) {
		this.enableEsLog = enableEsLog;
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

	public Map<String, String> getParameters() {
		return parameters;
	}

	public void setParameters(Map<String, String> parameters) {
		this.parameters = parameters;
	}

	public List<Integer> getPorts() {
		return ports;
	}

	public void setPorts(List<Integer> ports) {
		this.ports = ports;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

}
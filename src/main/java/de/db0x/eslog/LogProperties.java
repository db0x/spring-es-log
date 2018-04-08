package de.db0x.eslog;

import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(
		value = "spring.application.log", 
		ignoreInvalidFields = true, 
		ignoreUnknownFields = true)
@Component
public class LogProperties {

	private boolean enableEsLog;

	private Map<String, String> parameters;

	private String indexName;

	private String type;

	private String host;

	private Integer port;
	
	private String clustername;

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
		if ( type == null ) {
			type = "eslog";
		}
			
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

	public Integer getPort() {
		if ( this.port == null ) { 
			return 9200;
		}
		return this.port;
	}

	public void setPort(Integer port) {
		this.port = port;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getClustername() {
		if ( clustername == null || clustername.length() == 0 ) {
			return "elasticsearch";
		}
		return clustername;
	}

	public void setClustername(String clustername) {
		this.clustername = clustername;
	}

}
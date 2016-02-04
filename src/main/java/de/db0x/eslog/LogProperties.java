package de.db0x.eslog;

import java.util.ArrayList;
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
	
	private Integer clean ;
	
	private Integer cleanInterval;
	
	private Integer cleanNumberOfDocuments;
	
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

	public List<Integer> getPorts() {
		if ( ports == null || ports.size() != 2 || ports.get(0) == null || ports.get(1) == null ) {
			ports = new ArrayList<Integer>();
			ports.add(9200);
			ports.add(9300);
		}
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

	public Integer getClean() {
		if ( clean == null ) {
			clean = 5;
		}
		return clean;
	}

	public void setClean(Integer clean) {
		this.clean = clean;
	}

	public Integer getCleanIntervall() {
		if ( cleanInterval == null ) {
			cleanInterval =  60;
		}
		return cleanInterval;
	}

	public void setCleanIntervall(Integer cleanInterval) {
		this.cleanInterval = cleanInterval;
	}

	public Integer getCleanNumberOfDocuments() {
		if ( cleanNumberOfDocuments == null ) {
			cleanNumberOfDocuments = 10000;
		}
			
		return cleanNumberOfDocuments;
	}

	public void setCleanNumberOfDocuments(Integer cleanNumberOfDocuments) {
		this.cleanNumberOfDocuments = cleanNumberOfDocuments;
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
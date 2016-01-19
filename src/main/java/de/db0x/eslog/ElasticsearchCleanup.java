package de.db0x.eslog;

import java.util.Date;

import javax.annotation.PostConstruct;

import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.cluster.metadata.IndexMetaData;
import org.elasticsearch.common.collect.ImmutableOpenMap;
import org.elasticsearch.common.hppc.cursors.ObjectCursor;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
//import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
@EnableAsync
public class ElasticsearchCleanup {

	private final static Logger LOG = LoggerFactory.getLogger(ElasticsearchCleanup.class);

	@Autowired
	private IndexCleaner ic;

	@Autowired
	private LogProperties properties;

	// @Scheduled(fixedRate = 5000)
	@PostConstruct
	public void clean() {
		
		if ( !isAutocleanEnabled() )
			return;
		
		try (TransportClient client = new TransportClient()) {
			client.addTransportAddress(
					new InetSocketTransportAddress(properties.getHost(), properties.getPorts().get(1)));

			ImmutableOpenMap<String, IndexMetaData> indexes = client.admin().cluster().prepareState().execute()
					.actionGet().getState().getMetaData().getIndices();

			for (ObjectCursor<String> key : indexes.keys()) {
				if (Utils.indexNameMatch(key.value, "")) {
					Date created = new Date(indexes.get(key.value).creationDate());
					if (Utils.addDays(null, -1 * ( properties.getClean() )).after(created)) {
						LOG.info("cleanup index " + key.value + " [" 
								+ client.prepareCount(key.value).get().getCount()
								+ "]");
						ic.cleanAndDelete(key.value);
					}
				}
			}
		}
	}
	
	public boolean isAutocleanEnabled() {
		if ( properties.getClean() == null || properties.getClean() <= 0 ) {
			return false;
		}
		if ( properties.getPorts().size() < 2 ) {
			return false;
		}
		if ( properties.getPorts().get(1) == null) {
			return false;
		}
		if ( properties.getHost() == null ) {
			return false;
		}
		return true;
	}
}

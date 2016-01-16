package de.db0x.eslog;

import javax.annotation.PostConstruct;

import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.cluster.metadata.IndexMetaData;
import org.elasticsearch.common.collect.ImmutableOpenMap;
import org.elasticsearch.common.hppc.cursors.ObjectCursor;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
//import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling

public class ElasticsearchCleanup {

	private final static Logger LOG = LoggerFactory.getLogger(ElasticsearchCleanup.class);

	//@Scheduled(fixedRate = 5000)
	@PostConstruct
	public void clean() {
		try (TransportClient client = new TransportClient()) {
			client.addTransportAddress(new InetSocketTransportAddress("localhost", 9300));

			ImmutableOpenMap<String, IndexMetaData> indexes = client.admin().cluster().prepareState().execute()
					.actionGet().getState().getMetaData().getIndices();

			for (ObjectCursor<String> key : indexes.keys()) {
				LOG.info(key.value);
			}
		}
	}
}

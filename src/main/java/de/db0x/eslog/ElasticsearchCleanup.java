package de.db0x.eslog;

import static org.elasticsearch.index.query.QueryBuilders.termQuery;

import java.util.Date;

import javax.annotation.PostConstruct;

import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.search.SearchResponse;
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
				if ( Utils.indexNameMatch(key.value, "")) {
					Date created = new Date(indexes.get(key.value).creationDate());
					if ( Utils.addDays(null, -5).after(created) && client.prepareCount(key.value).get().getCount() > 0 ) {
						LOG.info("cleanup index "+ key.value+" ["+ client.prepareCount(key.value).get().getCount()+"]");
						try {
							clean( client, key.value );
						} catch ( Exception e) {
							LOG.error("unable to clean index "+ key.value+" "+ e.getMessage());
						}
					}
					if ( client.prepareCount(key.value).get().getCount() == 0 ) {
						LOG.info("delete index " + key.value+ " because it is empty" );
						try {
							delete(client, key.value);
						} catch ( Exception e ) {
							LOG.error("unable to delete index "+ key.value+" "+ e.getMessage());
						}
					}
				}
			}
			if ( LOG == null ) {
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	private void clean( TransportClient client, String indexName ) {
		SearchResponse response = client.prepareSearch().execute().actionGet();
		
		response.getHits().iterator().next().getId();
		
		client.prepareDeleteByQuery(indexName)
        .setQuery(termQuery("Application", ""))
        .execute()
        .actionGet();
						
	}
	
	private void delete( TransportClient client, String indexName ) {
		client.admin().indices().delete(new DeleteIndexRequest(indexName)).actionGet();
	}
}

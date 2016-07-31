package de.db0x.eslog;

import static org.elasticsearch.index.query.QueryBuilders.termQuery;

import java.net.InetAddress;

import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.flush.FlushRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.search.SearchHit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class IndexCleaner {

	private final static Logger LOG = LoggerFactory.getLogger(IndexCleaner.class);

	@Autowired
	private LogProperties properties;
	
	@Async
	public void cleanAndDelete(String indexName) {
		
		Settings settings = Settings.builder()
		        .put("cluster.name", properties.getClustername() ).build();

		try (TransportClient client = TransportClient.builder()
				.settings(settings)
				.build()
				.addTransportAddress(new InetSocketTransportAddress( 
						InetAddress.getByName(properties.getHost()), 
						properties.getPorts().get(1)));
				) {

			SearchResponse response = client.prepareSearch(indexName).setQuery(termQuery("_type", properties.getType()))
					.setFrom(0).setSize(properties.getCleanNumberOfDocuments()).execute().actionGet();  

			for (SearchHit sh : response.getHits().getHits()) {
				client.prepareDelete(indexName, properties.getType(), sh.getId()).execute();
			}
			
			client.admin().indices().flush(new FlushRequest(indexName)).actionGet();
			
			if (client.prepareSearch().execute().actionGet().getHits().getTotalHits() == 0) {
				LOG.info("delete index " + indexName + " because it is empty");
				try {
					delete(client, indexName);
				} catch (Exception e) {
					LOG.error("unable to delete index " + indexName + " " + e.getMessage());
				}
			}

		} catch (Exception e) {
			LOG.error("unable to clean index " + indexName + " " + e.getMessage());
		}
	}

	private void delete(TransportClient client, String indexName) {
		client.admin().indices().delete(new DeleteIndexRequest(indexName)).actionGet();
	}

}

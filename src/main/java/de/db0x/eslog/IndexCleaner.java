package de.db0x.eslog;

import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.search.SearchHit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import static org.elasticsearch.index.query.QueryBuilders.*;

@Component
public class IndexCleaner {

	private final static Logger LOG = LoggerFactory.getLogger(IndexCleaner.class);

	@Async
	public void clean(String indexName) {
		try (TransportClient client = new TransportClient()) {

			client.addTransportAddress(new InetSocketTransportAddress("localhost", 9300));

			SearchResponse response = client.prepareSearch(indexName).setQuery(termQuery("_type", "ic-log"))
					.setFrom(0).setSize(60).execute().actionGet();  // TODO implement with real data

			for (SearchHit sh : response.getHits().getHits()) {
				LOG.info(sh.getId());
			}

			if (client.prepareCount(indexName).get().getCount() == 0) {
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
		// client.prepareDeleteByQuery(indexName)
		// .setQuery(termQuery("Application", ""))
		// .execute()
		// .actionGet();
	}

	private void delete(TransportClient client, String indexName) {
		client.admin().indices().delete(new DeleteIndexRequest(indexName)).actionGet();
	}

}

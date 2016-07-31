package de.db0x.eslog;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;

import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.cluster.metadata.IndexMetaData;
import org.elasticsearch.common.collect.ImmutableOpenMap;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.carrotsearch.hppc.cursors.ObjectCursor;

@Component
@Scope("singleton")

@EnableScheduling
@EnableAsync
public class ElasticsearchCleanup {

	private final static Logger LOG = LoggerFactory.getLogger(ElasticsearchCleanup.class);

	@Autowired
	private IndexCleaner ic;

	@Autowired
	private LogProperties properties;
	
	@Value(value = "${spring.application.log.enable-es-log:false}")
	private Boolean enabled;

	private Date lastRun = null;

	@Scheduled(fixedRate= 60000)
	public void clean() {
		
		if ( !isAutocleanEnabled() ) {
			return;
		}
		
		if ( lastRun != null && lastRun.after( Utils.addMinutes( new Date(), -1*(properties.getCleanIntervall()) ))) {
			return;
		}
		
		try {			
			Settings settings = Settings.builder()
			        .put("cluster.name", properties.getClustername() ).build();
			try (
					TransportClient client = TransportClient.builder()
					.settings(settings)
					.build()
					.addTransportAddress(new InetSocketTransportAddress( 
							InetAddress.getByName(properties.getHost()), 
							properties.getPorts().get(1)));
					
				) {
				
				ImmutableOpenMap<String, IndexMetaData> indexes = client.admin().cluster().prepareState().execute()
						.actionGet().getState().getMetaData().getIndices();
	
				
				for (ObjectCursor<String> key : indexes.keys() ) {
					if (Utils.indexNameMatch(key.value, properties.getIndexName())) {
						Date created = new Date(indexes.get(key.value).getCreationDate() );
						if (Utils.addDays(null, -1 * ( properties.getClean() )).after(created)) {
							LOG.info("cleanup index " + key.value + " [" 
									+ client.prepareSearch().execute().actionGet().getHits().getTotalHits()
									+ "]");
							ic.cleanAndDelete(key.value);
						}
					}
				}
			} catch (UnknownHostException e) {
				e.printStackTrace();
			}
		} finally {
			lastRun = new Date();
		}
	}
	
	public boolean isAutocleanEnabled() {
		if ( enabled == null  || enabled == false) {
			return false;
		}
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

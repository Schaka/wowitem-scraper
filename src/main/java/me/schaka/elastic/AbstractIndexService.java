package me.schaka.elastic;

import java.io.InputStreamReader;
import java.io.Reader;

import org.elasticsearch.client.AdminClient;
import org.elasticsearch.client.Client;
import org.elasticsearch.cluster.health.ClusterHealthStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.CharStreams;

public abstract class AbstractIndexService {

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	protected final Client client;

	public AbstractIndexService(Client client) {
		this.client = client;
		waitforYellowStatus();
		buildIndex();
	}

	protected abstract void fillIndex();

	protected abstract String indexName();

	protected abstract String[] indexTypes();

	private void buildIndex() {
		AdminClient admin = client.admin();

		// the analysis part of settings.json could be used here, if the index was closed first and opened back up after applying analysis changes
		// source: https://www.elastic.co/guide/en/elasticsearch/reference/current/indices-update-settings.html
		// admin.indices().prepareUpdateSettings("jobs").setSettings(settings).setIndices("jobs").get();
		if (!indexExists()) {
			String settings = getJsonFromClassPath("/schema/" + indexName() + "/settings.json");
			admin.indices()
					.prepareCreate(indexName())
					.setSource(settings)
					.get();
		}

		// https://www.elastic.co/guide/en/elasticsearch/client/java-api/current/java-admin-indices.html
		for (String type : indexTypes()) {
			String mapping = getJsonFromClassPath("/schema/" + indexName() + "/" + type + "/mapping.json");
			admin.indices().preparePutMapping(indexName())
					.setType(type)
					.setSource(mapping)
					.get();
		}
	}

	protected boolean indexExists() {
		return client.admin().indices().prepareExists(indexName()).get().isExists();
	}

	protected void waitforYellowStatus() {
		while (ClusterHealthStatus.RED.equals(client.admin().cluster().prepareHealth().get().getStatus())) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				logger.warn("Couldn't put thread to sleep!");
			}
		}
	}

	protected String getJsonFromClassPath(String file) {
		try (Reader reader = new InputStreamReader(getClass().getResourceAsStream(file))) {
			return CharStreams.toString(reader);
		} catch (Exception e) {
			throw new IllegalStateException("Couldn't load necessary resource from classpath!", e);
		}
	}
}

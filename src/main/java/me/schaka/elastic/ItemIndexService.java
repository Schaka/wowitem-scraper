package me.schaka.elastic;

import org.elasticsearch.action.admin.indices.analyze.AnalyzeResponse;
import org.elasticsearch.client.Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ItemIndexService extends AbstractIndexService {

	private static final String INDEX_NAME = "items";
	private static final String[] INDEX_TYPES = new String[] { "item" };

	@Autowired
	public ItemIndexService(Client client) {
		super(client);
	}

	@Override
	protected void fillIndex() {

	}

	@Override
	protected String indexName() {
		return INDEX_NAME;
	}

	@Override
	protected String[] indexTypes() {
		return INDEX_TYPES;
	}

	public AnalyzeResponse analyzeStringWithAnalyzer(String string, String analyzer) {
		return client.admin().indices()
				.prepareAnalyze(indexName(), string)
				.setAnalyzer(analyzer)
				.get();
	}
}

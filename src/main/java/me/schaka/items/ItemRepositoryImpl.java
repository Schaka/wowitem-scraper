package me.schaka.items;

import static org.springframework.data.elasticsearch.core.query.Criteria.where;

import org.elasticsearch.client.Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.databind.ObjectMapper;

@Repository
public class ItemRepositoryImpl implements ItemRepositoryCustom {

	private static final String TOP_HITS = "by_top";

	private ElasticsearchTemplate elasticsearchTemplate;

	private Client client;

	private ObjectMapper objectMapper;

	@Autowired
	public ItemRepositoryImpl(ElasticsearchTemplate elasticsearchTemplate, Client client, ObjectMapper objectMapper) {
		this.elasticsearchTemplate = elasticsearchTemplate;
		this.client = client;
		this.objectMapper = objectMapper;
	}

	@Override
	public void deleteOlderThan(long dateTimeInMilliseconds) {
		Criteria deleteCriteria = where("tsupdate").lessThan(dateTimeInMilliseconds);
		CriteriaQuery deleteQuery = new CriteriaQuery(deleteCriteria);
		elasticsearchTemplate.delete(deleteQuery, ItemDocument.class);
	}

}

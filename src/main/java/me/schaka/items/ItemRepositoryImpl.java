package me.schaka.items;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Repository;

import static org.elasticsearch.index.query.MultiMatchQueryBuilder.Type.MOST_FIELDS;
import static org.springframework.data.elasticsearch.core.query.Criteria.where;

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
	public Page<ItemDocument> findForTerm(String term, Pageable pageable) {
		QueryBuilder query = QueryBuilders.multiMatchQuery(term, "name", "patch", "set").type(MOST_FIELDS);
		NativeSearchQueryBuilder qb = new NativeSearchQueryBuilder()
				.withQuery(query)
				.withIndices("items")
				.withTypes("item")
				.withPageable(pageable);
		return elasticsearchTemplate.queryForPage(qb.build(), ItemDocument.class);
	}

	@Override
	public void deleteOlderThan(long dateTimeInMilliseconds) {
		Criteria deleteCriteria = where("tsupdate").lessThan(dateTimeInMilliseconds);
		CriteriaQuery deleteQuery = new CriteriaQuery(deleteCriteria);
		elasticsearchTemplate.delete(deleteQuery, ItemDocument.class);
	}

}

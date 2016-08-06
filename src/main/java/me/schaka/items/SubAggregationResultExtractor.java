package me.schaka.items;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.metrics.tophits.TopHits;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.elasticsearch.core.ResultsExtractor;

import com.fasterxml.jackson.databind.ObjectMapper;

public class SubAggregationResultExtractor implements ResultsExtractor<List<ItemDocument>> {

	private static final Logger LOG = LoggerFactory.getLogger(SubAggregationResultExtractor.class);

	private String aggregationName;

	private String subAggregationName;

	private ObjectMapper objectMapper;

	public SubAggregationResultExtractor(String aggregationName, String subAggregationName, ObjectMapper objectMapper) {
		this.aggregationName = aggregationName;
		this.subAggregationName = subAggregationName;
		this.objectMapper = objectMapper;

	}

	@Override
	public List<ItemDocument> extract(SearchResponse searchResponse) {
		Terms terms = searchResponse.getAggregations().get(aggregationName);
		List<ItemDocument> returnList = new ArrayList<>();
		for (Terms.Bucket bucket : terms.getBuckets()) {
			TopHits hits = bucket.getAggregations().get(subAggregationName);
			for (SearchHit hit : hits.getHits().getHits()) {
				try {
					returnList.add(objectMapper.readValue(hit.sourceAsString(), ItemDocument.class));
				} catch (IOException e) {
					LOG.error("Could not extract result.", e);
				}
			}
		}
		return returnList;
	}
}

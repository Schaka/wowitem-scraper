package me.schaka.elastic;

import java.io.IOException;

import org.elasticsearch.client.Client;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.EntityMapper;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
public class ElasticConfiguration {

	@Bean
	public ElasticsearchTemplate elasticsearchTemplate(Client client, ObjectMapper objectMapper) {
		return new ElasticsearchTemplate(client, new CustomEntityMapper(objectMapper));
	}

	private static final class CustomEntityMapper implements EntityMapper {

		private ObjectMapper objectMapper;

		private CustomEntityMapper(ObjectMapper objectMapper) {
			this.objectMapper = objectMapper.copy();
			objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			objectMapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
		}

		@Override
		public String mapToString(Object object) throws IOException {
			return objectMapper.writeValueAsString(object);
		}

		@Override
		public <T> T mapToObject(String source, Class<T> clazz) throws IOException {
			return objectMapper.readValue(source, clazz);
		}
	}
}

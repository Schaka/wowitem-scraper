package me.schaka.items;

import org.springframework.data.elasticsearch.repository.ElasticsearchCrudRepository;

public interface ItemRepository extends ElasticsearchCrudRepository<ItemDocument, Long>, ItemRepositoryCustom {
}

package me.schaka.items;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ItemRepositoryCustom {

	void deleteOlderThan(long dateTimeInMilliseconds);

	Page<ItemDocument> findForTerm(String term, Pageable pageable);
}

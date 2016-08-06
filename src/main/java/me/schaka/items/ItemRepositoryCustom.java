package me.schaka.items;

public interface ItemRepositoryCustom {

	void deleteOlderThan(long dateTimeInMilliseconds);
}

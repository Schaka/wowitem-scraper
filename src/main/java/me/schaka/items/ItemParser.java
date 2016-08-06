package me.schaka.items;

import org.jsoup.nodes.Document;

/**
 * Created by Jonas on 06.08.2016.
 */
public interface ItemParser {

    ItemDocument parsePage(Document page);
}

package me.schaka.items;

import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.IntStream;

@RestController
@RequestMapping("/items")
public class ItemIndexController {

	private static final Logger LOG = LoggerFactory.getLogger(ItemIndexController.class);

	@Autowired
	private ItemService itemService;

	@Autowired
	private ThottbotParser thottbotParser;

	private final AtomicBoolean isIndexing = new AtomicBoolean(false);

	@RequestMapping("/index")
	public ResponseEntity<Void> createIndex(){
		if(isIndexing.get() == false){

			ArrayList<Integer> retry = new ArrayList<>(5000);

			isIndexing.set(true);
			IntStream.range(1, 40000).parallel().forEach(i -> {
				try {
					itemService.saveNewOnlyAsync(thottbotParser.parsePage(Jsoup.connect("http://web.archive.org/web/20050510121309/http://thottbot.com/?i="+i).timeout(1000).get()));
				} catch (HttpStatusException httpEx){

				} catch(SocketTimeoutException timeOut) {
					retry.add(i);
				} catch (Exception e) {
					LOG.error("Failed at http://web.archive.org/web/20050510121309/http://thottbot.com/?i="+i, e);
				}
			});

			retry.parallelStream().forEach(i -> {
				try {
					itemService.saveNewOnlyAsync(thottbotParser.parsePage(Jsoup.connect("http://web.archive.org/web/20050510121309/http://thottbot.com/?i="+i).timeout(30000).get()));
				} catch (Exception e) {
				}
			});

			isIndexing.set(false);
			return new ResponseEntity<Void>(HttpStatus.OK);
		}
		return new ResponseEntity<Void>(HttpStatus.SERVICE_UNAVAILABLE);
	}
}

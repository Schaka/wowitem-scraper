package me.schaka.items;

import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.IntStream;

@RestController
@RequestMapping("/items")
public class ItemIndexController {

	private static final Logger LOG = LoggerFactory.getLogger(ItemIndexController.class);

	private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");

	@Autowired
	private ItemService itemService;

	@Autowired
	private List<ItemParser> parsers;

	@Autowired
	private AllakhazamParser allakhazamParser;

	@Autowired
	private ThottbotParser thottbotParser;

	private final AtomicBoolean isIndexing = new AtomicBoolean(false);

	@RequestMapping("/index")
	public ResponseEntity<Void> createIndex(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date){
		if(isIndexing.get() == false){
			isIndexing.set(true);
			for (ItemParser parser: parsers) {
				doForParser(parser, date);
			}
			isIndexing.set(false);
			return new ResponseEntity<Void>(HttpStatus.OK);
		}
		return new ResponseEntity<Void>(HttpStatus.SERVICE_UNAVAILABLE);
	}

	@RequestMapping("/index-single/{type:allakhazam|thottbot}")
	public ResponseEntity<Void> indexSingle(@RequestParam String link, @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date, @PathVariable String type) throws IOException{
		Document page = Jsoup.connect(archiveLinkForDate(date)+link).get();
		switch(type){
			case "allakhazam":
				itemService.saveNewOnlyAsync(allakhazamParser.parsePage(page));
				break;
			case "thottbot":
				itemService.saveNewOnlyAsync(thottbotParser.parsePage(page));
				break;
		}
		return new ResponseEntity<Void>(HttpStatus.OK);
	}

	private void doForParser(ItemParser parser, LocalDate date){
		ArrayList<Integer> retry = new ArrayList<>(5000);
		IntStream.range(1, 40000).parallel().forEach(i -> {
			try {
				itemService.saveNewOnlyAsync(parser.parsePage(Jsoup.connect(archiveLinkForDate(date)+parser.getLink(i)).timeout(1000).get()));
			} catch (HttpStatusException httpEx){

			} catch(SocketTimeoutException timeOut) {
				retry.add(i);
			} catch (Exception e) {
				LOG.error("Failed at "+parser.getLink(i), e);
			}
		});

		retry.parallelStream().forEach(i -> {
			try {
				itemService.saveNewOnlyAsync(parser.parsePage(Jsoup.connect(archiveLinkForDate(date)+parser.getLink(i)).timeout(30000).get()));
			} catch (Exception e) {
			}
		});
	}

	private String archiveLinkForDate(LocalDate date){
		return "http://web.archive.org/web/"+date.format(formatter)+"000000/";
	}
}

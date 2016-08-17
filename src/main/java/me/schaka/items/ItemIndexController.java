package me.schaka.items;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.IntStream;

@Controller
@RequestMapping("/items")
public class ItemIndexController {

	private static final Logger LOG = LoggerFactory.getLogger(ItemIndexController.class);

	private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");

	@Autowired
	private ItemService itemService;

	@Autowired
	private ItemRepository itemRepository;

	@Autowired
	private List<ItemParser> parsers;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private AllakhazamParser allakhazamParser;

	@Autowired
	private ThottbotParser thottbotParser;

	private static final Sort sort = new Sort(new Sort.Order(Sort.Direction.DESC, "_score"));

	private final AtomicBoolean isIndexing = new AtomicBoolean(false);

	@GetMapping("")
	public String handlePage(){
		return "views/item-search";
	}

	@PostMapping("/search")
	public String handlePage(Model model, @RequestParam("q") String term , @RequestParam(defaultValue = "0") Integer page){
		model.addAttribute("items", itemRepository.findForTerm(term, new PageRequest(page, 20, sort)));
		return "views/item-search :: result-table";
	}

	@GetMapping("/search/{id}")
	public String handlePage(Model model, @PathVariable Long id) throws Exception{
		model.addAttribute("item", objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(itemRepository.findOne(id)));
		return "views/single-item";
	}

	@GetMapping("/index")
	public String handleIndexPage(){
		return "views/index";
	}

	@PostMapping("/index")
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

	@GetMapping("/index-single")
	public String handleIndexSinglePage(){
		return "views/index-single";
	}

	@PostMapping("/index-single/{type:allakhazam|thottbot}")
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
		IntStream.range(1, parser.itemId()).parallel().forEach(i -> {
			try {
				itemService.saveNewOnlyAsync(parser.parsePage(Jsoup.connect(archiveLinkForDate(date)+parser.getLink(i)).timeout(1000).get()));
			} catch (HttpStatusException httpEx){

			} catch (StringIndexOutOfBoundsException stringEx){

			} catch(SocketTimeoutException timeOut) {
				retry.add(i);
			} catch (Exception e) {
				LOG.error("Failed at "+archiveLinkForDate(date)+parser.getLink(i), e);
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

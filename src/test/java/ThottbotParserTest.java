import me.schaka.items.ItemDocument;
import me.schaka.items.ThottbotParser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Test;

/**
 * Created by Jonas on 06.08.2016.
 */
public class ThottbotParserTest {

    @Test
    public void testParsing() throws Exception{
        ThottbotParser parser = new ThottbotParser();
        Document doc = Jsoup.connect("http://web.archive.org/web/20050510121309/http://thottbot.com/?i=37841").get();
        ItemDocument item = parser.parsePage(doc);
    }
}

import me.schaka.items.AllakhazamParser;
import me.schaka.items.ItemDocument;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Test;

/**
 * Created by Jonas on 07.08.2016.
 */
public class AllakhazamParserTest {

    @Test
    public void testParsing() throws Exception{
        AllakhazamParser parser = new AllakhazamParser();
        Document doc = Jsoup.connect("http://web.archive.org/web/20041115062114/http://wow.allakhazam.com/db/item.html?witem=2933").get();
        ItemDocument item = parser.parsePage(doc);
    }
}

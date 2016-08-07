package me.schaka.items;

import com.google.common.collect.ImmutableMap;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Created by Jonas on 07.08.2016.
 */
@Component
public class AllakhazamParser extends ItemParser {

    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private static final ImmutableMap<String, String> QUALITY_MAP = ImmutableMap.<String, String>builder()
            .put("greyname", "Poor")
            .put("whitename", "Common")
            .put("greenname", "Uncommon")
            .put("bluename", "Rare")
            .put("purplename", "Epic")
            .put("orangename", "Legendary")
            .build();

    @Override
    public int itemId() {
        return 25000;
    }

    @Override
    public String getLink(int id) {
        return "http://wow.allakhazam.com/db/item.html?witem="+id;
    }

    @Override
    protected LocalDate getArchived(Document page) {
        String updateText = page.select("td.sm").html();
        int stringStart = updateText.indexOf("Updated: ");
        return LocalDate.parse(updateText.substring(stringStart+9, stringStart+19), formatter);
    }

    @Override
    protected Elements getItemBody(Document page) {
        return page.select("table.wowitemt");
    }

    @Override
    protected Element findNameElement(Elements itemBody) {
        for(String quality: QUALITY_MAP.keySet()){
            Elements item = itemBody.select("span."+quality);
            if(item.size() > 0){
                return item.first();
            }
        }
        return null;
    }

    @Override
    protected String getQuality(Elements itemBody) {
        return QUALITY_MAP.get(findNameElement(itemBody).html());
    }

    @Override
    protected Long findId(Document page) {
        return Long.valueOf(page.select("#wmtbURL").first().val().replace("http://wow.allakhazam.com/db/item.html?witem=", ""));
    }

    @Override
    protected void parseSpells(ItemDocument item, Element col) {
        String spellText = col.html();

        if(spellText.contains("Equip:") || spellText.contains("Use:") || spellText.contains("Chance on hit:") || spellText.contains("Passive:")){
            if(item.getSpell1() == null){
                item.setSpell1(col.html());
            }else{
                if(item.getSpell2() == null){
                    item.setSpell2(col.html());
                }else{
                    if(item.getSpell3() == null){
                        item.setSpell3(col.html());
                    }else{
                        item.setSpell4(col.html());
                    }
                }
            }
        }
    }

    @Override
    protected void parseSets(ItemDocument item, Element col) {
        Elements set = col.select("a");
        if(set.size() > 0 && set.first().attr("href").contains("itemset.html")){
            item.setSet(col.select("font").first().html());
        }
    }
}

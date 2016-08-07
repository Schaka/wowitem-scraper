package me.schaka.items;

import com.google.common.collect.ImmutableMap;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Created by Jonas on 06.08.2016.
 */
@Component
public class ThottbotParser extends ItemParser{

    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");

    private static final ImmutableMap<String, String> QUALITY_MAP = ImmutableMap.<String, String>builder()
            .put("quality0", "Poor")
            .put("quality", "Common")
            .put("quality2", "Uncommon")
            .put("quality3", "Rare")
            .put("quality4", "Epic")
            .put("quality5", "Artifact")
            .put("quality6", "Legendary")
            .build();

    @Override
    public String getLink(int id) {
        return "http://thottbot.com/?i="+id;
    }

    @Override
    protected LocalDate getArchived(Document page){
        return LocalDate.parse(page.select("input[name=\"date\"]").val().substring(0,8), formatter);
    }

    @Override
    protected Long findId(Document page){
        return findIdByName(findNameElement(getItemBody(page)).text());
    }

    @Override
    protected Elements getItemBody(Document page){
        return page.select("table.ttb");
    }

    @Override
    protected Element findNameElement(Elements itemBody){
        for (int i=0; i<7; i++){
            Elements element = itemBody.select("span.quality"+i);
            if(element.size() > 0) {
                return element.first();
            }
        }
        return itemBody.select("span.quality").first();
    }

    @Override
    protected String getQuality(Elements itemBody){
        return QUALITY_MAP.get(findNameElement(itemBody).attr("class"));
    }

    @Override
    protected void parseSets(ItemDocument item, Element col) {
        String setText = col.html();
        if(setText.contains("Set:")){
            item.setSet(setText);
        }
    }

    @Override
    protected void parseSpells(ItemDocument item, Element col){

        String spellText = col.html();

        if(spellText.contains("Equip:") || spellText.contains("Use:") || spellText.contains("Chance on hit:")){
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

}

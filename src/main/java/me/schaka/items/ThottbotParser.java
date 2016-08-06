package me.schaka.items;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.apache.commons.beanutils.BeanUtils.setProperty;

/**
 * Created by Jonas on 06.08.2016.
 */
@Component
public class ThottbotParser implements ItemParser{

    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");

    private ObjectMapper objectMapper = new ObjectMapper();

    private static final ImmutableMap<String, String> QUALITY_MAP = ImmutableMap.<String, String>builder()
            .put("quality0", "Poor")
            .put("quality", "Common")
            .put("quality2", "Uncommon")
            .put("quality3", "Rare")
            .put("quality4", "Epic")
            .put("quality5", "Artifact")
            .put("quality6", "Legendary")
            .build();

    private static final ImmutableMap<String, String> PARSE_MAP = ImmutableMap.<String, String>builder()
            .put("Binds when", "pickupType")
            .put("Speed", "speed")
            .put("Requires Level", "levelReq")
            .put("Armor", "armor")
            .put("Damage", "damage")
            .put("Stamina", "stamina")
            .put("Intellect", "intellect")
            .put("Spirit", "spirit")
            .put("Strength", "strength")
            .put("Agility", "agility")
            .put("Fire Resistance", "fireRes")
            .put("Frost Resistance", "frostRes")
            .put("Shadow Resistance", "shadowRes")
            .put("Nature Resistance", "natureRes")
            .put("Set:", "set")
            .build();

    private static final ImmutableSet<String> ITEM_SLOT = ImmutableSet.of(
            "Off Hand",
            "Main Hand",
            "One-hand",
            "Two-hand",
            "Ranged",
            "Chest",
            "Wrist",
            "Feet",
            "Head",
            "Hands",
            "Waist",
            "Legs",
            "Shoulder",
            "Neck",
            "Finger",
            "Trinket"
    );

    private static final ImmutableSet<String> ITEM_TYPE = ImmutableSet.of(
            "Dagger",
            "Sword",
            "Mace",
            "Axe",
            "Bow",
            "Crossbow",
            "Gun",
            "Wand",
            "Thrown",
            "Libram",
            "Totem",
            "Staff",
            "Fist Weapon",
            "Polearm",
            "Shield",
            "Held In Off-Hand",
            "Idol",
            "Cloth",
            "Leather",
            "Mail",
            "Plate"
    );

    @Override
    public ItemDocument parsePage(Document page){
        ItemDocument result = new ItemDocument();
        LocalDate archived = LocalDate.parse(page.select("input[name=\"date\"]").val().substring(0,8), formatter);
        result.setArchived(archived);

        Elements itemBody = page.select("table.ttb");
        Element name = findNameElement(itemBody);
        Long itemId = findIdByName(name.text());
        result.setId(uniqueId(itemId, archived));
        result.setItemId(itemId);
        result.setName(name.text());
        result.setQuality(QUALITY_MAP.get(name.attr("class")));
        parseTableStructure(result, itemBody);

        return result;
    }

    private Long uniqueId(Long itemId, LocalDate archived){
        return ((Integer)Objects.hash(itemId, archived)).longValue();
    }

    private Long findIdByName(String name){
        try {
            URL itemIdFinder = new URL("http://db.hellground.net/opensearch.php?search="+ URLEncoder.encode(name, "UTF-8"));
            List<List<List<Object>>> map = objectMapper.readValue(itemIdFinder, List.class);
            return ((Integer)map.get(7).get(0).get(1)).longValue();
        } catch (Exception e) {
        }
        return null;
    }

    private Element findNameElement(Elements itemBody){
        for (int i=0; i<7; i++){
            Elements element = itemBody.select("span.quality"+i);
            if(element.size() > 0) {
                return element.first();
            }
        }
        return itemBody.select("span.quality").first();
    }

    private void parseTableStructure(ItemDocument item, Elements htmlBody){
        for(Element row : htmlBody.select("tr")){
            for (Element col : row.select("td")){
                parseSimpleColumn(item, col);
                parseItemSlot(item, col);
                parseItemType(item, col);
                parseSpells(item, col);
            }
        }
    }

    private void parseItemSlot(ItemDocument item, Element col){
        if(ITEM_SLOT.contains(col.html())){
            item.setItemSlot(col.html());
        }
    }

    private void parseItemType(ItemDocument item, Element col){
        if(ITEM_TYPE.contains(col.html())){
            item.setItemType(col.html());
        }
    }

    private void parseSpells(ItemDocument item, Element col){

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

    /**
     * maps simple Strings to setter methods on ItemObject
     */
    private void parseSimpleColumn(ItemDocument item, Element col){
        String text = col.html();
        for(Map.Entry<String, String> entry : PARSE_MAP.entrySet()){
            if(text.contains(entry.getKey())){
                try {
                    setProperty(item, entry.getValue(), text);
                } catch (Exception e) {
                }
            }
        }
    }

}

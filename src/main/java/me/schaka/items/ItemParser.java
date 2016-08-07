package me.schaka.items;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.URL;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.apache.commons.beanutils.BeanUtils.setProperty;

/**
 * Created by Jonas on 07.08.2016.
 */
public abstract class ItemParser {

    protected static final ImmutableMap<String, String> PARSE_MAP = ImmutableMap.<String, String>builder()
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
            .build();

    protected static final ImmutableSet<String> ITEM_SLOT = ImmutableSet.of(
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

    protected static final ImmutableSet<String> ITEM_TYPE = ImmutableSet.of(
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
            "Miscellaneous",
            "Idol",
            "Cloth",
            "Leather",
            "Mail",
            "Plate"
    );

    protected static final ImmutableMap<LocalDate, String> PATCHES = ImmutableMap.<LocalDate, String>builder()
            .put(LocalDate.of(2004, Month.NOVEMBER, 7), "1.1")
            .put(LocalDate.of(2004, Month.DECEMBER, 18), "1.2")
            .put(LocalDate.of(2005, Month.MARCH, 7), "1.3")
            .put(LocalDate.of(2005, Month.APRIL, 8), "1.4")
            .put(LocalDate.of(2005, Month.JUNE, 7), "1.5")
            .put(LocalDate.of(2005, Month.JULY, 12), "1.6")
            .put(LocalDate.of(2005, Month.SEPTEMBER, 13), "1.7")
            .put(LocalDate.of(2005, Month.OCTOBER, 10), "1.8")
            .put(LocalDate.of(2006, Month.JANUARY, 3), "1.9")
            .put(LocalDate.of(2006, Month.MARCH, 28), "1.10")
            .put(LocalDate.of(2006, Month.JUNE, 20), "1.11")
            .put(LocalDate.of(2006, Month.AUGUST, 22), "1.12")
            .build();

    protected ObjectMapper objectMapper = new ObjectMapper();

    public ItemDocument parsePage(Document page){
        ItemDocument result = new ItemDocument();
        LocalDate archived = getArchived(page);
        result.setArchived(archived);

        Elements itemBody = getItemBody(page);
        Element name = findNameElement(itemBody);
        Long itemId = findId(page);
        String patch = findPatch(archived);
        result.setPatch(patch);
        result.setId(uniqueId(itemId, patch));
        result.setItemId(itemId);
        result.setName(name.text());
        result.setQuality(getQuality(itemBody));
        parseTableStructure(result, itemBody);
        return result;
    }

    public abstract int itemId();

    public abstract String getLink(int id);

    protected abstract LocalDate getArchived(Document page);

    protected abstract Elements getItemBody(Document page);

    protected abstract Element findNameElement(Elements itemBody);

    protected abstract String getQuality(Elements itemBody);

    protected abstract Long findId(Document page);

    protected abstract void parseSpells(ItemDocument item, Element col);

    protected abstract void parseSets(ItemDocument item, Element col);

    /**
     * creates a unique ID for this item, based on patch itemization
     */
    private Long uniqueId(Long itemId, String patch){
        return ((Integer) Objects.hash(itemId, patch)).longValue();
    }

    private String findPatch(LocalDate archived){
        Map.Entry<LocalDate, String> patch = null;
        for(Map.Entry<LocalDate, String> entry : PATCHES.entrySet()){
            if(archived.isAfter(entry.getKey())){
                patch = entry;
            }
        }
        return patch != null ? patch.getValue() : "1.1";
    }

    protected void parseTableStructure(ItemDocument item, Elements htmlBody){
        for(Element row : htmlBody.select("tr")){
            for (Element col : row.select("td")){
                parseSimpleColumn(item, col);
                parseItemSlot(item, col);
                parseItemType(item, col);
                parseSpells(item, col);
                parseSets(item, col);
            }
        }
    }

    protected void parseItemSlot(ItemDocument item, Element col){
        if(ITEM_SLOT.contains(col.html())){
            item.setItemSlot(col.html());
        }
    }

    protected void parseItemType(ItemDocument item, Element col){
        if(ITEM_TYPE.contains(col.html())){
            item.setItemType(col.html());
        }
    }

    /**
     * maps simple Strings to setter methods on ItemObject
     */
    private void parseSimpleColumn(ItemDocument item, Element col){
        String text = col.text().replace("\u00A0"," ");
        for(Map.Entry<String, String> entry : PARSE_MAP.entrySet()){
            if(text.contains(entry.getKey())){
                try {
                    setProperty(item, entry.getValue(), text);
                } catch (Exception e) {
                }
            }
        }
    }

    protected Long findIdByName(String name){
        try {
            URL itemIdFinder = new URL("http://db.hellground.net/opensearch.php?search="+ URLEncoder.encode(name, "UTF-8"));
            List<List<List<Object>>> map = objectMapper.readValue(itemIdFinder, List.class);
            return ((Integer)map.get(7).get(0).get(1)).longValue();
        } catch (Exception e) {
        }
        return null;
    }
}

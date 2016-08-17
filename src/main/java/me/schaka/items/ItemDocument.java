package me.schaka.items;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import java.time.LocalDate;

@Document(indexName = "items", type = "item", createIndex = false)
public class ItemDocument {

	@Id
	private Long id;

	private Long itemId;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	private LocalDate archived;

	private String name;

	private String pickupType;

	private String itemType;

	private String itemSlot;

	private String quality;

	private String armor;

	private String set;

	private String patch;

	private String damage;

	private String speed;

	private String stamina;

	private String intellect;

	private String spirit;

	private String strength;

	private String agility;

	private String fireRes;

	private String frostRes;

	private String natureRes;

	private String shadowRes;

	private String arcaneRes;

	private String levelReq;

	private String spell1;

	private String spell2;

	private String spell3;

	private String spell4;

	public Long getItemId() {
		return itemId;
	}

	public void setItemId(Long itemId) {
		this.itemId = itemId;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public LocalDate getArchived() {
		return archived;
	}

	public void setArchived(LocalDate archived) {
		this.archived = archived;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPickupType() {
		return pickupType;
	}

	public void setPickupType(String pickupType) {
		this.pickupType = pickupType;
	}

	public String getItemType() {
		return itemType;
	}

	public void setItemType(String itemType) {
		this.itemType = itemType;
	}

	public String getQuality() {
		return quality;
	}

	public void setQuality(String quality) {
		this.quality = quality;
	}

	public String getItemSlot() {
		return itemSlot;
	}

	public void setItemSlot(String itemSlot) {
		this.itemSlot = itemSlot;
	}

	public String getArmor() {
		return armor;
	}

	public void setArmor(String armor) {
		this.armor = armor;
	}

	public String getSet() {
		return set;
	}

	public String getPatch() {
		return patch;
	}

	public void setPatch(String patch) {
		this.patch = patch;
	}

	public void setSet(String set) {
		this.set = set;
	}

	public String getDamage() {
		return damage;
	}

	public void setDamage(String damage) {
		this.damage = damage;
	}

	public String getSpeed() {
		return speed;
	}

	public void setSpeed(String speed) {
		this.speed = speed;
	}

	public String getStamina() {
		return stamina;
	}

	public void setStamina(String stamina) {
		this.stamina = stamina;
	}

	public String getIntellect() {
		return intellect;
	}

	public void setIntellect(String intellect) {
		this.intellect = intellect;
	}

	public String getSpirit() {
		return spirit;
	}

	public void setSpirit(String spirit) {
		this.spirit = spirit;
	}

	public String getStrength() {
		return strength;
	}

	public void setStrength(String strength) {
		this.strength = strength;
	}

	public String getAgility() {
		return agility;
	}

	public void setAgility(String agility) {
		this.agility = agility;
	}

	public String getFireRes() {
		return fireRes;
	}

	public void setFireRes(String fireRes) {
		this.fireRes = fireRes;
	}

	public String getFrostRes() {
		return frostRes;
	}

	public void setFrostRes(String frostRes) {
		this.frostRes = frostRes;
	}

	public String getNatureRes() {
		return natureRes;
	}

	public void setNatureRes(String natureRes) {
		this.natureRes = natureRes;
	}

	public String getShadowRes() {
		return shadowRes;
	}

	public void setShadowRes(String shadowRes) {
		this.shadowRes = shadowRes;
	}

	public String getArcaneRes() {
		return arcaneRes;
	}

	public void setArcaneRes(String arcaneRes) {
		this.arcaneRes = arcaneRes;
	}

	public String getLevelReq() {
		return levelReq;
	}

	public void setLevelReq(String levelReq) {
		this.levelReq = levelReq;
	}

	public String getSpell1() {
		return spell1;
	}

	public void setSpell1(String spell1) {
		this.spell1 = spell1;
	}

	public String getSpell2() {
		return spell2;
	}

	public void setSpell2(String spell2) {
		this.spell2 = spell2;
	}

	public String getSpell3() {
		return spell3;
	}

	public void setSpell3(String spell3) {
		this.spell3 = spell3;
	}

	public String getSpell4() {
		return spell4;
	}

	public void setSpell4(String spell4) {
		this.spell4 = spell4;
	}
}

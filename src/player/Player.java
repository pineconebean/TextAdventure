package player;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import io.MessageHelper;
import io.MessageType;
import item.Equipment;
import item.EquipmentLocation;
import item.Storage;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.*;

/**
 * Created on 2017/7/22.
 * Description:
 * @author Liao
 */
public class Player {
    private static final String LINE_SEP = System.lineSeparator();
    private static final int ATTACK_INIT_VALUE = 30;
    private static final int DEFENCE_INIT_VALUE = 10;
    private static final int EXP_UP_VALUE = 100;
    private static final int ATTRIBUTE_UP_VARIATION_RANGE = 20;

    private int lifeValue, lifeValueMax;
    private int magicValue, magicValueMax;
    private int attack;
    private int defence;
    private int level;
    private int exp;
    private int expMax;
    private int luckyPoint; //between 0 and 10

    private String name;
    private Map<EquipmentLocation, Equipment> equipments;
    private Storage storage; //store items
    public final ExpHelper expHelper;

    /**
     * Create a new player
     */
    public Player(String name) {
        this.name = name;

        lifeValue = 100;
        lifeValueMax = 100;
        magicValue = 100;
        magicValueMax = 100;
        expMax = 50;
        level = 0;

        //the attack and defence
        Random random = new Random();
        attack = ATTACK_INIT_VALUE + random.nextInt(ATTRIBUTE_UP_VARIATION_RANGE);
        defence = DEFENCE_INIT_VALUE + random.nextInt(ATTRIBUTE_UP_VARIATION_RANGE);

        luckyPoint = random.nextInt(10);
        equipments = new HashMap<>();
        storage = new Storage(this);
        expHelper = new ExpHelper(this);
    }


    /**
     * Save the data in the json file, if the player already exists, this method will overwrite it.
     */
    public void save() {
        Gson gson = new Gson();
        //TODO complete and items saving
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("name", name);
        jsonObject.addProperty("lifeValue", lifeValue);
        jsonObject.addProperty("lifeValueMax", lifeValueMax);
        jsonObject.addProperty("magicValue", magicValue);
        jsonObject.addProperty("magicValueMax", magicValueMax);
        jsonObject.addProperty("attack", attack);
        jsonObject.addProperty("defence", defence);
        jsonObject.addProperty("level", level);
        jsonObject.addProperty("exp", exp);
        jsonObject.addProperty("expMax", expMax);

        //equipment part
        JsonObject equipmentObj = new JsonObject();
        for (EquipmentLocation location : EquipmentLocation.values()) {
            Equipment tmp = equipments.get(location);
            equipmentObj.addProperty(location.toString(), tmp == null ? "nothing" : tmp.getName());
        }
        jsonObject.add("equipments", equipmentObj);

        String dirName = "json/profiles/" + name;
        File file = new File(dirName);
        if (!file.exists()) {
            //noinspection ResultOfMethodCallIgnored
            file.mkdirs();
        }
        try (Writer writer = new FileWriter(dirName + "/" + name + "_profile.json")) {
            gson.toJson(jsonObject, writer);
            MessageHelper.printMessage("Save successfully", MessageType.PLAIN);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Display the player's attributes
     */
    public void printAttributes() {
        String attributes = "life: " + lifeValue + "/" + lifeValueMax + LINE_SEP
                + "magic: " + magicValue + "/" + magicValueMax + LINE_SEP
                + "level: " + level + LINE_SEP
                + "exp: " + exp + "/" + expMax + LINE_SEP
                + "attack: " + attack + LINE_SEP
                + "defence: " + defence;
        MessageHelper.printMessage(attributes, MessageType.PLAIN);
    }

    /**
     * Calculate the attack value
     * TODO finish this method
     */
    public void attack() {

    }

    /**
     * Equip an equipment and change the attribute, then update the equipments Map.
     * TODO finish this method in the condition that a equipment was replaced by another
     */
    public void equip(Equipment target) {
        target.equipTo(this);
        equipments.put(target.getLocation(), target);
    }

    /**
     * Print the equipment list and then remove the equipment user chosen.
     */
    public void removeEquipment() {
        listEquipment();
        List<EquipmentLocation> equipmentLocations = new ArrayList<>();
        equipmentLocations.addAll(equipments.keySet());
        if (equipmentLocations.size() == 0) {
            MessageHelper.printMessage("You have no equipment", MessageType.WARNING);
            return;
        }
        MessageHelper.printMenu(equipmentLocations);
        MessageHelper.printMessage("Enter the number of the part which you want to remove: ", MessageType.PROMPT);
        String userEntered = MessageHelper.take();
        try {
            int optNum = Integer.parseInt(userEntered);
            if (optNum >= equipmentLocations.size() || optNum < 0) {
                MessageHelper.printMessage("Out of bounds", MessageType.WARNING);
                return;
            }
            equipments.remove(equipmentLocations.get(optNum)).removeFrom(this);
            MessageHelper.printMessage("Remove succeed", MessageType.PROMPT);
        } catch (NumberFormatException e) {
            MessageHelper.printMessage("Invalid input", MessageType.WARNING);
        }
        listEquipment();
    }

    /**
     * Print equipment list. If the equipment in some place is empty, it'll be denoted by "nothing".
     */
    public void listEquipment() {
        StringBuilder equipmentInfo = new StringBuilder("Equipment:" + LINE_SEP);
        for (EquipmentLocation location : EquipmentLocation.values()) {
            Equipment tmp = equipments.get(location);
            equipmentInfo.append(location.toString()).append(" : ")
                    .append(tmp == null ? "nothing" : tmp.getName())
                    .append(LINE_SEP);
        }
        MessageHelper.printMessage(equipmentInfo.toString(), MessageType.PLAIN);
    }

    public int getLifeValue() {
        return lifeValue;
    }

    //Setter methods
    public void setLifeValue(int lifeValue) {
        if (lifeValue > lifeValueMax)
            this.lifeValue = lifeValueMax;
        else
            this.lifeValue = lifeValue;
    }

    public int getMagicValue() {
        return magicValue;
    }

    /**
     * The value of magic value mustn't be larger than maxValue.
     * @param magicValue the magic value you intend to set
     */
    public void setMagicValue(int magicValue) {
        if (magicValue > magicValueMax)
            this.magicValue = magicValueMax;
        else
            this.magicValue = magicValue;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getExp() {
        return exp;
    }

    public void setExp(int exp) {
        this.exp = exp;
    }

    public int getExpMax() {
        return expMax;
    }

    public void setExpMax(int expMax) {
        this.expMax = expMax;
    }

    public int getLuckyPoint() {
        return luckyPoint;
    }

    public void setLuckyPoint(int luckyPoint) {
        this.luckyPoint = luckyPoint;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getLifeValueMax() {
        return lifeValueMax;
    }

    public void setLifeValueMax(int lifeValueMax) {
        this.lifeValueMax = lifeValueMax;
    }

    public int getMagicValueMax() {
        return magicValueMax;
    }

    public void setMagicValueMax(int magicValueMax) {
        this.magicValueMax = magicValueMax;
    }

    public int getAttack() {
        return attack;
    }

    public void setAttack(int attack) {
        this.attack = attack;
    }

    public int getDefence() {
        return defence;
    }

    public void setDefence(int defence) {
        this.defence = defence;
    }

    public void setEquipments(Map<EquipmentLocation, Equipment> equipments) {
        this.equipments = equipments;
    }

    public void setStorage(Storage storage) {
        this.storage = storage;
    }


    //test code
//    public static void main(String[] args) {
//        Player player = new Player("test");
//        player.printAttributes();
//        player.equip(EquipmentRepository.INSTANCE.getEquipment("sword"));
//        player.printAttributes();
//        player.removeEquipment();
//        player.printAttributes();
//    }
}

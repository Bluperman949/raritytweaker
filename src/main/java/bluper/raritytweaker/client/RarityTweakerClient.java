package bluper.raritytweaker.client;

import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.Map;

public class RarityTweakerClient implements ClientModInitializer {
  static final Logger LOGGER = LogManager.getLogger();
  static final String RARITY_CONFIG_PATH = "config/raritytweaker-rarities.txt";
  static final String DEFAULT_RARITY_CONFIG =
      "#format: namespace:itemname COMMON|UNCOMMON|RARE|EPIC\n" +
          "#use *partialname to affect any item whose name contains the specified string.\n" +
          "#changes are applied top-to-bottom.\n" +
          "*netherite_ EPIC\n" +
          "#the line below overrides the line above.\n" +
          "minecraft:netherite_scrap UNCOMMON\n" +
          "minecraft:ancient_debris UNCOMMON\n" +
          "*disc UNCOMMON\n" +
          "*smithing_template RARE\n" +
          "minecraft:goat_horn UNCOMMON\n" +
          "minecraft:trident UNCOMMON\n" +
          "minecraft:bell UNCOMMON\n";
  public static final Map<Item, Rarity> RARITY_MAP = new Object2ObjectArrayMap<>();

  @Override
  public void onInitializeClient() {
    try {
      new File("config").mkdir();
      if (!new File(RARITY_CONFIG_PATH).exists()) {
        FileWriter writer = new FileWriter(RARITY_CONFIG_PATH);
        writer.write(DEFAULT_RARITY_CONFIG);
        writer.close();
      }
      BufferedReader reader = new BufferedReader(new FileReader(RARITY_CONFIG_PATH));
      reader.lines().forEach(line -> {
        if (line.startsWith("#")) return;
        String[] split = line.split(" ");
        if (split.length != 2) return;
        String itemString = split[0];
        String rarityString = split[1];

        Rarity rarity;
        try {
          rarity = Rarity.valueOf(rarityString);
        } catch (IllegalArgumentException e) {
          LOGGER.error("Unable to parse rarity name '{}'", rarityString);
          e.printStackTrace(System.err);
          return;
        }

        try {
          if (itemString.startsWith("*")) {
            String itemStringSub = itemString.substring(1);
            BuiltInRegistries.ITEM.stream().filter(item -> item.toString().contains(itemStringSub))
                .forEach(item -> RARITY_MAP.put(item, rarity));
          } else {
            Item item = BuiltInRegistries.ITEM.get(new ResourceLocation(itemString));
            RARITY_MAP.put(item, rarity);
          }
        } catch (Exception e) {
          LOGGER.error("Unable to parse item name '{}'", itemString);
          e.printStackTrace(System.err);
        }
      });
    } catch (IOException e) {
      LOGGER.error("RarityTweaker failed to read config");
      e.printStackTrace(System.err);
    }
  }
}

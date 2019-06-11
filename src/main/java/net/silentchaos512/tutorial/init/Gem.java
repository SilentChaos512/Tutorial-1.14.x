package net.silentchaos512.tutorial.init;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.util.LazyLoadBase;
import net.silentchaos512.tutorial.TutorialMod;
import net.silentchaos512.tutorial.block.OreBlock;

import java.util.Locale;

/**
 * Example of an enum being used to represent sets of similar blocks and items. This is especially
 * useful when you have a large number of variants (like in Silent's Gems:
 * https://github.com/SilentChaos512/SilentGems/blob/1.13/src/main/java/net/silentchaos512/gems/lib/Gems.java),
 * or when you have multiple types of blocks and items corresponding to the variants. Gems are a
 * good example. If you were making a mod with lots of metal ores, this would also be useful. I also
 * use enums for what I call "crafting items", items which exist almost exclusively for use in
 * recipes.
 * <p>
 * If you need to add a new variant, simply add a new enum constant to the list. Everything else
 * will be created for you.
 */
public enum Gem {
    RUBY,
    SAPPHIRE;

    // Block and item references. I use LazyLoadBase so the actual blocks and items are not created
    // until they are needed. While not strictly necessary, this will control when classes are
    // loaded, which can make debugging some issues easier.
    private final LazyLoadBase<OreBlock> oreBlock;
    private final LazyLoadBase<Block> storageBlock;
    private final LazyLoadBase<Item> gemItem;

    Gem() {
        // Note that this::getGemItem is a method reference. The gem item should not be created
        // until later. Conveniently, the method signature matches IItemProvider.
        oreBlock = new LazyLoadBase<>(() -> new OreBlock(this::getGemItem));
        storageBlock = new LazyLoadBase<>(() -> new Block(Block.Properties.create(Material.IRON).hardnessAndResistance(5, 6).sound(SoundType.METAL)));
        gemItem = new LazyLoadBase<>(() -> new Item(new Item.Properties().group(TutorialMod.ITEM_GROUP)));
    }

    /**
     * Gets a {@link net.minecraft.util.ResourceLocation}-friendly name for the gem.
     *
     * @return Enum constant name lowercased
     */
    public String getName() {
        // Locale.ROOT will ensure consistent behavior (prevent crashes) on all locales
        return name().toLowerCase(Locale.ROOT);
    }

    /**
     * Get the gem ore block
     *
     * @return The ore block
     */
    public OreBlock getOreBlock() {
        return oreBlock.getValue();
    }

    /**
     * Get the gem storage block (9 gems = 1 block)
     *
     * @return The storage block
     */
    public Block getStorageBlock() {
        return storageBlock.getValue();
    }

    /**
     * Get the gem item (drops from ore)
     *
     * @return The gem item
     */
    public Item getGemItem() {
        return gemItem.getValue();
    }
}

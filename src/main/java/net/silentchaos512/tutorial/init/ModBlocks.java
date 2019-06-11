package net.silentchaos512.tutorial.init;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.registries.ForgeRegistries;
import net.silentchaos512.tutorial.TutorialMod;

import javax.annotation.Nullable;

/**
 * Registers blocks and stores references to said blocks, if needed. Here I am just storing the
 * blocks in a public static field. Forge also offers the {@link net.minecraftforge.registries.ObjectHolder}
 * annotation to populate fields and make them final. Personally, I do not like this because it
 * creates "field may be null" warnings wherever the field is referenced. The warning is incorrect,
 * but IntelliJ will complain regardless.
 * <p>
 * Another possibility is to use {@link net.minecraft.util.LazyLoadBase}. This initially stores a
 * Supplier, then stores the block after the first access. This is similar to what I do in many
 * cases.
 * <p>
 * It is also possible to design this class as an enum, but that is not always a good idea. I would
 * discourage this if you are new to modding or Java. This is also a bad idea if the number of
 * blocks is large. Check Silent Gear's ModBlocks class for an example if you are curious
 * (https://github.com/SilentChaos512/Silent-Gear/blob/1.12/src/main/java/net/silentchaos512/gear/init/ModBlocks.java).
 * <p>
 * If you have a large number of blocks/items with variants, you could use an enum to represent the
 * material, like the Gems enum from Silent's Gems. This enum holds lazy references to all blocks
 * and items made of gems. Considering there are nearly 800 such objects, it makes the code much
 * less tedious! (https://github.com/SilentChaos512/SilentGems/blob/1.13/src/main/java/net/silentchaos512/gems/lib/Gems.java)
 */
public final class ModBlocks {
    public static Block blueStone;

    private ModBlocks() {}

    /**
     * Creates and registers blocks. Also creates block items, but those are not registered until
     * {@link ModItems#registerAll(RegistryEvent.Register)}. Forge registers blocks first, then
     * items. The order of other registries is not guaranteed.
     *
     * @param event The event
     */
    public static void registerAll(RegistryEvent.Register<Block> event) {
        // Verify we are getting the correct registry event. If not, just silently return.
        if (!event.getName().equals(ForgeRegistries.BLOCKS.getRegistryName())) {
            return;
        }

        // This could be on one line, I typically break before each chained method call on
        // Block.Properties. You can break up the line however you like. If extending a block class,
        // I usually create the Properties in the new class' constructor, instead of here.
        blueStone = register("blue_stone", new Block(Block.Properties.create(Material.ROCK)
                .hardnessAndResistance(1.5f, 6f)
                .sound(SoundType.STONE)
        ));

        // When registering enum items, we iterate over all enum values and register blocks in the
        // loop. Use getName() when creating the block's name, and the appropriate block getter.
        // Note I use two loops here. This affects the order the blocks are registered and
        // displayed. You could use one loop, but I prefer to group by block type. A single loop
        // would group by gem.
        for (Gem gem : Gem.values()) {
            // Names will be: ruby_block, sapphire_block
            // This comment is, of course, not necessary, so you can remove it
            register(gem.getName() + "_block", gem.getStorageBlock());
        }

        for (Gem gem : Gem.values()) {
            // ruby_ore, sapphire_ore
            register(gem.getName() + "_ore", gem.getOreBlock());
        }
    }

    /**
     * Registers the block with a standard item. Also sets the registry name.
     *
     * @param name  The path of the block ID
     * @param block The block
     * @param <T>   The class of the block
     * @return The block
     */
    private static <T extends Block> T register(String name, T block) {
        ItemBlock item = new ItemBlock(block, new Item.Properties().group(TutorialMod.ITEM_GROUP));
        return register(name, block, item);
    }

    /**
     * Registers the block with the given item. Also sets the registry name. If item is null, no
     * item is registered. Some blocks, like potted flowers, do not need an item because there is no
     * normal way to obtain them.
     * <p>
     * Note the {@link Nullable} annotation on the {@code item} parameter. While not required, I
     * encourage uses this on any parameter where you want to allow null values. You can also put
     * this annotation on methods and fields. IntelliJ will warn you if you try to deference a
     * {@link Nullable} object without checking if it is null.
     *
     * @param name  The path of the block ID
     * @param block The block
     * @param item  The item, or null if no item should be registered
     * @param <T>   The class of the block
     * @return The block
     */
    private static <T extends Block> T register(String name, T block, @Nullable ItemBlock item) {
        ResourceLocation id = TutorialMod.getId(name);
        block.setRegistryName(id);
        ForgeRegistries.BLOCKS.register(block);
        if (item != null) {
            ModItems.BLOCKS_TO_REGISTER.put(name, item);
        }
        return block;
    }
}

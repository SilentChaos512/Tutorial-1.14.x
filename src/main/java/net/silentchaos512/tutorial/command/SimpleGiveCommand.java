package net.silentchaos512.tutorial.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.command.arguments.ResourceLocationArgument;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Collection;

/**
 * A slightly modified version of vanilla's give command ({@link net.minecraft.command.impl.GiveCommand}).
 * This version does not support NBT, as we get the item using a different method. {@code
 * GiveCommand} uses {@link net.minecraft.command.arguments.ItemInput}, but we are using {@link
 * ResourceLocationArgument} for demonstration purposes. This is mainly to show off {@link
 * SuggestionProvider}s, which can suggest possible values for the player to type, along with tab
 * completion.
 * <p>
 * I have formatted the code in {@link #register(CommandDispatcher)} in a way I think is easier to
 * understand. By breaking lines where I do, different method calls "line up" correctly to show the
 * tree-like structure we are creating. This is similar to the example on the Brigadier GitHub page,
 * but I use fewer line breaks because the indentation gets out of hand quickly, at least with a
 * default indent size of 4 (which actually means 8 spaces per level in this case).
 * <p>
 * Also see the official Brigadier repo here: https://github.com/Mojang/brigadier
 */
public final class SimpleGiveCommand {
    /**
     * Provides suggestions for a command argument. In this case, item IDs. {@link
     * ISuggestionProvider} offers several methods for us to use. At the time of writing, many of
     * these methods still have obfuscated names. Here, we want the one that takes a {@code
     * Stream<ResourceLocation>} as the first parameter.
     */
    private static final SuggestionProvider<CommandSource> ITEM_ID_SUGGESTIONS = (context, builder) ->
            ISuggestionProvider.func_212476_a(ForgeRegistries.ITEMS.getKeys().stream(), builder);

    private SimpleGiveCommand() {}

    /**
     * Called to register the command, which should be done in {@link net.minecraftforge.fml.event.server.FMLServerStartingEvent}.
     * The syntax of this command is {@code /sgive <targets> <itemID> [<count>]}.
     *
     * @param dispatcher The {@link CommandDispatcher}, which is obtained from {@code
     *                   FMLServerStartingEvent}
     */
    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        // User types "/sgive"
        dispatcher.register(Commands.literal("sgive")
                // Needs permission level 2
                .requires(source -> source.hasPermissionLevel(2))
                // The target players (required argument)
                .then(Commands.argument("targets", EntityArgument.multiplePlayers())
                        // The item ID (required argument)
                        .then(Commands.argument("itemID", ResourceLocationArgument.resourceLocation())
                                // Make suggestions for the item IDs
                                .suggests(ITEM_ID_SUGGESTIONS)
                                // If no further arguments, give one of the item to all targets
                                .executes(context -> giveItem(
                                        context.getSource(),
                                        ResourceLocationArgument.getResourceLocation(context, "itemID"),
                                        EntityArgument.getPlayers(context, "targets"),
                                        1
                                ))
                                // Or we can optionally specify the number of the item to give
                                .then(Commands.argument("count", IntegerArgumentType.integer())
                                        // Which ends with giving the target players the given number of the item
                                        .executes(context -> giveItem(
                                                context.getSource(),
                                                ResourceLocationArgument.getResourceLocation(context, "itemID"),
                                                EntityArgument.getPlayers(context, "targets"),
                                                IntegerArgumentType.getInteger(context, "count")
                                        ))
                                )
                        )
                )
        );
    }

    /**
     * Copied from {@link net.minecraft.command.impl.GiveCommand} and modified to accept the item's
     * ID instead of {@link net.minecraft.command.arguments.ItemInput}. I refactored the code
     * slightly, giving variables more meaningful names.
     *
     * @param source  The command source
     * @param itemId  The item ID
     * @param targets The player(s) to give to
     * @param count   The number of the item to give
     * @return Size of {@code targets}, or 0 if the item ID is invalid
     */
    private static int giveItem(CommandSource source, ResourceLocation itemId, Collection<EntityPlayerMP> targets, int count) {
        Item item = ForgeRegistries.ITEMS.getValue(itemId);
        if (item == null) {
            source.sendErrorMessage(new TextComponentString("Item '" + itemId + "' does not exist?"));
            return 0;
        }

        for (EntityPlayerMP player : targets) {
            int remainingCount = count;

            while (remainingCount > 0) {
                @SuppressWarnings("deprecation") int stackCount = Math.min(item.getMaxStackSize(), remainingCount);
                remainingCount -= stackCount;
                ItemStack stack = new ItemStack(item, stackCount);
                boolean addedToInventory = player.inventory.addItemStackToInventory(stack);
                if (addedToInventory && stack.isEmpty()) {
                    stack.setCount(1);
                    EntityItem entityItem = player.dropItem(stack, false);
                    if (entityItem != null) {
                        entityItem.makeFakeItem();
                    }

                    player.world.playSound(
                            null, player.posX, player.posY, player.posZ,
                            SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS,
                            0.2F,
                            ((player.getRNG().nextFloat() - player.getRNG().nextFloat()) * 0.7F + 1.0F) * 2.0F);
                    player.inventoryContainer.detectAndSendChanges();
                } else {
                    EntityItem entityItem = player.dropItem(stack, false);
                    if (entityItem != null) {
                        entityItem.setNoPickupDelay();
                        entityItem.setOwnerId(player.getUniqueID());
                    }
                }
            }
        }

        ITextComponent itemText = new ItemStack(item, count).getTextComponent();
        if (targets.size() == 1) {
            source.sendFeedback(new TextComponentTranslation("commands.give.success.single", count, itemText, targets.iterator().next().getDisplayName()), true);
        } else {
            source.sendFeedback(new TextComponentTranslation("commands.give.success.single", count, itemText, targets.size()), true);
        }

        return targets.size();
    }
}

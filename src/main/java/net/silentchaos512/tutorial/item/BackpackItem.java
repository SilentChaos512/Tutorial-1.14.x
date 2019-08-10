package net.silentchaos512.tutorial.item;

import net.minecraft.item.DyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.silentchaos512.tutorial.TutorialMod;

public class BackpackItem extends Item {
    private static final String NBT_COLOR = "BackpackColor";

    public BackpackItem() {
        super(new Properties().group(TutorialMod.ITEM_GROUP).maxStackSize(1));
    }

    public static int getBackpackColor(ItemStack stack) {
        return stack.getOrCreateTag().getInt(NBT_COLOR);
    }

    public static void setBackpackColor(ItemStack stack, int color) {
        stack.getOrCreateTag().putInt(NBT_COLOR, color);
    }

    public static int getItemColor(ItemStack stack, int tintIndex) {
        if (tintIndex == 0) {
            return getBackpackColor(stack);
        }
        return 0xFFFFFF;
    }

    @Override
    public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
        if (isInGroup(group)) {
            for (DyeColor color : DyeColor.values()) {
                ItemStack stack = new ItemStack(this);
                setBackpackColor(stack, color.getFireworkColor());
                items.add(stack);
            }
        }
    }
}

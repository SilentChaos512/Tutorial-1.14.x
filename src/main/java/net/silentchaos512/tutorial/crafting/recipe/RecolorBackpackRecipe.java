package net.silentchaos512.tutorial.crafting.recipe;

import com.google.gson.JsonObject;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.SpecialRecipe;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.Tags;
import net.minecraftforge.registries.ForgeRegistryEntry;
import net.silentchaos512.tutorial.TutorialMod;
import net.silentchaos512.tutorial.item.BackpackItem;

import java.util.ArrayList;
import java.util.Collection;

public class RecolorBackpackRecipe extends SpecialRecipe {
    public static final ResourceLocation NAME = TutorialMod.getId("recolor_backpack");
    public static final Serializer SERIALIZER = new Serializer();

    public RecolorBackpackRecipe(ResourceLocation idIn) {
        super(idIn);
    }

    @Override
    public boolean matches(CraftingInventory inv, World worldIn) {
        int backpackCount = 0;
        int dyeCount = 0;

        for (int i = 0; i < inv.getSizeInventory(); ++i) {
            ItemStack stack = inv.getStackInSlot(i);
            if (!stack.isEmpty()) {
                if (stack.getItem() instanceof BackpackItem) {
                    ++backpackCount;
                } else if (stack.getItem().isIn(Tags.Items.DYES)) {
                    ++dyeCount;
                } else {
                    return false;
                }
            }
        }

        return backpackCount == 1 && dyeCount > 0;
    }

    @Override
    public ItemStack getCraftingResult(CraftingInventory inv) {
        ItemStack backpack = ItemStack.EMPTY;
        Collection<ItemStack> dyes = new ArrayList<>();

        for (int i = 0; i < inv.getSizeInventory(); ++i) {
            ItemStack stack = inv.getStackInSlot(i);
            if (!stack.isEmpty()) {
                if (stack.getItem() instanceof BackpackItem) {
                    backpack = stack;
                } else if (stack.getItem().isIn(Tags.Items.DYES)) {
                    dyes.add(stack);
                }
            }
        }

        ItemStack result = backpack.copy();
        applyDyes(result, dyes);
        return result;
    }

    private static void applyDyes(ItemStack backpack, Collection<ItemStack> dyes) {
        int[] componentSums = new int[3];
        int maxColorSum = 0;
        int colorCount = 0;

        int backpackColor = BackpackItem.getBackpackColor(backpack);
        if (backpackColor != DyeColor.WHITE.getFireworkColor()) {
            float r = (float) (backpackColor >> 16 & 255) / 255.0F;
            float g = (float) (backpackColor >> 8 & 255) / 255.0F;
            float b = (float) (backpackColor & 255) / 255.0F;
            maxColorSum = (int) ((float) maxColorSum + Math.max(r, Math.max(g, b)) * 255.0F);
            componentSums[0] = (int) ((float) componentSums[0] + r * 255.0F);
            componentSums[1] = (int) ((float) componentSums[1] + g * 255.0F);
            componentSums[2] = (int) ((float) componentSums[2] + b * 255.0F);
            ++colorCount;
        }

        for (ItemStack dye : dyes) {
            DyeColor dyeColor = DyeColor.getColor(dye);
            if (dyeColor != null) {
                float[] componentValues = dyeColor.getColorComponentValues();
                int r = (int) (componentValues[0] * 255.0F);
                int g = (int) (componentValues[1] * 255.0F);
                int b = (int) (componentValues[2] * 255.0F);
                maxColorSum += Math.max(r, Math.max(g, b));
                componentSums[0] += r;
                componentSums[1] += g;
                componentSums[2] += b;
                ++colorCount;
            }
        }

        if (colorCount > 0) {
            int r = componentSums[0] / colorCount;
            int g = componentSums[1] / colorCount;
            int b = componentSums[2] / colorCount;
            float maxAverage = (float) maxColorSum / (float) colorCount;
            float max = (float) Math.max(r, Math.max(g, b));
            r = (int) ((float) r * maxAverage / max);
            g = (int) ((float) g * maxAverage / max);
            b = (int) ((float) b * maxAverage / max);
            int finalColor = (r << 8) + g;
            finalColor = (finalColor << 8) + b;
            BackpackItem.setBackpackColor(backpack, finalColor);
        }
    }

    @Override
    public boolean canFit(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
        return SERIALIZER;
    }

    public static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<RecolorBackpackRecipe> {
        @Override
        public RecolorBackpackRecipe read(ResourceLocation recipeId, JsonObject json) {
            return new RecolorBackpackRecipe(recipeId);
        }

        @Override
        public RecolorBackpackRecipe read(ResourceLocation recipeId, PacketBuffer buffer) {
            return new RecolorBackpackRecipe(recipeId);
        }

        @Override
        public void write(PacketBuffer buffer, RecolorBackpackRecipe recipe) {
        }
    }
}

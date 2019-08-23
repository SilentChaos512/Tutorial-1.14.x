package net.silentchaos512.tutorial.init;

import net.minecraft.item.crafting.IRecipeSerializer;
import net.silentchaos512.tutorial.crafting.recipe.RecolorBackpackRecipe;

public class ModRecipes {
    public static void init() {
        IRecipeSerializer.register(RecolorBackpackRecipe.NAME.toString(), RecolorBackpackRecipe.SERIALIZER);
    }
}

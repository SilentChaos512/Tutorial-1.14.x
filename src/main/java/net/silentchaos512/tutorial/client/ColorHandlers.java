package net.silentchaos512.tutorial.client;

import net.minecraftforge.client.event.ColorHandlerEvent;
import net.silentchaos512.tutorial.init.ModItems;
import net.silentchaos512.tutorial.item.BackpackItem;

public class ColorHandlers {
    public static void registerItemColor(ColorHandlerEvent.Item event) {
        event.getItemColors().register(BackpackItem::getItemColor, ModItems.backpack);
    }
}

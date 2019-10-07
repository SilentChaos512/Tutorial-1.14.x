package net.silentchaos512.tutorial;

import net.minecraft.block.Block;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.*;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.silentchaos512.tutorial.client.ColorHandlers;
import net.silentchaos512.tutorial.command.SimpleGiveCommand;
import net.silentchaos512.tutorial.init.ModBlocks;
import net.silentchaos512.tutorial.init.ModContainerTypes;
import net.silentchaos512.tutorial.init.ModItems;
import net.silentchaos512.tutorial.init.ModRecipes;

/**
 * SideProxy allows client and server code to be separated, while executing common code on both
 * sides. You could use this just for the sided code, but I initialize everything in proxy classes.
 * There are two nested classes, {@link Client} and {@link Server}.
 */
class SideProxy {
    SideProxy() {
        // Life-cycle events
        FMLJavaModLoadingContext.get().getModEventBus().addListener(SideProxy::commonSetup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(SideProxy::enqueueIMC);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(SideProxy::processIMC);
        FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(Block.class, ModBlocks::registerAll);
        FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(ContainerType.class, ModContainerTypes::registerContainerTypes);
        FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(Item.class, ModItems::registerAll);

        // Other events
        MinecraftForge.EVENT_BUS.register(this);

        ModRecipes.init();
    }

    /**
     * Called after registry events, so we know blocks, items, etc. are registered
     *
     * @param event The event
     */
    private static void commonSetup(FMLCommonSetupEvent event) {
        TutorialMod.LOGGER.debug("commonSetup for Tutorial Mod");
    }

    /**
     * Send IMC messages to other mods
     *
     * @param event The event
     */
    private static void enqueueIMC(final InterModEnqueueEvent event) {
    }

    /**
     * Receive and process IMC messages from other mods
     *
     * @param event The event
     */
    private static void processIMC(final InterModProcessEvent event) {
    }

    /**
     * One of several events fired when a server (integrated or dedicated) is starting up. Here, we
     * can register commands and classes which process resources. For example, if you have a machine
     * with custom recipes, you would register your resource manager and reload resources as the
     * server is starting. We will cover that in a later episode.
     *
     * @param event The event
     */
    @SubscribeEvent
    public void serverStarting(FMLServerStartingEvent event) {
        SimpleGiveCommand.register(event.getCommandDispatcher());
    }

    /**
     * In addition to everything handled by SideProxy, this will handle client-side resources. This
     * is where you would register things like models and color handlers.
     */
    static class Client extends SideProxy {
        Client() {
            FMLJavaModLoadingContext.get().getModEventBus().addListener(Client::clientSetup);
            FMLJavaModLoadingContext.get().getModEventBus().addListener(ColorHandlers::registerItemColor);
            FMLJavaModLoadingContext.get().getModEventBus().addListener(ModContainerTypes::registerScreens);
        }

        private static void clientSetup(FMLClientSetupEvent event) {
        }
    }

    /**
     * Only created on dedicated servers.
     */
    static class Server extends SideProxy {
        Server() {
            FMLJavaModLoadingContext.get().getModEventBus().addListener(Server::serverSetup);
        }

        private static void serverSetup(FMLDedicatedServerSetupEvent event) {
        }
    }
}

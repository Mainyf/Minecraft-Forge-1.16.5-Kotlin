package com.example.examplemod

import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent
import net.minecraft.block.Block
import net.minecraft.block.Blocks
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent
import net.minecraftforge.fml.InterModComms
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent
import net.minecraftforge.fml.InterModComms.IMCMessage
import java.util.stream.Collectors
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.event.server.FMLServerStartingEvent
import net.minecraftforge.fml.common.Mod.EventBusSubscriber
import net.minecraftforge.event.RegistryEvent.Register
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.Mod
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

// The value here should match an entry in the META-INF/mods.toml file
@Mod(ExampleMod.ID)
object ExampleMod {
    // the modid of our mod
    const val ID: String = "examplemod"

    // the logger for our mod
    val LOGGER: Logger = LogManager.getLogger()

    private fun setup(event: FMLCommonSetupEvent) {
        // some preinit code
        LOGGER.info("HELLO FROM PREINIT")
        LOGGER.info("DIRT BLOCK >> {}", Blocks.DIRT.registryName)
    }

    private fun doClientStuff(event: FMLClientSetupEvent) {
        // do something that can only be done on the client
        LOGGER.info("Got game settings {}", event.minecraftSupplier.get().gameSettings)
    }

    private fun enqueueIMC(event: InterModEnqueueEvent) {
        // some example code to dispatch IMC to another mod
        InterModComms.sendTo("examplemod", "helloworld") {
            LOGGER.info("Hello world from the MDK")
            "Hello world"
        }
    }

    private fun processIMC(event: InterModProcessEvent) {
        // some example code to receive and process InterModComms from other mods
        LOGGER.info("Got IMC {}", event.imcStream.map { m: IMCMessage -> m.getMessageSupplier<Any>().get() }.collect(Collectors.toList()))
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    fun onServerStarting(event: FMLServerStartingEvent?) {
        // do something when the server starts
        LOGGER.info("HELLO from server starting")
    }

    // You can use EventBusSubscriber to automatically subscribe events on the contained class (this is subscribing to the MOD
    // Event bus for receiving Registry Events)
    @EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
    object RegistryEvents {
        @SubscribeEvent
        fun onBlocksRegistry(blockRegistryEvent: Register<Block?>?) {
            // register a new block here
            LOGGER.info("HELLO from Register Block")
        }
    }

    init {
        // Register the setup method for modloading
        FMLJavaModLoadingContext.get().modEventBus.addListener { event: FMLCommonSetupEvent -> setup(event) }
        // Register the enqueueIMC method for modloading
        FMLJavaModLoadingContext.get().modEventBus.addListener { event: InterModEnqueueEvent -> enqueueIMC(event) }
        // Register the processIMC method for modloading
        FMLJavaModLoadingContext.get().modEventBus.addListener { event: InterModProcessEvent -> processIMC(event) }
        // Register the doClientStuff method for modloading
        FMLJavaModLoadingContext.get().modEventBus.addListener { event: FMLClientSetupEvent -> doClientStuff(event) }

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this)
    }
}
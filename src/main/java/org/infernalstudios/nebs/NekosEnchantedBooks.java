package org.infernalstudios.nebs;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.minecraft.data.DataGenerator;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.world.item.Items;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;

@Mod(NekosEnchantedBooks.MOD_ID)

public class NekosEnchantedBooks
{
	public static final String MOD_ID = "nebs";
	public static Map<String, Float> enchantementMap;

	public static final Logger LOGGER = LogManager.getLogger();

	public NekosEnchantedBooks ()
	{
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);

		MinecraftForge.EVENT_BUS.register(this);
	}

	private void doClientStuff (final FMLClientSetupEvent event)
	{
		InputStreamReader input = new InputStreamReader(Objects.requireNonNull(
				GatherDataSubscriber.class.getClassLoader().getResourceAsStream("assets/nebs/models/properties.json")),
				StandardCharsets.UTF_8);
		Type type = new TypeToken<Map<String, Float>>(){}.getType();
		enchantementMap = new Gson().fromJson(new BufferedReader(input), type);

		ItemProperties.register(Items.ENCHANTED_BOOK, new ResourceLocation("nebs:enchant"), (stack, world, entity, i) -> {
			Map<Enchantment, Integer> map = EnchantmentHelper.getEnchantments(stack);
			if(map.isEmpty() || enchantementMap == null)
				return 0.0F;

			String key = map.entrySet().iterator().next().getKey().getDescriptionId();
			return enchantementMap.getOrDefault(key, 0.0F);
		});
	}

	@Mod.EventBusSubscriber(modid = NekosEnchantedBooks.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
	public static class GatherDataSubscriber
	{
		@SubscribeEvent
		public static void gatherData (GatherDataEvent event)
		{
			DataGenerator gen = event.getGenerator();

			if (event.includeClient())
			{
				gen.addProvider(new ModItemModelProvider(gen, event.getExistingFileHelper()));
			}
		}
	}
}

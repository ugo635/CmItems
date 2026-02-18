package com.me.cmitems;

import com.me.cmitems.items.AimBot.AimbotArrow;
import com.me.cmitems.items.AimBot.AimbotBow;
import com.me.cmitems.items.GravityBow.GravityArrow;
import com.me.cmitems.items.EnderBow;
import com.me.cmitems.items.GravityBow.GravityBow;
import com.me.cmitems.items.LightningStick;
import com.me.cmitems.items.TntBow;
import com.me.cmitems.items.block.tnt.ArrowTnt.ArrowTnt;
import com.me.cmitems.items.block.tnt.ArrowTnt.ArrowTntEntityRenderer;
import net.fabricmc.api.ModInitializer;
import net.minecraft.client.MinecraftClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CmItems implements ModInitializer {
	public static final String MOD_ID = "cmitems";
	public static MinecraftClient mc = MinecraftClient.getInstance();


	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {

		// Items
		TntBow.register();
		EnderBow.register();

		AimbotBow.register();
		AimbotArrow.register();

		GravityBow.register();
		GravityArrow.register();

		LightningStick.register();

		// Entities
		ArrowTntEntityRenderer.register();

		// Blocks
		ArrowTnt.register();

		// System
		ModComponents.register();
		CustomCreativeItem.register();
	}

}
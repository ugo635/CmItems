package com.me.cmitems;

import com.me.cmitems.creator.ModComponents;
import com.me.cmitems.items.bows.AimbotBow.AimbotArrow;
import com.me.cmitems.items.bows.AimbotBow.AimbotBow;
import com.me.cmitems.items.bows.GravityBow.GravityArrow;
import com.me.cmitems.items.bows.EnderBow;
import com.me.cmitems.items.bows.GravityBow.GravityBow;
import com.me.cmitems.entities.bullet.pistol.PistolBulletRenderer;
import com.me.cmitems.items.guns.pistol.BasicPistol;
import com.me.cmitems.items.tools.drills.DiamondDrill;
import com.me.cmitems.items.other.LightningStick;
import com.me.cmitems.items.bows.TntBow;
import com.me.cmitems.blocks.tnts.arrowtnt.ArrowTnt;
import com.me.cmitems.blocks.tnts.arrowtnt.ArrowTntEntityRenderer;
import com.me.cmitems.items.tools.pickaxe.ExplosionPickaxe;
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

		ExplosionPickaxe.register();
		DiamondDrill.register();

		// Guns
		BasicPistol.register();

		// Entities
		ArrowTntEntityRenderer.register();
		PistolBulletRenderer.register();

		// Blocks
		ArrowTnt.register();

		// Damage Type
		ModDamageSource.register();

		// System
		ModComponents.register();
		CustomCreativeItem.register();
	}

}
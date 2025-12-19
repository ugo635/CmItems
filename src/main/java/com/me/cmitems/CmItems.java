package com.me.cmitems;

import com.me.cmitems.items.EnderBow;
import com.me.cmitems.items.LightningStick;
import com.me.cmitems.items.TntBow;
import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CmItems implements ModInitializer {
	public static final String MOD_ID = "cmitems";


	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		TntBow.register();
		EnderBow.register();
		LightningStick.register();



		CustomCreativeItem.register();
	}
}
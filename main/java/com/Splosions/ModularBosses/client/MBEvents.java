package com.Splosions.ModularBosses.client;

import com.Splosions.ModularBosses.ModularBosses;
import com.Splosions.ModularBosses.entity.player.MBExtendedPlayer;
import com.Splosions.ModularBosses.proxy.ClientProxy;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.event.entity.EntityEvent.EntityConstructing;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MBEvents {

	/*
	 * used to make the player look ghostly when in limbo. 
	 */
	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onRenderPlayer(RenderPlayerEvent.Pre event) {
		if (MBExtendedPlayer.get((EntityPlayer)event.entity).preLimbo > 0){
			GlStateManager.enableBlend();
			GlStateManager.disableAlpha();
			GlStateManager.blendFunc(1, 1);
		}
	}
	
	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onRenderPlayer(RenderPlayerEvent.Post event) {
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
	}
	
	
	@SubscribeEvent
	public void onEntityConstructing(EntityConstructing event){
		if (event.entity instanceof EntityPlayer && MBExtendedPlayer.get((EntityPlayer) event.entity) == null)
			MBExtendedPlayer.register((EntityPlayer) event.entity);
		if (event.entity instanceof EntityPlayer && event.entity.getExtendedProperties(MBExtendedPlayer.EXT_PROP_NAME) == null)
			event.entity.registerExtendedProperties(MBExtendedPlayer.EXT_PROP_NAME, new MBExtendedPlayer((EntityPlayer) event.entity));
	}
	
	
	@SubscribeEvent
	public void onLivingUpdate(LivingUpdateEvent event) {
		if (event.entity instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) event.entity;
			MBExtendedPlayer.get(player).onUpdate();
		}
	}
		
	
	
	@SubscribeEvent
	public void onBakeModel(ModelBakeEvent event) {
		for (ModelResourceLocation resource : ClientProxy.smartModels.keySet()) {
			Object object =  event.modelRegistry.getObject(resource);
			if (object instanceof IBakedModel) {
				Class<? extends IBakedModel> clazz = ClientProxy.smartModels.get(resource);
				try {
					IBakedModel customRender = clazz.getConstructor(IBakedModel.class).newInstance((IBakedModel) object);
					event.modelRegistry.putObject(resource, customRender);
					ModularBosses.logger.warn("Registered new renderer for resource " + resource + ": " + customRender.getClass().getSimpleName());
				} catch (NoSuchMethodException e) {
					ModularBosses.logger.warn("Failed to swap model: class " + clazz.getSimpleName() + " is missing a constructor that takes an IBakedModel");
				} catch (Exception e) {
					ModularBosses.logger.warn("Failed to swap model with exception: " + e.getMessage());
				}
			} else {
				ModularBosses.logger.warn("Resource is not a baked model! Failed resource: " + resource.toString());
			}
		}
	}
	
}

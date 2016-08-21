package com.Splosions.ModularBosses.handler;

import com.Splosions.ModularBosses.Config;
import com.Splosions.ModularBosses.ModularBosses;
import com.Splosions.ModularBosses.entity.player.EntityRendererAlt;
import com.Splosions.ModularBosses.entity.player.MBExtendedPlayer;
import com.Splosions.ModularBosses.proxy.ClientProxy;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.WorldServer;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.event.entity.EntityEvent.EntityConstructing;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.RenderTickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MBEventHandler {

	private Minecraft mc;
	private EntityRenderer renderer, prevRenderer;

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onRenderTick(RenderTickEvent event) {
		if (event.phase == TickEvent.Phase.START) {
			if (renderer == null) {
				renderer = new EntityRendererAlt(mc, mc.getResourceManager());
			}
			if (mc.entityRenderer != renderer) {
				// be sure to store the previous renderer
				prevRenderer = mc.entityRenderer;
				mc.entityRenderer = renderer;
			}
		} else if (prevRenderer != null && mc.entityRenderer != prevRenderer) {
			// reset the renderer
			mc.entityRenderer = prevRenderer;
		}

	}

	/*
	 * used to make the player look ghostly when in limbo.
	 */
	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onRenderPlayer(RenderPlayerEvent.Pre event) {
		if (MBExtendedPlayer.get((EntityPlayer) event.entity).preLimbo > 0) {
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
	public void onEntityConstructing(EntityConstructing event) {
		if (event.entity instanceof EntityPlayer && MBExtendedPlayer.get((EntityPlayer) event.entity) == null)
			MBExtendedPlayer.register((EntityPlayer) event.entity);
		if (event.entity instanceof EntityPlayer
				&& event.entity.getExtendedProperties(MBExtendedPlayer.EXT_PROP_NAME) == null)
			event.entity.registerExtendedProperties(MBExtendedPlayer.EXT_PROP_NAME,
					new MBExtendedPlayer((EntityPlayer) event.entity));
	}

	@SubscribeEvent
	public void onLivingUpdate(LivingUpdateEvent event) {
		if (event.entity instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) event.entity;
			MBExtendedPlayer.get(player).onUpdate();
		}
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onBakeModel(ModelBakeEvent event) {
		for (ModelResourceLocation resource : ClientProxy.smartModels.keySet()) {
			Object object = event.modelRegistry.getObject(resource);
			if (object instanceof IBakedModel) {
				Class<? extends IBakedModel> clazz = ClientProxy.smartModels.get(resource);
				try {
					IBakedModel customRender = clazz.getConstructor(IBakedModel.class)
							.newInstance((IBakedModel) object);
					event.modelRegistry.putObject(resource, customRender);
					ModularBosses.logger.warn("Registered new renderer for resource " + resource + ": "
							+ customRender.getClass().getSimpleName());
				} catch (NoSuchMethodException e) {
					ModularBosses.logger.warn("Failed to swap model: class " + clazz.getSimpleName()
							+ " is missing a constructor that takes an IBakedModel");
				} catch (Exception e) {
					ModularBosses.logger.warn("Failed to swap model with exception: " + e.getMessage());
				}
			} else {
				ModularBosses.logger.warn("Resource is not a baked model! Failed resource: " + resource.toString());
			}
		}
	}

}

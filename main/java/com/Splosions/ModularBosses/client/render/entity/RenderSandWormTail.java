package com.Splosions.ModularBosses.client.render.entity;


import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelSpider;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.layers.LayerSpiderEyes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import org.lwjgl.opengl.GL11;

import com.Splosions.ModularBosses.client.models.entity.ModelChorpChorp;
import com.Splosions.ModularBosses.entity.EntityChorpChorp;
import com.Splosions.ModularBosses.entity.EntityEyeballOctopus;



@SideOnly(Side.CLIENT)
public class RenderSandWormTail extends RenderLiving
{
	ResourceLocation rec = new ResourceLocation("mb:textures/mobs/SandWormSegment.png");

    public RenderSandWormTail(RenderManager renderManager, ModelBase model, float shadowSize) {
		super(renderManager, model, shadowSize);
		
 
    }
    

	@Override
	protected ResourceLocation getEntityTexture(Entity entity) {
		float scale = 1F;
		GL11.glScalef(scale, scale, scale);
		GL11.glTranslatef(0.0F, -10F, 0.0F);
		GL11.glRotatef(90, 0, 1, 0);
		GL11.glRotatef(entity.rotationPitch, 1, 0, 0);
		return rec;
	}

}
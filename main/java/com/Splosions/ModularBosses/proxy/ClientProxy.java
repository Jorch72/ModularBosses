package com.Splosions.ModularBosses.proxy;

import java.util.Map;

import org.lwjgl.input.Mouse;

import com.Splosions.ModularBosses.ModularBosses;
import com.Splosions.ModularBosses.Reference;
import com.Splosions.ModularBosses.blocks.ICustomStateMapper;
import com.Splosions.ModularBosses.blocks.ISpecialRenderer;
import com.Splosions.ModularBosses.blocks.ModBlocks;
import com.Splosions.ModularBosses.blocks.tileentity.TileEntityControlBlock;
import com.Splosions.ModularBosses.blocks.tileentity.TileEntityPortalBlock;
import com.Splosions.ModularBosses.blocks.tileentity.TileEntityReturnPortalBlock;
import com.Splosions.ModularBosses.client.ISwapModel;
import com.Splosions.ModularBosses.client.models.ModModelManager;
import com.Splosions.ModularBosses.client.render.tileentity.RenderTileEntityControlBlock;
import com.Splosions.ModularBosses.client.render.tileentity.RenderTileEntityPortalBlock;
import com.Splosions.ModularBosses.client.render.tileentity.RenderTileEntityReturnPortalBlock;
import com.Splosions.ModularBosses.entity.ModularBossesEntities;
import com.Splosions.ModularBosses.handler.MBClientEventHandler;
import com.Splosions.ModularBosses.handler.MBEventHandler;
import com.Splosions.ModularBosses.handler.RenderTickHandler;
import com.Splosions.ModularBosses.items.IModItem;
import com.Splosions.ModularBosses.items.ModularBossesItems;
import com.google.common.collect.Maps;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.entity.Entity;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.ReflectionHelper.UnableToFindMethodException;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;

@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy{
	
	private final Minecraft mc = Minecraft.getMinecraft();

	private static Method f_loadShader;

	
	/** Stores all models which need to be replaced during {@link ModelBakeEvent} */
	@SuppressWarnings("deprecation")
	public static final Map<ModelResourceLocation, Class<? extends net.minecraft.client.resources.model.IBakedModel>> smartModels = Maps.newHashMap();
	/** Accessible version of EffectRenderer's IParticleFactory map */
	public static Map<Integer, IParticleFactory> particleFactoryMap;
	
	
	
	public static void sobelShader(){
		try {
			f_loadShader.invoke(Minecraft.getMinecraft().entityRenderer, new ResourceLocation("shaders/post/sobel.json"));
			
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
	
	
	public static void clearShader(){
		try{
			ReflectionHelper.setPrivateValue(EntityRenderer.class, Minecraft.getMinecraft().entityRenderer, false, new String[]{"field_175083_ad", "useShader"});
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
	
	public static boolean getShader(){
		boolean result = false;
		try{
			result = ReflectionHelper.getPrivateValue(EntityRenderer.class, Minecraft.getMinecraft().entityRenderer, new String[]{"field_175083_ad", "useShader"});
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return result;
	}
	
	
	@Override
	public void registerRenders() {
		f_loadShader = ReflectionHelper.findMethod(EntityRenderer.class, null, new String[]{"func_175069_a", "loadShader"}, ResourceLocation.class);
		
		ModularBossesEntities.registerRenderers();
		ModularBossesItems.registerRenders();
		
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityPortalBlock.class, new RenderTileEntityPortalBlock());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityReturnPortalBlock.class, new RenderTileEntityReturnPortalBlock());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityControlBlock.class, new RenderTileEntityControlBlock());
		
		try {
			for (Field f: ModularBossesItems.class.getFields()) {
				if (Item.class.isAssignableFrom(f.getType())) {
					Item item = (Item) f.get(null);
					if (item instanceof IModItem) {
						((IModItem) item).registerRenderers(mc.getRenderItem().getItemModelMesher());
					}
					if (item instanceof ISwapModel) {
						addModelToSwap((ISwapModel) item);
					}
				}
			}
		} catch(Exception e) {
			ModularBosses.logger.warn("Caught exception while registering item renderers: " + e.toString());
			e.printStackTrace();
		}
	
	
	
	try {
		for (Field f: ModBlocks.class.getFields()) {
			if (Block.class.isAssignableFrom(f.getType())) {
				Block block = (Block) f.get(null);
				if (block != null) {
					if (block instanceof ISpecialRenderer) {
						((ISpecialRenderer) block).registerSpecialRenderer();
					}
					if (block instanceof ISwapModel) {
						addModelToSwap((ISwapModel) block);
					}
					String name = block.getUnlocalizedName();
					Item item = GameRegistry.findItem(Reference.MOD_ID, name.substring(name.lastIndexOf(".") + 1));
					if (item instanceof IModItem) {
						((IModItem) item).registerRenderers(mc.getRenderItem().getItemModelMesher());
					}
					if (item instanceof ISwapModel) {
						addModelToSwap((ISwapModel) item);
					}
				}
			}
		}
	} catch(Exception e) {
		ModularBosses.logger.warn("Caught exception while registering block renderers: " + e.toString());
		e.printStackTrace();
	}
	
	}
	
	/**
	 * Adds the model swap information to the map
	 */
	private void addModelToSwap(ISwapModel swap) {
		for (ModelResourceLocation resource : swap.getDefaultResources()) {
			if (smartModels.containsKey(resource)) {
				if (smartModels.get(resource) != swap.getNewModel()) {
					ModularBosses.logger.warn("Conflicting models for resource " + resource.toString() + ": models=[old: " + smartModels.get(resource).getSimpleName() + ", new: " + swap.getNewModel().getSimpleName());
				}
			} else {
				ModularBosses.logger.warn("Swapping model for " + resource.toString() + " to class " + swap.getNewModel().getSimpleName());
				smartModels.put(resource, swap.getNewModel());
			}
		}
	}

	
	@Override
	public void preInit() {
		super.preInit();
		//FMLCommonHandler.instance().bus().register(new RenderTickHandler());
		ModModelManager.INSTANCE.registerAllModels();
		MinecraftForge.EVENT_BUS.register(new MBClientEventHandler());
	}
	
}

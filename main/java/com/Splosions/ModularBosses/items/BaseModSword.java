package com.Splosions.ModularBosses.items;

import com.Splosions.ModularBosses.MBCreativeTabs;
import com.Splosions.ModularBosses.Reference;

import net.minecraft.client.renderer.ItemModelMesher;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;


/**
 * 
 * Same as {@link BaseModItem}, but with an ItemSword base.
 *
 */
public class BaseModSword extends ItemSword implements IModItem {

	public BaseModSword(ToolMaterial material) {
		super(material);
		setCreativeTab(MBCreativeTabs.tabTools);
	}

	/**
	 * Returns "item.mb.unlocalized_name" for translation purposes
	 */
	@Override
	public String getUnlocalizedName() {
		return super.getUnlocalizedName().replaceFirst("item.", "item.mb.");
	}

	/**
	 * Returns "item.mb.unlocalized_name" for translation purposes
	 */
	@Override
	public String getUnlocalizedName(ItemStack stack) {
		return super.getUnlocalizedName(stack).replaceFirst("item.", "item.mb.");
	}

	/**
	 * Default behavior returns NULL to not register any variants
	 */
	@Override
	public String[] getVariants() {
		return null;
	}

	/**
	 * Default implementation suggested by {@link IModItem#registerVariants()}
	 */
	@Override
	@SideOnly(Side.CLIENT)
	public void registerVariants() {
		String[] variants = getVariants();
		if (variants != null) {
			ModelBakery.addVariantName(this, variants);
		}
	}

	/**
	 * Register all of this Item's renderers here, including for any subtypes.
	 * Default behavior registers a single inventory-based mesher for each variant
	 * returned by {@link #getVariants() getVariants}.
	 * If no variants are available, "mod_id:" plus the item's unlocalized name is used.
	 */
	@Override
	@SideOnly(Side.CLIENT)
	public void registerRenderers(ItemModelMesher mesher) {
		String[] variants = getVariants();
		if (variants == null || variants.length < 1) {
			String name = getUnlocalizedName();
			variants = new String[]{Reference.MOD_ID + ":" + name.substring(name.lastIndexOf(".") + 1)};
		}
		for (int i = 0; i < variants.length; ++i) {
			mesher.register(this, i, new ModelResourceLocation(variants[i], "inventory"));
		}
	}
}

package com.Splosions.ModularBosses.blocks;


import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.MaterialLiquid;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.*;
import net.minecraftforge.fml.common.registry.GameRegistry;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

import com.Splosions.ModularBosses.MBCreativeTabs;
import com.Splosions.ModularBosses.Reference;
import com.Splosions.ModularBosses.blocks.tileentity.TileEntityControlBlock;
import com.Splosions.ModularBosses.blocks.tileentity.TileEntityTempWormBlood;

public class ModFluids {
	public static Fluid fluidStatic;
	public static Fluid fluidStaticGas;
	public static Fluid fluidWormBlood;
	public static Fluid fluidTempWormBlood;
	public static Fluid fluidWormAcid;
	public static Fluid fluidTempWormAcid;
	public static Fluid fluidWormSaliva;
	public static Fluid gasWormGas;

	/**
	 * The fluids registered by this mod. Includes fluids that were already registered by another mod.
	 */
	public static Set<Fluid> fluids = new HashSet<>();

	/**
	 * The fluid blocks from this mod only. Doesn't include blocks for fluids that were already registered by another mod.
	 */
	public static Set<IFluidBlock> modFluidBlocks = new HashSet<>();

	public static void registerFluids() {
		/**
		fluidStatic = createFluid("static", "mb:blocks/fluid_static", false, 10, 1000, 1000, false,
				fluid -> new BlockFluidNoFlow(fluid, new MaterialLiquid(MapColor.brownColor)));

		fluidStaticGas = createFluid("staticgas", "mb:blocks/fluid_staticGas", false, 10, -800, 1500, true,
				fluid -> new BlockFluidNoFlow(fluid, new MaterialLiquid(MapColor.brownColor)));
		*/
		fluidWormSaliva = createFluid("fluid_worm_saliva", "mb:blocks/fluid_worm_saliva", true, 10, 1, 1500, false,
				fluid -> new FluidWormSaliva(fluid, new MaterialLiquid(MapColor.adobeColor))).setLuminosity(200);
		
		fluidWormBlood = createFluid("fluid_worm_blood", "mb:blocks/fluid_worm_blood", true, 10, 1, 1500, false,
				fluid -> new FluidWormBlood(fluid, new MaterialLiquid(MapColor.adobeColor)));
		
		fluidTempWormBlood = createFluid("fluid_temp_worm_blood", "mb:blocks/fluid_worm_blood", true, 10, 1, 1500, false,
				fluid -> new FluidTempWormBlood(fluid, new MaterialLiquid(MapColor.adobeColor)));
		GameRegistry.registerTileEntity(TileEntityTempWormBlood.class, Reference.MOD_ID + ":tileEntityTempWormBlood");

		
		fluidWormAcid = createFluid("fluid_worm_acid", "mb:blocks/fluid_worm_acid", true, 10, 1, 1500, false,
				fluid -> new FluidWormAcid(fluid, new MaterialLiquid(MapColor.adobeColor))).setLuminosity(200);

		fluidTempWormAcid = createFluid("fluid_temp_worm_acid", "mb:blocks/fluid_worm_acid", true, 10, 1, 1500, false,
				fluid -> new FluidTempWormAcid(fluid, new MaterialLiquid(MapColor.adobeColor))).setLuminosity(200);

		gasWormGas = createFluid("gas_worm_gas", "mb:blocks/gas_worm_gas", true, 10, -10000, 100, true,
				fluid -> new GasWormGas(fluid, new MaterialLiquid(MapColor.adobeColor)));
	}

	public static void registerFluidContainers() { 
		//registerTank(FluidRegistry.WATER);
		//registerTank(FluidRegistry.LAVA);

		for (Fluid fluid : fluids) {
			//registerBucket(fluid);
			//registerTank(fluid);
		}
	}

	/**
	 * Create a {@link Fluid} and its {@link IFluidBlock}, or use the existing ones if a fluid has already been registered with the same name.
	 *
	 * @param name         The name of the fluid
	 * @param textureName  The base name of the fluid's texture
	 * @param hasFlowIcon  Does the fluid have a flow icon?
	 * @param luminosity   The fluid's luminosity
	 * @param density      The fluid's density
	 * @param viscosity    The fluid's viscosity
	 * @param gaseous      Is the fluid gaseous?
	 * @param blockFactory A function to call to create the {@link IFluidBlock}
	 * @return The fluid and block
	 */
	private static <T extends Block & IFluidBlock> Fluid createFluid(String name, String textureName, boolean hasFlowIcon, int luminosity, int density, int viscosity, boolean gaseous, Function<Fluid, T> blockFactory) {
		ResourceLocation still = new ResourceLocation(textureName + "_still");
		ResourceLocation flowing = hasFlowIcon ? new ResourceLocation(textureName + "_flow") : still;

		Fluid fluid = new Fluid(name, still, flowing).setLuminosity(luminosity).setDensity(density).setViscosity(viscosity).setGaseous(gaseous);
		boolean useOwnFluid = FluidRegistry.registerFluid(fluid);

		if (useOwnFluid) {
			registerFluidBlock(blockFactory.apply(fluid));
		} else {
			fluid = FluidRegistry.getFluid(name);
		}

		fluids.add(fluid);

		return fluid;
	}

	private static <T extends Block & IFluidBlock> T registerFluidBlock(T block) {
		String fluidName = block.getFluid().getUnlocalizedName();
		block.setUnlocalizedName(fluidName);
		block.setCreativeTab(MBCreativeTabs.tabBlocks);
		GameRegistry.registerBlock(block, fluidName);

		modFluidBlocks.add(block);

		return block;
	}

	/**
	private static void registerBucket(Fluid fluid) {
		ItemStack filledBucket = ModItems.bucket.registerBucketForFluid(fluid);

		if (!FluidContainerRegistry.registerFluidContainer(fluid, filledBucket, FluidContainerRegistry.EMPTY_BUCKET)) {
			Logger.error("Unable to register bucket of %s as fluid container", fluid.getName());
		}
	}

	private static void registerTank(Fluid fluid) {
		FluidStack fluidStack = new FluidStack(fluid, 10 * FluidContainerRegistry.BUCKET_VOLUME);
		((ItemFluidTank) Item.getItemFromBlock(ModBlocks.fluidTank)).addFluid(fluidStack);
	}
	*/
}
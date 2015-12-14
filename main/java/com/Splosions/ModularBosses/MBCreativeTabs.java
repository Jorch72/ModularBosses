package com.Splosions.ModularBosses;

import java.util.Collections;
import java.util.List;

import com.Splosions.ModularBosses.items.ModularBossesItems;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;



public class MBCreativeTabs
{
	/**
	 * 
	 * Sorts creative tab using {@link ZSSItems#itemstackComparator}, allowing
	 * tabs to be sorted correctly even on old world saves.
	 *
	 */
	public abstract static class MBCreativeTab extends CreativeTabs {
		public MBCreativeTab(String label) {
			super(label);
		}

		@Override
		@SideOnly(Side.CLIENT)
		public void displayAllReleventItems(List itemstacks) {
			super.displayAllReleventItems(itemstacks);
			
		}
	}


	public static CreativeTabs tabTools = new MBCreativeTab("mb.tools") {
		@Override
		@SideOnly(Side.CLIENT)
		public Item getTabIconItem() {
			return  ModularBossesItems.Legends_Sword;
		}
	};

	public static CreativeTabs tabEggs = new MBCreativeTab("mb.eggs") {
		@Override
		@SideOnly(Side.CLIENT)
		public Item getTabIconItem() {
			return ModularBossesItems.eggSpawner;
		}
	};
}
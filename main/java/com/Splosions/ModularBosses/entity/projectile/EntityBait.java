package com.Splosions.ModularBosses.entity.projectile;

import java.util.List;

import com.Splosions.ModularBosses.entity.EntitySandWorm;
import com.Splosions.ModularBosses.items.ModularBossesItems;
import com.Splosions.ModularBosses.util.TargetUtils;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class EntityBait extends EntityThrowable {

	public List<EntitySandWorm> wormList;

	public EntityBait(World world) {
		super(world);
	}

	public EntityBait(World world, double x, double y, double z) {
		super(world, x, y, z);
	}

	public EntityBait(World world, EntityLivingBase shooter) {
		super(world, shooter);

	}

	@Override
	public void onUpdate() {
		super.onUpdate();

		if (!this.world.isRemote) {

			if (this.ticksExisted % 20 == (20 - 1)) {
				this.wormList = TargetUtils.getSpecificList(this, EntitySandWorm.class, 256, 256);
				for (EntitySandWorm worm : this.wormList) {
					worm.baitAction(this.posX, this.posY, this.posZ);
				}
			}

			if (this.ticksExisted > 20) {
				pickup(this.world.getEntitiesWithinAABB(EntityPlayer.class, this.getEntityBoundingBox().expand(1, 1, 1)));
			}

		}
	}

	public void pickup(List list) {
		for (int i = 0; i < list.size(); ++i) {
			EntityPlayer player = (EntityPlayer) list.get(i);
			TargetUtils.addItemToInventory(player, new ItemStack(ModularBossesItems.itemBait));
			this.setDead();
		}
	}



	@Override
	protected void onImpact(RayTraceResult result) {
		this.motionX = this.motionY = this.motionZ = 0;
		
	}

}
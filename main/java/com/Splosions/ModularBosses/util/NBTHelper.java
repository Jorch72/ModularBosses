package com.Splosions.ModularBosses.util;


import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

import java.util.ArrayList;
import java.util.List;

public class NBTHelper {
    public static List<TileEntity> readTileEntitiesFromCompound(final NBTTagCompound compound) {
        return readTileEntitiesFromCompound(compound, new ArrayList<TileEntity>());
    }

    public static List<TileEntity> readTileEntitiesFromCompound(final NBTTagCompound compound, final List<TileEntity> tileEntities) {
        final NBTTagList tagList = compound.getTagList("TileEntities", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < tagList.tagCount(); i++) {
            final NBTTagCompound tileEntityCompound = tagList.getCompoundTagAt(i);
            final TileEntity tileEntity = readTileEntityFromCompound(tileEntityCompound);
            tileEntities.add(tileEntity);
        }

        return tileEntities;
    }

    public static NBTTagCompound writeTileEntitiesToCompound(final List<TileEntity> tileEntities) {
        return writeTileEntitiesToCompound(tileEntities, new NBTTagCompound());
    }

    public static NBTTagCompound writeTileEntitiesToCompound(final List<TileEntity> tileEntities, final NBTTagCompound compound) {
        final NBTTagList tagList = new NBTTagList();
        for (TileEntity tileEntity : tileEntities) {
            final NBTTagCompound tileEntityCompound = writeTileEntityToCompound(tileEntity);
            tagList.appendTag(tileEntityCompound);
        }

        compound.setTag("TileEntities", tagList);

        return compound;
    }

    public static List<Entity> readEntitiesFromCompound(final NBTTagCompound compound) {
        return readEntitiesFromCompound(compound, null, new ArrayList<Entity>());
    }

    public static List<Entity> readEntitiesFromCompound(final NBTTagCompound compound, final World world) {
        return readEntitiesFromCompound(compound, world, new ArrayList<Entity>());
    }

    public static List<Entity> readEntitiesFromCompound(final NBTTagCompound compound, final List<Entity> entities) {
        return readEntitiesFromCompound(compound, null, entities);
    }

    public static List<Entity> readEntitiesFromCompound(final NBTTagCompound compound, final World world, final List<Entity> entities) {
        final NBTTagList tagList = compound.getTagList("Entities", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < tagList.tagCount(); i++) {
            final NBTTagCompound entityCompound = tagList.getCompoundTagAt(i);
            final Entity entity = readEntityFromCompound(entityCompound, world);
            if (entity != null) {
                entities.add(entity);
            }
        }

        return entities;
    }

    public static NBTTagCompound writeEntitiesToCompound(final List<Entity> entities) {
        return writeEntitiesToCompound(entities, new NBTTagCompound());
    }

    public static NBTTagCompound writeEntitiesToCompound(final List<Entity> entities, final NBTTagCompound compound) {
        final NBTTagList tagList = new NBTTagList();
        for (Entity entity : entities) {
            final NBTTagCompound entityCompound = new NBTTagCompound();
            entity.writeToNBT(entityCompound);
            tagList.appendTag(entityCompound);
        }

        compound.setTag("Entities", tagList);

        return compound;
    }

    public static TileEntity reloadTileEntity(TileEntity tileEntity) throws NBTConversionException {
        return reloadTileEntity(tileEntity, 0, 0, 0);
    }

    public static TileEntity reloadTileEntity(TileEntity tileEntity, int offsetX, int offsetY, int offsetZ) throws NBTConversionException {
        if (tileEntity == null) {
            return null;
        }

        try {
            NBTTagCompound tileEntityCompound = writeTileEntityToCompound(tileEntity);
            tileEntity = readTileEntityFromCompound(tileEntityCompound);
            final BlockPos pos = tileEntity.getPos();
            tileEntity.setPos(pos.add(-offsetX, -offsetY, -offsetZ));
        } catch (Throwable t) {
            throw new NBTConversionException(tileEntity, t);
        }

        return tileEntity;
    }

    

    public static NBTTagCompound writeTileEntityToCompound(final TileEntity tileEntity) {
        final NBTTagCompound tileEntityCompound = new NBTTagCompound();
        tileEntity.writeToNBT(tileEntityCompound);
        return tileEntityCompound;
    }

    public static TileEntity readTileEntityFromCompound(final NBTTagCompound tileEntityCompound) {
        return TileEntity.createAndLoadEntity(tileEntityCompound);
    }

    public static NBTTagCompound writeEntityToCompound(final Entity entity) {
        final NBTTagCompound entityCompound = new NBTTagCompound();
        if (entity.writeToNBTOptional(entityCompound)) {
            return entityCompound;
        }

        return null;
    }

    public static Entity readEntityFromCompound(final NBTTagCompound nbtTagCompound, final World world) {
        return EntityList.createEntityFromNBT(nbtTagCompound, world);
    }
}

package ben_mkiv.minecarttweaks.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.DimensionManager;

import java.util.HashSet;
import java.util.List;

public class EntityTeleportUtils {
    // thanks to Corail31 and brandon3055 for the following method (saved me some headache :))
    public static Entity teleportNonPlayerInterdimensional(Entity entity, DimensionType dimension, BlockPos position) {
        MinecraftServer worldServer = entity.getEntityWorld().getServer();

        ServerWorld targetWorld = DimensionManager.getWorld(worldServer, dimension, false, true);
        ServerWorld sourceWorld = DimensionManager.getWorld(worldServer, entity.getEntityWorld().getDimension().getType(), false, true);

        Vec3d motion = entity.getMotion();
        entity.dimension = dimension;
        entity.detach();

        float yaw = 0;
        float pitch = 0;

        Entity newEntity = entity.getType().create(targetWorld);
        if (newEntity != null) {
            CompoundNBT nbt = new CompoundNBT();
            entity.writeUnlessRemoved(nbt);
            nbt.remove("Dimension");
            newEntity.read(nbt);
            newEntity.timeUntilPortal = entity.timeUntilPortal;
            newEntity.setLocationAndAngles(position.getX(), position.getY(), position.getZ(), yaw, pitch);
            newEntity.setMotion(motion);
            targetWorld.func_217460_e(newEntity); //spawnEntity
            sourceWorld.resetUpdateEntityTick();
            targetWorld.resetUpdateEntityTick();
        }

        entity.remove(false);
        return newEntity;
    }

    public static Entity teleportPlayer(ServerPlayerEntity entity, DimensionType dimension, BlockPos position){
        if(!entity.getEntityWorld().getDimension().getType().equals(dimension))
            entity.changeDimension(dimension);

        entity.connection.setPlayerLocation(position.getX() + 0.5, position.getY(), position.getZ() + 0.5, 0, 0);

        return entity;
    }


    public static Entity teleport(Entity entity, DimensionType dimension, BlockPos position){
        HashSet<Entity> remountPassangers = new HashSet<>();

        // dismount passangers and teleport them first
        for(Entity passanger : entity.getPassengers()) {
            passanger.stopRiding();
            remountPassangers.add(teleport(passanger, dimension, position));
        }

        if(entity instanceof ServerPlayerEntity)
            teleportPlayer((ServerPlayerEntity) entity, dimension, position);
        else
            entity = teleportNonPlayerInterdimensional(entity, dimension, position);

        // let passangers remount the cart
        for(Entity passanger : remountPassangers) {
            //passanger.setPosition(entity.getPosition().getX() + 0.5, entity.getPosition().getY(), entity.getPosition().getZ() + 0.5);
            passanger.setLocationAndAngles(entity.getPosition().getX() + 0.5, entity.getPosition().getY(), entity.getPosition().getZ() + 0.5, 0, 0);
            passanger.startRiding(entity, false);
        }

        return entity;
    }
}

package ben_mkiv.minecarttweaks.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.minecart.AbstractMinecartEntity;
import net.minecraft.entity.item.minecart.ContainerMinecartEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;

import javax.annotation.Nullable;

public class MinecartUtils {

    private static DamageSource damageSource = new DamageSource("MinecartTweaks");

    public static void dropItems(ContainerMinecartEntity cart, int slot, int amount){
        ItemStack stack = cart.getStackInSlot(slot);
        ItemStack dropStack = stack.copy();
        dropStack.setCount(Math.min(dropStack.getCount(), amount));
        stack.shrink(dropStack.getCount());

        ItemUtils.dropItem(dropStack, cart.getEntityWorld(), getDropPosition(cart), false, 10);
        cart.setInventorySlotContents(slot, stack);
    }

    public static void voidItems(ContainerMinecartEntity cart, int slot, int amount){
        ItemStack stack = cart.getStackInSlot(slot);
        stack.shrink(amount);
        cart.setInventorySlotContents(slot, stack);
    }

    public static int getWorkSlot(ContainerMinecartEntity cart, int currentSlot){
        while(currentSlot < cart.getSizeInventory() && cart.getStackInSlot(currentSlot).isEmpty())
            currentSlot++;

        if(currentSlot < 0 || currentSlot >= cart.getSizeInventory())
            currentSlot = 0;

        return currentSlot;
    }



    private static Vec3d getDropPosition(AbstractMinecartEntity cart){
        return new Vec3d(cart.getPosition()).add(0.5, -1.5, 0.5);
    }

    public static void dropMinecart(AbstractMinecartEntity entity, @Nullable Vec3d position){
        if(entity.isAlive()) {
            if(position != null) {
                entity.setPosition(position.getX(), position.getY(), position.getZ());
            }

            entity.killMinecart(damageSource);
        }
    }



    public static void dropPassangers(AbstractMinecartEntity entity){
        for(Entity rider : entity.getPassengers())
            rider.stopRiding();
    }

    public static Entity getPassanger(AbstractMinecartEntity entity){
        return entity.getPassengers().size() > 0 ? entity.getPassengers().get(0) : null;
    }



    public static void sendNotify(AbstractMinecartEntity entity, int radius, ITextComponent message){
        sendNotify(entity, radius, message, false);
    }

    public static void sendNotify(AbstractMinecartEntity entity, int radius, ITextComponent message, boolean actionBar){
        AxisAlignedBB notificationArea = new AxisAlignedBB(0, 0, 0, 1, 1, 1).grow(radius).offset(entity.getPosition());

        for(PlayerEntity player : entity.getEntityWorld().getEntitiesWithinAABB(PlayerEntity.class, notificationArea))
            player.sendStatusMessage(message, actionBar);
    }

}

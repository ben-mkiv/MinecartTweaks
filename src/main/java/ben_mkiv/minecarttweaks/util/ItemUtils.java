package ben_mkiv.minecarttweaks.util;

import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class ItemUtils {
    public static void dropItem(ItemStack stack, World world, Vec3d pos, boolean motion, int pickupDelay){
        if(world.isRemote) return;

        if(stack.getMaxStackSize() <= 0 || stack.isEmpty())
            return;

        ItemEntity entityitem = new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(), stack);
        entityitem.setPickupDelay(pickupDelay);

        if(!motion)
            entityitem.setMotion(0, 0, 0);

        world.addEntity(entityitem);
    }
}


package ben_mkiv.minecarttweaks.cartCommands;

import ben_mkiv.minecarttweaks.capability.ICartCommand;
import ben_mkiv.minecarttweaks.util.MinecartUtils;
import net.minecraft.entity.item.minecart.AbstractMinecartEntity;
import net.minecraft.entity.item.minecart.ContainerMinecartEntity;
import net.minecraft.util.text.ITextComponent;

import java.util.*;

public class ccVoidItem implements ICartCommand {
    int workSlot = 0;

    @Override
    public boolean validate(String command){
        return command.equals("voiditem");
    }

    @Override
    public HashSet<EventType> getPhases(){
        return new HashSet<>(Arrays.asList(EventType.TICK));
    }

    @Override
    public void execute(AbstractMinecartEntity entity){
        if(!(entity instanceof ContainerMinecartEntity))
            return;

        if(entity.ticksExisted % 20 != 0)
            return;

        ContainerMinecartEntity cart = (ContainerMinecartEntity) entity;

        workSlot = MinecartUtils.getWorkSlot(cart, workSlot);

        MinecartUtils.voidItems(cart, workSlot, 1);
    }

    public ccVoidItem create(ITextComponent[] signData){
        return new ccVoidItem();
    }

}
package ben_mkiv.minecarttweaks.cartCommands;

import ben_mkiv.minecarttweaks.capability.ICartCommand;
import ben_mkiv.minecarttweaks.util.MinecartUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.minecart.AbstractMinecartEntity;
import net.minecraft.util.text.ITextComponent;

import java.util.Arrays;
import java.util.HashSet;

public class ccDropCartToPlayer implements ICartCommand {
    @Override
    public boolean validate(String data){
        return data.equals("droptoplayer");
    }

    @Override
    public HashSet<ICartCommand.EventType> getPhases(){
        return new HashSet<>(Arrays.asList(ICartCommand.EventType.STOPPED));
    }

    @Override
    public void execute(AbstractMinecartEntity cart){
        if(cart.ticksExisted < 15)
            return;

        Entity passanger = MinecartUtils.getPassanger(cart);

        MinecartUtils.dropPassangers(cart);
        MinecartUtils.dropMinecart(cart, passanger != null ? passanger.getPositionVec() : null);
    }



    public ccDropCartToPlayer create(ITextComponent[] signData){
        return new ccDropCartToPlayer();
    }

}

package ben_mkiv.minecarttweaks.cartCommands;

import ben_mkiv.minecarttweaks.capability.ICartCommand;
import ben_mkiv.minecarttweaks.util.MinecartUtils;
import net.minecraft.entity.item.minecart.AbstractMinecartEntity;
import net.minecraft.util.text.ITextComponent;

import java.util.Arrays;
import java.util.HashSet;

public class ccEjectPassangers implements ICartCommand {
    @Override
    public boolean validate(String data){
        return data.equals("eject");
    }

    @Override
    public HashSet<ICartCommand.EventType> getPhases(){
        return new HashSet<>(Arrays.asList(EventType.STOPPED));
    }

    @Override
    public void execute(AbstractMinecartEntity cart){
        if(cart.ticksExisted < 15)
            return;

        MinecartUtils.dropPassangers(cart);
    }


    public ccEjectPassangers create(ITextComponent[] signData){
        return new ccEjectPassangers();
    }

}

package ben_mkiv.minecarttweaks.cartCommands;

import ben_mkiv.minecarttweaks.capability.ICartCommand;
import ben_mkiv.minecarttweaks.util.MinecartUtils;
import net.minecraft.entity.item.minecart.AbstractMinecartEntity;
import net.minecraft.util.text.ITextComponent;

import java.util.Arrays;
import java.util.HashSet;

public class ccDropCartAsItem implements ICartCommand {
    @Override
    public boolean validate(String data){
        return data.equals("drop");
    }

    @Override
    public HashSet<ICartCommand.EventType> getPhases(){
        return new HashSet<>(Arrays.asList(ICartCommand.EventType.STOPPED));
    }

    @Override
    public void execute(AbstractMinecartEntity cart){
        if(cart.ticksExisted < 15)
            return;

        MinecartUtils.dropMinecart(cart, null);
    }


    public ccDropCartAsItem create(ITextComponent[] signData){
        return new ccDropCartAsItem();
    }

}

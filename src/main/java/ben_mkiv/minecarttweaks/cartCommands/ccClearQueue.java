package ben_mkiv.minecarttweaks.cartCommands;

import ben_mkiv.minecarttweaks.capability.ICartCommand;
import ben_mkiv.minecarttweaks.capability.capability;
import net.minecraft.entity.item.minecart.AbstractMinecartEntity;
import net.minecraft.util.text.ITextComponent;

import java.util.Arrays;
import java.util.HashSet;

public class ccClearQueue implements ICartCommand {

    public boolean validate(String data){
        return data.equals("clear");
    }

    @Override
    public HashSet<EventType> getPhases(){
        return new HashSet<>(Arrays.asList(EventType.TICK));
    }

    public void execute(AbstractMinecartEntity entity){
        entity.getCapability(capability.CAPABILITY).ifPresent(cart ->
            cart.getCommands().commands.clear()
        );
    }

    public ccClearQueue create(ITextComponent[] signData){
        return new ccClearQueue();
    }

}

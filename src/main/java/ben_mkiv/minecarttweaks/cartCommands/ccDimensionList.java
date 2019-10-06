package ben_mkiv.minecarttweaks.cartCommands;

import ben_mkiv.minecarttweaks.capability.ICartCommand;
import ben_mkiv.minecarttweaks.util.MinecartUtils;
import net.minecraft.entity.item.minecart.AbstractMinecartEntity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.dimension.DimensionType;

import java.util.Arrays;
import java.util.HashSet;

public class ccDimensionList implements ICartCommand {
    private long lastNotify = 0;

    @Override
    public boolean validate(String data){
        return data.equals("dimlist");
    }

    @Override
    public HashSet<EventType> getPhases(){
        return new HashSet<>(Arrays.asList(EventType.TICK));
    }

    @Override
    public void execute(AbstractMinecartEntity cart){
        if(cart.ticksExisted < 15)
            return;

        if(lastNotify != 0)
            return;

        for(DimensionType dimensionType : DimensionType.getAll())
            MinecartUtils.sendNotify(cart, 4, new StringTextComponent("id: "+dimensionType.getId()+", dimension: " + dimensionType.getRegistryName().toString()));

        lastNotify = System.currentTimeMillis();
    }


    public ccDimensionList create(ITextComponent[] signData){
        return new ccDimensionList();
    }

}

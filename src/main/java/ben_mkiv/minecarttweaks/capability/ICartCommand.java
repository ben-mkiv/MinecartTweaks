package ben_mkiv.minecarttweaks.capability;

import net.minecraft.entity.item.minecart.AbstractMinecartEntity;
import net.minecraft.util.text.ITextComponent;

import java.util.HashSet;

public interface ICartCommand {

    enum EventType { STARTED, STOPPED, SPAWNED, TICK, MOUNTED }

    void execute(AbstractMinecartEntity cart);
    boolean validate(String data);
    HashSet<EventType> getPhases();

    ICartCommand create(ITextComponent[] signData);
}

package ben_mkiv.minecarttweaks.capability;

import net.minecraft.entity.Entity;

public interface IMinecartCapability {
    void onSpawn();
    void onMount(Entity mountingEntity);
    CartCommands getCommands();
}

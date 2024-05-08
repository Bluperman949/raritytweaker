package bluper.raritytweaker.client.mixin;

import bluper.raritytweaker.client.RarityTweakerClient;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {
  @Shadow public abstract Item getItem();

  @ModifyVariable(
      method = "getRarity",
      at = @At("STORE"),
      ordinal = 0
  )
  private Rarity getRarity(Rarity rarity) {
    return RarityTweakerClient.RARITY_MAP.getOrDefault(this.getItem(), rarity);
  }
}

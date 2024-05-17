package top.prohect.psp.mixin;

import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Box;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@Mixin(MobEntity.class)
public abstract class MobEntityMixin {
    @Inject(method = "interactWithItem", at = @At("HEAD"),cancellable = true)
    private void interactWithItem(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        /*
          that refers to this in the injected method
         */
        ItemStack itemStack = player.getStackInHand(hand);
        MobEntity that = ((MobEntity)(Object)this);
        if (itemStack.isOf(Items.AIR)) {
            double i= player.getX(),j = player.getY(),k = player.getZ();
            AtomicBoolean flag = new AtomicBoolean(false);
            List<MobEntity> list = player.getWorld().getNonSpectatingEntities(MobEntity.class, new Box((double)i - 7.0, (double)j - 7.0, (double)k - 7.0, (double)i + 7.0, (double)j + 7.0, (double)k + 7.0));
            list.forEach(mob -> {if (mob.getHoldingEntity() == player && mob != that) {mob.attachLeash(that,true);
                flag.set(true);
            }});
            if (flag.get()) {cir.setReturnValue(ActionResult.SUCCESS);}
        }
    }
}

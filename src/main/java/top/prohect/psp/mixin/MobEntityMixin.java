package top.prohect.psp.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.LeadItem;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Box;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.prohect.psp.server.EntityHoldingEntityMap;
import top.prohect.psp.server.command.command4Module.OptimizeLead;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@Mixin(MobEntity.class)
public abstract class MobEntityMixin {
    @Unique
    private static final float radius = 32 * 16F;

    @Inject(method = "interactWithItem", at = @At("HEAD"), cancellable = true)
    private void interactWithItem(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        if (!OptimizeLead.on()) return;
        double i = player.getX(), j = player.getY(), k = player.getZ();
        AtomicBoolean flag = new AtomicBoolean(false);
        List<MobEntity> list = player.getWorld().getNonSpectatingEntities(MobEntity.class, new Box((double) i - radius, (double) j - radius, (double) k - radius, (double) i + radius, (double) j + radius, (double) k + radius));
        /*
          that refers to this in the injected method
         */
        MobEntity that = ((MobEntity) (Object) this);
        list.forEach(mob -> {
            if (mob.getHoldingEntity() == player && mob != that) {
//            if (mob != that) {
                EntityHoldingEntityMap entityHoldingEntityMap = EntityHoldingEntityMap.getOrAddEntityHoldingEntityMapByWorld(that.getWorld());
                if (player.isSneaking()) entityHoldingEntityMap.entityHoldingMap.put(that, player);
                mob.attachLeash(that, true);
                flag.set(true);
            }
        });
        if (flag.get()) {
            cir.setReturnValue(ActionResult.SUCCESS);
            return;
        }
        ItemStack itemStack = player.getStackInHand(hand);
        if (itemStack.getItem() instanceof LeadItem) {
            if (!that.isLeashed()) {
                that.attachLeash(player, true);
                itemStack.decrement(1);
                if (player.isSneaking())
                    EntityHoldingEntityMap.getOrAddEntityHoldingEntityMapByWorld(that.getWorld()).entityHoldingMap.put(that, player);
                cir.setReturnValue(ActionResult.success(that.getWorld().isClient));
            } else {
                if (player.isSneaking()) {
                    that.detachLeash(true, true);
                    cir.setReturnValue(ActionResult.success(that.getWorld().isClient));
                }
            }
            return;
        }
    }

    @Inject(method = "convertTo", at = @At("HEAD"), cancellable = true)
    public <T extends MobEntity> void convertTo(EntityType<T> entityType, boolean keepEquipment, CallbackInfoReturnable<T> cir) {
        if (!OptimizeLead.on()) return;
        MobEntity that = (MobEntity) (Object) this;
        if (that.isRemoved()) {
            cir.setReturnValue(null);
        }
        MobEntity mobEntity = (MobEntity) entityType.create(that.getWorld());
        if (mobEntity == null) {
            cir.setReturnValue(null);
        }
        mobEntity.copyPositionAndRotation(that);
        mobEntity.setBaby(that.isBaby());
        mobEntity.setAiDisabled(that.isAiDisabled());
        mobEntity.attachLeash(that.getHoldingEntity(), true);
        if (that.hasCustomName()) {
            mobEntity.setCustomName(that.getCustomName());
            mobEntity.setCustomNameVisible(that.isCustomNameVisible());
        }
        if (that.isPersistent()) {
            mobEntity.setPersistent();
        }
        mobEntity.setInvulnerable(that.isInvulnerable());
        if (keepEquipment) {
            mobEntity.setCanPickUpLoot(that.canPickUpLoot());
            for (EquipmentSlot equipmentSlot : EquipmentSlot.values()) {
                ItemStack itemStack = that.getEquippedStack(equipmentSlot);
                if (itemStack.isEmpty()) continue;
                mobEntity.equipStack(equipmentSlot, itemStack.copyAndEmpty());
                mobEntity.setEquipmentDropChance(equipmentSlot, that.getDropChance(equipmentSlot));
            }
        }
        that.getWorld().spawnEntity(mobEntity);
        if (that.hasVehicle()) {
            Entity entity = that.getVehicle();
            that.stopRiding();
            mobEntity.startRiding(entity, true);
        }
        that.discard();
        cir.setReturnValue((T) mobEntity);
        ;
    }

    @Inject(method = "detachLeash", at = @At("HEAD"))
    public void detachLeash(boolean sendPacket, boolean dropItem, CallbackInfo ci) {
        MobEntity that = (MobEntity) (Object) this;
        if (dropItem)
            EntityHoldingEntityMap.getOrAddEntityHoldingEntityMapByWorld(that.getWorld()).entityHoldingMap.remove(that);
    }
}

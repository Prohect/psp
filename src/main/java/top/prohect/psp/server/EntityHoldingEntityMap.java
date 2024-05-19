package top.prohect.psp.server;

import net.minecraft.entity.Entity;
import net.minecraft.world.World;

import java.util.HashMap;

public class EntityHoldingEntityMap {
    private final World world;
    public final HashMap<Entity, Entity> entityHoldingMap;

    private EntityHoldingEntityMap(World world) {
        this.world = world;
        this.entityHoldingMap = new HashMap<>();
    }

    private static EntityHoldingEntityMap getEntityHoldingMapByWorld(World world) {
        for (EntityHoldingEntityMap entityHoldingEntityMap : PspServer.entityHoldingEntityMaps) {
            if (entityHoldingEntityMap.world.getDimension().equals(world.getDimension())) {
                return entityHoldingEntityMap;
            }
        }
        return null;
    }

    public static EntityHoldingEntityMap getOrAddEntityHoldingEntityMapByWorld(World world) {
        EntityHoldingEntityMap entityHoldingEntityMap = getEntityHoldingMapByWorld(world);
        if (entityHoldingEntityMap == null) {
            entityHoldingEntityMap = new EntityHoldingEntityMap(world);
            PspServer.entityHoldingEntityMaps.add(entityHoldingEntityMap);
            return entityHoldingEntityMap;
        }
        return entityHoldingEntityMap;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (!(obj instanceof EntityHoldingEntityMap)) return false;
        else {
            EntityHoldingEntityMap other = (EntityHoldingEntityMap) obj;
            return other.world.getDimension().equals(this.world.getDimension());
        }
    }

    @Override
    public int hashCode() {
        return world.getDimension().hashCode() * 31 + entityHoldingMap.hashCode();
    }
}

package top.prohect.psp.server;

import net.fabricmc.api.DedicatedServerModInitializer;

import java.util.ArrayList;
import java.util.List;

public class PspServer implements DedicatedServerModInitializer {

    public static final List<EntityHoldingEntityMap> entityHoldingEntityMaps = new ArrayList<>();

    @Override
    public void onInitializeServer() {

    }
}

package com.fabbe50.langsplit.forge;

import com.fabbe50.langsplit.common.Langsplit;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;

public class LangsplitImpl {
    public static void register() {
        Langsplit.register();

        try {
            DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> LangsplitCloth::register);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

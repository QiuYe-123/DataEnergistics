package com.fish_dan_.data_energistics.mixin;

import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public final class DataEnergisticsMixinPlugin implements IMixinConfigPlugin {
    private static final String AE2LT_SENTINEL_CLASS = "com/moakiee/ae2lt/logic/EjectModeRegistry.class";
    private static final String AE2LT_MIXIN_PREFIX = "com.fish_dan_.data_energistics.mixin.Ae2lt";
    private static final boolean AE2LT_PRESENT =
            DataEnergisticsMixinPlugin.class.getClassLoader().getResource(AE2LT_SENTINEL_CLASS) != null;

    @Override
    public void onLoad(String mixinPackage) {
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        if (mixinClassName.startsWith(AE2LT_MIXIN_PREFIX)) {
            return AE2LT_PRESENT;
        }
        return true;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }
}

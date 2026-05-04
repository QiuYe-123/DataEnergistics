package com.fish_dan_.data_energistics.menu.common;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.LongSupplier;
import java.util.regex.Pattern;

import org.jetbrains.annotations.Nullable;

import appeng.api.networking.IGrid;
import appeng.blockentity.crafting.PatternProviderBlockEntity;
import appeng.api.crafting.PatternDetailsHelper;
import appeng.helpers.patternprovider.PatternContainer;
import appeng.helpers.patternprovider.PatternProviderLogicHost;
import appeng.parts.crafting.PatternProviderPart;
import com.fish_dan_.data_energistics.ae2.AdaptivePatternProviderHost;
import com.fish_dan_.data_energistics.blockentity.AdaptivePatternProviderBlockEntity;
import com.fish_dan_.data_energistics.util.PatternEncodingSourceHelper;
import com.fish_dan_.data_energistics.part.AdaptivePatternProviderPart;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public final class PatternProviderSyncHelper {
    private static final Pattern TOKEN_SPLITTER = Pattern.compile("[^\\p{IsAlphabetic}\\p{IsDigit}\\p{IsIdeographic}]+");

    private PatternProviderSyncHelper() {
    }

    public static PatternEncodingPreviewMenu.SyncedPatternProviderList collectSyncedPatternProviders(
            @Nullable IGrid grid,
            Map<PatternContainer, Long> syncedPatternProviderIds,
            LongSupplier nextIdSupplier,
            ItemStack encodedPattern) {
        if (grid == null) {
            syncedPatternProviderIds.clear();
            return PatternEncodingPreviewMenu.SyncedPatternProviderList.EMPTY;
        }

        ResourceLocation preferredWorkstationId = PatternEncodingSourceHelper.readPatternSource(encodedPattern);
        List<DiscoveredPatternProvider> discoveredProviders = new ArrayList<>();
        Map<PatternContainer, Boolean> activeProviders = new IdentityHashMap<>();
        Map<PatternContainer, Boolean> discoveredProviderSet = new IdentityHashMap<>();

        collectDirectPatternProviders(grid, syncedPatternProviderIds, nextIdSupplier, discoveredProviders, activeProviders,
                discoveredProviderSet, preferredWorkstationId);

        for (var machineClass : grid.getMachineClasses()) {
            var patternContainerClass = asPatternContainerClass(machineClass);
            if (patternContainerClass == null) {
                continue;
            }

            for (var container : grid.getMachines(patternContainerClass)) {
                addProviderIfVisible(container, syncedPatternProviderIds, nextIdSupplier, discoveredProviders, activeProviders,
                        discoveredProviderSet, preferredWorkstationId);
            }
        }

        syncedPatternProviderIds.keySet().removeIf(provider -> !activeProviders.containsKey(provider));

        discoveredProviders.sort(Comparator
                .comparing(DiscoveredPatternProvider::preferredForEncodedPattern).reversed()
                .thenComparingLong(DiscoveredPatternProvider::sortOrder)
                .thenComparing(provider -> provider.displayName().getString()));

        List<PatternEncodingPreviewMenu.SyncedPatternProvider> providers = new ArrayList<>(discoveredProviders.size());
        for (var provider : discoveredProviders) {
            providers.add(new PatternEncodingPreviewMenu.SyncedPatternProvider(
                    provider.id(),
                    provider.displayName(),
                    provider.iconItemId(),
                    provider.useAeButtonStyle(),
                    provider.patternSlotCount(),
                    provider.usedPatternSlotCount()));
        }

        return providers.isEmpty()
                ? PatternEncodingPreviewMenu.SyncedPatternProviderList.EMPTY
                : new PatternEncodingPreviewMenu.SyncedPatternProviderList(providers);
    }

    @Nullable
    public static PatternContainer findProviderById(Map<PatternContainer, Long> syncedPatternProviderIds, long providerId) {
        for (var entry : syncedPatternProviderIds.entrySet()) {
            if (entry.getValue() != null && entry.getValue() == providerId) {
                return entry.getKey();
            }
        }
        return null;
    }

    public static ItemStack transferEncodedPatternToProvider(PatternContainer container, ItemStack encodedPattern) {
        if (container == null || encodedPattern.isEmpty() || !PatternDetailsHelper.isEncodedPattern(encodedPattern)) {
            return encodedPattern;
        }

        var patternInventory = container.getTerminalPatternInventory();
        if (patternInventory.size() <= 0) {
            return encodedPattern;
        }

        ItemStack remainder = patternInventory.addItems(encodedPattern.copy(), false);
        if (remainder.getCount() == encodedPattern.getCount()) {
            return encodedPattern;
        }

        if (container instanceof PatternProviderLogicHost providerHost) {
            providerHost.getLogic().updatePatterns();
            providerHost.saveChanges();
        }

        return remainder;
    }

    private static void collectDirectPatternProviders(
            IGrid grid,
            Map<PatternContainer, Long> syncedPatternProviderIds,
            LongSupplier nextIdSupplier,
            List<DiscoveredPatternProvider> discoveredProviders,
            Map<PatternContainer, Boolean> activeProviders,
            Map<PatternContainer, Boolean> discoveredProviderSet,
            @Nullable ResourceLocation preferredWorkstationId) {
        try {
            for (var providerHost : grid.getMachines(PatternProviderLogicHost.class)) {
                addProviderIfVisible(providerHost, syncedPatternProviderIds, nextIdSupplier, discoveredProviders,
                        activeProviders, discoveredProviderSet, preferredWorkstationId);
            }
        } catch (Exception ignored) {
        }
    }

    private static void addProviderIfVisible(
            PatternContainer container,
            Map<PatternContainer, Long> syncedPatternProviderIds,
            LongSupplier nextIdSupplier,
            List<DiscoveredPatternProvider> discoveredProviders,
            Map<PatternContainer, Boolean> activeProviders,
            Map<PatternContainer, Boolean> discoveredProviderSet,
            @Nullable ResourceLocation preferredWorkstationId) {
        if (!isProviderContainer(container) || discoveredProviderSet.containsKey(container)) {
            return;
        }

        var patternInventory = container.getTerminalPatternInventory();
        if (patternInventory.size() <= 0) {
            return;
        }

        discoveredProviderSet.put(container, Boolean.TRUE);

        long providerId = syncedPatternProviderIds.computeIfAbsent(container,
                ignored -> nextIdSupplier.getAsLong());
        activeProviders.put(container, Boolean.TRUE);
        Component displayName = resolveProviderDisplayName(container);
        ResourceLocation iconItemId = resolveProviderIconItemId(container);
        int usedPatternSlots = countUsedPatternSlots(patternInventory);
        discoveredProviders.add(new DiscoveredPatternProvider(
                providerId,
                container.getTerminalSortOrder(),
                displayName,
                iconItemId,
                shouldUseAeButtonStyle(container),
                patternInventory.size(),
                usedPatternSlots,
                isPreferredProvider(displayName, iconItemId, patternInventory.size(), usedPatternSlots,
                        preferredWorkstationId)));
    }

    private static boolean isProviderContainer(PatternContainer container) {
        if (container instanceof PatternProviderLogicHost) {
            return true;
        }

        String className = container.getClass().getSimpleName().toLowerCase(Locale.ROOT);
        return className.contains("provider");
    }

    private static boolean shouldUseAeButtonStyle(PatternContainer container) {
        return isProviderContainer(container);
    }

    @Nullable
    private static Class<? extends PatternContainer> asPatternContainerClass(Class<?> machineClass) {
        return PatternContainer.class.isAssignableFrom(machineClass)
                ? machineClass.asSubclass(PatternContainer.class)
                : null;
    }

    private static Component resolveProviderDisplayName(PatternContainer container) {
        if (container instanceof AdaptivePatternProviderHost adaptiveHost) {
            return adaptiveHost.getGuiDisplayName();
        }

        var terminalGroup = container.getTerminalGroup();
        if (terminalGroup != null && terminalGroup.name() != null) {
            return terminalGroup.name();
        }

        ItemStack icon = resolveProviderIcon(container);
        if (!icon.isEmpty()) {
            return icon.getHoverName();
        }

        return Component.literal(container.getClass().getSimpleName());
    }

    private static ResourceLocation resolveProviderIconItemId(PatternContainer container) {
        ItemStack icon = resolveProviderIcon(container);
        ResourceLocation iconItemId = BuiltInRegistries.ITEM.getKey(icon.getItem());
        if (iconItemId != null) {
            return iconItemId;
        }
        return BuiltInRegistries.ITEM.getKey(Items.AIR);
    }

    private static ItemStack resolveProviderIcon(PatternContainer container) {
        var terminalGroup = container.getTerminalGroup();
        if (terminalGroup != null && terminalGroup.icon() != null) {
            ItemStack groupIcon = terminalGroup.icon().toStack();
            if (!groupIcon.isEmpty()) {
                return groupIcon;
            }
        }

        if (container instanceof AdaptivePatternProviderBlockEntity blockEntity) {
            return blockEntity.getMainMenuIcon();
        }
        if (container instanceof AdaptivePatternProviderPart part) {
            return part.getMainMenuIcon();
        }
        if (container instanceof PatternProviderBlockEntity blockEntity) {
            return blockEntity.getMainMenuIcon();
        }
        if (container instanceof PatternProviderPart part) {
            return part.getMainMenuIcon();
        }

        ItemStack reflectedIcon = resolveMainMenuIconReflectively(container);
        if (!reflectedIcon.isEmpty()) {
            return reflectedIcon;
        }

        if (container instanceof PatternProviderLogicHost providerHost) {
            var terminalIcon = providerHost.getTerminalIcon();
            if (terminalIcon != null) {
                ItemStack terminalIconStack = terminalIcon.toStack();
                if (!terminalIconStack.isEmpty()) {
                    return terminalIconStack;
                }
            }
        }

        return ItemStack.EMPTY;
    }

    private static ItemStack resolveMainMenuIconReflectively(Object source) {
        try {
            Method method = source.getClass().getMethod("getMainMenuIcon");
            Object result = method.invoke(source);
            if (result instanceof ItemStack stack && !stack.isEmpty()) {
                return stack.copy();
            }
        } catch (Exception ignored) {
        }

        return ItemStack.EMPTY;
    }

    private static int countUsedPatternSlots(appeng.api.inventories.InternalInventory inventory) {
        int usedSlots = 0;
        for (int slot = 0; slot < inventory.size(); slot++) {
            if (!inventory.getStackInSlot(slot).isEmpty()) {
                usedSlots++;
            }
        }
        return usedSlots;
    }

    private static boolean isPreferredProvider(Component displayName, ResourceLocation iconItemId,
                                               int patternSlotCount, int usedPatternSlotCount,
                                               @Nullable ResourceLocation preferredWorkstationId) {
        if (preferredWorkstationId == null || patternSlotCount <= 0 || usedPatternSlotCount >= patternSlotCount) {
            return false;
        }

        String providerName = normalizeForMatch(displayName.getString());
        String providerIconName = normalizeForMatch(iconItemId.toString());
        if (providerName.isEmpty() && providerIconName.isEmpty()) {
            return false;
        }

        Component workstationDisplayName = resolveWorkstationDisplayName(preferredWorkstationId);
        String workstationName = normalizeForMatch(workstationDisplayName.getString());
        String workstationId = normalizeForMatch(preferredWorkstationId.toString());

        return containsSimilarText(providerName, workstationName)
                || containsSimilarText(providerName, workstationId)
                || containsSimilarText(providerIconName, workstationName)
                || containsSimilarText(providerIconName, workstationId)
                || sharesKeyword(providerName, workstationName)
                || sharesKeyword(providerName, workstationId)
                || sharesKeyword(providerIconName, workstationName)
                || sharesKeyword(providerIconName, workstationId);
    }

    private static Component resolveWorkstationDisplayName(ResourceLocation workstationId) {
        var item = BuiltInRegistries.ITEM.getOptional(workstationId).orElse(null);
        if (item != null) {
            return item.getDefaultInstance().getHoverName();
        }

        var block = BuiltInRegistries.BLOCK.getOptional(workstationId).orElse(null);
        if (block != null) {
            return block.getName();
        }

        return Component.literal(workstationId.toString());
    }

    private static boolean containsSimilarText(String providerText, String workstationText) {
        return !providerText.isEmpty()
                && !workstationText.isEmpty()
                && (providerText.contains(workstationText) || workstationText.contains(providerText));
    }

    private static boolean sharesKeyword(String providerText, String workstationText) {
        if (providerText.isEmpty() || workstationText.isEmpty()) {
            return false;
        }

        for (String workstationToken : TOKEN_SPLITTER.split(workstationText)) {
            if (workstationToken.length() < 2) {
                continue;
            }
            if (providerText.contains(workstationToken)) {
                return true;
            }
        }

        return false;
    }

    private static String normalizeForMatch(String text) {
        StringBuilder normalized = new StringBuilder(text.length());
        text.codePoints()
                .map(Character::toLowerCase)
                .filter(PatternProviderSyncHelper::isMatchCharacter)
                .forEach(normalized::appendCodePoint);
        return normalized.toString();
    }

    private static boolean isMatchCharacter(int codePoint) {
        return Character.isLetterOrDigit(codePoint) || Character.UnicodeScript.of(codePoint) == Character.UnicodeScript.HAN;
    }

    private record DiscoveredPatternProvider(
            long id,
            long sortOrder,
            Component displayName,
            ResourceLocation iconItemId,
            boolean useAeButtonStyle,
            int patternSlotCount,
            int usedPatternSlotCount,
            boolean preferredForEncodedPattern) {
    }
}

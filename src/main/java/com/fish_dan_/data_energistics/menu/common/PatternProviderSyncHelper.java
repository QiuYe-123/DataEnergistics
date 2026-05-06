package com.fish_dan_.data_energistics.menu.common;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.LongSupplier;
import java.util.regex.Matcher;
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
    private static final Pattern NEOECOAE_TIER_TOKEN = Pattern.compile("([fl]\\d+)", Pattern.CASE_INSENSITIVE);
    private static final String EXTENDEDAE_ASSEMBLER_MATRIX_NAME_KEY = "gui.extendedae.assembler_matrix";
    private static final String NEOECOAE_NAMESPACE = "neoecoae";
    private static final String NEOECOAE_CRAFTING_SYSTEM_PATH = "eco_crafting_system";
    private static final String NEOECOAE_CRAFTING_SYSTEM_PREFIX = "crafting_system_";
    private static final String NEOECOAE_CRAFTING_WORKER_PATH = "crafting_worker";
    private static final String NEOECOAE_CRAFTING_PATTERN_BUS_PATH = "crafting_pattern_bus";
    private static final String NEOECOAE_ECO_CRAFTING_WORKER_PATH = "eco_crafting_worker";
    private static final ResourceLocation CRAFTING_TABLE_ID = ResourceLocation.withDefaultNamespace("crafting_table");
    private static final ResourceLocation STONECUTTER_ID = ResourceLocation.withDefaultNamespace("stonecutter");
    private static final ResourceLocation SMITHING_TABLE_ID = ResourceLocation.withDefaultNamespace("smithing_table");
    private static final ResourceLocation AE2_MOLECULAR_ASSEMBLER_ID =
            ResourceLocation.fromNamespaceAndPath("ae2", "molecular_assembler");
    private static final ResourceLocation EXTENDEDAE_ASSEMBLER_MATRIX_SPEED_ID =
            ResourceLocation.fromNamespaceAndPath("extendedae", "assembler_matrix_speed");
    private static final ResourceLocation EXTENDEDAE_CRYSTAL_ASSEMBLER_ID =
            ResourceLocation.fromNamespaceAndPath("extendedae", "crystal_assembler");
    private static final ResourceLocation EXTENDEDAE_EX_MOLECULAR_ASSEMBLER_ID =
            ResourceLocation.fromNamespaceAndPath("extendedae", "ex_molecular_assembler");
    private static final ResourceLocation AE2CS_METEORITE_PATTERN_PROVIDER_ID =
            ResourceLocation.fromNamespaceAndPath("ae2cs", "meteorite_pattern_provider");

    private PatternProviderSyncHelper() {
    }

    public static PatternEncodingPreviewMenu.SyncedPatternProviderList collectSyncedPatternProviders(
            @Nullable IGrid grid,
            Map<PatternContainer, Long> syncedPatternProviderIds,
            Map<Long, List<PatternContainer>> syncedProviderTargetsById,
            LongSupplier nextIdSupplier,
            ItemStack encodedPattern) {
        syncedProviderTargetsById.clear();
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

        List<AggregatedPatternProvider> aggregatedProviders = aggregateDiscoveredProviders(discoveredProviders);
        aggregatedProviders.sort(Comparator
                .comparingInt(AggregatedPatternProvider::preferredScore).reversed()
                .thenComparingLong(AggregatedPatternProvider::sortOrder)
                .thenComparing(provider -> provider.displayName().getString()));

        List<PatternEncodingPreviewMenu.SyncedPatternProvider> providers = new ArrayList<>(aggregatedProviders.size());
        for (var provider : aggregatedProviders) {
            syncedProviderTargetsById.put(provider.id(), List.copyOf(provider.containers()));
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

    @Nullable
    public static List<PatternContainer> findProvidersById(Map<Long, List<PatternContainer>> syncedProviderTargetsById,
                                                           long providerId) {
        return syncedProviderTargetsById.get(providerId);
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

    public static ItemStack transferEncodedPatternToProviders(List<PatternContainer> containers, ItemStack encodedPattern) {
        if (containers == null || containers.isEmpty() || encodedPattern.isEmpty()) {
            return encodedPattern;
        }

        ItemStack remainder = encodedPattern.copy();
        boolean transferred = false;
        for (var container : containers) {
            if (remainder.isEmpty()) {
                break;
            }

            ItemStack nextRemainder = transferEncodedPatternToProvider(container, remainder);
            if (nextRemainder.getCount() != remainder.getCount()) {
                transferred = true;
            }
            remainder = nextRemainder;
        }

        return transferred ? remainder : encodedPattern;
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
                container,
                providerId,
                container.getTerminalSortOrder(),
                displayName,
                iconItemId,
                shouldUseAeButtonStyle(container),
                patternInventory.size(),
                usedPatternSlots,
                getPreferredProviderScore(container, displayName, iconItemId, patternInventory.size(), usedPatternSlots,
                        preferredWorkstationId)));
    }

    private static boolean isProviderContainer(PatternContainer container) {
        if (container instanceof PatternProviderLogicHost) {
            return true;
        }

        String className = container.getClass().getSimpleName().toLowerCase(Locale.ROOT);
        if (className.contains("provider")
                || className.contains("pattern")
                || className.contains("crafting")) {
            return true;
        }

        ResourceLocation terminalIconItemId = resolveTerminalIconItemId(container);
        return isNeoEcoCraftingSubsystemIcon(terminalIconItemId)
                || !resolveTerminalGroupIcon(container).isEmpty()
                || !resolveMainMenuIconReflectively(container).isEmpty();
    }

    private static boolean shouldUseAeButtonStyle(PatternContainer container) {
        return isProviderContainer(container);
    }

    private static boolean isAssemblerMatrixPatternContainer(PatternContainer container) {
        return container.getClass().getSimpleName().equals("TileAssemblerMatrixPattern");
    }

    private static boolean isNeoEcoCraftingSubsystemContainer(DiscoveredPatternProvider provider) {
        return isNeoEcoCraftingSubsystemIcon(provider.iconItemId())
                || isNeoEcoCraftingSubsystemClassName(provider.container())
                || isNeoEcoCraftingSubsystemName(provider.displayName().getString());
    }

    private static List<AggregatedPatternProvider> aggregateDiscoveredProviders(
            List<DiscoveredPatternProvider> discoveredProviders) {
        List<AggregatedPatternProvider> aggregatedProviders = new ArrayList<>();
        Map<String, AggregatedPatternProvider> aggregatedSpecialProviders = new HashMap<>();

        for (var provider : discoveredProviders) {
            if (shouldAggregateSpecialProvider(provider)) {
                String key = getAggregationKey(provider);
                var aggregated = aggregatedSpecialProviders.get(key);
                if (aggregated == null) {
                    aggregated = new AggregatedPatternProvider(provider);
                    aggregatedSpecialProviders.put(key, aggregated);
                }
                aggregated.include(provider);
            } else {
                var aggregated = new AggregatedPatternProvider(provider);
                aggregated.include(provider);
                aggregatedProviders.add(aggregated);
            }
        }

        aggregatedProviders.addAll(aggregatedSpecialProviders.values());
        return aggregatedProviders;
    }

    private static boolean shouldAggregateSpecialProvider(DiscoveredPatternProvider provider) {
        return isAssemblerMatrixPatternContainer(provider.container())
                || isNeoEcoCraftingSubsystemContainer(provider);
    }

    private static String getAggregationKey(DiscoveredPatternProvider provider) {
        if (isAssemblerMatrixPatternContainer(provider.container())) {
            return "extendedae:assembler_matrix";
        }
        if (isNeoEcoCraftingSubsystemContainer(provider)) {
            String tierKey = resolveNeoEcoCraftingSubsystemTierKey(provider);
            return tierKey == null
                    ? "neoecoae:crafting_system"
                    : "neoecoae:crafting_system:" + tierKey;
        }
        return provider.iconItemId() + "|" + provider.displayName().getString();
    }

    @Nullable
    private static Class<? extends PatternContainer> asPatternContainerClass(Class<?> machineClass) {
        return PatternContainer.class.isAssignableFrom(machineClass)
                ? machineClass.asSubclass(PatternContainer.class)
                : null;
    }

    private static Component resolveProviderDisplayName(PatternContainer container) {
        if (isAssemblerMatrixPatternContainer(container)) {
            return Component.translatable(EXTENDEDAE_ASSEMBLER_MATRIX_NAME_KEY);
        }

        if (container instanceof AdaptivePatternProviderHost adaptiveHost) {
            var attachedGroup = adaptiveHost.getPrimaryAttachedMachineGroup();
            if (attachedGroup != null && attachedGroup.name() != null) {
                return attachedGroup.name();
            }
            var terminalGroup = container.getTerminalGroup();
            if (terminalGroup != null && terminalGroup.name() != null) {
                return terminalGroup.name();
            }
            return adaptiveHost.getTerminalDisplayName();
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

    @Nullable
    private static ResourceLocation resolveTerminalIconItemId(PatternContainer container) {
        ItemStack icon = resolveTerminalGroupIcon(container);
        if (icon.isEmpty()) {
            return null;
        }

        ResourceLocation iconItemId = BuiltInRegistries.ITEM.getKey(icon.getItem());
        if (iconItemId == null || Items.AIR.equals(icon.getItem())) {
            return null;
        }

        return iconItemId;
    }

    private static ItemStack resolveTerminalGroupIcon(PatternContainer container) {
        var terminalGroup = container.getTerminalGroup();
        if (terminalGroup != null && terminalGroup.icon() != null) {
            ItemStack groupIcon = terminalGroup.icon().toStack();
            if (!groupIcon.isEmpty()) {
                return groupIcon;
            }
        }

        return ItemStack.EMPTY;
    }

    private static ItemStack resolveProviderIcon(PatternContainer container) {
        if (isAssemblerMatrixPatternContainer(container)) {
            var speedBlock = BuiltInRegistries.BLOCK.getOptional(EXTENDEDAE_ASSEMBLER_MATRIX_SPEED_ID).orElse(null);
            if (speedBlock != null) {
                ItemStack speedCore = speedBlock.asItem().getDefaultInstance();
                if (!speedCore.isEmpty()) {
                    return speedCore;
                }
            }
        }

        if (container instanceof AdaptivePatternProviderHost adaptiveHost) {
            var attachedGroup = adaptiveHost.getPrimaryAttachedMachineGroup();
            if (attachedGroup != null && attachedGroup.icon() != null) {
                ItemStack attachedIcon = attachedGroup.icon().toStack();
                if (!attachedIcon.isEmpty()) {
                    return attachedIcon;
                }
            }
        }

        ItemStack terminalIcon = resolveTerminalGroupIcon(container);
        if (!terminalIcon.isEmpty()) {
            return terminalIcon;
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
            var providerTerminalIcon = providerHost.getTerminalIcon();
            if (providerTerminalIcon != null) {
                ItemStack terminalIconStack = providerTerminalIcon.toStack();
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

    private static int getPreferredProviderScore(PatternContainer container, Component displayName, ResourceLocation iconItemId,
                                                 int patternSlotCount, int usedPatternSlotCount,
                                                 @Nullable ResourceLocation preferredWorkstationId) {
        if (preferredWorkstationId == null || patternSlotCount <= 0 || usedPatternSlotCount >= patternSlotCount) {
            return 0;
        }

        if (isWorkbenchFamily(preferredWorkstationId)) {
            int workbenchFamilyPriority = getWorkbenchFamilyPriority(container, displayName, iconItemId);
            if (workbenchFamilyPriority > 0) {
                return workbenchFamilyPriority;
            }
        }

        String providerName = normalizeForMatch(displayName.getString());
        String providerIconName = normalizeForMatch(iconItemId.toString());
        if (providerName.isEmpty() && providerIconName.isEmpty()) {
            return 0;
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
                || sharesKeyword(providerIconName, workstationId)
                ? 1
                : 0;
    }

    private static boolean isWorkbenchFamily(ResourceLocation workstationId) {
        return CRAFTING_TABLE_ID.equals(workstationId)
                || STONECUTTER_ID.equals(workstationId)
                || SMITHING_TABLE_ID.equals(workstationId)
                || AE2_MOLECULAR_ASSEMBLER_ID.equals(workstationId)
                || EXTENDEDAE_CRYSTAL_ASSEMBLER_ID.equals(workstationId)
                || EXTENDEDAE_ASSEMBLER_MATRIX_SPEED_ID.equals(workstationId);
    }

    private static int getWorkbenchFamilyPriority(PatternContainer container, Component displayName,
                                                  ResourceLocation iconItemId) {
        Integer neoEcoTier = resolveNeoEcoCraftingSubsystemTier(container, displayName, iconItemId);
        if (neoEcoTier != null) {
            return switch (neoEcoTier) {
                case 9 -> 70;
                case 6 -> 60;
                case 4 -> 50;
                default -> 0;
            };
        }

        if (EXTENDEDAE_ASSEMBLER_MATRIX_SPEED_ID.equals(iconItemId)) {
            return 40;
        }
        if (isMeteoriteWorkbenchProvider(container, displayName, iconItemId)) {
            return 30;
        }
        if (EXTENDEDAE_EX_MOLECULAR_ASSEMBLER_ID.equals(iconItemId)) {
            return 20;
        }
        if (AE2_MOLECULAR_ASSEMBLER_ID.equals(iconItemId)) {
            return 10;
        }

        return 0;
    }

    private static boolean isMeteoriteWorkbenchProvider(PatternContainer container, Component displayName,
                                                        ResourceLocation iconItemId) {
        if (AE2CS_METEORITE_PATTERN_PROVIDER_ID.equals(iconItemId)) {
            return true;
        }

        if (container instanceof AdaptivePatternProviderHost adaptiveHost && adaptiveHost.isMeteoriteProviderSelected()) {
            return true;
        }

        String normalizedName = normalizeForMatch(displayName.getString());
        return normalizedName.contains("自装配式样板供应器")
                || normalizedName.contains("meteoritepatternprovider");
    }

    private static boolean isNeoEcoCraftingSubsystemIcon(@Nullable ResourceLocation iconItemId) {
        if (iconItemId == null || !NEOECOAE_NAMESPACE.equals(iconItemId.getNamespace())) {
            return false;
        }

        String path = iconItemId.getPath();
        return path.equals(NEOECOAE_CRAFTING_SYSTEM_PATH)
                || path.startsWith(NEOECOAE_CRAFTING_SYSTEM_PREFIX)
                || path.equals(NEOECOAE_CRAFTING_WORKER_PATH)
                || path.equals(NEOECOAE_CRAFTING_PATTERN_BUS_PATH)
                || path.equals(NEOECOAE_ECO_CRAFTING_WORKER_PATH);
    }

    private static boolean isNeoEcoCraftingSubsystemClassName(PatternContainer container) {
        String className = container.getClass().getName().toLowerCase(Locale.ROOT);
        return className.contains("neoeco")
                && (className.contains("crafting")
                || className.contains("patternbus")
                || className.contains("worker"));
    }

    private static boolean isNeoEcoCraftingSubsystemName(String displayName) {
        String normalizedName = normalizeForMatch(displayName);
        return normalizedName.contains("可拓展合成子系统")
                || normalizedName.contains("ecocraftingsystem")
                || normalizedName.contains("craftingsystem");
    }

    @Nullable
    private static Integer resolveNeoEcoCraftingSubsystemTier(PatternContainer container, Component displayName,
                                                              ResourceLocation iconItemId) {
        String tierKey = extractNeoEcoTierToken(iconItemId.getPath());
        if (tierKey == null) {
            tierKey = extractNeoEcoTierToken(displayName.getString());
        }
        if (tierKey == null) {
            tierKey = extractNeoEcoTierToken(container.getClass().getSimpleName());
        }
        if (tierKey == null) {
            tierKey = extractNeoEcoTierToken(container.getClass().getName());
        }
        if (tierKey == null || tierKey.length() < 2) {
            return null;
        }

        try {
            return Integer.parseInt(tierKey.substring(1));
        } catch (NumberFormatException ignored) {
            return null;
        }
    }

    @Nullable
    private static String resolveNeoEcoCraftingSubsystemTierKey(DiscoveredPatternProvider provider) {
        Integer tier = resolveNeoEcoCraftingSubsystemTier(provider.container(), provider.displayName(), provider.iconItemId());
        return tier == null ? null : "F" + tier;
    }

    @Nullable
    private static String extractNeoEcoTierToken(@Nullable String text) {
        if (text == null || text.isEmpty()) {
            return null;
        }

        Matcher matcher = NEOECOAE_TIER_TOKEN.matcher(text);
        if (!matcher.find()) {
            return null;
        }

        return matcher.group(1).toUpperCase(Locale.ROOT);
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
            PatternContainer container,
            long id,
            long sortOrder,
            Component displayName,
            ResourceLocation iconItemId,
            boolean useAeButtonStyle,
            int patternSlotCount,
            int usedPatternSlotCount,
            int preferredScore) {
    }

    private static final class AggregatedPatternProvider {
        private long id;
        private long sortOrder;
        private Component displayName;
        private ResourceLocation iconItemId;
        private boolean useAeButtonStyle;
        private int patternSlotCount;
        private int usedPatternSlotCount;
        private int preferredScore;
        private int representationPriority;
        private final List<PatternContainer> containers = new ArrayList<>();

        private AggregatedPatternProvider(DiscoveredPatternProvider provider) {
            this.id = provider.id();
            this.sortOrder = provider.sortOrder();
            this.displayName = provider.displayName();
            this.iconItemId = provider.iconItemId();
            this.useAeButtonStyle = provider.useAeButtonStyle();
            this.representationPriority = getRepresentationPriority(provider);
        }

        private void include(DiscoveredPatternProvider provider) {
            this.id = Math.min(this.id, provider.id());
            this.sortOrder = Math.min(this.sortOrder, provider.sortOrder());
            this.patternSlotCount += provider.patternSlotCount();
            this.usedPatternSlotCount += provider.usedPatternSlotCount();
            this.preferredScore = Math.max(this.preferredScore, provider.preferredScore());
            this.useAeButtonStyle |= provider.useAeButtonStyle();
            int incomingPriority = getRepresentationPriority(provider);
            if (incomingPriority > this.representationPriority) {
                this.displayName = provider.displayName();
                this.iconItemId = provider.iconItemId();
                this.representationPriority = incomingPriority;
            }
            this.containers.add(provider.container());
        }

        private long id() {
            return this.id;
        }

        private long sortOrder() {
            return this.sortOrder;
        }

        private Component displayName() {
            return this.displayName;
        }

        private ResourceLocation iconItemId() {
            return this.iconItemId;
        }

        private boolean useAeButtonStyle() {
            return this.useAeButtonStyle;
        }

        private int patternSlotCount() {
            return this.patternSlotCount;
        }

        private int usedPatternSlotCount() {
            return this.usedPatternSlotCount;
        }

        private int preferredScore() {
            return this.preferredScore;
        }

        private List<PatternContainer> containers() {
            return this.containers;
        }

        private int getRepresentationPriority(DiscoveredPatternProvider provider) {
            if (isAssemblerMatrixPatternContainer(provider.container())) {
                return 3;
            }
            if (isNeoEcoCraftingSubsystemIcon(provider.iconItemId())
                    && provider.iconItemId().getPath().startsWith(NEOECOAE_CRAFTING_SYSTEM_PREFIX)) {
                return 3;
            }
            if (isNeoEcoCraftingSubsystemIcon(provider.iconItemId())
                    && provider.iconItemId().getPath().equals(NEOECOAE_CRAFTING_SYSTEM_PATH)) {
                return 2;
            }
            if (isNeoEcoCraftingSubsystemContainer(provider)) {
                return 1;
            }
            return 0;
        }
    }
}

package com.fish_dan_.data_energistics.util;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Map;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import org.jetbrains.annotations.Nullable;

import appeng.menu.me.items.PatternEncodingTermMenu;
import appeng.parts.encoding.EncodingMode;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.fish_dan_.data_energistics.menu.common.PatternEncodingPreviewMenu;
import com.fish_dan_.data_energistics.menu.common.PatternEncodingSourceAware;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;

public final class PatternEncodingSourceHelper {
    public static final String ACTION_SET_PATTERN_SOURCE = "dataEnergisticsSetPatternSource";
    public static final String CLEAR_PATTERN_SOURCE = "";
    private static final String PLAYER_PATTERN_SOURCE_ROOT = "data_energistics_pattern_source";
    private static final String TAG_PENDING = "pending";
    private static final String TAG_LAST = "last";
    private static final String TAG_ENABLED = "enabled";
    private static final String WORKSTATION_MAPPINGS_RESOURCE = "data_energistics/pattern_workstation_mappings.json";
    private static final ResourceLocation CRAFTING_TABLE_ID = ResourceLocation.withDefaultNamespace("crafting_table");
    private static final ResourceLocation FURNACE_ID = ResourceLocation.withDefaultNamespace("furnace");
    private static final ResourceLocation BLAST_FURNACE_ID = ResourceLocation.withDefaultNamespace("blast_furnace");
    private static final ResourceLocation SMOKER_ID = ResourceLocation.withDefaultNamespace("smoker");
    private static final ResourceLocation CAMPFIRE_ID = ResourceLocation.withDefaultNamespace("campfire");
    private static final ResourceLocation STONECUTTER_ID = ResourceLocation.withDefaultNamespace("stonecutter");
    private static final ResourceLocation SMITHING_TABLE_ID = ResourceLocation.withDefaultNamespace("smithing_table");
    private static final ResourceLocation AE2_INSCRIBER_ID = ResourceLocation.fromNamespaceAndPath("ae2", "inscriber");
    private static final ResourceLocation AE2_CHARGER_ID = ResourceLocation.fromNamespaceAndPath("ae2", "charger");
    private static final ResourceLocation EXTENDEDAE_ASSEMBLER_MATRIX_SPEED_ID =
            ResourceLocation.fromNamespaceAndPath("extendedae", "assembler_matrix_speed");
    private static final ResourceLocation EXTENDEDAE_CRYSTAL_ASSEMBLER_ID =
            ResourceLocation.fromNamespaceAndPath("extendedae", "crystal_assembler");
    private static final ResourceLocation MEKANISM_COMBINER_ID =
            ResourceLocation.fromNamespaceAndPath("mekanism", "combiner");
    private static final ResourceLocation MEKANISM_OSMIUM_COMPRESSOR_ID =
            ResourceLocation.fromNamespaceAndPath("mekanism", "osmium_compressor");
    private static final ResourceLocation MEKANISM_CRUSHER_ID =
            ResourceLocation.fromNamespaceAndPath("mekanism", "crusher");
    private static final ResourceLocation MEKANISM_ENRICHMENT_CHAMBER_ID =
            ResourceLocation.fromNamespaceAndPath("mekanism", "enrichment_chamber");
    private static final ResourceLocation MEKANISM_CHEMICAL_INJECTION_CHAMBER_ID =
            ResourceLocation.fromNamespaceAndPath("mekanism", "chemical_injection_chamber");
    private static final ResourceLocation MEKANISM_PURIFICATION_CHAMBER_ID =
            ResourceLocation.fromNamespaceAndPath("mekanism", "purification_chamber");
    private static final ResourceLocation MEKANISM_METALLURGIC_INFUSER_ID =
            ResourceLocation.fromNamespaceAndPath("mekanism", "metallurgic_infuser");
    private static final ResourceLocation MEKANISM_PAINTING_MACHINE_ID =
            ResourceLocation.fromNamespaceAndPath("mekanism", "painting_machine");
    private static final ResourceLocation MEKANISM_PRECISION_SAWMILL_ID =
            ResourceLocation.fromNamespaceAndPath("mekanism", "precision_sawmill");
    private static final ResourceLocation MEKANISM_ENERGIZED_SMELTER_ID =
            ResourceLocation.fromNamespaceAndPath("mekanism", "energized_smelter");
    private static final ResourceLocation MEKANISM_ELECTROLYTIC_SEPARATOR_ID =
            ResourceLocation.fromNamespaceAndPath("mekanism", "electrolytic_separator");
    private static final ResourceLocation MEKANISM_CHEMICAL_WASHER_ID =
            ResourceLocation.fromNamespaceAndPath("mekanism", "chemical_washer");
    private static final ResourceLocation MEKANISM_SOLAR_NEUTRON_ACTIVATOR_ID =
            ResourceLocation.fromNamespaceAndPath("mekanism", "solar_neutron_activator");
    private static final ResourceLocation MEKANISM_CHEMICAL_CRYSTALLIZER_ID =
            ResourceLocation.fromNamespaceAndPath("mekanism", "chemical_crystallizer");
    private static final ResourceLocation MEKANISM_CHEMICAL_DISSOLUTION_CHAMBER_ID =
            ResourceLocation.fromNamespaceAndPath("mekanism", "chemical_dissolution_chamber");
    private static final ResourceLocation MEKANISM_CHEMICAL_OXIDIZER_ID =
            ResourceLocation.fromNamespaceAndPath("mekanism", "chemical_oxidizer");
    private static final ResourceLocation MEKANISM_PIGMENT_EXTRACTOR_ID =
            ResourceLocation.fromNamespaceAndPath("mekanism", "pigment_extractor");
    private static final ResourceLocation MEKANISM_PIGMENT_MIXER_ID =
            ResourceLocation.fromNamespaceAndPath("mekanism", "pigment_mixer");
    private static final ResourceLocation MEKANISM_ROTARY_CONDENSENTRATOR_ID =
            ResourceLocation.fromNamespaceAndPath("mekanism", "rotary_condensentrator");
    private static final ResourceLocation MEKANISM_THERMAL_EVAPORATION_CONTROLLER_ID =
            ResourceLocation.fromNamespaceAndPath("mekanism", "thermal_evaporation_controller");
    private static final ResourceLocation MEKANISM_CHEMICAL_INFUSER_ID =
            ResourceLocation.fromNamespaceAndPath("mekanism", "chemical_infuser");
    private static final ResourceLocation MEKANISM_ANTIPROTONIC_NUCLEOSYNTHESIZER_ID =
            ResourceLocation.fromNamespaceAndPath("mekanism", "antiprotonic_nucleosynthesizer");
    private static final ResourceLocation MEKANISM_PRESSURIZED_REACTION_CHAMBER_ID =
            ResourceLocation.fromNamespaceAndPath("mekanism", "pressurized_reaction_chamber");
    private static final ResourceLocation CREATE_MECHANICAL_MIXER_ID =
            ResourceLocation.fromNamespaceAndPath("create", "mechanical_mixer");
    private static final ResourceLocation CREATE_MECHANICAL_SAW_ID =
            ResourceLocation.fromNamespaceAndPath("create", "mechanical_saw");
    private static final ResourceLocation CREATE_MECHANICAL_PRESS_ID =
            ResourceLocation.fromNamespaceAndPath("create", "mechanical_press");
    private static final ResourceLocation CREATE_DEPLOYER_ID =
            ResourceLocation.fromNamespaceAndPath("create", "deployer");
    private static final ResourceLocation CREATE_SPOUT_ID =
            ResourceLocation.fromNamespaceAndPath("create", "spout");
    private static final ResourceLocation CREATE_MECHANICAL_CRAFTER_ID =
            ResourceLocation.fromNamespaceAndPath("create", "mechanical_crafter");
    private static final ResourceLocation CREATE_MILLSTONE_ID =
            ResourceLocation.fromNamespaceAndPath("create", "millstone");
    private static final ResourceLocation CREATE_CRUSHING_WHEEL_ID =
            ResourceLocation.fromNamespaceAndPath("create", "crushing_wheel");
    private static final ResourceLocation CREATE_ENCASED_FAN_ID =
            ResourceLocation.fromNamespaceAndPath("create", "encased_fan");
    private static final ResourceLocation CREATE_BASIN_ID =
            ResourceLocation.fromNamespaceAndPath("create", "basin");
    private static final ExternalMappings EXTERNAL_MAPPINGS = loadExternalMappings();
    private static final Map<String, ResourceLocation> RECIPE_TYPE_TO_WORKSTATION = createRecipeTypeToWorkstationMap();
    private static final Map<String, String> WORKSTATION_PATH_HINTS = createWorkstationPathHints();
    private static final List<String> EXTENDED_HINT_TOKENS = List.of(
            "extended", "plus", "advanced", "super", "ultimate", "elite", "扩展", "增强", "高级", "超级");
    private static final List<String> NON_WORKSTATION_HINT_TOKENS = List.of(
            "controller", "cable", "cover", "upgrade", "facade", "terminal", "bus", "interface",
            "panel", "part", "hatch", "frame", "wall", "glass", "pipe", "conduit", "wire", "io");
    private static final List<String> WORKSTATION_HINT_TOKENS = List.of(
            "machine", "assembler", "assembly", "station", "processor", "worker", "crafter", "chamber",
            "crusher", "press", "mixer", "saw", "infuser", "reactor", "spout", "deployer", "charger",
            "inscriber", "smelter", "furnace", "stonecutter", "smith", "mill", "milling", "cutting",
            "mixing", "pressing", "reaction", "crafting", "process", "processing",
            "机器", "装配", "工作站", "处理站", "处理器", "压印", "充能", "切石", "锻造", "编译", "合成");

    private PatternEncodingSourceHelper() {
    }

    @Nullable
    public static ResourceLocation resolveWorkstationForTransferRecipe(@Nullable Object recipe) {
        return resolveWorkstationForTransfer(recipe, null);
    }

    @Nullable
    public static ResourceLocation resolveWorkstationForTransfer(@Nullable Object recipe, @Nullable Object transferContext) {
        if (recipe instanceof RecipeHolder<?> holder) {
            ResourceLocation directRecipeWorkstation = resolveWorkstationForRecipe(holder.value());
            if (directRecipeWorkstation != null) {
                return directRecipeWorkstation;
            }
        }
        if (recipe instanceof Recipe<?> vanillaRecipe) {
            ResourceLocation directRecipeWorkstation = resolveWorkstationForRecipe(vanillaRecipe);
            if (directRecipeWorkstation != null) {
                return directRecipeWorkstation;
            }
        }

        ResourceLocation recipeWorkstation = resolveWorkstationFromTransferContext(recipe);
        if (recipeWorkstation != null) {
            return recipeWorkstation;
        }

        ResourceLocation contextWorkstation = resolveWorkstationFromTransferContext(transferContext);
        if (contextWorkstation != null) {
            return contextWorkstation;
        }

        return null;
    }

    @Nullable
    public static ResourceLocation resolveWorkstationForRecipe(@Nullable Recipe<?> recipe) {
        if (recipe == null) {
            return null;
        }

        ResourceLocation recipeTypeId = BuiltInRegistries.RECIPE_TYPE.getKey(recipe.getType());
        if (recipeTypeId == null) {
            return null;
        }

        ResourceLocation mappedId = RECIPE_TYPE_TO_WORKSTATION.get(recipeTypeId.toString());
        if (mappedId != null) {
            return mappedId;
        }

        if (BuiltInRegistries.ITEM.containsKey(recipeTypeId)) {
            return recipeTypeId;
        }

        if (BuiltInRegistries.BLOCK.containsKey(recipeTypeId)) {
            return recipeTypeId;
        }

        ResourceLocation derivedId = resolveDerivedWorkstationId(recipeTypeId, recipe);
        if (derivedId != null) {
            return derivedId;
        }

        return null;
    }

    @Nullable
    public static ResourceLocation resolveFallbackWorkstationForMode(@Nullable EncodingMode mode) {
        if (mode == null) {
            return null;
        }

        return switch (mode) {
            case CRAFTING -> CRAFTING_TABLE_ID;
            case STONECUTTING -> STONECUTTER_ID;
            case SMITHING_TABLE -> SMITHING_TABLE_ID;
            case PROCESSING -> null;
        };
    }

    public static void rememberTransferSource(PatternEncodingTermMenu menu, @Nullable Object recipe) {
        rememberTransferSource(menu, recipe, null);
    }

    public static void rememberTransferSource(PatternEncodingTermMenu menu, @Nullable Object recipe,
                                              @Nullable Object transferContext) {
        if (menu instanceof PatternEncodingSourceAware sourceAware) {
            if (shouldIgnoreWorkstationMemory(sourceAware)) {
                sourceAware.setPendingPatternSource(null);
                sourceAware.setLastEncodedPatternSource(null);
                return;
            }

            ResourceLocation workstationId = resolveWorkstationForTransfer(recipe, transferContext);
            sourceAware.setPendingPatternSource(workstationId);
            if (sourceAware.isPatternSourceEnabled()) {
                sourceAware.setLastEncodedPatternSource(workstationId);
            }
        }
    }

    public static void applyPatternSource(ItemStack stack, PatternEncodingSourceAware sourceAware,
                                          @Nullable ResourceLocation fallbackWorkstationId) {
        if (shouldIgnoreWorkstationMemory(sourceAware)) {
            sourceAware.setLastEncodedPatternSource(null);
            return;
        }

        ResourceLocation workstationId = resolvePreferredWorkstationId(sourceAware);
        if (workstationId == null) {
            workstationId = fallbackWorkstationId;
        }

        sourceAware.setLastEncodedPatternSource(workstationId);
    }

    @Nullable
    public static ResourceLocation resolvePreferredWorkstationId(PatternEncodingSourceAware sourceAware) {
        if (!sourceAware.isPatternSourceEnabled()) {
            return null;
        }

        if (shouldIgnoreWorkstationMemory(sourceAware)) {
            return null;
        }

        ResourceLocation workstationId = sourceAware.getPendingPatternSource();
        if (workstationId != null) {
            return workstationId;
        }

        return sourceAware.getLastEncodedPatternSource();
    }

    private static boolean shouldIgnoreWorkstationMemory(PatternEncodingSourceAware sourceAware) {
        if (!(sourceAware instanceof PatternEncodingPreviewMenu previewMenuHost)) {
            return false;
        }

        EncodingMode mode = previewMenuHost.getEncodingMode();
        return mode == EncodingMode.CRAFTING
                || mode == EncodingMode.STONECUTTING
                || mode == EncodingMode.SMITHING_TABLE;
    }

    @Nullable
    public static ResourceLocation readPendingPatternSource(Player player) {
        CompoundTag tag = getPatternSourceData(player, false);
        if (tag == null) {
            return null;
        }

        String value = tag.getString(TAG_PENDING);
        return value.isEmpty() ? null : ResourceLocation.tryParse(value);
    }

    public static void writePendingPatternSource(Player player, @Nullable ResourceLocation workstationId) {
        CompoundTag tag = getPatternSourceData(player, workstationId != null);
        if (tag == null) {
            return;
        }

        if (workstationId == null) {
            tag.remove(TAG_PENDING);
        } else {
            tag.putString(TAG_PENDING, workstationId.toString());
        }
        cleanupPatternSourceData(player, tag);
    }

    @Nullable
    public static ResourceLocation readLastEncodedPatternSource(Player player) {
        CompoundTag tag = getPatternSourceData(player, false);
        if (tag == null) {
            return null;
        }

        String value = tag.getString(TAG_LAST);
        return value.isEmpty() ? null : ResourceLocation.tryParse(value);
    }

    public static void writeLastEncodedPatternSource(Player player, @Nullable ResourceLocation workstationId) {
        CompoundTag tag = getPatternSourceData(player, workstationId != null);
        if (tag == null) {
            return;
        }

        if (workstationId == null) {
            tag.remove(TAG_LAST);
        } else {
            tag.putString(TAG_LAST, workstationId.toString());
        }
        cleanupPatternSourceData(player, tag);
    }

    public static boolean readPatternSourceEnabled(Player player) {
        CompoundTag tag = getPatternSourceData(player, false);
        return tag == null || !tag.contains(TAG_ENABLED) || tag.getBoolean(TAG_ENABLED);
    }

    public static void writePatternSourceEnabled(Player player, boolean enabled) {
        CompoundTag tag = getPatternSourceData(player, true);
        tag.putBoolean(TAG_ENABLED, enabled);
        if (!enabled) {
            tag.remove(TAG_PENDING);
            tag.remove(TAG_LAST);
        }
        cleanupPatternSourceData(player, tag);
    }

    public static Component resolveWorkstationDisplayName(ResourceLocation workstationId) {
        var item = BuiltInRegistries.ITEM.getOptional(workstationId).orElse(null);
        if (item != null) {
            return item.getDefaultInstance().getHoverName().copy();
        }

        var block = BuiltInRegistries.BLOCK.getOptional(workstationId).orElse(null);
        if (block != null) {
            return block.getName().copy();
        }

        return Component.literal(workstationId.toString());
    }

    @Nullable
    private static ResourceLocation resolveWorkstationFromTransferContext(@Nullable Object context) {
        if (context == null) {
            return null;
        }

        ResourceLocation catalystWorkstation = resolveWorkstationFromCatalysts(context);
        if (catalystWorkstation != null) {
            return catalystWorkstation;
        }

        Object backingRecipe = invokeNoArg(context, "getBackingRecipe");
        if (backingRecipe instanceof RecipeHolder<?> holder) {
            return resolveWorkstationForRecipe(holder.value());
        }
        if (backingRecipe instanceof Recipe<?> recipe) {
            return resolveWorkstationForRecipe(recipe);
        }

        Object category = invokeNoArg(context, "getCategory");
        ResourceLocation categoryId = tryReadResourceLocation(category, "getId");
        if (categoryId != null) {
            ResourceLocation categoryWorkstation = resolveWorkstationFromIdentifier(categoryId, category, context);
            if (categoryWorkstation != null) {
                return categoryWorkstation;
            }
        }

        ResourceLocation directId = tryReadResourceLocation(context, "getId");
        if (directId != null) {
            ResourceLocation directWorkstation = resolveWorkstationFromIdentifier(directId, context, category);
            if (directWorkstation != null) {
                return directWorkstation;
            }
        }

        ResourceLocation titleWorkstation = resolveWorkstationFromTextHints(null, category, context);
        if (titleWorkstation != null) {
            return titleWorkstation;
        }

        return null;
    }

    @Nullable
    private static ResourceLocation resolveWorkstationFromCatalysts(Object context) {
        Object catalysts = invokeNoArg(context, "getCatalysts");
        Collection<?> collection;
        if (catalysts instanceof Collection<?> catalystCollection) {
            collection = catalystCollection;
        } else {
            Object catalystSlots = invokeSlotViewsByRole(context, "CATALYST");
            if (catalystSlots instanceof Collection<?> slotCollection) {
                collection = slotCollection;
            } else {
                return null;
            }
        }

        List<String> hintTexts = collectHintTexts(tryReadResourceLocation(context, "getId"),
                invokeNoArg(context, "getCategory"),
                context,
                invokeNoArg(context, "getBackingRecipe"));
        ResourceLocation bestCandidate = null;
        int bestScore = Integer.MIN_VALUE;
        for (Object catalyst : collection) {
            for (ResourceLocation candidate : collectWorkstationCandidatesFromCatalyst(catalyst)) {
                int score = scoreCatalystCandidate(candidate, hintTexts);
                if (score > bestScore) {
                    bestScore = score;
                    bestCandidate = candidate;
                }
            }
        }

        return bestCandidate;
    }

    private static List<ResourceLocation> collectWorkstationCandidatesFromCatalyst(@Nullable Object catalyst) {
        return collectWorkstationCandidatesFromCatalyst(catalyst,
                Collections.newSetFromMap(new IdentityHashMap<>()));
    }

    private static List<ResourceLocation> collectWorkstationCandidatesFromCatalyst(@Nullable Object catalyst,
                                                                                   Set<Object> visited) {
        List<ResourceLocation> candidates = new ArrayList<>();
        if (catalyst == null) {
            return candidates;
        }
        if (!visited.add(catalyst)) {
            return candidates;
        }

        if (catalyst instanceof ItemStack stack && !stack.isEmpty()) {
            appendWorkstationCandidate(candidates, BuiltInRegistries.ITEM.getKey(stack.getItem()));
            return candidates;
        }

        Object displayedItemStack = invokeNoArg(catalyst, "getDisplayedItemStack");
        if (displayedItemStack instanceof java.util.Optional<?> optional && optional.orElse(null) instanceof ItemStack stack
                && !stack.isEmpty()) {
            appendWorkstationCandidate(candidates, BuiltInRegistries.ITEM.getKey(stack.getItem()));
        }

        Object itemStacks = invokeNoArg(catalyst, "getItemStacks");
        if (itemStacks instanceof java.util.stream.Stream<?> stream) {
            try (stream) {
                stream.limit(8).forEach(entry -> {
                    if (entry instanceof ItemStack stack && !stack.isEmpty()) {
                        appendWorkstationCandidate(candidates, BuiltInRegistries.ITEM.getKey(stack.getItem()));
                    }
                });
            }
        }

        Object itemStack = invokeNoArg(catalyst, "getItemStack");
        if (itemStack instanceof ItemStack stack && !stack.isEmpty()) {
            appendWorkstationCandidate(candidates, BuiltInRegistries.ITEM.getKey(stack.getItem()));
        }

        Object emiStacks = invokeNoArg(catalyst, "getEmiStacks");
        if (emiStacks instanceof Collection<?> collection) {
            for (Object emiStack : collection) {
                for (ResourceLocation candidate : collectWorkstationCandidatesFromCatalyst(emiStack, visited)) {
                    appendWorkstationCandidate(candidates, candidate);
                }
            }
        }

        ResourceLocation id = tryReadResourceLocation(catalyst, "getId");
        if (id != null) {
            appendWorkstationCandidate(candidates, resolveWorkstationFromIdentifier(id, catalyst));
        }

        return candidates;
    }

    @Nullable
    private static ResourceLocation resolveWorkstationFromIdentifier(@Nullable ResourceLocation id, Object... hintSources) {
        if (id == null) {
            return null;
        }

        ResourceLocation mappedId = RECIPE_TYPE_TO_WORKSTATION.get(id.toString());
        if (mappedId != null) {
            return mappedId;
        }

        if (BuiltInRegistries.ITEM.containsKey(id)) {
            return id;
        }

        if (BuiltInRegistries.BLOCK.containsKey(id)) {
            return id;
        }

        ResourceLocation derivedId = resolveDerivedWorkstationId(id, hintSources);
        if (derivedId != null) {
            return derivedId;
        }

        return null;
    }

    @Nullable
    private static ResourceLocation resolveDerivedWorkstationId(ResourceLocation id, Object... hintSources) {
        String path = id.getPath();
        String namespace = id.getNamespace();
        if (path.isEmpty()) {
            return null;
        }

        ResourceLocation directCandidate = tryResolveWorkstationCandidate(namespace, path);
        if (directCandidate != null) {
            return directCandidate;
        }

        for (var entry : WORKSTATION_PATH_HINTS.entrySet()) {
            if (!path.contains(entry.getKey())) {
                continue;
            }

            ResourceLocation candidate = tryResolveWorkstationCandidate(namespace, entry.getValue());
            if (candidate != null) {
                return candidate;
            }

            for (String aliasNamespace : getAliasedNamespaces(namespace)) {
                candidate = tryResolveWorkstationCandidate(aliasNamespace, entry.getValue());
                if (candidate != null) {
                    return candidate;
                }
            }
        }

        return resolveWorkstationFromTextHints(id, hintSources);
    }

    @Nullable
    private static ResourceLocation tryResolveWorkstationCandidate(String namespace, String path) {
        ResourceLocation candidate = ResourceLocation.fromNamespaceAndPath(namespace, path);
        if (BuiltInRegistries.ITEM.containsKey(candidate) || BuiltInRegistries.BLOCK.containsKey(candidate)) {
            return candidate;
        }
        return null;
    }

    @Nullable
    private static ResourceLocation resolveWorkstationFromTextHints(@Nullable ResourceLocation baseId, Object... hintSources) {
        List<String> hints = collectHintTexts(baseId, hintSources);
        if (hints.isEmpty()) {
            return null;
        }

        List<String> namespaces = collectCandidateNamespaces(baseId);
        ResourceLocation bestId = null;
        int bestScore = 0;

        for (String namespace : namespaces) {
            for (ResourceLocation candidateId : BuiltInRegistries.BLOCK.keySet()) {
                if (!namespace.equals(candidateId.getNamespace())) {
                    continue;
                }

                int score = scoreCandidate(candidateId, hints, true);
                if (score > bestScore) {
                    bestScore = score;
                    bestId = candidateId;
                }
            }

            for (ResourceLocation candidateId : BuiltInRegistries.ITEM.keySet()) {
                if (!namespace.equals(candidateId.getNamespace())) {
                    continue;
                }

                int score = scoreCandidate(candidateId, hints, false);
                if (score > bestScore) {
                    bestScore = score;
                    bestId = candidateId;
                }
            }
        }

        return bestScore >= 60 ? bestId : null;
    }

    private static List<String> collectHintTexts(@Nullable ResourceLocation baseId, Object... hintSources) {
        List<String> hints = new ArrayList<>();
        if (baseId != null) {
            hints.add(baseId.getPath());
            hints.add(baseId.toString());
        }

        for (Object hintSource : hintSources) {
            appendHintText(hints, hintSource);
            appendHintText(hints, invokeNoArg(hintSource, "getTitle"));
            appendHintText(hints, invokeNoArg(hintSource, "getName"));
            appendHintText(hints, invokeNoArg(hintSource, "getTooltip"));
        }

        hints.removeIf(String::isBlank);
        return hints;
    }

    private static void appendWorkstationCandidate(List<ResourceLocation> candidates, @Nullable ResourceLocation candidate) {
        if (candidate != null && !candidates.contains(candidate)) {
            candidates.add(candidate);
        }
    }

    private static void appendHintText(List<String> hints, @Nullable Object value) {
        if (value == null) {
            return;
        }
        if (value instanceof Component component) {
            hints.add(component.getString());
            return;
        }
        if (value instanceof CharSequence sequence) {
            hints.add(sequence.toString());
            return;
        }
        if (value instanceof ResourceLocation id) {
            hints.add(id.getPath());
            hints.add(id.toString());
        }
    }

    private static List<String> collectCandidateNamespaces(@Nullable ResourceLocation baseId) {
        List<String> namespaces = new ArrayList<>();
        if (baseId != null) {
            namespaces.add(baseId.getNamespace());
            for (String aliasNamespace : getAliasedNamespaces(baseId.getNamespace())) {
                if (!namespaces.contains(aliasNamespace)) {
                    namespaces.add(aliasNamespace);
                }
            }
        }
        return namespaces;
    }

    private static List<String> getAliasedNamespaces(String namespace) {
        return EXTERNAL_MAPPINGS.namespaceAliases().getOrDefault(namespace, List.of());
    }

    private static int scoreCandidate(ResourceLocation candidateId, List<String> hints, boolean blockCandidate) {
        String candidatePath = normalizeHintText(candidateId.getPath());
        if (candidatePath.isEmpty()) {
            return 0;
        }

        List<String> candidateTexts = collectCandidateTexts(candidateId, blockCandidate);
        int score = blockCandidate ? 6 : 0;
        for (String hint : hints) {
            String normalizedHint = normalizeHintText(hint);
            if (normalizedHint.isEmpty()) {
                continue;
            }

            if (normalizedHint.equals(candidatePath) || normalizedHint.endsWith(candidatePath)) {
                score += 120;
            }
            if (candidatePath.endsWith(normalizedHint) && normalizedHint.length() >= 4) {
                score += 65;
            }

            for (String candidateText : candidateTexts) {
                if (candidateText.isEmpty()) {
                    continue;
                }
                if (normalizedHint.equals(candidateText) || normalizedHint.endsWith(candidateText)) {
                    score += 100;
                }
                if (candidateText.endsWith(normalizedHint) && normalizedHint.length() >= 4) {
                    score += 55;
                }
            }

            for (String token : tokenize(normalizedHint)) {
                if (token.length() < 3) {
                    continue;
                }

                String expandedToken = expandToken(token);
                if (expandedToken.equals(candidatePath)) {
                    score += 100;
                    continue;
                }
                if (candidatePath.contains(expandedToken)) {
                    score += 45;
                    continue;
                }
                if (expandedToken.contains(candidatePath)) {
                    score += 30;
                }

                for (String candidateText : candidateTexts) {
                    if (candidateText.equals(expandedToken)) {
                        score += 90;
                        break;
                    }
                    if (candidateText.contains(expandedToken)) {
                        score += 36;
                        break;
                    }
                    if (expandedToken.contains(candidateText) && candidateText.length() >= 3) {
                        score += 24;
                        break;
                    }
                }
            }
        }
        return score;
    }

    private static int scoreCatalystCandidate(ResourceLocation candidateId, List<String> hints) {
        int score = 0;
        if (candidateId == null) {
            return Integer.MIN_VALUE;
        }

        boolean isBlock = BuiltInRegistries.BLOCK.containsKey(candidateId);
        boolean isItem = BuiltInRegistries.ITEM.containsKey(candidateId);
        if (isBlock) {
            score += 40;
        } else if (isItem) {
            score += 10;
        }

        score += scoreCandidate(candidateId, hints, isBlock);

        String normalizedPath = normalizeHintText(candidateId.getPath());
        if (containsAny(normalizedPath, NON_WORKSTATION_HINT_TOKENS)) {
            score -= 55;
        }

        if (containsAny(normalizedPath, WORKSTATION_HINT_TOKENS)) {
            score += 24;
        }

        List<String> candidateTexts = collectCandidateTexts(candidateId, isBlock);
        if (candidateTexts.stream().anyMatch(text -> containsAny(text, WORKSTATION_HINT_TOKENS))) {
            score += 18;
        }
        if (candidateTexts.stream().anyMatch(text -> containsAny(text, NON_WORKSTATION_HINT_TOKENS))) {
            score -= 32;
        }

        boolean hintWantsExtended = hints.stream()
                .map(PatternEncodingSourceHelper::normalizeHintText)
                .anyMatch(text -> containsAny(text, EXTENDED_HINT_TOKENS));
        boolean candidateIsExtended = containsAny(normalizedPath, EXTENDED_HINT_TOKENS)
                || candidateTexts.stream().anyMatch(text -> containsAny(text, EXTENDED_HINT_TOKENS));
        if (hintWantsExtended == candidateIsExtended) {
            score += 22;
        } else if (hintWantsExtended) {
            score -= 45;
        } else if (candidateIsExtended) {
            score -= 12;
        }

        return score;
    }

    private static List<String> tokenize(String text) {
        List<String> tokens = new ArrayList<>();
        for (String token : text.split("[^\\p{IsAlphabetic}\\p{IsDigit}\\p{IsIdeographic}]+")) {
            if (!token.isBlank()) {
                tokens.add(token);
            }
        }
        return tokens;
    }

    private static String expandToken(String token) {
        String normalized = normalizeHintText(token);
        for (var entry : WORKSTATION_PATH_HINTS.entrySet()) {
            if (normalized.contains(entry.getKey())) {
                return entry.getValue();
            }
        }
        return normalized;
    }

    private static String normalizeHintText(String text) {
        StringBuilder normalized = new StringBuilder(text.length());
        text.codePoints()
                .map(Character::toLowerCase)
                .filter(codePoint -> Character.isLetterOrDigit(codePoint)
                        || Character.UnicodeScript.of(codePoint) == Character.UnicodeScript.HAN)
                .forEach(normalized::appendCodePoint);
        return normalized.toString();
    }

    private static List<String> collectCandidateTexts(ResourceLocation candidateId, boolean blockCandidate) {
        List<String> texts = new ArrayList<>();
        texts.add(normalizeHintText(candidateId.getPath()));
        texts.add(normalizeHintText(candidateId.toString()));

        if (blockCandidate) {
            BuiltInRegistries.BLOCK.getOptional(candidateId)
                    .ifPresent(block -> appendNormalizedCandidateText(texts, block.getName().getString()));
        }

        BuiltInRegistries.ITEM.getOptional(candidateId)
                .ifPresent(item -> appendNormalizedCandidateText(texts, item.getDefaultInstance().getHoverName().getString()));

        texts.removeIf(String::isBlank);
        return texts;
    }

    private static void appendNormalizedCandidateText(List<String> texts, @Nullable String text) {
        if (text == null || text.isBlank()) {
            return;
        }

        String normalized = normalizeHintText(text);
        if (!normalized.isBlank() && !texts.contains(normalized)) {
            texts.add(normalized);
        }
    }

    private static boolean containsAny(String text, List<String> tokens) {
        if (text == null || text.isBlank()) {
            return false;
        }

        String normalized = normalizeHintText(text);
        for (String token : tokens) {
            String normalizedToken = normalizeHintText(token);
            if (!normalizedToken.isBlank() && normalized.contains(normalizedToken)) {
                return true;
            }
        }
        return false;
    }

    private static Map<String, String> createWorkstationPathHints() {
        Map<String, String> hints = new LinkedHashMap<>();
        hints.put("metallurgic_infus", "metallurgic_infuser");
        hints.put("chemical_infus", "chemical_infuser");
        hints.put("nucleosynth", "antiprotonic_nucleosynthesizer");
        hints.put("reaction", "pressurized_reaction_chamber");
        hints.put("purif", "purification_chamber");
        hints.put("inject", "chemical_injection_chamber");
        hints.put("enrich", "enrichment_chamber");
        hints.put("compress", "osmium_compressor");
        hints.put("paint", "painting_machine");
        hints.put("combin", "combiner");
        hints.put("crush", "crusher");
        hints.put("saw", "precision_sawmill");
        hints.put("mix", "mechanical_mixer");
        hints.put("cut", "mechanical_saw");
        hints.put("press", "mechanical_press");
        hints.put("deploy", "deployer");
        hints.put("fill", "spout");
        hints.put("empty", "spout");
        hints.put("mechanical_craft", "mechanical_crafter");
        hints.put("sequenced", "mechanical_crafter");
        hints.put("mill", "millstone");
        hints.put("grind", "millstone");
        hints.put("mixer", "mechanical_mixer");
        hints.put("milling", "millstone");
        hints.put("cutting", "mechanical_saw");
        hints.put("pressing", "mechanical_press");
        hints.put("deploying", "deployer");
        hints.put("filling", "spout");
        hints.put("emptying", "spout");
        hints.put("combining", "combiner");
        hints.put("compressing", "osmium_compressor");
        hints.put("crushing", "crusher");
        hints.put("enriching", "enrichment_chamber");
        hints.put("injecting", "chemical_injection_chamber");
        hints.put("purifying", "purification_chamber");
        hints.put("painting", "painting_machine");
        hints.put("sawing", "precision_sawmill");
        hints.put("smelt", "energized_smelter");
        hints.put("smelting", "energized_smelter");
        hints.put("energized", "energized_smelter");
        hints.put("separating", "electrolytic_separator");
        hints.put("separator", "electrolytic_separator");
        hints.put("electrolytic", "electrolytic_separator");
        hints.put("washing", "chemical_washer");
        hints.put("washer", "chemical_washer");
        hints.put("activating", "solar_neutron_activator");
        hints.put("activator", "solar_neutron_activator");
        hints.put("solar", "solar_neutron_activator");
        hints.put("neutron", "solar_neutron_activator");
        hints.put("crystallizing", "chemical_crystallizer");
        hints.put("crystallizer", "chemical_crystallizer");
        hints.put("dissolving", "chemical_dissolution_chamber");
        hints.put("dissolution", "chemical_dissolution_chamber");
        hints.put("oxidizing", "chemical_oxidizer");
        hints.put("oxidizer", "chemical_oxidizer");
        hints.put("pigmentextract", "pigment_extractor");
        hints.put("pigmentmix", "pigment_mixer");
        hints.put("rotary", "rotary_condensentrator");
        hints.put("condensentrator", "rotary_condensentrator");
        hints.put("evaporating", "thermal_evaporation_controller");
        hints.put("evaporation", "thermal_evaporation_controller");
        hints.put("nucleosynthesizing", "antiprotonic_nucleosynthesizer");
        hints.put("splash", "encased_fan");
        hints.put("haunt", "encased_fan");
        hints.putAll(EXTERNAL_MAPPINGS.pathHints());
        return hints;
    }

    private static Map<String, ResourceLocation> createRecipeTypeToWorkstationMap() {
        Map<String, ResourceLocation> mappings = new LinkedHashMap<>();
        mappings.put("minecraft:crafting", CRAFTING_TABLE_ID);
        mappings.put("minecraft:smelting", FURNACE_ID);
        mappings.put("minecraft:blasting", BLAST_FURNACE_ID);
        mappings.put("minecraft:smoking", SMOKER_ID);
        mappings.put("minecraft:campfire_cooking", CAMPFIRE_ID);
        mappings.put("minecraft:stonecutting", STONECUTTER_ID);
        mappings.put("minecraft:smithing", SMITHING_TABLE_ID);
        mappings.put("ae2:inscriber", AE2_INSCRIBER_ID);
        mappings.put("ae2:charger", AE2_CHARGER_ID);
        mappings.put("extendedae:crystal_assembler", EXTENDEDAE_CRYSTAL_ASSEMBLER_ID);
        mappings.put("mekanism:combining", MEKANISM_COMBINER_ID);
        mappings.put("mekanism:compressing", MEKANISM_OSMIUM_COMPRESSOR_ID);
        mappings.put("mekanism:crushing", MEKANISM_CRUSHER_ID);
        mappings.put("mekanism:enriching", MEKANISM_ENRICHMENT_CHAMBER_ID);
        mappings.put("mekanism:injecting", MEKANISM_CHEMICAL_INJECTION_CHAMBER_ID);
        mappings.put("mekanism:purifying", MEKANISM_PURIFICATION_CHAMBER_ID);
        mappings.put("mekanism:metallurgic_infusing", MEKANISM_METALLURGIC_INFUSER_ID);
        mappings.put("mekanism:painting", MEKANISM_PAINTING_MACHINE_ID);
        mappings.put("mekanism:sawing", MEKANISM_PRECISION_SAWMILL_ID);
        mappings.put("mekanism:smelting", MEKANISM_ENERGIZED_SMELTER_ID);
        mappings.put("mekanism:separating", MEKANISM_ELECTROLYTIC_SEPARATOR_ID);
        mappings.put("mekanism:washing", MEKANISM_CHEMICAL_WASHER_ID);
        mappings.put("mekanism:activating", MEKANISM_SOLAR_NEUTRON_ACTIVATOR_ID);
        mappings.put("mekanism:crystallizing", MEKANISM_CHEMICAL_CRYSTALLIZER_ID);
        mappings.put("mekanism:dissolving", MEKANISM_CHEMICAL_DISSOLUTION_CHAMBER_ID);
        mappings.put("mekanism:oxidizing", MEKANISM_CHEMICAL_OXIDIZER_ID);
        mappings.put("mekanism:pigment_extracting", MEKANISM_PIGMENT_EXTRACTOR_ID);
        mappings.put("mekanism:pigment_mixing", MEKANISM_PIGMENT_MIXER_ID);
        mappings.put("mekanism:rotary", MEKANISM_ROTARY_CONDENSENTRATOR_ID);
        mappings.put("mekanism:evaporating", MEKANISM_THERMAL_EVAPORATION_CONTROLLER_ID);
        mappings.put("mekanism:chemical_infusing", MEKANISM_CHEMICAL_INFUSER_ID);
        mappings.put("mekanism:nucleosynthesizing", MEKANISM_ANTIPROTONIC_NUCLEOSYNTHESIZER_ID);
        mappings.put("mekanism:reaction", MEKANISM_PRESSURIZED_REACTION_CHAMBER_ID);
        mappings.put("create:mixing", CREATE_MECHANICAL_MIXER_ID);
        mappings.put("create:compacting", CREATE_BASIN_ID);
        mappings.put("create:cutting", CREATE_MECHANICAL_SAW_ID);
        mappings.put("create:pressing", CREATE_MECHANICAL_PRESS_ID);
        mappings.put("create:deploying", CREATE_DEPLOYER_ID);
        mappings.put("create:filling", CREATE_SPOUT_ID);
        mappings.put("create:emptying", CREATE_SPOUT_ID);
        mappings.put("create:mechanical_crafting", CREATE_MECHANICAL_CRAFTER_ID);
        mappings.put("create:sequenced_assembly", CREATE_MECHANICAL_CRAFTER_ID);
        mappings.put("create:milling", CREATE_MILLSTONE_ID);
        mappings.put("create:crushing", CREATE_CRUSHING_WHEEL_ID);
        mappings.put("create:splashing", CREATE_ENCASED_FAN_ID);
        mappings.put("create:haunting", CREATE_ENCASED_FAN_ID);
        mappings.putAll(EXTERNAL_MAPPINGS.identifierToWorkstation());
        return mappings;
    }

    private static ExternalMappings loadExternalMappings() {
        try (InputStream stream = PatternEncodingSourceHelper.class.getClassLoader()
                .getResourceAsStream(WORKSTATION_MAPPINGS_RESOURCE)) {
            if (stream == null) {
                return ExternalMappings.EMPTY;
            }

            try (Reader reader = new InputStreamReader(stream, StandardCharsets.UTF_8)) {
                JsonElement parsed = JsonParser.parseReader(reader);
                if (!parsed.isJsonObject()) {
                    return ExternalMappings.EMPTY;
                }

                JsonObject root = parsed.getAsJsonObject();
                return new ExternalMappings(
                        parseResourceLocationMap(root.getAsJsonObject("identifier_to_workstation")),
                        parseStringMap(root.getAsJsonObject("path_hints")),
                        parseStringListMap(root.getAsJsonObject("namespace_aliases")));
            }
        } catch (Exception ignored) {
            return ExternalMappings.EMPTY;
        }
    }

    private static Map<String, ResourceLocation> parseResourceLocationMap(@Nullable JsonObject object) {
        Map<String, ResourceLocation> mappings = new LinkedHashMap<>();
        if (object == null) {
            return mappings;
        }

        for (var entry : object.entrySet()) {
            if (!entry.getValue().isJsonPrimitive()) {
                continue;
            }

            ResourceLocation workstationId = ResourceLocation.tryParse(entry.getValue().getAsString());
            if (workstationId != null) {
                mappings.put(entry.getKey(), workstationId);
            }
        }
        return mappings;
    }

    private static Map<String, String> parseStringMap(@Nullable JsonObject object) {
        Map<String, String> mappings = new LinkedHashMap<>();
        if (object == null) {
            return mappings;
        }

        for (var entry : object.entrySet()) {
            if (!entry.getValue().isJsonPrimitive()) {
                continue;
            }
            mappings.put(entry.getKey(), entry.getValue().getAsString());
        }
        return mappings;
    }

    private static Map<String, List<String>> parseStringListMap(@Nullable JsonObject object) {
        Map<String, List<String>> mappings = new LinkedHashMap<>();
        if (object == null) {
            return mappings;
        }

        for (var entry : object.entrySet()) {
            List<String> values = new ArrayList<>();
            if (entry.getValue().isJsonPrimitive()) {
                values.add(entry.getValue().getAsString());
            } else if (entry.getValue().isJsonArray()) {
                for (JsonElement element : entry.getValue().getAsJsonArray()) {
                    if (element.isJsonPrimitive()) {
                        values.add(element.getAsString());
                    }
                }
            }

            values.removeIf(String::isBlank);
            if (!values.isEmpty()) {
                mappings.put(entry.getKey(), List.copyOf(values));
            }
        }
        return mappings;
    }

    @Nullable
    private static CompoundTag getPatternSourceData(Player player, boolean create) {
        CompoundTag persistentData = player.getPersistentData();
        if (!persistentData.contains(Player.PERSISTED_NBT_TAG, CompoundTag.TAG_COMPOUND)) {
            if (!create) {
                return null;
            }
            persistentData.put(Player.PERSISTED_NBT_TAG, new CompoundTag());
        }

        CompoundTag persisted = persistentData.getCompound(Player.PERSISTED_NBT_TAG);
        if (!persisted.contains(PLAYER_PATTERN_SOURCE_ROOT, CompoundTag.TAG_COMPOUND)) {
            if (!create) {
                return null;
            }
            persisted.put(PLAYER_PATTERN_SOURCE_ROOT, new CompoundTag());
        }

        return persisted.getCompound(PLAYER_PATTERN_SOURCE_ROOT);
    }

    private static void cleanupPatternSourceData(Player player, CompoundTag tag) {
        CompoundTag persistentData = player.getPersistentData();
        CompoundTag persisted = persistentData.getCompound(Player.PERSISTED_NBT_TAG);
        if (tag.isEmpty()) {
            persisted.remove(PLAYER_PATTERN_SOURCE_ROOT);
        }
        if (persisted.isEmpty()) {
            persistentData.remove(Player.PERSISTED_NBT_TAG);
        }
    }

    @Nullable
    private static ResourceLocation tryReadResourceLocation(@Nullable Object target, String methodName) {
        Object value = invokeNoArg(target, methodName);
        return value instanceof ResourceLocation id ? id : null;
    }

    @Nullable
    private static Object invokeSlotViewsByRole(@Nullable Object target, String roleName) {
        if (target == null) {
            return null;
        }

        Class<?> type = target.getClass();
        while (type != null) {
            for (Method method : type.getDeclaredMethods()) {
                if (!method.getName().equals("getSlotViews") || method.getParameterCount() != 1) {
                    continue;
                }

                Class<?> parameterType = method.getParameterTypes()[0];
                if (!parameterType.isEnum()) {
                    continue;
                }

                try {
                    @SuppressWarnings({"rawtypes", "unchecked"})
                    Object enumValue = Enum.valueOf((Class<? extends Enum>) parameterType.asSubclass(Enum.class), roleName);
                    method.setAccessible(true);
                    return method.invoke(target, enumValue);
                } catch (ReflectiveOperationException | IllegalArgumentException ignored) {
                    return null;
                }
            }
            type = type.getSuperclass();
        }
        return null;
    }

    @Nullable
    private static Object invokeNoArg(@Nullable Object target, String methodName) {
        if (target == null) {
            return null;
        }

        Class<?> type = target.getClass();
        while (type != null) {
            try {
                Method method = type.getDeclaredMethod(methodName);
                method.setAccessible(true);
                return method.invoke(target);
            } catch (NoSuchMethodException ignored) {
                type = type.getSuperclass();
            } catch (ReflectiveOperationException ignored) {
                return null;
            }
        }
        return null;
    }

    private record ExternalMappings(Map<String, ResourceLocation> identifierToWorkstation,
                                    Map<String, String> pathHints,
                                    Map<String, List<String>> namespaceAliases) {
        private static final ExternalMappings EMPTY = new ExternalMappings(Map.of(), Map.of(), Map.of());
    }
}

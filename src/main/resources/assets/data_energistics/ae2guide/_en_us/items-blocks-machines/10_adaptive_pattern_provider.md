---
navigation:
  parent: data_energistics:items-blocks-machines/data_energistics.md
  title: Adaptive Pattern Provider
  icon: data_energistics:adaptive_pattern_provider
  position: 10
item_ids:
- data_energistics:adaptive_pattern_provider
- data_energistics:adaptive_pattern_provider_part
---

# Adaptive

## Adaptive Pattern Provider
<GameScene zoom="6" background="transparent">
    <Block id="data_energistics:adaptive_pattern_provider" x="0" y="0" z="0" />
   <IsometricCamera yaw="25" pitch="25" />
</GameScene>

<Row>
  <RecipeFor id="data_energistics:adaptive_pattern_provider" />
</Row>
Reject, parse, adapt, and merge. It executes the target machine by inheriting its behavior.  
Place the matching pattern provider into the GUI and it will inherit that provider’s properties.

---

<Row>
    <ItemImage id="data_energistics:adaptive_pattern_provider_part" scale="6" />
  <RecipeFor id="data_energistics:adaptive_pattern_provider_part" />
</Row>
Its part form.

---

# Upgrades
<Row>
    <ItemLink id="ae2:capacity_card" />
    <ItemImage id="ae2:capacity_card" />
</Row>
Capacity Card:  
base capacity + number of Capacity Cards × 4

<Row>
    <ItemLink id="data_energistics:redstone_tuning_card" />
    <ItemImage id="data_energistics:redstone_tuning_card" />
</Row>
Redstone Tuning Card:  
After installation it can either emit a redstone pulse or react to one.  
Emit redstone pulse: exactly what it sounds like, sends one redstone pulse. When used with some very fast providers this can undercount pulses. For pattern processing, enabling item-return blocking mode may be required for correct pulse output.  
Receive redstone pulse: when a pulse is received, the provider automatically requests one primary output for every craftable pattern inside. Very fast pulses may behave unpredictably.

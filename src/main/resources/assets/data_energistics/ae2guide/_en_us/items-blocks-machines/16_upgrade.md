---
navigation:
  parent: data_energistics:items-blocks-machines/data_energistics.md
  title: Upgrades
  icon: ae2:advanced_card
  position: 16
item_ids:
- data_energistics:card_saber_energy
- data_energistics:redstone_tuning_card
---

# Upgrades

## Saber Energy Card
<Row>
  <ItemImage id="data_energistics:card_saber_energy" scale="6" />
  <RecipeFor id="data_energistics:card_saber_energy" />
</Row>

An offensive upgrade card mainly used by weapons and several Data Crystal tools.

Supported targets:
<Row>
  <ItemImage id="data_energistics:matter_converging_crossbow" />
  <ItemImage id="data_energistics:data_light_saber" />
  <ItemImage id="data_energistics:data_sanctifier" />
  <ItemImage id="data_energistics:data_crystal_cutting_knife" />
  <ItemImage id="data_energistics:data_crystal_sword" />
  <ItemImage id="data_energistics:data_crystal_axe" />
  <ItemImage id="data_energistics:data_crystal_pickaxe" />
  <ItemImage id="data_energistics:data_crystal_hoe" />
  <ItemImage id="data_energistics:data_crystal_shovel" />
</Row>

Effects:  
<ItemImage id="data_energistics:matter_converging_crossbow" />: final damage = base damage × (number of Saber Energy Cards × 2) × current speed (3.15), then × 1.5 on critical hit. When a fully charged <ItemImage id="data_energistics:data_light_saber" components="ae2:stored_energy=20000.0d" /> is used as ammunition, the maximum true damage percentage is increased further.  
<ItemImage id="data_energistics:data_light_saber" />: increases the damage ceiling, increases energy cost, and allows fully charged left-click light blade attacks.  
<ItemImage id="data_energistics:data_sanctifier" />: belongs to the same empowered branch as the Data Light Saber.  
<ItemImage id="data_energistics:data_crystal_cutting_knife" />: increases teleport range and stored Data Flow capacity.  
<ItemImage id="data_energistics:data_crystal_sword" />: attacks can consume Data Flow to temporarily strip target AI.  
<ItemImage id="data_energistics:data_crystal_axe" />: can consume Data Flow to chain a whole tree.  
<ItemImage id="data_energistics:data_crystal_pickaxe" />: can consume Data Flow to chain nearby ores and duplicate one extra drop.  
<ItemImage id="data_energistics:data_crystal_hoe" />: can consume Data Flow to keep farmland hydrated.  
<ItemImage id="data_energistics:data_crystal_shovel" />: can switch area breaking and consumes Data Flow when breaking.

---

## Redstone Tuning Card
<Row>
  <ItemImage id="data_energistics:redstone_tuning_card" scale="6" />
  <RecipeFor id="data_energistics:redstone_tuning_card" />
</Row>

Used to convert part of a device’s behavior into redstone-driven behavior.

Supported targets:
<Row>
  <ItemImage id="data_energistics:adaptive_pattern_provider" />
  <ItemImage id="data_energistics:matter_converging_crossbow" />
</Row>

Effects:  
<ItemImage id="data_energistics:adaptive_pattern_provider" />:  
(Effectively all pattern providers in this branch.) When emitting a redstone pulse, it actively sends one pulse; when receiving a redstone pulse, it requests one primary output from every craftable pattern inside.  
<ItemImage id="data_energistics:matter_converging_crossbow" />:  
Normal ammunition now costs base energy × 5 per shot, and the projectile gains homing toward the nearest nearby non-player living target.

---
navigation:
  parent: data_energistics:items-blocks-machines/data_energistics.md
  title: Tools / Weapons
  icon: data_energistics:data_sanctifier
  position: 13
item_ids:
- data_energistics:data_crystal_sword
- data_energistics:data_crystal_axe
- data_energistics:data_crystal_pickaxe
- data_energistics:data_crystal_hoe
- data_energistics:data_crystal_shovel
- data_energistics:data_crystal_cutting_knife

- data_energistics:data_light_saber
- data_energistics:data_sanctifier

- data_energistics:matter_converging_crossbow
---

# Tools / Weapons
## Data Crystal Tools
Basic tools made with a special process. They combine Netherite-like toughness with the lightness of gold, and the process also lets them store a small amount of energy.

<Row>
  <ItemImage id="data_energistics:data_crystal_sword" scale="4" />
  <ItemImage id="data_energistics:data_crystal_axe" scale="4" />
  <ItemImage id="data_energistics:data_crystal_pickaxe" scale="4" />
  <ItemImage id="data_energistics:data_crystal_hoe" scale="4" />
  <ItemImage id="data_energistics:data_crystal_shovel" scale="4" />
  <ItemImage id="data_energistics:data_crystal_cutting_knife" scale="4" />
</Row>

| Name | Stored Power | Attack Speed | Attack Damage | Harvest Tier / Speed | Supported Upgrades |
|---|---:|---:|---:|---:|---|
| <ItemImage id="data_energistics:data_crystal_sword" /> Data Crystal Sword | 20k AE | 2 | 10 | None | <ItemLink id="ae2:speed_card"/>: +0.2 attack speed, +100 AE cost / <ItemLink id="ae2:energy_card"/>: max energy = base capacity × (1 + 8 × number of Energy Cards) |
| <ItemImage id="data_energistics:data_crystal_axe" /> Data Crystal Axe | 20k AE | 1.2 | 12 | 5 / 12 | <ItemLink id="ae2:speed_card"/>: +0.2 attack/mining speed, higher power cost / <ItemLink id="ae2:energy_card"/>: max energy = base capacity × (1 + 8 × number of Energy Cards) |
| <ItemImage id="data_energistics:data_crystal_pickaxe" /> Data Crystal Pickaxe | 20k AE | 1.4 | 6 | 5 / 12 | <ItemLink id="ae2:speed_card"/>: +0.2 attack/mining speed, higher power cost / <ItemLink id="ae2:energy_card"/>: max energy = base capacity × (1 + 8 × number of Energy Cards) |
| <ItemImage id="data_energistics:data_crystal_hoe" /> Data Crystal Hoe | 20k AE | 1.4 | 6 | 5 / 12 | <ItemLink id="ae2:speed_card"/>: +0.2 attack/mining speed, higher power cost / <ItemLink id="ae2:energy_card"/>: max energy = base capacity × (1 + 8 × number of Energy Cards) |
| <ItemImage id="data_energistics:data_crystal_shovel" /> Data Crystal Shovel | 20k AE | 1.4 | 6 | 5 / 12 | <ItemLink id="ae2:speed_card"/>: +0.2 attack/mining speed, higher power cost / <ItemLink id="ae2:energy_card"/>: max energy = base capacity × (1 + 8 × number of Energy Cards) |
| <ItemImage id="data_energistics:data_crystal_cutting_knife" /> Data Crystal Cutting Knife | 20k AE | None | Sneak right-click can rename AE devices when EAE support is present | Replaced by its special Data Flow and teleport features | <ItemLink id="ae2:energy_card"/>: max energy = base capacity × (1 + 8 × number of Energy Cards) |

---

## Data Light Saber
A fun but dangerous little “toy.” Want to cosplay as a Jedi?

<Row>
    <ItemImage id="data_energistics:data_light_saber" scale="4" />
    <ItemImage id="data_energistics:data_light_saber" components="ae2:stored_energy=20000.0d" scale="4" />
</Row>
Stores 20k AE, has attack speed 2 and attack damage 17. After a short right-click charge it can be thrown. In the inventory, right-click it with dye or a color applicator to recolor it.

# Data Sanctifier
A special light saber crafted from the red, yellow, and blue sabers.

<Row>
    <ItemImage id="data_energistics:data_sanctifier" scale="4" />
    <ItemImage id="data_energistics:data_sanctifier" components="ae2:stored_energy=20000.0d" scale="4" />
</Row>
Stores 20k AE, deals 36 attack damage, and can be thrown after a short right-click charge.

---

# Matter Converging Crossbow
A crossbow that launches condensed matter, drawing the most violent arc in the sky.

<Row>
    <ItemImage id="data_energistics:matter_converging_crossbow" scale="4" />
</Row>
Stores 20k AE. It can use colored Matter Balls, Singularities, and a fully charged unupgraded Data Light Saber as ammunition.

| Ammo | Damage | Energy Cost |
|---|---:|---:|
| <ItemImage id="ae2:matter_ball" /> Colored Matter Balls | 10 | 200 AE |
| <ItemImage id="ae2:singularity" /> Singularity | 25 | 200 AE |
| <ItemImage id="data_energistics:data_light_saber" components="ae2:stored_energy=20000.0d" /> Fully Charged Data Light Saber | 20 (1% to 5% max HP true damage) | 200000 AE + extra cost (cap 300000 AE / shot) |

---

# Upgrades
<Row>
    <ItemLink id="ae2:speed_card" />
    <ItemImage id="ae2:speed_card" />
</Row>
Speed Card:
<Row>
    <ItemImage id="data_energistics:data_crystal_sword" />
    <ItemImage id="data_energistics:data_light_saber" />
    <ItemImage id="data_energistics:data_sanctifier" />
</Row>
: +0.2 attack speed and +100 AE cost. On light sabers it also shortens the charge time.

<Row>
    <ItemImage id="data_energistics:data_crystal_axe" />
    <ItemImage id="data_energistics:data_crystal_pickaxe" />
    <ItemImage id="data_energistics:data_crystal_hoe" />
    <ItemImage id="data_energistics:data_crystal_shovel" />
</Row>
: +0.2 attack speed, +100 AE cost, and +8 mining speed.

<Row>
    <ItemImage id="data_energistics:matter_converging_crossbow" />
</Row>
Projectile speed:  
Normal ammo: `3.15 + number of Speed Cards * 1.0`  
Special ammo: `3.15 * 1.5 + number of Speed Cards * 1.0` (special ammo does not benefit from projectile speed scaling)  
When the number of Speed Cards is `>= 4`, fully drawing the string will immediately load and fire the shot.

---

<Row>
 <ItemLink id="ae2:energy_card"/>
 <ItemImage id="ae2:energy_card" />
</Row>
Energy Card:
<Row>
    <ItemImage id="data_energistics:data_crystal_sword" />
    <ItemImage id="data_energistics:data_crystal_axe" />
    <ItemImage id="data_energistics:data_crystal_pickaxe" />
    <ItemImage id="data_energistics:data_crystal_hoe" />
    <ItemImage id="data_energistics:data_crystal_shovel" />
    <ItemImage id="data_energistics:data_crystal_cutting_knife" />
    <ItemImage id="data_energistics:data_light_saber" />
    <ItemImage id="data_energistics:data_sanctifier" />
</Row>
: max energy = base capacity × (1 + 8 × number of Energy Cards)

<ItemImage id="data_energistics:matter_converging_crossbow" />
: max energy = base capacity × (1 + 8 × number of Energy Cards)  
True damage percentage = 1% + 1% for every extra 25000 AE spent

---

<Row>
    <ItemLink id="data_energistics:redstone_tuning_card" />
    <ItemImage id="data_energistics:redstone_tuning_card" />
</Row>
Redstone Tuning Card:  
<ItemImage id="data_energistics:matter_converging_crossbow" />:  
Normal ammo now costs base energy × 5 per shot. The projectile gains homing and tracks the nearest nearby non-player living target.

---

<Row>
    <ItemLink id="data_energistics:card_saber_energy"/>
    <ItemImage id="data_energistics:card_saber_energy" />
</Row>
Saber Energy Card:
<ItemImage id="data_energistics:matter_converging_crossbow" />: final damage = base damage × (number of Saber Energy Cards × 2) × current speed (3.15), then × 1.5 on critical hit. When <ItemImage id="data_energistics:data_light_saber" components="ae2:stored_energy=20000.0d" /> is used as ammo, it also gains +5% maximum true damage percentage.  
<ItemImage id="data_energistics:data_light_saber" />: raises damage cap, increases energy cost, and allows the fully charged saber to fire light blades on left-click. <ItemImage id="data_energistics:data_sanctifier" components="ae2:stored_energy=20000.0d" /> also becomes twice as large while empowered.  
<ItemImage id="data_energistics:data_crystal_cutting_knife" />: increases teleport range.  
The following also gain a Data Flow storage function:  
<ItemImage id="data_energistics:data_crystal_sword" />: max damage = base damage × (number of Saber Energy Cards × 2), +500 AE cost, attack consumes 20 Data Flow to strip AI for 20 ticks.  
<ItemImage id="data_energistics:data_crystal_axe" />: consumes 20 Data Flow to vein-cut an entire tree, +500 AE cost.  
<ItemImage id="data_energistics:data_crystal_pickaxe" />: consumes 20 Data Flow to chain nearby ores and duplicate one extra drop, +500 AE cost.  
<ItemImage id="data_energistics:data_crystal_hoe" />: consumes 20 Data Flow to keep farmland permanently hydrated, +500 AE cost.  
<ItemImage id="data_energistics:data_crystal_shovel" />: can toggle between 3×3 and 5×5 breaking while sneaking, +500 AE cost, and consumes 20 Data when breaking.

---
navigation:
  parent: data_energistics:items-blocks-machines/data_energistics.md
  title: Data Mimetic Field
  icon: data_energistics:data_mimetic_field
  position: 8
item_ids:
- data_energistics:data_mimetic_field
- data_energistics:mob_data_carrier
- data_energistics:crop_data_carrier
- data_energistics:ore_data_carrier
---

# Data Mimetic Field
Within a dead civilization’s sea of memory, data reconstructs life as a silicon-born echo of lost stars.
<GameScene zoom="6" background="transparent">
    <Block id="data_energistics:data_mimetic_field" x="0" y="0" z="0" />
   <IsometricCamera yaw="205" pitch="25" />
</GameScene>
<Row>
  <RecipeFor id="data_energistics:data_mimetic_field" />
</Row>

---

# Upgrades
<Row>
    <ItemLink id="ae2:speed_card"/>
    <ItemImage id="ae2:speed_card" />
</Row>
Speed Card:  
final speed = base speed - number of Speed Cards × 40

## Reference Table

| Speed Cards | Work Cycle | Yield per Carrier | Key Cost per Carrier |
|:---|:---|:---|:---|
| 0 | 200t | 48 | 150 |
| 1 | 160t | 64 | 200 |
| 2 | 120t | 80 | 250 |
| 3 | 80t | 96 | 300 |
| 4 | 40t | 112 | 350 |

Mob / ore / crop output = 48 + Speed Cards × 16  
Key cost per active carrier = 150 + Speed Cards × 50  
Total key cost = active carrier count × (150 + Speed Cards × 50)  
Idle drain = active carrier count × 500 AE/t

---

<Row>
    <ItemLink id="ae2:capacity_card" />
    <ItemImage id="ae2:capacity_card" />
</Row>
Capacity Card:  
base capacity + number of Capacity Cards × 4

---

# Carriers
On the data chip, every flowing current becomes a carrier for the will of an age, preserving the key that turns chaos into a computable future.

<Row>
    <ItemImage id="data_energistics:mob_data_carrier"  scale="6" />
    <ItemImage id="data_energistics:crop_data_carrier"  scale="6" />
    <ItemImage id="data_energistics:ore_data_carrier"  scale="6" />
</Row>

<ItemImage id="data_energistics:data_mimetic_field"  scale="6" />  
Only the three fully recorded data carriers can be inserted.  
Once inserted, they automatically produce content based on their type.

## Mob Data Carrier

- Recorded target: one specific mob type
- Role in the field: simulates killing that mob and outputs its drops
- Output source:
- Priority goes to configured mob output rules
- Otherwise the entity’s own loot table is used
- If the mob can naturally spawn with equipment, that equipment can also appear in the output
- If the mob has passengers, companion entities, or mounts, their drops are resolved too

## Ore Data Carrier

- Recorded target: one ore target
- Role in the field: continuously produces the corresponding ore output
- Output source:
- Priority goes to configured ore output rules
- If none exist, the recorded ore item itself is produced by default

## Crop Data Carrier

- Recorded target: one crop, sapling, fungus, flower, sugar cane, cactus, or other valid recorded plant
- Role in the field: continuously produces the corresponding plant output
- Output source:
- Priority goes to configured crop output rules
- If the target is a sapling or propagule, the matching tree loot table is preferred
- If a source block was recorded, drops are generated from the mature block’s loot table
- If no other result is available, the recorded item itself is produced by default

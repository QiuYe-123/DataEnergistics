---
navigation:
  parent: data_energistics:items-blocks-machines/data_energistics.md
  title: Data Extractor
  icon: data_energistics:data_extractor
  position: 7
item_ids:
- data_energistics:data_extractor
- data_energistics:data_carrier
---

# Data Extractor
The Data Extractor pulls hidden rules out of massive information streams, turning raw material into structured data.
<GameScene zoom="6" background="transparent">
    <Block id="data_energistics:data_extractor" x="0" y="0" z="0" />
   <IsometricCamera yaw="205" pitch="25" />
</GameScene>
<Row>
  <RecipeFor id="data_energistics:data_extractor" />
</Row>

---

# Upgrades

<Row>
    <ItemLink id="ae2:capacity_card"/>
    <ItemImage id="ae2:capacity_card" />
</Row>
Capacity Cards:  
Increase both the target limit and the working area.

Target limit formula:  
target cap = base target cap + number of Capacity Cards × extra targets per card

With the default config:  
target cap = 20 + number of Capacity Cards × 5

Range formula:
- Horizontal radius grows by 1 block per Capacity Card on each side
- Vertical height = 3 + number of Capacity Cards × 2

By default:
- 0 Capacity Cards: horizontal base radius 1, vertical height 3
- Every extra card adds 1 horizontal block per side and 2 vertical blocks

---

<Row>
    <ItemLink id="ae2:speed_card"/>
    <ItemImage id="ae2:speed_card" />
</Row>
Speed Cards shorten the work cycle.

Interval formula:  
final work time in seconds = base work time - number of Speed Cards × 1

With the default config, base work time is 5 seconds:
- 0 cards: once every 5 seconds
- 1 card: once every 4 seconds
- 2 cards: once every 3 seconds
- 3 cards: once every 2 seconds
- 4 or more cards: once every 1 second

---

<Row>
    <ItemLink id="ae2:energy_card"/>
    <ItemImage id="ae2:energy_card" />
</Row>
Energy Cards increase the base Data Flow generated per cycle and also increase the internal AE energy buffer.

Base Data Flow formula:  
base Data Flow per cycle = base value + number of Energy Cards × 200 + damage dealt this cycle × extra Data Flow per point of damage

With the default config:  
base Data Flow per cycle = 100 + number of Energy Cards × 200 + damage dealt × 20

Internal AE buffer formula:  
internal buffer = 1600 + number of Energy Cards × 100

---

# Multi-Target Scaling
When a cycle affects multiple targets, the base Data Flow is multiplied by a target scaling factor.

Formula:  
final Data Flow = base Data Flow per cycle × (1 + max(0, target count - 1) × extra target multiplier)

With the default extra target multiplier of 0.25:
- 1 target: × 1.00
- 2 targets: × 1.25
- 3 targets: × 1.50
- 4 targets: × 1.75

All of these base values can be adjusted further in the config file.

---

# Carrier
<ItemImage id="data_energistics:data_carrier"  scale="6" />

The Data Extractor uses a blank  
<Row>
    <ItemLink id="data_energistics:data_carrier"/>
    <ItemImage id="data_energistics:data_carrier" />
</Row>
carrier as its base medium.  
Once recording is complete, it automatically converts into the corresponding finished data carrier.

Basic rules:
- Put a blank data carrier into the upper-left main slot
- One blank carrier can only record one category at a time
- Once biological data recording starts, mineral or crop data cannot be mixed in
- Once mineral recording starts, biological or crop data cannot be mixed in
- Once crop recording starts, biological or mineral data cannot be mixed in
- When progress reaches the requirement, the blank carrier becomes the matching finished carrier

---

# Mob Data Carrier
<Row>
    <ItemLink id="data_energistics:mob_data_carrier"/>
    <ItemImage id="data_energistics:mob_data_carrier" />
</Row>

Recording process:
- Put a blank data carrier in the main slot
- A sword may be placed in the sword slot; otherwise the machine uses its base magic damage
- The extractor periodically damages living targets inside its range
- On the first successful record, the carrier locks onto the current mob type being damaged
- After that, only valid damage dealt to the same mob type continues the progress

Progress source:
- Progress directly tracks the actual damage dealt to the target mob
- When the requirement is met, the blank carrier becomes <ItemImage id="data_energistics:mob_data_carrier" />

Traits:
- Suitable for locking onto one specific creature type
- Can later be placed into the Data Mimetic Field to simulate that creature’s drops

---

# Ore Data Carrier
<Row>
    <ItemLink id="data_energistics:ore_data_carrier"/>
    <ItemImage id="data_energistics:ore_data_carrier" />
</Row>

Recording process:
- Put a blank data carrier in the main slot
- Insert ore, or another valid mineral input allowed by the config, into the mineral slot
- On the first record, the carrier locks onto the mineral target represented by the current input
- After that, only inputs matching the same target continue progress

Progress source:
- Normal ores count as `1:1` by default
- Raw ores count as `0.5:1` by default
- If an ore rule exists in `input_rules`, it takes priority through:
  - `recorded_item`
  - `progress_per_item`
  - `required_amount`

Completion:
- Once the requirement is met, the blank carrier becomes <ItemImage id="data_energistics:ore_data_carrier" />

---

# Crop Data Carrier
<Row>
    <ItemLink id="data_energistics:crop_data_carrier"/>
    <ItemImage id="data_energistics:crop_data_carrier" />
</Row>

Recording process:
- Put a blank data carrier in the main slot
- Put crops, saplings, fungi, flowers, bamboo, sugar cane, cactus, or another valid configured input into the crop slot
- On the first record, the carrier locks onto the crop target represented by the current input
- After that, only matching inputs continue progress

Progress source:
- Standard crop inputs count as `1:1` by default
- Some mapped inputs can be converted by config, for example:
  - Wheat Seeds -> Wheat, `0.5`
  - Beetroot Seeds -> Beetroot, `0.5`
  - Melon Seeds / Melon Slice -> Melon, `0.5`
  - Pumpkin Seeds -> Pumpkin, `0.5`
- If a crop rule exists in `input_rules`, it takes priority through:
  - `recorded_item`
  - `progress_per_item`
  - `required_amount`

Valid targets:
- Standard crops
- Nether Wart
- Saplings and propagules
- Mushrooms and fungi
- Sweet Berries
- Melons and pumpkins
- Bamboo, sugar cane, cactus
- All kinds of flowers
- Any extra configured input you add

Completion:
- Once the requirement is met, the blank carrier becomes <ItemImage id="data_energistics:crop_data_carrier" />

---

# Config Rules
The Data Extractor and Data Mimetic Field support an independent rule table:

`config/data_energistics-data_extractor_rules.json`

---

Where:
- `input_rules` controls what can be inserted into which extractor slot, what data it records as, how much progress each item gives, and the total requirement
- `output_rules` controls what a finished data carrier produces in the Data Mimetic Field

If no matching `input_rules` entry exists, the Data Extractor falls back to its built-in defaults.  
If no `output_rule` exists, the Data Mimetic Field falls back to the default loot table or hardcoded drop logic.

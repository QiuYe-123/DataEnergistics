---
navigation:
  parent: data_energistics:items-blocks-machines/data_energistics.md
  title: Data Crystals
  icon: data_energistics:data_crystal
  position: 2
item_ids:
- data_energistics:budding_data_crystal_0
- data_energistics:budding_data_crystal_1
- data_energistics:budding_data_crystal_2
- data_energistics:budding_data_crystal_3
- data_energistics:budding_data_crystal_4
- data_energistics:small_data_crystal_bud
- data_energistics:medium_data_crystal_bud
- data_energistics:large_data_crystal_bud
- data_energistics:data_crystal_cluster
- data_energistics:data_crystal
- data_energistics:data_dust
- data_energistics:data_crystal_block
---

# The Data Crystal Family
Exploration, movement, and editing are hidden inside these mysterious crystals. They conduct data well and are often used in circuit boards and frames, though no one truly knows the final consequence.

<GameScene zoom="3" background="transparent">
  <Block id="data_energistics:budding_data_crystal_0" x="0" y="0" z="0" />
  <Block id="data_energistics:budding_data_crystal_1" x="1" y="0" z="0" />
  <Block id="data_energistics:budding_data_crystal_2" x="2" y="0" z="0" />
  <Block id="data_energistics:budding_data_crystal_3" x="3" y="0" z="0" />
  <Block id="data_energistics:budding_data_crystal_4" x="4" y="0" z="0" />

  <Block id="data_energistics:small_data_crystal_bud" x="1" y="1" z="0" />
  <Block id="data_energistics:medium_data_crystal_bud" x="2" y="1" z="0" />
  <Block id="data_energistics:medium_data_crystal_bud" x="3" y="1" z="0" />
  <Block id="data_energistics:data_crystal_cluster" x="4" y="1" z="0" />
 <IsometricCamera yaw="0" pitch="25" />
</GameScene>

---

## Motherrocks / Blocks
Like Certus Quartz, they arrive with meteorites from the cosmos. You can find them inside fallen AE digitalized meteorites.  
Charged Data Crystal Motherrock has a 27% chance to generate 1 to 2 blocks inside a meteorite.

<GameScene zoom="4" background="transparent">
    <Block id="data_energistics:budding_data_crystal_0" x="0" y="0" z="0" />
    <Block id="data_energistics:budding_data_crystal_4" x="2" y="0" z="0" />
    <IsometricCamera yaw="0" pitch="25" />
</GameScene>

---

Activating a Deactivated Data Crystal Motherrock eventually yields weaker motherrock states. After each successful growth, there is a 1/12 chance to drop by one grade.

<GameScene zoom="4" background="transparent">
  <Block id="data_energistics:budding_data_crystal_1" x="2" y="0" z="0" />
  <Block id="data_energistics:budding_data_crystal_2" x="4" y="0" z="0" />
  <Block id="data_energistics:budding_data_crystal_3" x="6" y="0" z="0" />
  <IsometricCamera yaw="0" pitch="25" />
</GameScene>
<Row>
  <RecipeFor id="data_energistics:budding_data_crystal_3" />
</Row>
---

## Crystal Stages

<Row>
  <ItemImage id="small_data_crystal_bud" scale="3" />
  <ItemImage id="medium_data_crystal_bud" scale="3" />
  <ItemImage id="large_data_crystal_bud" scale="3" />
  <ItemImage id="data_crystal_cluster" scale="3" />
  <ItemImage id="data_crystal" scale="3" />
  <ItemImage id="data_dust" scale="3" />
</Row>

## Reference Table

| Stage | Drops without Silk Touch | Drop and Chance |
|:---|:---|:---|
| <ItemImage id="data_energistics:small_data_crystal_bud" /> | <ItemImage id="data_energistics:data_dust" /> | 0% <ItemImage id="data_energistics:data_dust" /> |
| <ItemImage id="data_energistics:medium_data_crystal_bud" /> | <ItemImage id="data_energistics:data_dust" /> | 15% <ItemImage id="data_energistics:data_dust" /> |
| <ItemImage id="data_energistics:large_data_crystal_bud" /> | <ItemImage id="data_energistics:data_dust" /> | 25% <ItemImage id="data_energistics:data_dust" /> |
| <ItemImage id="data_energistics:data_crystal_cluster" /> | <ItemImage id="data_energistics:data_dust" /> <ItemImage id="data_energistics:data_crystal" /> | 40% <ItemImage id="data_energistics:data_dust" />, otherwise 60% <ItemImage id="data_energistics:data_crystal" /> |

## Data Crystal
<Column>
    <Row>
        <Recipe id="data_energistics:ae2/charger/data_crystal" />
        <RecipeFor id="data_energistics:data_crystal" />
        <Recipe id="data_energistics:crafting/data_crystal" />
    </Row>
</Column>
After a violent instant of activation, it gains excellent conductivity and crystal toughness, making it useful in the frames of many devices.

---
navigation:
  parent: data_energistics:items-blocks-machines/data_energistics.md
  title: Fluids
  icon: data_energistics:ender_bucket
  position: 15
item_ids:
- data_energistics:ender_bucket
- data_energistics:data_corrosion_liquid_bucket
---

# Fluids

<Row>
  <ItemImage id="data_energistics:ender_bucket" scale="6" />
  <ItemImage id="data_energistics:data_corrosion_liquid_bucket" scale="6" />
</Row>

---

## Ender Bucket

When living entities come into contact with Ender fluid, they are randomly teleported within the nearby area.

<Row>
    <ItemImage id="data_energistics:ender_bucket" scale="3" />
    <Recipe id="data_energistics:data_energistics/data_reassembler/ender" />
</Row>

<GameScene zoom="6" background="transparent">
  <Block id="data_energistics:guide_ender_display" x="0" y="0" z="0" />
  <IsometricCamera yaw="25" pitch="25" />
</GameScene>

The fluid itself does not deal persistent damage, but it constantly disrupts the position of creatures standing inside it.

---

## Data Corrosion Liquid Bucket

Data Corrosion Liquid is highly corrosive and deals very high continuous damage to living entities that touch it. It can also generate around digitalized meteorites.

<Column>
  <Row>
    <ItemImage id="data_energistics:data_corrosion_liquid_bucket" scale="3" />
  </Row>
</Column>

<GameScene zoom="6" background="transparent">
  <Block id="data_energistics:guide_data_corrosion_liquid_display" x="0" y="0" z="0" />
  <IsometricCamera yaw="25" pitch="25" />
</GameScene>

It also emits a faint light level, making it suitable for dangerous zones and special traps.

---
navigation:
  parent: data_energistics:items-blocks-machines/data_energistics.md
  title: Materials
  icon: data_energistics:obsidian_dust
  position: 3
item_ids:
- data_energistics:obsidian_dust
- data_energistics:solidified_obsidian
- data_energistics:digisidian_memorize_ingot
- data_energistics:data_framework
- data_energistics:residual_data_ore
- data_energistics:data_capture_ball
---

# Materials / Frames

# Data Framework
An extremely sturdy frame that uses crystals for data conduction and refractive transfer.
<GameScene zoom="6" background="transparent">
    <Block id="data_energistics:data_framework" x="0" y="0" z="0" />
   <IsometricCamera yaw="25" pitch="25" />
</GameScene>
<Row>
    <RecipeFor id="data_energistics:data_framework" />
</Row>

---

## Solidified Obsidian
A durable material with excellent insulation.
<Row>
    <ItemImage id="data_energistics:obsidian_dust" scale="6" />
    <ItemImage id="data_energistics:solidified_obsidian" scale="6" />
</Row>

<Row>
  <RecipeFor id="data_energistics:obsidian_dust" />
  <RecipeFor id="data_energistics:solidified_obsidian" />
</Row>

---

## Digisidian Memory Alloy
A special alloy with “memory,” able to return to its original shape through temperature change after deformation. It combines good insulation with strong data conduction.
<Row>
    <ItemImage id="data_energistics:digisidian_memorize_ingot" scale="6" />
    <RecipeFor id="data_energistics:digisidian_memorize_ingot" />
</Row>

---

## Ore
A strange ore found in the End. The instant you mine it, you feel as if something enters your body.
<GameScene zoom="6" background="transparent">
<Block id="data_energistics:residual_data_ore" x="0" y="0" z="0" />
<IsometricCamera yaw="25" pitch="25" />
</GameScene>
Mining it has a chance to drop 0 to 3 Dispersing Data, affected by Fortune.

---

## Data Capture Ball
A sphere formed from condensed matter energy, used to capture Dispersing Data. If its stored energy runs out, the ball is destroyed together with the data inside it.
<Row>
<ItemImage id="data_energistics:data_capture_ball" scale="6" />
</Row>
<Row>
<Recipe id="data_energistics:condenser/data_capture_ball" />
</Row>

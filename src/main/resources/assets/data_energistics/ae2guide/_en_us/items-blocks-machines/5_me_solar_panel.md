---
navigation:
  parent: data_energistics:items-blocks-machines/data_energistics.md
  title: ME Solar Panel
  icon: data_energistics:me_solar_panel
  position: 6
item_ids:
- data_energistics:me_solar_panel
- data_energistics:me_solar_panel_part
---

# ME Solar Panel
<GameScene zoom="6" background="transparent">
    <Block id="data_energistics:me_solar_panel" x="0" y="0" z="0" />
   <IsometricCamera yaw="25" pitch="25" />
</GameScene>
<Row>
  <RecipeFor id="data_energistics:me_solar_panel" />
</Row>
A small and compact solar generator. It requires clear sky directly above to work.  
It produces 3000 AE/t during the day, and 1/3 of that rate at night.

---

ME Solar Panel in part form

<Row>
    <ItemImage id="data_energistics:me_solar_panel_part" scale="6" />
</Row>
<Row>
  <RecipeFor id="data_energistics:me_solar_panel_part" />
</Row>
A smaller panelized form of the same solar generator. Because it is thinner, its generation is weaker.  
It produces 2500 AE/t during the day and 1/3 of that at night. When mounted on a side face it is reduced by another 1/3, and on the bottom face it produces nothing.

---

# Upgrades
<ItemLink id="ae2:speed_card"/>: +75% generation per card

<Row>
    <ItemImage id="ae2:speed_card" />
</Row>
Formula: base generation × (1 + number of Speed Cards × 0.75)

---

<ItemLink id="ae2:energy_card"/>: +80000 AE capacity per card

<Row>
    <ItemImage id="ae2:energy_card" />
</Row>
Formula: base capacity 160000 AE + number of Energy Cards × 80000 AE

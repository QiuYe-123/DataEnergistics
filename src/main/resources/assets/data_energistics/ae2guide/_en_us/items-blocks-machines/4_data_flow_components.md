---
navigation:
  parent: data_energistics:items-blocks-machines/data_energistics.md
  title: Data Flow Components
  icon: data_energistics:data_flow_component_housing
  position: 4
item_ids:
- data_energistics:data_storage_component_1k
- data_energistics:data_storage_component_4k
- data_energistics:data_storage_component_16k
- data_energistics:data_storage_component_64k
- data_energistics:data_storage_component_256k
- data_energistics:data_flow_component_housing
- data_energistics:data_flow_cell_1k
- data_energistics:data_flow_cell_4k
- data_energistics:data_flow_cell_16k
- data_energistics:data_flow_cell_64k
- data_energistics:data_flow_cell_256k
- data_energistics:portable_data_flow_cell_1k
- data_energistics:portable_data_flow_cell_4k
- data_energistics:portable_data_flow_cell_16k
- data_energistics:portable_data_flow_cell_64k
- data_energistics:portable_data_flow_cell_256k
- data_energistics:data_cell_infinity
---

# Data Flow Components

<Column>
  <Row>
    <ItemImage id="data_storage_component_1k" scale="4" />
    <ItemImage id="data_storage_component_4k" scale="4" />
    <ItemImage id="data_storage_component_16k" scale="4" />
  </Row>

  <Row>
    <ItemImage id="data_storage_component_64k" scale="4" />
    <ItemImage id="data_storage_component_256k" scale="4" />
    <ItemImage id="data_flow_component_housing" scale="4" />
  </Row>
</Column>

Data Flow Components are a family of parts and finished cells built around Data Flow storage.

---

## Storage Components

Data Flow storage components determine the capacity tier of the finished cell. Five tiers are currently available, from 1K to 256K.

<Column>
  <Row>
    <RecipeFor id="data_storage_component_1k" />
    <RecipeFor id="data_storage_component_4k" />
    <RecipeFor id="data_storage_component_16k" />
  </Row>

  <Row>
    <RecipeFor id="data_storage_component_64k" />
    <RecipeFor id="data_storage_component_256k" />
  </Row>
</Column>

---

## Component Housing

The Data Flow Component Housing is used to package a storage component into a usable cell.

<Row>
  <RecipeFor id="data_flow_component_housing" />
</Row>

---

## Data Flow Storage Cells

Once assembled, they become Data Flow storage cells of the matching tier.

<Column>
  <Row>
    <RecipeFor id="data_flow_cell_1k" />
  </Row>

  <Row>
    <Recipe id="data_energistics:crafting/cell/data_flow_cell_1k_1" />
  </Row>
</Column>

The other storage tiers follow the same recipe pattern.  
If the cell does not contain any Data Flow, it can be disassembled with Shift + Right Click.

---

## Portable Data Flow Cells

The other portable storage tiers follow the same recipe pattern.

<Column>
  <Row>
    <RecipeFor id="portable_data_flow_cell_1k" />
  </Row>
</Column>

If it does not contain any Data Flow, it can be disassembled with Shift + Right Click.

---

# Upgrades

<Row>
    <ItemLink id="ae2:energy_card"/>
    <ItemImage id="ae2:energy_card" />
</Row>
Energy Card:  
Maximum energy = base capacity × (1 + 8 × number of Energy Cards)

---

# Creative Cell
<Row>
    <RecipeFor id="data_cell_infinity" />
</Row>
A component born from an unknown power, able to output infinite Data Flow and Data. No one knows where it came from, and no one knows where it will go.

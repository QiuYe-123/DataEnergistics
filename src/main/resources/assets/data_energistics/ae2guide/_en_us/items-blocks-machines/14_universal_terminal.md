---
navigation:
  parent: data_energistics:items-blocks-machines/data_energistics.md
  title: Universal Terminal
  icon: data_energistics:universal_terminal
  position: 14
item_ids:
- data_energistics:universal_terminal
---

# Universal Terminal
Bundles multiple terminal functions into one item, reducing the need to constantly swap between hotbar slots and inventory tools.

<Row>
  <ItemImage id="data_energistics:universal_terminal" scale="6" />
</Row>

---

# Basic Use
The Universal Terminal is essentially a “terminal container.”

It can install multiple terminal types into one item and lets you switch the currently active terminal UI when used.  
The item tooltip directly lists the terminals currently installed.

Built-in support includes:
- ME Terminal
- Crafting Terminal
- Pattern Access Terminal
- Pattern Encoding Terminal

In addition, the system tries to detect compatible terminal-like parts from other mods and register them as installable targets.

---

# Crafting and Expansion
The Universal Terminal has two merge rules:

1. Universal Terminal + one supported terminal
- Installs that new terminal into the existing Universal Terminal
- If that terminal type is already installed, it will not be added again

2. Two different supported terminals
- Crafts directly into a new Universal Terminal
- The new Universal Terminal stores both terminals at once

Restrictions:
- Two Universal Terminals cannot be merged with each other
- Two identical terminals cannot be merged into a new entry

---

# Switching
The Universal Terminal stores a “currently active terminal.”

When used:
- It opens the UI of the current active terminal
- You can cycle through installed terminals
- The selected mode is saved into the item data

If the active terminal becomes invalid, the system automatically falls back to the first installed terminal in the list.

---

# Saved Data
The Universal Terminal stores:
- the list of installed terminals
- the original terminal item data for each terminal
- the name of the currently active terminal

This means:
- one Universal Terminal can keep growing over time
- adding another terminal later does not erase previous content
- the tooltip can directly show all installed terminals

---

# Compatibility
The Universal Terminal prioritizes native AE2 terminals.  
For third-party terminals, support works by:
- automatically detecting terminal part types
- automatically matching a suitable menu type
- identifying whether it belongs to storage, crafting, pattern access, or pattern encoding

Because of this, some third-party terminals can be merged directly into the Universal Terminal.  
If a terminal is not recognized, it usually means its part type or menu structure does not match the current compatibility rules.

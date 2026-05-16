---
navigation:
  parent: data_energistics:items-blocks-machines/data_energistics.md
  title: 数位化陨石
  icon: data_energistics:data_meteorite_0
  position: 3
item_ids:
- data_energistics:data_meteorite_0
- data_energistics:data_meteorite_1
- data_energistics:data_meteorite_2
- ae2:mysterious_cube
- ae2:sky_stone_block
- ae2:quartz_block
- ae2:flawed_budding_quartz
- data_energistics:budding_data_crystal
- data_energistics:data_crystal_block
- data_energistics:data_crystal_cluster
---

# 数位化陨石

## 说明

<Row>
  <ItemImage id="data_energistics:data_meteorite_0" scale="5" />
  <ItemImage id="data_energistics:data_meteorite_1" scale="5" />
  <ItemImage id="data_energistics:data_meteorite_2" scale="5" />
</Row>

这些数位化陨石会混入你的陨石结构内部与外围，可掉落天域石、即散数据，以及不同概率的末影粉与陨石粉。

---

## 场景示例

下面这个例子就是 GuideME 里做 3D 场景展示的基本写法。

<GameScene zoom="7" background="transparent">
  <Block id="ae2:sky_stone_block" x="0" y="0" z="0" />
  <Block id="ae2:sky_stone_block" x="1" y="0" z="0" />
  <Block id="ae2:sky_stone_block" x="2" y="0" z="0" />
  <Block id="ae2:sky_stone_block" x="0" y="0" z="1" />
  <Block id="data_energistics:data_meteorite_0" x="1" y="0" z="1" />
  <Block id="ae2:sky_stone_block" x="2" y="0" z="1" />
  <Block id="ae2:sky_stone_block" x="0" y="0" z="2" />
  <Block id="ae2:sky_stone_block" x="1" y="0" z="2" />
  <Block id="ae2:sky_stone_block" x="2" y="0" z="2" />

  <Block id="ae2:flawed_budding_quartz" x="0" y="1" z="0" />
  <Block id="data_energistics:data_crystal_cluster" x="1" y="1" z="0" />
  <Block id="data_energistics:budding_data_crystal" x="2" y="1" z="0" />
  <Block id="ae2:quartz_block" x="0" y="1" z="1" />
  <Block id="ae2:mysterious_cube" x="1" y="1" z="1" />
  <Block id="data_energistics:data_crystal_block" x="2" y="1" z="1" />
  <Block id="ae2:sky_stone_block" x="0" y="1" z="2" />
  <Block id="data_energistics:data_meteorite_1" x="1" y="1" z="2" />
  <Block id="data_energistics:data_meteorite_2" x="2" y="1" z="2" />

  <Block id="ae2:sky_stone_block" x="0" y="2" z="0" />
  <Block id="ae2:sky_stone_block" x="1" y="2" z="0" />
  <Block id="ae2:sky_stone_block" x="2" y="2" z="0" />

  <IsometricCamera yaw="195" pitch="25" />

  <BlockAnnotation x="1" y="0" z="1">
    开裂数位化陨石
  </BlockAnnotation>
</GameScene>

这个例子里用到的关键标签是：

* `<GameScene>`：创建一个 3D 场景。
* `<Block>`：在指定坐标摆一个方块。
* `<IsometricCamera>`：设置等距视角。
* `<BlockAnnotation>`：给某个方块加高亮框和悬浮提示。

---

## 用法建议

如果你后面想做和截图里一样更复杂的核心展示，推荐这样做：

* 小示例直接手写 `<Block>`。
* 大结构先做成结构文件，再用 `<ImportStructure>` 导入。
* 想强调某个方块时，用 `<BlockAnnotation>` 或 `<BoxAnnotation>`。

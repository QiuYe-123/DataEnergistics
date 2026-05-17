---
navigation:
  parent: data_energistics:items-blocks-machines/data_energistics.md
  title: 数据水晶
  icon: data_energistics:data_crystal
  position: 1
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
---

## 数据水晶家族
探索，移动，编辑以神秘的构造存在于晶体之中,其具有良好的数据传导性,往往用于电路板以及框架的制作，但是没有人知道它会发生什么后果
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

## 数据水晶母岩/块
 它与赛特斯石英一样，随宇宙中的陨石而来。你可以在落地的 AE 数位化陨石内部发现它。  
<GameScene zoom="4" background="transparent">
    <Block id="data_energistics:budding_data_crystal_0" x="0" y="0" z="0" />
    <Block id="data_energistics:budding_data_crystal_4" x="2" y="0" z="0" />
    <IsometricCamera yaw="0" pitch="25" />
</GameScene>  

<GameScene zoom="4" background="transparent">
  <Block id="data_energistics:budding_data_crystal_1" x="2" y="0" z="0" />
  <Block id="data_energistics:budding_data_crystal_2" x="4" y="0" z="0" />
  <Block id="data_energistics:budding_data_crystal_2" x="6" y="0" z="0" />
  <IsometricCamera yaw="0" pitch="25" />
</GameScene>  

---

## 数据晶簇

<Row>
  <ItemImage id="small_data_crystal_bud" scale="4" />
  <ItemImage id="medium_data_crystal_bud" scale="4" />
  <ItemImage id="large_data_crystal_bud" scale="4" />
  <ItemImage id="data_crystal_cluster" scale="4" />
  <ItemImage id="data_dust" scale="4" />
</Row>

## 对照表

| 阶段 | 非精准采集下掉落 | 掉落概率 |
| :-- | :-- | :-- |
| <ItemImage id="data_energistics:small_data_crystal_bud" /> | <ItemImage id="data_energistics:data_dust" /> | 0% <ItemImage id="data_energistics:data_dust" /> |
| <ItemImage id="data_energistics:medium_data_crystal_bud" /> | <ItemImage id="data_energistics:data_dust" /> | 15% <ItemImage id="data_energistics:data_dust" /> |
| <ItemImage id="data_energistics:large_data_crystal_bud" /> | <ItemImage id="data_energistics:data_dust" /> | 25% <ItemImage id="data_energistics:data_dust" /> |
| <ItemImage id="data_energistics:data_crystal_cluster" /> | <ItemImage id="data_energistics:data_dust" /> <ItemImage id="minecraft:redstone" /> | 40% <ItemImage id="data_energistics:data_dust" />，否则 60% <ItemImage id="minecraft:redstone" /> |

## 数据水晶
<Column>
    <Row>
        <Recipe id="data_energistics:ae2/charger/data_crystal" />
        <RecipeFor id="data_energistics:data_crystal" />
        <Recipe id="data_energistics:crafting/data_crystal" />
    </Row>
</Column>
由于一瞬间的猛烈激活而具有良好的导电性以及晶体韧性，往往被用于做成各个设备的框架。

---

## 黑曜石粉|固化黑曜石
由黑曜石或哭泣黑曜石粉碎而来。
<Row>
    <RecipeFor id="data_energistics:obsidian_dust" />
</Row>

---

由黑曜石粉充能而来，因极其的稳定性，从而可用于制成电路板。
<Row>
    <RecipeFor id="data_energistics:solidified_obsidian" />
</Row>

---

## 残存数据

<Row>
  <ItemImage id="data_energistics:residual_data_ore" scale="6" />
</Row>

<GameScene zoom="6" background="transparent">
  <Entity id="data_energistics:dispersing_data" rotationY="45" x="0.5" y="0.0" z="0.5" />
  <IsometricCamera yaw="195" pitch="25" />
</GameScene>
自末地的矿石似乎是前代文明留下。你暂时没有完全挖透它的用途，只知道在破坏它时，似乎有什么东西进入了体内，对自己造成了一点微乎其微的更改。你只能以数据捕捉的形式，将它留于身边。即散数据以实体形式存在，一分钟不理它，它将会被抹杀。

---

<Row>
    <ItemImage id="data_energistics:data_capture_ball" scale="4" />
</Row>
你使用压缩再压缩的方式，获得了这个神奇的球。它能稳定地捕捉即散数据。

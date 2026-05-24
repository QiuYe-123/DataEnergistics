---
navigation:
  parent: data_energistics:items-blocks-machines/data_energistics.md
  title: 工具/武器
  icon: data_energistics:data_sanctifier
  position: 12
item_ids:
- data_energistics:data_crystal_sword
- data_energistics:data_crystal_axe
- data_energistics:data_crystal_pickaxe
- data_energistics:data_crystal_hoe
- data_energistics:data_crystal_shovel
- data_energistics:data_crystal_cutting_knife

- data_energistics:data_light_saber
- data_energistics:data_sanctifier

- data_energistics:card_saber_energy
---

# 工具/武器
## 数据水晶
一种以特殊工艺制作的基础工具。使其拥有下界合金般的硬度，以及金子的轻便程度. 因其特殊工艺，使他们自身能存储少量能量
<Row>
  <ItemImage id="data_energistics:data_crystal_sword" scale="4" />
  <ItemImage id="data_energistics:data_crystal_axe" scale="4" />
  <ItemImage id="data_energistics:data_crystal_pickaxe" scale="4" />
  <ItemImage id="data_energistics:data_crystal_hoe" scale="4" />
  <ItemImage id="data_energistics:data_crystal_shovel" scale="4" />
  <ItemImage id="data_energistics:data_crystal_cutting_knife" scale="4" />
</Row>

| 名称 | 存储能力 | 攻击速度 | 攻击伤害 | 挖掘等级/速率 | 可装载升级 |
|---|-|-|-|---|---|
| <ItemImage id="data_energistics:data_crystal_sword" /> 数据水晶剑 | 20kae | 2 | 10 | 无       | <ItemLink id="ae2:speed_card"/> : 增加0.2攻击速度，增加100ae耗能 / <ItemLink id="ae2:energy_card"/> : 最大能源 = 基础容量 × (1 + 8 × 能源卡数量) |
| <ItemImage id="data_energistics:data_crystal_axe" /> 数据水晶斧 | 20kae | 1.2 | 12 | 下界合金\金  | <ItemLink id="ae2:speed_card"/> : 增加0.2攻击\挖掘速度，增加耗能 / <ItemLink id="ae2:energy_card"/> : 最大能源 = 基础容量 × (1 + 8 × 能源卡数量) |
| <ItemImage id="data_energistics:data_crystal_pickaxe" /> 数据水晶镐 | 20kae | 1.4 | 6 | 下界合金\金  | <ItemLink id="ae2:speed_card"/> : 增加0.2攻击\挖掘速度，增加耗能 / <ItemLink id="ae2:energy_card"/> : 最大能源 = 基础容量 × (1 + 8 × 能源卡数量) |
| <ItemImage id="data_energistics:data_crystal_hoe" /> 数据水晶锄 | 20kae | 1.4 | 6 | 下界合金\金  | <ItemLink id="ae2:speed_card"/> : 增加0.2攻击\挖掘速度，增加耗能 / <ItemLink id="ae2:energy_card"/> : 最大能源 = 基础容量 × (1 + 8 × 能源卡数量) |
| <ItemImage id="data_energistics:data_crystal_shovel" /> 数据水晶锹 | 20kae | 1.4 | 6 | 下界合金\金  | <ItemLink id="ae2:speed_card"/> : 增加0.2攻击/挖掘速度，增加耗能 / <ItemLink id="ae2:energy_card"/> : 最大能源 = 基础容量 × (1 + 8 × 能源卡数量) |
| <ItemImage id="data_energistics:data_crystal_cutting_knife" /> 数据切割刀 | 20kae | 无 | 当装载eae时潜行右键可以给AE设备重命名 | 替换为特殊功能，存储数据流可在传送锚之间互相传送 | <ItemLink id="ae2:energy_card"/> : 最大能源 = 基础容量 × (1 + 8 × 能源卡数量) |

---

## 数据光剑
一种快乐而又危险的"小玩具"，能让你cosplay一下绝地武士？
<Row>
    <ItemImage id="data_energistics:data_light_saber" scale="4" />
</Row> 
耐久为20kae，攻击速度为2，攻击伤害为17，右键短暂蓄力后可以投掷，打开背包后拿染料或染色器右键可以对其染色  

# 数据圣裁者  
一种特殊的光剑，使用光剑红，黄，蓝合成
<Row>
    <ItemImage id="data_energistics:data_sanctifier" scale="4" />
</Row> 
耐久为20kae，攻击伤害为36，右键短暂蓄力后可以投掷

# 升级
<Row>
    <ItemLink id="ae2:speed_card"/> 
    <ItemImage id="ae2:speed_card" />
</Row>
加速卡：
 增加0.2攻击速度，增加100ae耗能(当光剑装载时，减少蓄力时间)  
 
<Row>
 <ItemLink id="ae2:energy_card"/>  
 <ItemImage id="ae2:energy_card" />
</Row>
能源卡:
最大能源 = 基础容量 × (1 + 8 × 能源卡数量)

<Row>
 <ItemLink id="data_energistics:card_saber_energy"/>  
 <ItemImage id="data_energistics:card_saber_energy" />
</Row>
聚能卡:
在安装聚能卡后，左键攻击可以发射出光刃(数据圣裁者安装后旋转向周围每0.2秒发射三道光刃)

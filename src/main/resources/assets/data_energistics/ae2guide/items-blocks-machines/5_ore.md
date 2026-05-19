---
navigation:
  parent: data_energistics:items-blocks-machines/data_energistics.md
  title: 矿石/捕捉球
  icon: data_energistics:residual_data_ore
  position: 5
item_ids:
- data_energistics:residual_data_ore
- data_energistics:data_capture_ball
---
# 矿石/捕捉球
## 矿石
一种存在于末地的奇异矿石，当破坏的一瞬间，你感觉什么东西似乎进入了你的身体？
<GameScene zoom="6" background="transparent">
    <Block id="data_energistics:residual_data_ore" x="0" y="0" z="0" />
   <IsometricCamera yaw="25" pitch="25" />
</GameScene>
挖掘时有概率掉落0~3个即散数据，受时运影响

---

## 数据捕捉球
经过物质凝聚的能量形成的球体，用于捕捉即散数据，当能量耗空时它会被销毁里面的数据也会被销毁
<Row>
    <ItemImage id="data_energistics:data_capture_ball" scale="6" />
</Row>  
<Row>
  <Recipe id="data_energistics:condenser/data_capture_ball" />
</Row>
# DanceCubeBot

一个基于Mirai的舞立方机器人

*目前只测试在**舞小铃**的账号上*，  
~~如果你看到了这个机器人，就说明它的框架是这个b写的~~

自用机器人早期版本，相当的 屎山 （为什么有人敢fork？🤔  
没时间维护代码和帮助文档，自己凑合着看吧💦  
给个**star**或许我会很开心🥰
因为是学生比较~~懒~~忙，不介意pr项目

## 文件配置

***前情提要：不难的其实，就是第一次有一点点的麻烦了...***

***当然，后续~~可能~~会优化***

---

首先要在**运行jar目录之外(与 mcl文件夹 并列)的目录下**
创建一个文件夹 `DcConfig`放入如下文件，使用如下文件结构

```
- root
- mcl
 - mcl
 - plugins
 - libs
 - ...
- DcConfig
 - UserTokens.json
 - TokenIds.json
 - ApiKeys.yml
 - UserCommands.json
- Images
  - Background.png
  - ...
```

| 文件                           | 类型      | 功能           | 要求         |
|------------------------------|---------|--------------|------------|
| [`Images`](#Images)          | **文件夹** | 存放素材图片文件     | **手动配置**   |
| `UserTokens.json`            | 文件      | 用于保存用户令牌     | **无需手动配置** |
| [`TokenIds.json`](#TokenIds) | 文件      | 用于获取二维码登录    | **手动配置**   |
| [`ApiKeys.yml`](#ApiKeys)    | 文件      | 用于API令牌      | **手动配置**   |
| `UserCommands.json`          | 文件      | 用于保存用户信息触发指令 | **无需手动配置** |

### TokenIds

用于登录时获取二维码，需要在 [舞立方制谱网站](https://danceweb.shenghuayule.com/MusicMaker/#/) 上
抓包找到一个类似心跳的请求，然后多复制几个它的`client_id`，写入`DcConfig`里面

类似于：`client_id: yyQ6VxqMeIK62hbylWSpUVmnVN4WUUQ8`  的

以**json**格式写入文件如下（星号是我加的）

```json
[
  "yyQ6VxqMeIL2hceWzZ******81Ru8pIE",
  "yyQ6VxqMeILLsdi*****SnddhlyVGcNa",
  "yyQ6VxqMeILneEzfVyXPFVCZo****oH3",
  "yyQ6VxqMeIL2h**********xNf/hHSzH",
  "yyQ6Vxq******zVmQuHtNAU******xmR"
]
```

### ApiKeys

用于**二维码识别**和**地名转经纬度**

*本项目使用的是[**腾讯SDK**](https://cloud.tencent.com/)和[**高德地图**](https://lbs.amap.com/)
的API，每月限度不算很低且**免费**，所以请自行申请API令牌*

~~所以别偷我的Key了！！~~

---

**当然，如果有别的需求或者使用其它第三方平台SDK，请自己修改源码**

```yaml
# 腾讯OCR SDK密钥
tencentScannerKeys:
  secretId: AKIDK****TBnFXeibIm*********
  secretKey: HLCrQoyzrZ8Z1************
# 高德地图定位 SDK密钥
gaodeMapKeys:
  apiKey: b1bbd99c****1172**************
```

~~唯独这个是YML文件，因为我觉得这个最像配置文件~~

### Images

在`Images`放入文件`Background.png`  
如果想要和我一样用这样的效果：  
可以[点击链接](https://i.328888.xyz/2023/04/11/ip37wc.png)获取模板

如果想自定义模板，需要修改`Image`类的源码  
你也可以进入[即时设计](https://js.design/f/M5a8Zp)中获取本图片模板，
自定义编辑文件

![ip3gYJ.png](https://i.328888.xyz/2023/04/11/ip3gYJ.md.png)

## 开发说明

你先别急...

## 一些提醒

如果真的有人敢`fork`，以下是一些注意事项：

- 请不要高频http请求
- 本项目和[**广州市胜骅动漫科技有限公司**](https://arccer.com/#/home)无关
- 项目仅供学习交流，严禁用于商业用途

## 鸣谢

**艾鲁BotNB!!**

---

草好困我睡觉了（2023.4.10
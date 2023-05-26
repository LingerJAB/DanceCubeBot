# DanceCubeBot

一个基于Mirai的舞立方机器人

目前只测试在**舞小铃**的账号上，  
~~如果你看到了这个机器人，就说明它的框架是这个b写的~~

自用机器人早期版本，相当的 屎山 （为什么有人敢fork？🤔  
没时间维护代码和帮助文档，自己凑合着看吧💦  
因为是学生比较~~懒~~忙，假期一点一点写的💦💦不介意pr项目啦

给个**star**或许我会很开心🥰

## 功能介绍

详情可发送**菜单**了解

用户可用功能：

- 扫码至登录机器人
- 查看个人信息（战力，排行，金币，积分等等）
- 自定义个人信息指令（mydc,mywlf,舞立方等等）
- 舞立方机台二维码登录（发送给机器人二维码）~~至少不用微信扫码了~~
- 查找地区在线/离线的舞立方
- 自动批量兑换自制谱兑换码
- Token每日自动更新

---
管理员可用功能：

- 读取/写入Tokens
- 查看个人Token
- 强制刷新Token

> ~~一些咕咕咕还没做的功能~~：
> - 借号登录机台
> - 战力分析
> - 商品购买（因为懒才没做）
> - 在线mcz转舞立方铺面
>
> 或许等到2077年才能做出来💦💦

## 开发指南

如果你只是插件使用者，只要配置好文件就行了

### 文件配置

***前情提要：不难的其实，就是第一次有一点点的麻烦了...***

***当然，后续~~可能~~会优化***

---

首先要在**与 mcl文件夹 并列**的目录下  
创建一个文件夹 `DcConfig`放入如下文件，使用如下文件结构（**注意`DcConfig`在`mcl`外面**）

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

~~其实是我不会写Mirai配置文件，才把文件夹放在外面的~~

---

以下是相关文件作用（可点击超链接跳转具体说明）

| 文件                           | 类型      | 功能           | 要求         |
|------------------------------|---------|--------------|------------|
| [`Images`](#Images)          | **文件夹** | 存放素材图片文件     | **手动配置**   |
| `UserTokens.json`            | 文件      | 用于保存用户令牌     | **无需手动配置** |
| [`TokenIds.json`](#TokenIds) | 文件      | 用于获取二维码登录    | **手动配置**   |
| [`ApiKeys.yml`](#ApiKeys)    | 文件      | 用于API令牌      | **手动配置**   |
| `UserCommands.json`          | 文件      | 用于保存用户信息触发指令 | **无需手动配置** |

#### TokenIds

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

可能你会发现不管开几个标签都是一样的，可以尝试先**登录**一个二维码，再打开另一个标签页

#### ApiKeys

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

#### Images

在`Images`放入文件`Background.png`  
如果想要和我一样用这样的效果：  
可以[点击链接](https://i.328888.xyz/2023/04/11/ip37wc.png)获取模板

如果想自定义模板，需要修改`Image`类的源码  
你也可以进入[即时设计](https://js.design/f/M5a8Zp)中获取本图片模板，
自定义编辑文件

![ip3gYJ.png](https://i.328888.xyz/2023/04/11/ip3gYJ.md.png)

### 开发帮助

#### 指令功能

本机器人支持**正则指令**和**参数指令**两种指令触发

所有的指令存放在 `AllCommands` 类中，具体在**声明指令**后，
会在调用 `init()` 后，被自动保存到如下两个属性中

```java
public class AllCommands {
  public static HashSet<RegexCommand> regexCommands = new HashSet<>();  //所有正则指令
  public static HashSet<ArgsCommand> argsCommands = new HashSet<>();  //所有参数指令

  // your commands...
}
```

##### 指令声明

你需要使用 `@DeclaredCommand("name")` 来声明一个`public static final`指令对象，  
没有 `@DeclaredCommand("name")` 的对象不会被保存，  
参数为指令名，没有实际用途，仅便于开发者，使用具体见以下实例

##### 正则指令

你可以通过 `RegexCommandBuilder` 链式调用来创建一个 `RegexCommand` 对象，例如：

```java
public class AllCommands {

  @DeclaredCommand("舞立方自制谱兑换")  //指令声明
  public static final RegexCommand gainMusicByCode = new RegexCommandBuilder()
          .regex("[a-zA-Z0-9]{15}", false)
          .onCall(Scope.USER, (event, contact, qq, args) -> {
            Token token = loginDetect(contact, qq);
            if(token==null) return;

            // type your code here

          }).build();
}
```

---
`regex(String regex, boolean lineOnly)`  
正则匹配方式，`regex`为正则表达式字符串， `lineOnly`为是否仅匹配单行，当为`true`时会默认加上 `^...$`
行匹配标识，默认为`true`

`onCall(Scope scope, MsgHandleable (lambda) )`  
调用指令，`scope`为作用域，`lambda`为调用指令实现体，你需要传入`(event, contact, qq, args) -> {}`,其中`args`无需实现。

`build()`  
构建指令，返回一个`RegexCommand`对象

##### 参数指令

类似于**正则指令**，你需要使用`ArgsCommandBuilder`来创建一个`ArgsCommand`对象

```java
public class AllCommands {
  @DeclaredCommand("查找舞立方机台")
  public static final ArgsCommand msgMachineList = new ArgsCommandBuilder()
          .prefix("查找舞立方", "查找机台", "舞立方")
          .form(ArgsCommand.CHAR)
          .onCall(Scope.GROUP, (event, contact, qq, args) -> {
            if(args==null) return;

            // type your code here...

          }).build();
}
```

---
`prefix(String... name)`  
用于声明一个参数指令的前缀，仅当消息触发前缀后才会匹配参数

`form(Pattern... patterns)`  
声明参数的格式，建议使用`ArgsCommand`类提供的模板：

```java
public class ArgsCommand extends AbstractCommand {
  // 数字
  public static final Pattern NUMBER = Pattern.compile("\\d+");
  // 字母＋数字
  public static final Pattern WORD = Pattern.compile("[0-9a-zA-z]+");
  // 非空字符
  public static final Pattern CHAR = Pattern.compile("\\S+");
}
```

`onCall(Scope scope, MsgHandleable (lambda) )`  
和参数指令类似，但是获取参数的值需要使用到`args`来获取（需要做非`null`判定）

##### 作用域

```java
public enum Scope {
  GLOBAL, // 全局指令
  USER, // 仅用户
  GROUP //仅群聊
}
```

作用域用于对不同的聊天环境触发不同的`onCall()`功能

### 开发说明

- **\*重要：如果你是Java8以上，出现了`ImageCombiner`类的报错，请添加`mcl.cmd`或者`mcl`
  的`Java运行参数`：`--add-exports java.desktop/sun.font=ALL-UNNAMED`**

- 如果你的`DcConfig`文件夹是在IDEA项目下，而报错**找不到路径**，请把`linuxRootPath`改成`windowsRootPath`变量
- 对于不需要的功能，可以自己改代码的🤔比如机台查找，你可以注释掉获取`ApiKeys.yml`中`key`的相关代码
- 在`MiraiBot.java`中的**自动刷新Token功能**建议把`refresh(true)`改为`refresh()`或者`refresh(false)`，这里是一个*
  *刷新token而忽略等待时间**的参数，对于大量的`Token`不建议**忽略**
- 其它问题问我或者自行debug（~~对不起bug真的好多好多~~

## 一些提醒

如果真的有人敢`fork`，以下是一些注意事项：

- 请不要高频http请求
- 本项目和[**广州市胜骅动漫科技有限公司**](https://arccer.com/#/home)无关

## 鸣谢

**感谢 艾鲁Bot 的API提供**
（或许可以试试隔壁的机器人🤔


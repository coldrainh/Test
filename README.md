# Godzilla v1.0 源码

Godzilla v1.0 源码（Decompile &amp; Fixed）

当然不能忘了支持原作者： [Godzilla](https://github.com/BeichenDream/Godzilla)

主要目的是方便大家进行部分定制，避免被直接查杀

## 开发环境

JDK1.8 + Gradle

以下介绍直接援引自原作者的README

## 个人修改

- 移除F1键彩蛋
- 移除初始化时的Tips
- 移除查看About之后强制弹出项目主页的功能

版权相关信息已全部保留

## 运行环境
 1. JavaDynamicPayload -> jre5及以上
 2. CShapDynamicPayload -> .net2.0及以上
 3. PhpDynamicPayload -> php5.0及以上

## 简介

### Payload以及加密器支持

哥斯拉内置了3种Payload以及6种加密器,6种支持脚本后缀,20个内置插件

 1. JavaDynamicPayload
	 1. JAVA_AES_BASE64
	 	1. jsp
	 	2. jspx
     2. JAVA_AES_RAW
	     1. jsp
	     2. jspx

 2. CShapDynamicPayload
	 1. CSHAP_AES_BASE64
		 1. aspx
		 2. asmx
		 3. ashx
	 2. JAVA_AES_RAW
		 1. aspx
		 2. asmx
		 3. ashx
 3. PhpDynamicPayload
	 1. PHP_XOR_BASE64
		 1. php
     2. PHP_XOR_RAW
	     1. php

### Raw or Base64 加密器区别

Raw : Raw是将加密后的数据直接发送或者输出

![raw](https://raw.githubusercontent.com/BeichenDream/Godzilla/master/raw.png)

Base64 : Base64是将加密后的数据再进行Base64编码

![base64](https://raw.githubusercontent.com/BeichenDream/Godzilla/master/base64.png)

## 插件支持

 1. JavaDynamicPayload
       1. MemoryShell

     ```
     支持 哥斯拉 冰蝎 菜刀 ReGeorg 的内存shell  并且支持卸载
     ```

       2. Screen

     ```
     屏幕截图
     ```

       3. JRealCmd

     ```
     虚拟终端 可以用netcat连接
     ```

       4. JMeterpreter

     ```
     与MSF联动
     ```

       5. ServletManage

     ```
     Servlet管理 Servlet卸载
     ```

       6. JarLoader

     ```
     内存加载Jar 将Jar加载到 SystemClassLoader
     ```

       7. JZip

     ```
     ZIP压缩 ZIP解压
     ```
 2. CShapDynamicPayload
	 1. CZip
	 ```
	 ZIP压缩 ZIP解压

      ```

     2. ShellcodeLoader

     ```
     Shellcode加载 与MSF联动
     ```

     3. SafetyKatz

     ```
     Mimikatz
     ```

     4. lemon

     ```
     读取服务器 FileZilla navicat sqlyog Winscp xmangager 的配置信息以及密码
     ```

     5. CRevlCmd

     ```
     虚拟终端 可以用netcat连接
     ```

     6. BadPotato

     ```
     Windows权限提升 2012-2019
     ```

     7. ShapWeb
	 ```
     读取服务器 谷歌 IE 火狐 浏览器保存的账号密码
     ```
     8. SweetPotato

     ```
      Windwos权限提升		烂土豆的C#版本 甜土豆 
     ```
 3. PhpDynamicPayload
     1. PMeterpreter

     ```
     与MSF联动
     ```

     2. ByPassOpenBasedir

     ```
     绕过OpenBasedir
     ```

     3. PZip

     ```
     ZIP压缩 ZIP解压
     ```

     4. P_Eval_Code

     ```
     代码执行
     ```

     5. BypassDisableFunctions

     ```
     绕过 DisableFunctions
     ```

     
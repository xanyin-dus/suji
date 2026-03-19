# 📒 Suji 记账本

简洁高效的个人财务管理应用

[![License](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE)
[![Kotlin](https://img.shields.io/badge/Kotlin-1.9.20-purple.svg)](https://kotlinlang.org)
[![Compose](https://img.shields.io/badge/Jetpack%20Compose-1.5.4-blue.svg)](https://developer.android.com/compose)

## ✨ 功能特点

| 功能 | 说明 |
|------|------|
| 💰 **收支记录** | 轻松记录日常收入和支出，操作简单直观 |
| 📂 **分类管理** | 自定义消费分类，便于统计和分析 |
| 📚 **账本管理** | 支持多账本，满足不同场景需求 |
| 📊 **数据分析** | 图表展示，洞察消费习惯和趋势 |
| 📝 **待处理记录** | 草稿功能，不错过每一笔账目 |
| 🤖 **AI 智能分析** | 智能洞察你的消费趋势和建议 |
| 🌙 **深色模式** | 支持深色/浅色主题切换，保护眼睛 |

## 🛠 技术栈

**框架 & 语言**
- Kotlin 1.9.20
- Jetpack Compose
- Material Design 3

**架构 & 依赖注入**
- MVVM 架构
- Hilt 依赖注入
- Coroutines + Flow

**主要依赖**
- Room 数据库
- Navigation Compose
- MPAndroidChart 图表
- iTextPDF PDF 生成
- Coil 图片加载
- OkHttp + Gson 网络请求

## 🏗 项目架构

```
┌─────────────────────────────────────┐
│              UI 层                   │
│   Jetpack Compose + Material 3      │
├─────────────────────────────────────┤
│             业务层                   │
│      ViewModel + UseCase            │
├─────────────────────────────────────┤
│              数据层                  │
│   Repository + Room + DataStore     │
├─────────────────────────────────────┤
│             DI 层                   │
│           Hilt                       │
└─────────────────────────────────────┘
```

## 📂 目录结构

```
app/src/main/java/com/suji/accountbook/
├── data/                    # 数据层
│   ├── local/              # 本地数据（Room）
│   └── repository/         # 仓库实现
├── di/                     # 依赖注入
├── domain/                 # 业务层
│   ├── model/              # 领域模型
│   └── usecase/            # 用例
└── ui/                     # UI 层
    ├── about/              # 关于页面
    ├── analysis/           # 数据分析
    ├── components/         # 公共组件
    ├── home/               # 首页
    ├── navigation/         # 导航
    ├── record/             # 记录管理
    ├── settings/           # 设置
    └── theme/              # 主题
```

## 🚀 快速开始

### 环境要求

- JDK 11+
- Android Studio Hedgehog (2023.1.1) 或更高版本
- Android SDK 34

### 构建项目

```bash
# 克隆项目
git clone https://github.com/xanyin-dus/suji.git

# 进入目录
cd suji

# 构建 Debug 版本
./gradlew assembleDebug

# 构建 Release 版本
./gradlew assembleRelease
```

## 📋 更新日志

### v1.1.0 (2024-03-19)

**🎉 新功能**
- ✨ 新增深色模式功能，支持深色/浅色主题切换，设置自动保存
- ✨ 新增精美弹窗组件，支持成功/错误/警告/信息四种类型
- ✨ 关于页面新增功能：
  - 点击 GitHub 仓库跳转到项目地址
  - 点击"给个好评"显示感谢弹窗
  - 点击"反馈建议"跳转 QQ 群
  - 点击"开源许可"显示开源库信息

**� 问题修复**
- 🛠 修复导航栏切换动画方向问题，现在动画方向与导航顺序一致
- 🛠 修复首页添加记录页面无法滚动的问题，现在可以正常查看所有内容
- 🛠 修复关于页面按钮点击无响应的问题

**💅 优化改进**
- 💄 优化弹窗显示逻辑，新弹窗出现时旧弹窗立即消失
- 💄 优化导航动画，根据导航方向智能切换动画效果
- 💄 更新版本号至 1.1.0
- 💄 更新关于页面底部署名

### v1.0.0 (初始版本)

**🎉 核心功能**
- 💰 收支记录管理
- 📂 分类管理
- 📚 多账本支持
- 📊 数据分析图表
- 📝 待处理记录
- 🤖 AI 智能分析

## �📄 许可证

本项目基于 MIT 许可证开源，详见 [LICENSE](LICENSE) 文件。

---

*Made With Love By xuanying*

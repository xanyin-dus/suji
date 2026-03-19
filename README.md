# 📒 Suji 记账本

简洁高效的个人财务管理应用

[![License](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE)
[![Kotlin](https://img.shields.io/badge/Kotlin-1.9.20-purple.svg)](https://kotlinlang.org)
[![Compose](https://img.shields.io/badge/Jetpack%20Compose-1.5.4-blue.svg)](https://developer.android.com/compose)

##  功能特点

| 功能 | 说明 |
|------|------|
| 💰 **收支记录** | 轻松记录日常收入和支出，操作简单直观 |
| 📂 **分类管理** | 自定义消费分类，便于统计和分析 |
| 📚 **账本管理** | 支持多账本，满足不同场景需求 |
| 📊 **数据分析** | 图表展示，洞察消费习惯和趋势 |
| 📝 **待处理记录** | 草稿功能，不错过每一笔账目 |
| 🤖 **AI 智能分析** | 智能洞察你的消费趋势和建议 |

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
    ├── home/               # 首页
    ├── navigation/         # 导航
    ├── record/             # 记录管理
    └── settings/           # 设置
```

## 🚀 快速开始

### 环境要求

- JDK 11+
- Android Studio Hedgehog (2023.1.1) 或更高版本
- Android SDK 34

### 构建项目

```bash
# 克隆项目
git clone https://github.com/yourusername/Suji.git

# 进入目录
cd Suji

# 构建 Debug 版本
./gradlew assembleDebug

# 构建 Release 版本
./gradlew assembleRelease
```

## 📄 许可证

本项目基于 MIT 许可证开源，详见 [LICENSE](LICENSE) 文件。

---

*Suji 记账本 · 让财务管理更简单*

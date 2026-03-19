<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Suji - 记账本 | Suji - Account Book</title>
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }
        body {
            font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
            line-height: 1.6;
            padding: 20px;
            max-width: 900px;
            margin: 0 auto;
            background: #f5f5f5;
        }
        .container {
            background: white;
            border-radius: 12px;
            box-shadow: 0 2px 8px rgba(0,0,0,0.1);
            overflow: hidden;
        }
        .header {
            background: linear-gradient(135deg, #6366f1, #8b5cf6);
            color: white;
            padding: 30px;
            text-align: center;
        }
        .header h1 {
            font-size: 2.5em;
            margin-bottom: 10px;
        }
        .header p {
            font-size: 1.1em;
            opacity: 0.9;
        }
        .lang-switch {
            text-align: center;
            padding: 15px;
            background: #f8fafc;
            border-bottom: 1px solid #e2e8f0;
        }
        .lang-btn {
            background: #6366f1;
            color: white;
            border: none;
            padding: 10px 24px;
            border-radius: 6px;
            cursor: pointer;
            font-size: 14px;
            transition: background 0.3s;
        }
        .lang-btn:hover {
            background: #4f46e5;
        }
        .content {
            padding: 30px;
        }
        .section {
            margin-bottom: 25px;
        }
        .section h2 {
            color: #6366f1;
            border-bottom: 2px solid #e0e7ff;
            padding-bottom: 8px;
            margin-bottom: 15px;
            font-size: 1.3em;
        }
        .zh { display: block; }
        .en { display: none; }
        .zh:lang(en) { display: none; }
        .zh:lang(zh) { display: block; }
        :lang(en) .zh { display: none; }
        :lang(en) .en { display: block; }
        ul {
            list-style-position: inside;
            color: #374151;
        }
        li {
            margin-bottom: 8px;
        }
        .tech-tag {
            display: inline-block;
            background: #e0e7ff;
            color: #4338ca;
            padding: 4px 12px;
            border-radius: 16px;
            font-size: 13px;
            margin: 4px 4px 4px 0;
        }
        .footer {
            text-align: center;
            padding: 20px;
            color: #6b7280;
            font-size: 14px;
            border-top: 1px solid #e5e7eb;
        }
    </style>
</head>
<body>
    <div class="container">
        <div class="header">
            <h1>📒 Suji 记账本</h1>
            <p class="zh">简单、高效的个人财务管理系统</p>
            <p class="en">Simple & Efficient Personal Finance Management System</p>
        </div>
        
        <div class="lang-switch">
            <button class="lang-btn" onclick="toggleLang()">
                <span class="zh">🌐 Switch to English</span>
                <span class="en">🌐 切换到中文</span>
            </button>
        </div>

        <div class="content">
            <div class="section">
                <h2 class="zh">📱 功能特点</h2>
                <h2 class="en">📱 Features</h2>
                <ul class="zh">
                    <li>收支记录：轻松记录日常收入和支出</li>
                    <li>分类管理：自定义消费分类，便于统计</li>
                    <li>账本管理：支持多账本，满足不同场景需求</li>
                    <li>数据分析：图表展示，洞察消费习惯</li>
                    <li>待处理记录：草稿功能，不错过每一笔账目</li>
                    <li>AI 智能分析：智能洞察你的消费趋势</li>
                </ul>
                <ul class="en">
                    <li>Income & Expense Tracking: Easily record daily income and expenses</li>
                    <li>Category Management: Customizable categories for better statistics</li>
                    <li>Account Book Management: Multiple account books for different scenarios</li>
                    <li>Data Analysis: Visual charts to understand spending habits</li>
                    <li>Pending Records: Draft feature to never miss any transaction</li>
                    <li>AI Smart Analysis: Intelligent insights into your spending trends</li>
                </ul>
            </div>

            <div class="section">
                <h2 class="zh">🛠 技术栈</h2>
                <h2 class="en">🛠 Tech Stack</h2>
                <div class="zh">
                    <span class="tech-tag">Kotlin</span>
                    <span class="tech-tag">Jetpack Compose</span>
                    <span class="tech-tag">Material Design 3</span>
                    <span class="tech-tag">Hilt</span>
                    <span class="tech-tag">Room</span>
                    <span class="tech-tag">Navigation Compose</span>
                    <span class="tech-tag">Coroutines + Flow</span>
                    <span class="tech-tag">MPAndroidChart</span>
                    <span class="tech-tag">iTextPDF</span>
                    <span class="tech-tag">Coil</span>
                    <span class="tech-tag">OkHttp</span>
                    <span class="tech-tag">Gson</span>
                </div>
                <div class="en">
                    <span class="tech-tag">Kotlin</span>
                    <span class="tech-tag">Jetpack Compose</span>
                    <span class="tech-tag">Material Design 3</span>
                    <span class="tech-tag">Hilt</span>
                    <span class="tech-tag">Room</span>
                    <span class="tech-tag">Navigation Compose</span>
                    <span class="tech-tag">Coroutines + Flow</span>
                    <span class="tech-tag">MPAndroidChart</span>
                    <span class="tech-tag">iTextPDF</span>
                    <span class="tech-tag">Coil</span>
                    <span class="tech-tag">OkHttp</span>
                    <span class="tech-tag">Gson</span>
                </div>
            </div>

            <div class="section">
                <h2 class="zh">🏗 项目架构</h2>
                <h2 class="en">🏗 Architecture</h2>
                <ul class="zh">
                    <li><strong>UI 层</strong>：Jetpack Compose + Material Design 3</li>
                    <li><strong>业务层</strong>：ViewModel + UseCase</li>
                    <li><strong>数据层</strong>：Repository + Room + DataStore</li>
                    <li><strong>依赖注入</strong>：Hilt</li>
                </ul>
                <ul class="en">
                    <li><strong>UI Layer</strong>：Jetpack Compose + Material Design 3</li>
                    <li><strong>Business Layer</strong>：ViewModel + UseCase</li>
                    <li><strong>Data Layer</strong>：Repository + Room + DataStore</li>
                    <li><strong>DI</strong>：Hilt</li>
                </ul>
            </div>
        </div>

        <div class="footer">
            <p>Suji Account Book © 2024</p>
        </div>
    </div>

    <script>
        function toggleLang() {
            const html = document.documentElement;
            const currentLang = html.lang;
            html.lang = currentLang === 'zh-CN' ? 'en' : 'zh-CN';
            localStorage.setItem('preferredLang', html.lang);
        }
        
        const savedLang = localStorage.getItem('preferredLang');
        if (savedLang) {
            document.documentElement.lang = savedLang;
        }
    </script>
</body>
</html>

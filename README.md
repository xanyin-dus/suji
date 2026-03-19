<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Suji 记账本</title>
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }
        body {
            font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
            line-height: 1.8;
            padding: 40px 20px;
            background: linear-gradient(135deg, #f5f7fa 0%, #e4e8f0 100%);
            min-height: 100vh;
        }
        .container {
            max-width: 800px;
            margin: 0 auto;
        }
        .card {
            background: white;
            border-radius: 16px;
            box-shadow: 0 4px 20px rgba(0,0,0,0.08);
            overflow: hidden;
            margin-bottom: 24px;
        }
        .header {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            padding: 50px 40px;
            text-align: center;
        }
        .header h1 {
            font-size: 3em;
            margin-bottom: 12px;
            font-weight: 700;
        }
        .header .subtitle {
            font-size: 1.2em;
            opacity: 0.9;
        }
        .header .version {
            display: inline-block;
            margin-top: 16px;
            background: rgba(255,255,255,0.2);
            padding: 6px 16px;
            border-radius: 20px;
            font-size: 0.9em;
        }
        .content {
            padding: 40px;
        }
        h2 {
            color: #667eea;
            font-size: 1.4em;
            margin-bottom: 20px;
            padding-bottom: 10px;
            border-bottom: 2px solid #f0f0f0;
            display: flex;
            align-items: center;
            gap: 10px;
        }
        h2::before {
            content: '';
            width: 4px;
            height: 24px;
            background: linear-gradient(135deg, #667eea, #764ba2);
            border-radius: 2px;
        }
        .feature-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
            gap: 16px;
            margin-top: 16px;
        }
        .feature-item {
            background: #f8f9ff;
            border-radius: 12px;
            padding: 20px;
            border-left: 4px solid #667eea;
        }
        .feature-item h3 {
            color: #333;
            font-size: 1.1em;
            margin-bottom: 8px;
        }
        .feature-item p {
            color: #666;
            font-size: 0.95em;
        }
        .tech-section {
            display: flex;
            flex-wrap: wrap;
            gap: 10px;
            margin-top: 16px;
        }
        .tech-tag {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            padding: 8px 18px;
            border-radius: 20px;
            font-size: 14px;
            font-weight: 500;
        }
        .architecture {
            background: #f8f9ff;
            border-radius: 12px;
            padding: 24px;
            margin-top: 16px;
        }
        .arch-layer {
            display: flex;
            align-items: center;
            margin-bottom: 16px;
            padding: 16px;
            background: white;
            border-radius: 10px;
            box-shadow: 0 2px 8px rgba(0,0,0,0.04);
        }
        .arch-layer:last-child {
            margin-bottom: 0;
        }
        .arch-label {
            background: linear-gradient(135deg, #667eea, #764ba2);
            color: white;
            padding: 8px 16px;
            border-radius: 8px;
            font-weight: 600;
            min-width: 100px;
            text-align: center;
        }
        .arch-content {
            margin-left: 20px;
            color: #444;
        }
        .footer {
            text-align: center;
            padding: 30px;
            color: #888;
            font-size: 14px;
        }
        .footer a {
            color: #667eea;
            text-decoration: none;
        }
    </style>
</head>
<body>
    <div class="container">
        <div class="card">
            <div class="header">
                <h1>📒 Suji 记账本</h1>
                <p class="subtitle">简单、高效的个人财务管理系统</p>
                <span class="version">v1.0.0</span>
            </div>
        </div>

        <div class="card">
            <div class="content">
                <h2>📱 功能特点</h2>
                <div class="feature-grid">
                    <div class="feature-item">
                        <h3>💰 收支记录</h3>
                        <p>轻松记录日常收入和支出，操作简单直观</p>
                    </div>
                    <div class="feature-item">
                        <h3>� 分类管理</h3>
                        <p>自定义消费分类，便于统计和分析</p>
                    </div>
                    <div class="feature-item">
                        <h3>📚 账本管理</h3>
                        <p>支持多账本，满足不同场景需求</p>
                    </div>
                    <div class="feature-item">
                        <h3>📊 数据分析</h3>
                        <p>图表展示，洞察消费习惯和趋势</p>
                    </div>
                    <div class="feature-item">
                        <h3>📝 待处理记录</h3>
                        <p>草稿功能，不错过每一笔账目</p>
                    </div>
                    <div class="feature-item">
                        <h3>🤖 AI 智能分析</h3>
                        <p>智能洞察你的消费趋势和建议</p>
                    </div>
                </div>
            </div>
        </div>

        <div class="card">
            <div class="content">
                <h2>🛠 技术栈</h2>
                <div class="tech-section">
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
        </div>

        <div class="card">
            <div class="content">
                <h2>🏗 项目架构</h2>
                <div class="architecture">
                    <div class="arch-layer">
                        <span class="arch-label">UI 层</span>
                        <span class="arch-content">Jetpack Compose + Material Design 3</span>
                    </div>
                    <div class="arch-layer">
                        <span class="arch-label">业务层</span>
                        <span class="arch-content">ViewModel + UseCase</span>
                    </div>
                    <div class="arch-layer">
                        <span class="arch-label">数据层</span>
                        <span class="arch-content">Repository + Room + DataStore</span>
                    </div>
                    <div class="arch-layer">
                        <span class="arch-label">DI</span>
                        <span class="arch-content">Hilt 依赖注入</span>
                    </div>
                </div>
            </div>
        </div>

        <div class="footer">
            <p>Suji 记账本 · 个人财务管理应用</p>
        </div>
    </div>
</body>
</html>

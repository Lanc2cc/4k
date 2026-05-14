@echo off
chcp 65001
echo ================================
echo  4K影视资源网站 - 启动指南
echo ================================
echo.
echo 请按以下步骤启动项目：
echo.
echo [步骤1] 准备环境
echo   - 安装 Java 21 / Maven 3.9+
echo   - 安装 Node.js 18+
echo   - 安装 MySQL 5.7+ 并启动
echo   - 安装 Redis 并启动
echo.
echo [步骤2] 初始化数据库
echo   在 MySQL 中执行：movie-backend\src\main\resources\schema.sql
echo.
echo [步骤3] 启动后端
echo   cd movie-backend
echo   mvn clean install -DskipTests
echo   mvn spring-boot:run
echo   (后端运行在 http://localhost:8088)
echo.
echo [步骤4] 启动前端
echo   cd movie-frontend
echo   npm install
echo   npm run dev
echo   (前端运行在 http://localhost:5173)
echo.
echo [步骤5] 访问网站
echo   打开浏览器：http://localhost:5173
echo.
echo [管理员账号]
echo   用户名: admin
echo   密码: admin123
echo   注意: 首次使用需要先注册一个admin账号
echo   或者在数据库中手动更新admin的密码hash
echo.
echo ================================
pause

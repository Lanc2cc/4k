
# AI Ultimate Working Rules
## 4K Movie Website Graduation Project (SpringBoot + Vue3)

本文件是 **AI 编程助手最高效率配置**，适用于：
- Cursor
- GitHub Copilot
- ChatGPT
- Codeium

目标：
让 AI **自动完成 80% 毕业设计开发工作**。

---

# 一、项目基础信息

项目名称：4K影视资源网站

项目类型：本科毕业设计

核心目标：

构建一个完整的影视资源网站系统，支持：

- 用户注册登录
- 电影浏览
- 电影搜索
- 电影详情
- 在线播放
- 收藏电影
- 观看历史
- 后台管理系统
- 自动同步 TMDB 影视资源

系统架构：

前后端分离

B/S 架构

---

# 二、技术栈（必须遵守）

后端

Java 21  
SpringBoot 3.x  
MyBatis Plus  
MySQL 5.7  
Redis  
Maven 3.9.12  

前端

Vue3  
Element Plus  
Axios  
Vite  

第三方数据

TMDB API

---

# 三、系统核心模块

用户模块

注册  
登录  
个人中心  
观看历史  

电影模块

电影列表  
电影详情  
电影分类  
电影搜索  

播放模块

在线播放  
切换播放源  

互动模块

收藏电影  
评论电影  

后台管理

用户管理  
电影管理  
评论管理  
分类管理  
API同步管理  

---

# 四、AI开发规则（最重要）

AI 执行任务必须遵循：

1 分析需求

2 制定开发计划

3 分步骤实现

4 输出完整代码

5 保证代码可运行

如果需求不清晰

必须先询问用户

所有回复默认使用

中文

---

# 五、项目目录结构

后端

movie-backend

controller
service
serviceImpl
mapper
entity
dto
config
utils
common

前端

movie-frontend

src

api
views
components
router
store
utils

---

# 六、数据库设计

核心表

user

movie

movie_source

favorite

comment

watch_history

api_log

字段规则

主键

id

时间字段

create_time  
update_time

数据库命名

snake_case

Java命名

camelCase

---

# 七、TMDB API 同步系统

系统必须实现自动同步电影数据

同步流程

调用 TMDB API

获取 JSON

解析电影数据

去重

写入 MySQL

去重规则

movie_name + release_year

管理员可设置

每次同步数量

例如

50
100
200

触发方式

系统启动

用户登录

管理员后台手动同步

---

# 八、Redis 缓存策略

缓存数据

热门电影

电影详情

搜索结果

缓存时间

热门电影 30分钟

电影详情 1小时

搜索结果 5分钟

目标

减少数据库压力

提高响应速度

---

# 九、RESTful API 规范

接口格式

/api/module/action

示例

/api/user/login

/api/movie/list

/api/movie/detail

/api/movie/search

/api/favorite/add

返回格式

{
    "code": 200,
    "message": "success",
    "data": {}
}

---

# 十、UI设计规范

使用组件

Element Plus

UI原则

简洁

现代

统一风格

主要页面

首页

电影列表页

电影详情页

播放页

个人中心

后台管理

---

# 十一、开发顺序（非常重要）

AI 必须按顺序开发

第一阶段

用户登录注册

第二阶段

电影列表

第三阶段

电影详情

第四阶段

电影搜索

第五阶段

电影播放

第六阶段

收藏功能

第七阶段

后台管理

第八阶段

TMDB API 自动同步

---

# 十二、Git提交规范

每完成一个功能

git add .

git commit -m "feat: 功能名称"

git push

提交类型

feat 新功能

fix 修复bug

refactor 重构

docs 文档

---

# 十三、代码生成规则

AI 生成代码时必须

1 生成完整文件

2 包含 import

3 保证可编译

4 保证逻辑完整

5 提供必要说明

禁止

只给代码片段

---

# 十四、毕业设计最终目标

完成一个完整系统

包含

前端系统

后端系统

数据库

API同步系统

用户系统

后台管理系统

Redis缓存


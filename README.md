# 4K Movie

基于 Spring Boot 3 + Vue3 的前后端分离影视资源网站，支持电影浏览、在线播放、用户互动与后台管理，集成 TMDB 数据源实现电影内容自动同步。

## 技术栈

| 层级 | 技术 |
| --- | --- |
| 前端 | Vue3 · TypeScript · Element Plus · Vue Router · Pinia · Axios · Vite |
| 后端 | Java 21 · Spring Boot 3.2 · MyBatis-Plus 3.5 · Maven |
| 数据库 | MySQL 8 · Redis |
| 外部 API | TMDB (The Movie Database) |

## 功能模块

### 用户端
- 注册 / 登录（JWT 认证）
- 首页精选推荐 · 分类浏览 · 关键词搜索
- 电影详情展示 · 多播放源切换 · 在线播放
- 评论互动 · 收藏管理 · 观看历史

### 管理端
- 仪表板数据概览
- 用户治理（查询 / 状态管理）
- 电影治理（新增 / 编辑 / 删除 / 上下架）
- 播放源管理（新增 / 编辑 / 删除）
- 评论审核 · 分类维护
- TMDB 同步任务执行 · 同步日志查询

## 项目结构

```
4kmovie/
├── movie-backend/                 # Spring Boot 后端
│   └── src/main/java/com/movie/
│       ├── common/                # 统一响应体 (Result) · 分页封装 (PageResult)
│       ├── config/                # CORS · MyBatis-Plus · Redis · WebMvc 配置
│       ├── controller/            # 接口控制器
│       ├── dto/                   # 数据传输对象
│       ├── entity/                # 数据库实体
│       ├── interceptor/           # JWT 登录拦截器
│       ├── mapper/                # MyBatis-Plus Mapper 接口
│       ├── service/               # 业务接口
│       └── service/impl/          # 业务实现（含 TMDB 同步服务）
├── movie-frontend/                # Vue3 前端
│   └── src/
│       ├── api/                   # Axios 接口封装（按模块拆分）
│       ├── router/                # Vue Router（含路由守卫）
│       ├── store/                 # Pinia 用户状态管理
│       ├── utils/                 # Axios 实例与拦截器
│       └── views/                 # 页面组件
│           └── admin/             # 管理端页面
└── thesis-diagrams/               # 论文用 ER 图 / 数据流图（Mermaid 格式）
```

## 数据库设计

8 张核心表：`user` · `category` · `movie` · `movie_source` · `comment` · `favorite` · `watch_history` · `api_log`

ER 关系详见 `thesis-diagrams/er-diagram.md`。

## 快速启动

### 环境要求

- Java 21 + Maven 3.9+
- Node.js 18+
- MySQL 8.0+
- Redis 7.0+

### 1. 初始化数据库

```bash
mysql -u root -p < movie-backend/src/main/resources/schema.sql
```

### 2. 配置后端

```bash
cp movie-backend/src/main/resources/application.example.yml \
   movie-backend/src/main/resources/application.yml
```

编辑 `application.yml`，填入你的 MySQL 密码、Redis 地址和 [TMDB API Key](https://www.themoviedb.org/settings/api)。

### 3. 启动后端

```bash
cd movie-backend
mvn spring-boot:run
```

后端运行在 `http://localhost:8088`。

### 4. 启动前端

```bash
cd movie-frontend
npm install
npm run dev
```

前端运行在 `http://localhost:5173`，API 请求自动代理至后端。

### 5. 同步电影数据

登录管理员账号后，在后台 → 同步管理页面，选择同步类型和数量执行 TMDB 数据同步。系统会自动拉取电影信息、生成播放源并写入数据库。

### 默认账号

| 角色 | 用户名 | 密码 |
| --- | --- | --- |
| 管理员 | admin | admin123 |

## API 规范

- 统一响应格式：`{ "code": 200, "message": "success", "data": ... }`
- 认证方式：`Authorization: Bearer <token>`
- 接口前缀：`/api/`

## 缓存策略

| 缓存对象 | TTL | Key 前缀 |
| --- | --- | --- |
| 热门电影列表 | 30 min | `movie:hot:` |
| 电影详情 | 1 h | `movie:detail:` |
| 搜索结果 | 5 min | `movie:search:` |

TMDB 同步完成后自动清空 `movie:*` 缓存，保证数据一致性。

## License

MIT

# Thesis Figures Mermaid (V1 Full)

## 图1-1 课题研究背景与问题定位示意图

```mermaid
flowchart TB
  subgraph A[研究背景驱动]
    A1[宽带网络与终端性能持续提升]
    A2[用户对高清观影体验要求提高]
    A3[在线视频平台成为主要获取方式]
  end

  subgraph B[行业痛点]
    B1[影视资源来源分散 更新频繁]
    B2[数据结构复杂 查询压力大]
    B3[普通用户与管理员权限边界要求高]
    B4[平台内容维护与同步成本高]
  end

  subgraph C[课题定位]
    C1[构建4K影视资源管理与服务平台]
    C2[采用前后端分离架构]
    C3[建立用户端与管理端双端协同]
  end

  subgraph D[项目技术路径]
    D1[前端 Vue3 + TypeScript + Element Plus]
    D2[后端 Spring Boot + MyBatis Plus]
    D3[数据层 MySQL + Redis 缓存]
    D4[外部数据 TMDB 同步与日志追踪]
    D5[安全机制 JWT + 拦截器 + 路由守卫]
  end

  subgraph E[预期效果]
    E1[提升用户检索 播放 互动体验]
    E2[提升后台治理效率与可维护性]
    E3[形成可扩展 可演示的毕业设计成果]
  end

  A1 --> A2 --> A3
  A3 --> B1
  A3 --> B2
  A3 --> B3
  A3 --> B4

  B1 --> C1
  B2 --> C1
  B3 --> C1
  B4 --> C1

  C1 --> C2 --> C3
  C3 --> D1
  C3 --> D2
  C3 --> D3
  C3 --> D4
  C3 --> D5

  D1 --> E1
  D2 --> E2
  D3 --> E1
  D4 --> E2
  D5 --> E3
  E1 --> E3
  E2 --> E3
```

## 图1-2 系统建设目标与价值示意图

```mermaid
flowchart LR
  C[系统建设总目标<br/>构建4K影视资源管理与服务平台]

  U1[用户价值]
  U2[快速检索影片]
  U3[流畅播放体验]
  U4[评论 收藏 历史闭环]

  M1[管理价值]
  M2[电影 分类 评论治理]
  M3[TMDB自动同步]
  M4[同步日志可追踪]

  E1[工程价值]
  E2[前后端分离与分层解耦]
  E3[统一接口与分页规范]
  E4[缓存优化与权限控制]

  C --> U1 --> U2
  U1 --> U3
  U1 --> U4

  C --> M1 --> M2
  M1 --> M3
  M1 --> M4

  C --> E1 --> E2
  E1 --> E3
  E1 --> E4

  U2 --> R[预期结果]
  U3 --> R
  U4 --> R
  M2 --> R
  M3 --> R
  M4 --> R
  E2 --> R
  E3 --> R
  E4 --> R

  R[提升用户体验与运维效率]
```

## 图2-1 前端技术栈与模块关系图

```mermaid
flowchart TB
  subgraph FE[前端应用层 Vue3]
    FE1[页面层<br/>Home MovieList MovieDetail Play Search]
    FE2[管理端页面<br/>Dashboard User Movie Comment Category Sync]
    FE3[状态与路由<br/>Pinia Vue Router]
    FE4[请求封装<br/>Axios Request Interceptor]
  end

  API[RESTful API]

  subgraph BE[后端业务层 Spring Boot]
    BE1[Controller 层<br/>User Movie Comment Favorite History Admin]
    BE2[Service 层<br/>业务规则 校验 缓存策略]
    BE3[Interceptor 层<br/>JWT校验 角色鉴权]
    BE4[Mapper 层<br/>MyBatis Plus CRUD 分页]
  end

  subgraph DATA[数据与外部资源层]
    DB[(MySQL)]
    REDIS[(Redis)]
    TMDB[TMDB API]
  end

  FE1 --> FE4
  FE2 --> FE4
  FE3 --> FE4
  FE4 --> API --> BE1
  BE1 --> BE2 --> BE4 --> DB
  BE2 --> REDIS
  BE2 --> TMDB
  BE3 --> BE1
```

## 图3-1 用户端核心业务流程图

```mermaid
flowchart TD
  S([开始]) --> A[进入首页/电影列表]
  A --> B{是否搜索或筛选}
  B -- 是 --> C[输入关键词或选择分类]
  C --> D[获取电影列表]
  B -- 否 --> D

  D --> E[点击电影进入详情页]
  E --> F{是否登录}
  F -- 否 --> G[仅可浏览详情与播放源]
  F -- 是 --> H[可评论 收藏 记录历史]

  G --> I[点击播放]
  H --> I
  I --> J[进入播放页并切换线路]
  J --> K{播放是否成功}
  K -- 是 --> L[继续观影]
  K -- 否 --> M[切换播放源或返回详情]
  M --> J

  L --> N{是否产生互动行为}
  N -- 评论 --> O[提交评论并刷新评论列表]
  N -- 收藏 --> P[加入收藏或取消收藏]
  N -- 仅观看 --> Q[写入/更新时间观看历史]

  O --> R([结束])
  P --> R
  Q --> R
```

## 图4-1 系统总体架构图

```mermaid
flowchart TB
  U[用户浏览器]

  subgraph FRONT[前端层]
    F1[Vue3 页面组件]
    F2[Router 路由守卫]
    F3[Axios 请求拦截器]
  end

  subgraph BACK[后端层 Spring Boot]
    B1[Controller]
    B2[LoginInterceptor]
    B3[Service]
    B4[Mapper]
  end

  subgraph STORAGE[存储层]
    S1[(MySQL)]
    S2[(Redis)]
  end

  EXT[TMDB 外部接口]

  U --> F1
  F1 --> F2 --> F3 --> B2
  B2 --> B1 --> B3 --> B4 --> S1
  B3 <--> S2
  B3 <--> EXT
  B2 -.鉴权失败401.-> F1
  B1 -.统一Result返回.-> F1
  F1 --> U
```

## 图5-1 用户登录与权限控制实现流程图

```mermaid
sequenceDiagram
  autonumber
  participant U as 用户
  participant FE as 前端(Vue3)
  participant BE as 后端(UserController)
  participant INT as 登录拦截器
  participant DB as MySQL

  U->>FE: 输入用户名/密码并点击登录
  FE->>BE: POST /api/user/login
  BE->>DB: 查询用户信息
  DB-->>BE: 返回账号与密码哈希
  BE->>BE: 校验密码与账号状态

  alt 登录成功
    BE->>BE: 生成JWT(token)
    BE-->>FE: code=200 + token + role
    FE->>FE: 保存token到localStorage
  else 登录失败
    BE-->>FE: code!=200 + 错误信息
  end

  U->>FE: 访问个人中心/后台页
  FE->>FE: 路由守卫检查token与role
  FE->>INT: 携带Authorization请求受保护接口

  alt token有效且权限通过
    INT->>BE: 放行请求
    BE-->>FE: 返回业务数据
  else token失效或未登录
    INT-->>FE: 401 未登录/已过期
    FE->>FE: 清理登录态并跳转登录页
  else 角色不足
    INT->>BE: 请求到达管理员接口
    BE-->>FE: 403 无管理员权限
  end
```

## 图6-1 系统后续迭代演进路线图

```mermaid
flowchart LR
  V1[阶段1 当前版本 V1.0<br/>基础功能闭环]
  V2[阶段2 体验增强 V1.5<br/>多维检索 推荐雏形]
  V3[阶段3 治理增强 V2.0<br/>安全审计 可观测体系]
  V4[阶段4 工程化升级 V2.5<br/>容器化 自动化部署]
  V5[阶段5 平台化演进 V3.0<br/>多端协同 智能运营]

  V1 --> V2 --> V3 --> V4 --> V5

  V2 --> A1[个性化推荐]
  V2 --> A2[交互生态扩展]

  V3 --> B1[RBAC细粒度权限]
  V3 --> B2[日志监控告警]

  V4 --> C1[CI/CD流程]
  V4 --> C2[多环境配置管理]

  V5 --> D1[跨端统一体验]
  V5 --> D2[数据驱动运营优化]
```

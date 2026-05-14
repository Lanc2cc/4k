# 4K影视资源网系统 - 用户与管理员ER图

## 用户ER图

```mermaid
erDiagram
    USER ||--o{ COMMENT : "发布"
    USER ||--o{ FAVORITE : "收藏"
    USER ||--o{ WATCH_HISTORY : "观看"

    USER {
        int id PK
        string username
        string password
        string nickname
        string email
        string avatar
        int role
        int status
        datetime create_time
        datetime update_time
    }

    COMMENT {
        int id PK
        int user_id FK
        int movie_id FK
        string content
        int status
        datetime create_time
    }

    FAVORITE {
        int id PK
        int user_id FK
        int movie_id FK
        datetime create_time
    }

    WATCH_HISTORY {
        int id PK
        int user_id FK
        int movie_id FK
        datetime create_time
        datetime update_time
    }
```

## 管理员ER图

```mermaid
erDiagram
    ADMIN ||--o{ USER : "管理"
    ADMIN ||--o{ MOVIE : "维护"
    ADMIN ||--o{ CATEGORY : "维护"
    ADMIN ||--o{ MOVIE_SOURCE : "维护"
    ADMIN ||--o{ COMMENT : "审核"
    ADMIN ||--o{ API_LOG : "查看"

    ADMIN {
        int id PK
        string username
        string permission_level
        int role
        datetime create_time
    }

    USER {
        int id PK
        string username
        int status
        int role
    }

    MOVIE {
        int id PK
        string movie_name
        int status
        int tmdb_id
    }

    CATEGORY {
        int id PK
        string name
    }

    MOVIE_SOURCE {
        int id PK
        int movie_id FK
        string source_name
        string source_url
        string quality
    }

    COMMENT {
        int id PK
        int user_id FK
        int movie_id FK
        int status
    }

    API_LOG {
        int id PK
        string sync_type
        int sync_count
        int status
        datetime create_time
    }
```

## 关键关系说明

### 用户ER图

| 关系 | 类型 | 说明 |
| --- | --- | --- |
| User-Comment | 1对多 | 一个用户可发表多条评论 |
| User-Favorite | 1对多 | 一个用户可收藏多部电影 |
| User-WatchHistory | 1对多 | 一个用户可产生多条观看记录 |

### 管理员ER图

| 关系 | 类型 | 说明 |
| --- | --- | --- |
| Admin-User | 1对多 | 一个管理员可管理多个普通用户 |
| Admin-Movie | 1对多 | 一个管理员可维护多部电影 |
| Admin-Category | 1对多 | 一个管理员可维护多个分类 |
| Admin-MovieSource | 1对多 | 一个管理员可维护多个播放源 |
| Admin-Comment | 1对多 | 一个管理员可审核多条评论 |
| Admin-ApiLog | 1对多 | 一个管理员可查看多条同步日志 |

### 图示说明

- 用户ER图直接对应 `User.java` 及其相关行为表，便于说明普通用户业务闭环。
- 管理员ER图是根据 `AdminController` 的权限校验和后台管理接口抽象出来的逻辑图，代码里通过 `role=1` 区分管理员。
- 这两个图都保持简化，适合论文展示，不展开数据库全部字段。


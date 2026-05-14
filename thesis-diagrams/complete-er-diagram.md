# 4K 电影系统完整 E-R 图

为了确保图表简洁且逻辑清晰，本图将实体分为**核心实体**（USER, MOVIE, CATEGORY）和**关联实体**（FAVORITE, WATCH_HISTORY, COMMENT, MOVIE_SOURCE），通过星型布局减少线路交叉。

```mermaid
erDiagram
    %% 核心关联
    USER ||--o{ FAVORITE : "收藏"
    USER ||--o{ WATCH_HISTORY : "观看"
    USER ||--o{ COMMENT : "发表"

    MOVIE ||--o{ FAVORITE : "被收藏"
    MOVIE ||--o{ WATCH_HISTORY : "被观看"
    MOVIE ||--o{ COMMENT : "被评价"
    
    %% 业务关联
    MOVIE ||--o{ MOVIE_SOURCE : "关联"
    CATEGORY ||--o{ MOVIE : "归属"

    USER {
        bigint id PK "唯一ID"
        string username "用户名"
        string password "加密密码"
        string nickname "昵称"
        int role "角色权限"
    }

    MOVIE {
        bigint id PK "唯一ID"
        string movie_name "电影名称"
        int tmdb_id "外部ID"
        decimal vote_average "评分"
        bigint category_id FK "分类外键"
    }

    MOVIE_SOURCE {
        bigint id PK "唯一ID"
        bigint movie_id FK "电影外键"
        string source_name "资源名称"
        string source_url "播放地址"
    }

    CATEGORY {
        bigint id PK "唯一ID"
        string name "分类名称"
    }

    FAVORITE {
        bigint id PK "唯一ID"
        bigint user_id FK "用户外键"
        bigint movie_id FK "电影外键"
    }

    WATCH_HISTORY {
        bigint id PK "唯一ID"
        bigint user_id FK "用户外键"
        bigint movie_id FK "电影外键"
    }

    COMMENT {
        bigint id PK "唯一ID"
        bigint user_id FK "用户外键"
        bigint movie_id FK "电影外键"
        text content "评论内容"
    }
```

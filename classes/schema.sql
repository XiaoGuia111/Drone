-- ============================================================
-- 数据库初始化脚本（schema.sql）
-- 适用于 SQLite 数据库，应用启动时自动执行
-- 功能：创建 users（用户表）和 uavs（无人机表）
--       并插入默认的 admin 管理员账户
-- ============================================================

-- 用户表：存储系统登录用户信息
-- 注意：password 字段当前为明文存储，生产环境建议使用 BCrypt 加密
CREATE TABLE IF NOT EXISTS users (
    id          INTEGER PRIMARY KEY AUTOINCREMENT,  -- 主键，自增
    username    VARCHAR(50)  NOT NULL UNIQUE,        -- 用户名（唯一约束，用作登录凭证）
    password    VARCHAR(255) NOT NULL,               -- 密码（当前为明文，生产环境需加密）
    email       VARCHAR(100),                        -- 电子邮箱（预留字段）
    role        VARCHAR(20)  DEFAULT 'USER',         -- 角色：ADMIN / USER
    created_at  DATETIME,                            -- 记录创建时间
    updated_at  DATETIME                             -- 记录更新时间
);

-- 无人机表：存储每台无人机的完整信息
-- serial_number 字段存在 UNIQUE 约束，由应用层和数据库层双重保证唯一性
CREATE TABLE IF NOT EXISTS uavs (
    id               INTEGER PRIMARY KEY AUTOINCREMENT,  -- 主键，自增
    name             VARCHAR(100) NOT NULL,               -- 无人机名称（必填）
    model            VARCHAR(50)  NOT NULL,               -- 型号（必填）
    serial_number    VARCHAR(50)  NOT NULL UNIQUE,        -- 唯一序列号（业务唯一键）
    manufacturer     VARCHAR(100),                        -- 制造商
    manufacture_date DATE,                                -- 生产日期
    max_altitude     DECIMAL(10,2),                       -- 最大飞行高度（单位：米）
    max_speed        DECIMAL(10,2),                       -- 最大飞行速度（单位：km/h）
    weight           DECIMAL(10,2),                       -- 无人机重量（单位：kg）
    status           VARCHAR(20)  DEFAULT 'ACTIVE',       -- 状态：ACTIVE/MAINTENANCE/SCRAPPED
    description      TEXT,                                -- 描述备注信息
    created_at       DATETIME,                            -- 记录创建时间
    updated_at       DATETIME                             -- 记录最后更新时间
);

-- 插入默认管理员账户（用于系统初始化后首次登录）
-- 默认账号：admin / admin123
-- 注意：admin123 为明文密码，仅供测试环境使用，生产环境请修改密码
INSERT OR IGNORE INTO users (username, password, email, role, created_at, updated_at)
VALUES ('admin', 'admin123', 'admin@example.com', 'ADMIN', datetime('now'), datetime('now'));

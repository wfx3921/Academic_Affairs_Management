# 教务管理系统 (Academic Affairs Management System)

> 基于 Spring Boot + MyBatis-Plus + MySQL 的高校/培训机构教务管理平台  
> 前端采用 Vue 框架，提供完整的学生招生、学籍、教务和成绩管理功能，支持批量导入/导出 Excel、模板管理和权限控制。

---
```
项目根目录/
├── Academic_Affairs_Management/        # Spring Boot 后端工程
│   ├── pom.xml                        # Maven 配置
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   │   └── com/yourorg/academic/
│   │   │   │       ├── controller/    # 控制器
│   │   │   │       ├── service/       # 业务逻辑
│   │   │   │       ├── mapper/        # MyBatis-Plus 或 JPA 接口
│   │   │   │       ├── entity/        # 实体类
                    .
                    .
                    .
│   │   │   └── resources/
│   │   │       └── application.yml    # 配置文件
│   │   └── test/                      # 单元测试
│   └── README.md                      # 项目说明
│
├── front_academic/                    # 前端工程（如 Vue/React）
│   ├── package.json
│   ├── public/                        # 公共静态资源（index.html、favicon 等）
│   └── src/
│       ├── assets/                    # 图片、样式等资源
│       ├── components/                # 公共组件
│       ├── views/                     # 页面视图
│       ├── router/                    # 路由配置
│       ├── store/                     # 状态管理（Vuex/Pinia 或 Redux）
│       ├── utils/                     # 工具函数
│       ├── App.vue
│       └── main.js (或 main.ts)
│
├── 导入导出功能所需的excel表/            # 后端导入/导出用到的 Excel 模板
│   ├── 学生导入模板.xlsx
│   └── 成绩导出模板.xlsx
│
└── academic_affairs.sql               # 数据库建表与初始化脚本
```


## 一、技术栈

- **后端**  
  - Spring Boot  
  - MyBatis-Plus  
  - MySQL  
  - Maven  
  - Spring Security（可选，用于权限管理）  
- **前端**  
  - Vue 3 (或 React)  
  - Vuex 或 Pinia（状态管理）  
  - Vue Router（路由管理）  
  - Element Plus / Ant Design Vue（UI 组件库）  
- **辅助工具**  
  - Lombok  
  - Swagger UI（API 文档）  
  - Apache POI（Excel 导入/导出）  

---

## 二、主要功能

| 模块       | 功能项         | 子功能                         |
|------------|----------------|--------------------------------|
| **招生模块**   | 报名信息管理     | 学员报名数据增删改查               |
|            | 录取信息发送     | 录取结果推送（邮件/短信）          |
|            | 打印录取通知书   | PDF/Word 格式通知书生成           |
| **学生管理**   | 学生信息管理     | 个人基本信息、查询                 |
|            | 学籍管理       | 报到状态、学籍状态管理             |
|            | 学生奖惩管理     | 奖惩记录的增删改查                 |
| **教务模块**   | 年级 & 班级管理  | 年级、班级基础信息维护             |
|            | 专业 & 课程管理  | 专业设置、课程维护                 |
|            | 课程表管理      | 自动排课、查看/导出课程表           |
|            | 成绩管理       | 成绩录入、变更审批                 |
|            | 毕业管理       | 毕业审核、档案生成                 |
|            | 在读证明管理     | 在读证明申请与打印                 |
| **导入/导出**  | Excel 模板管理   | 批量导入学生、成绩等；一键导出报表    |
| **模板管理**   | 前端打印模板    | 各类通知书、证明、报表的 HTML/WORD 模板 |
| **权限 & 用户** | 角色管理       | 分配不同角色及其权限               |
|            | 用户管理       | 用户账号增删改查、密码重置           |

---

## 三、环境与依赖

1. **JDK 1.8+**  
2. **Maven 3.6+**  
3. **Node.js 14+ & npm 6+**（前端构建）  
4. **MySQL 5.7+**  
5. **Redis**（可选，用于缓存和会话管理）  

---

## 四、默认账号信息

* **管理员**

    * 用户名：`supadmin`
    * 密码：`123456`

* **管理员账户包含该项目全部功能**
> 强烈建议部署前修改初始用户名和密码。
---

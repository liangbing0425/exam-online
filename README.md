# 基于SSM的在线考试与自动组卷系统

## 项目简介

这是一个基于Spring Boot 3 + MyBatis Plus开发的在线考试与自动组卷系统，支持管理员、教师和学生三种角色，提供完整的考试管理功能。

## 技术栈

- **后端框架**: Spring Boot 3.2.0
- **持久层**: MyBatis Plus 3.5.5
- **数据库**: MySQL 8.0+
- **安全认证**: JWT (jjwt 0.12.3)
- **工具类**: Hutool 5.8.24
- **JDK版本**: Java 17

## 项目结构

```
exam-online/
├── src/main/java/org/college/examonline/
│   ├── common/              # 通用类
│   │   ├── Result.java      # 统一返回结果
│   │   └── JwtUtil.java     # JWT工具类
│   ├── config/              # 配置类
│   │   ├── CorsConfig.java          # 跨域配置
│   │   ├── MybatisPlusConfig.java   # MyBatis Plus配置
│   │   └── MyMetaObjectHandler.java # 自动填充配置
│   ├── controller/          # 控制器层
│   │   ├── UserController.java      # 用户管理
│   │   ├── QuestionController.java  # 题库管理
│   │   ├── PaperController.java     # 试卷管理
│   │   ├── ExamController.java      # 考试管理
│   │   └── ScoreController.java     # 成绩管理
│   ├── entity/              # 实体类
│   │   ├── User.java        # 用户
│   │   ├── Question.java    # 试题
│   │   ├── Paper.java       # 试卷
│   │   ├── Exam.java        # 考试
│   │   ├── Score.java       # 成绩
│   │   ├── Subject.java     # 科目
│   │   └── Chapter.java     # 章节
│   ├── mapper/              # Mapper接口
│   │   ├── UserMapper.java
│   │   ├── QuestionMapper.java
│   │   ├── PaperMapper.java
│   │   ├── ExamMapper.java
│   │   ├── ScoreMapper.java
│   │   ├── SubjectMapper.java
│   │   └── ChapterMapper.java
│   ├── service/             # Service接口
│   │   ├── UserService.java
│   │   ├── QuestionService.java
│   │   ├── PaperService.java
│   │   ├── ExamService.java
│   │   └── ScoreService.java
│   ├── service/impl/        # Service实现类
│   │   ├── UserServiceImpl.java
│   │   ├── QuestionServiceImpl.java
│   │   ├── PaperServiceImpl.java
│   │   ├── ExamServiceImpl.java
│   │   └── ScoreServiceImpl.java
│   ├── vo/                  # VO对象
│   │   └── LoginVO.java     # 登录VO
│   └── ExamOnlineApplication.java  # 启动类
└── src/main/resources/
    ├── application.yml      # 配置文件
    └── sql/
        └── exam_online.sql  # 数据库脚本
```

## 快速开始

### 1. 环境要求

- JDK 17+
- Maven 3.6+
- MySQL 8.0+

### 2. 数据库初始化

执行 `src/main/resources/sql/exam_online.sql` 文件创建数据库和表：

```bash
mysql -u root -p < src/main/resources/sql/exam_online.sql
```

### 3. 修改配置

编辑 `src/main/resources/application.yml`，修改数据库连接信息：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/exam_online?...
    username: your_username
    password: your_password
```

### 4. 运行项目

```bash
mvn spring-boot:run
```

或者打包后运行：

```bash
mvn clean package
java -jar target/exam-online-0.0.1-SNAPSHOT.jar
```

### 5. 访问接口

项目启动后，访问地址：`http://localhost:8080/api`

## API接口说明

### 用户管理 (/api/user)

- `POST /user/login` - 用户登录
- `GET /user/page` - 分页查询用户
- `POST /user` - 添加用户
- `PUT /user` - 更新用户
- `DELETE /user/{id}` - 删除用户
- `PUT /user/toggle/{id}` - 切换用户状态

### 题库管理 (/api/question)

- `GET /question/page` - 分页查询试题
- `GET /question/{id}` - 查询试题详情
- `POST /question` - 添加试题
- `PUT /question` - 更新试题
- `DELETE /question/{id}` - 删除试题

### 试卷管理 (/api/paper)

- `GET /paper/page` - 分页查询试卷
- `GET /paper/{id}` - 查询试卷详情
- `POST /paper` - 添加试卷
- `PUT /paper` - 更新试卷
- `DELETE /paper/{id}` - 删除试卷
- `POST /paper/auto-generate` - 自动组卷

### 考试管理 (/api/exam)

- `GET /exam/page` - 分页查询考试
- `GET /exam/{id}` - 查询考试详情
- `POST /exam` - 添加考试
- `PUT /exam` - 更新考试
- `DELETE /exam/{id}` - 删除考试
- `POST /exam/enter/{examId}` - 进入考试
- `POST /exam/submit/{examId}` - 提交考试

### 成绩管理 (/api/score)

- `GET /score/page` - 分页查询成绩
- `GET /score/my-scores/{studentId}` - 查询我的成绩
- `GET /score/statistics/{examId}` - 查询考试统计
- `POST /score/grade` - 阅卷

## 默认账户

系统初始化后提供以下测试账户（密码均为：123456）：

| 用户名 | 密码 | 角色 | 说明 |
|--------|------|------|------|
| admin | 123456 | 管理员 | 系统管理员 |
| teacher01 | 123456 | 教师 | 张教授 |
| teacher02 | 123456 | 教师 | 李老师 |
| student01 | 123456 | 学生 | 王小明 |
| student02 | 123456 | 学生 | 李小红 |
| student03 | 123456 | 学生 | 张伟 |

## 核心功能

### 1. 用户管理
- 支持管理员、教师、学生三种角色
- 用户CRUD操作
- 用户状态管理

### 2. 题库管理
- 支持单选题、多选题、判断题、填空题、简答题
- 试题难度分级（简单、中等、困难）
- 按科目、章节分类管理

### 3. 试卷管理
- 手动组卷和自动组卷
- 试卷配置管理
- 试卷发布与草稿

### 4. 考试管理
- 考试时间安排
- 防作弊机制（切屏监控）
- 考试状态管理

### 5. 成绩管理
- 自动评分（客观题）
- 手动阅卷（主观题）
- 成绩统计分析

## 注意事项

1. 首次运行前务必执行SQL脚本初始化数据库
2. 确保MySQL服务已启动
3. 检查端口8080是否被占用
4. 生产环境请修改JWT密钥和数据库密码

## 开发计划

- [ ] 完善自动组卷算法
- [ ] 实现考试防作弊功能
- [ ] 添加数据统计分析图表
- [ ] 优化阅卷流程
- [ ] 增加消息通知功能
- [ ] 支持文件上传下载

## 许可证

MIT License

## 联系方式

如有问题，请提交Issue或联系开发者。

# 前端与后端API对接修改说明

## 修改概述
已将前端HTML文件从使用mock数据改为从后端API获取真实数据，完全对齐ExamController及相关Controller的接口。

## 主要修改内容

### 1. 全局状态增强
- 新增 `currentExamId`：保存当前考试ID
- 新增 `examAnswers`：保存学生答题数据

### 2. Mock数据清理
- 删除了所有模拟的用户、试题、试卷、考试、成绩数据
- 仅保留科目和章节信息（用于表单下拉选项）

### 3. 首页 (renderDashboard)
**修改前：** 使用mockData统计数量
**修改后：** 
- 调用 `ApiService.user.getPage()` 获取用户总数
- 调用 `ApiService.question.getPage()` 获取试题总数
- 调用 `ApiService.paper.getPage()` 获取试卷总数
- 调用 `ApiService.exam.getPage()` 获取考试总数和列表
- 异步加载数据，显示实时统计

### 4. 考试管理页面 (renderExams)
**修改前：** 遍历mockData.exams显示考试列表
**修改后：**
- 调用 `ApiService.exam.getPage()` 获取考试列表
- 支持按状态筛选（进行中、未开始、已结束）
- 根据用户角色显示不同操作按钮
- 异步加载，错误处理

### 5. 我的考试页面 (renderMyExams)
**修改前：** 从mockData.exams过滤显示
**修改后：**
- 调用 `ApiService.exam.getPage()` 获取所有考试
- 前端按状态分类（ongoing、upcoming、ended）
- 学生可点击进入进行中的考试
- 可查看已结束考试的成绩

### 6. 进入考试功能 (enterExam)
**修改前：** 从mockData查找考试，使用mockData.questions显示试题
**修改后：**
- 调用 `ApiService.exam.enter(examId, studentId)` 进入考试
- 从后端返回的examData中获取试题列表
- 初始化答题状态 `state.examAnswers = []`
- 记录当前考试ID `state.currentExamId = examId`
- 启动倒计时和监控

### 7. 答题卡更新 (updateAnswerSheet)
**修改前：** 仅更新UI样式
**修改后：**
- 保存答案到 `state.examAnswers[index]`
- 记录题目索引和答案内容
- 实时更新已答/未答统计

### 8. 提交考试功能 (submitExam)
**修改前：** 仅显示提示，跳转到成绩页面
**修改后：**
- 从 `state.examAnswers` 提取答案数据
- 调用 `ApiService.exam.submit(examId, studentId, answers)` 提交
- 处理提交结果（成功/失败）
- 成功后跳转到"我的成绩"页面
- 完整的错误处理

### 9. 我的成绩页面 (renderMyScores)
**修改前：** 从mockData.scores过滤特定学生成绩
**修改后：**
- 调用 `ApiService.score.getMyScores(studentId)` 获取个人成绩
- 动态计算统计数据（参加考试数、平均分、最高分）
- 显示完整成绩记录
- 异步加载，错误处理

### 10. 成绩管理页面 (renderScores)
**修改前：** 遍历 mockData.scores 显示所有成绩
**修改后：**
- 调用 `ApiService.score.getPage()` 获取成绩列表
- 动态计算统计数据（参考人数、平均分、最高分、及格率）
- 显示完整的成绩表格
- 支持查看详情和导出操作

### 11. 题库管理页面 (renderQuestions)
**修改前：** 遍历 mockData.questions 显示试题列表
**修改后：**
- 调用 `ApiService.question.getPage()` 获取试题列表（支持分页）
- 支持按题型、科目、难度筛选
- 支持搜索功能（防抖处理）
- 异步加载，错误处理
- 正确解析后端返回的数据格式（options 为 JSON 字符串，tags 为逗号分隔字符串）

### 12. 添加试题功能 (showAddQuestionModal & saveQuestion)
**修改前：** 无实际功能或使用 mock 数据
**修改后：**
- 动态生成科目和章节下拉选项（使用 mockData.subjects 和 mockData.chapters）
- 根据题型动态显示/隐藏选项输入框
- 单选题/多选题：显示 A/B/C/D 选项输入
- 判断题：显示正确/错误单选
- 填空题/简答题：显示文本域输入答案
- 调用 `ApiService.question.add()` 提交试题数据
- 数据格式处理：
  - options 数组转为 JSON 字符串
  - tags 保持为逗号分隔的字符串
  - score 转为浮点数
  - 自动设置 status 为 'active'
- 完整的错误处理和成功提示

### 13. 编辑试题功能 (editQuestion & updateQuestion)
**修改前：** 仅显示提示信息，无实际编辑功能
**修改后：**
- 调用 `ApiService.question.getById(id)` 获取试题详情
- 解析试题数据（options、tags）
- 预填充编辑表单，包括：
  - 题型、难度、分值
  - 科目、章节（联动更新）
  - 题目内容
  - 选项（根据题型显示）
  - 正确答案（根据题型显示不同的输入方式）
  - 标签
- 调用 `ApiService.question.update()` 更新试题
- 数据格式处理同添加试题
- 完整的错误处理和成功提示

### 14. 删除试题功能 (deleteQuestion)
**修改前：** 从 mockData 中删除
**修改后：**
- 调用 `ApiService.question.delete(id)` 删除试题
- 删除成功后重新加载列表
- 完整的错误处理

### 15. 试题列表渲染 (renderQuestionCards)
**修改前：** 直接使用 mockData.questions 的属性
**修改后：**
- 正确解析后端返回的数据格式：
  - creatorName 代替 creator 显示创建者
  - options 从 JSON 字符串解析为数组
  - tags 从逗号分隔字符串解析为数组
  - score 处理 BigDecimal 类型（可能为对象）
  - createTime 格式化显示
- 根据题型显示不同的答案展示方式
- 显示完整的试题信息（题型、难度、科目、章节、分值、标签等）

## API 接口对应关系

| 前端功能 | 调用的 API | Controller 方法 |
|---------|----------|---------------|
| **题库管理** |  |  |
| 获取试题列表 | GET /question/page | QuestionController.getQuestionPage() |
| 获取试题详情 | GET /question/{id} | QuestionController.getQuestionById() |
| 添加试题 | POST /question | QuestionController.addQuestion() |
| 更新试题 | PUT /question | QuestionController.updateQuestion() |
| 删除试题 | DELETE /question/{id} | QuestionController.deleteQuestion() |
| **考试管理** |  |  |
| 获取考试列表 | GET /exam/page | ExamController.getExamPage() |
| 获取考试详情 | GET /exam/{id} | ExamController.getExamById() |
| 进入考试 | POST /exam/enter/{examId} | ExamController.enterExam() |
| 提交考试 | POST /exam/submit/{examId} | ExamController.submitExam() |
| **成绩管理** |  |  |
| 获取我的成绩 | GET /score/my-scores/{studentId} | ScoreController.getMyScores() |
| 获取成绩列表 | GET /score/page | ScoreController.getScorePage() |
| **用户管理** |  |  |
| 获取用户列表 | GET /user/page | UserController.getUserPage() |
| **试卷管理** |  |  |
| 获取试卷列表 | GET /paper/page | PaperController.getPaperPage() |

## 关键改进点

1. **异步数据加载**：所有页面渲染函数改为async/await模式
2. **错误处理**：每个API调用都有try-catch错误处理
3. **空数据处理**：当API返回空数据时显示友好提示
4. **状态管理**：使用state对象统一管理考试状态和答题数据
5. **实时统计**：统计数据从后端实时获取，而非硬编码
6. **角色权限**：根据用户角色显示不同的操作按钮
7. **数据格式转换**：正确处理前后端数据格式差异（JSON字符串、BigDecimal等）
8. **表单动态渲染**：根据题型动态显示不同的表单项
9. **防抖处理**：搜索功能使用防抖，减少API调用次数
10. **分页支持**：完整实现分页功能，包括页码计算和边界处理

## 测试建议

1. 确保后端服务运行在 http://localhost:8080
2. 测试不同角色登录（admin、teacher、student）
3. 测试考试流程：查看考试 → 进入考试 → 答题 → 提交
4. 测试成绩查看功能
5. 检查网络请求失败时的错误提示

## 注意事项

1. 后端需要正确实现所有API接口
2. 确保CORS配置允许前端访问
3. Token认证需要在请求头中正确传递
4. 考试提交时的答案格式需要与后端期望一致
5. 时间格式可能需要前后端统一

## 文件位置
- 修改的文件：`src/main/resources/R7129 -基于 SSM 的在线考试与自动组卷系统2.html`
- 总行数变化：约减少200行（删除mock数据），增加约300行（API调用逻辑）

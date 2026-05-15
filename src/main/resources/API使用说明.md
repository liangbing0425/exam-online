# 前端API调用说明文档

## 概述

前端代码已修改为从后端服务获取数据，所有API调用都封装在 `api-calls.js` 文件中。

## API配置

### 基础配置
```javascript
const API_BASE_URL = 'http://localhost:8080/api';
```

### 认证机制
- 使用JWT Token进行身份认证
- Token存储在localStorage中
- 每次请求自动携带Authorization头

## API接口列表

### 1. 用户管理 (/user)

#### 登录
```javascript
// 调用方式
const result = await API.login({ username, password, role });

// 返回数据
{
  token: "eyJhbGciOiJIUzI1NiJ9...",
  userInfo: {
    id: 1,
    username: "admin",
    name: "系统管理员",
    role: "admin",
    ...
  }
}
```

#### 分页查询用户
```javascript
// 调用方式
const result = await API.getUserPage({ 
  pageNum: 1, 
  pageSize: 10, 
  username: '', 
  role: '', 
  status: '' 
});

// 返回数据 (MyBatis Plus分页格式)
{
  records: [...],  // 用户列表
  total: 100,      // 总记录数
  size: 10,        // 每页大小
  current: 1,      // 当前页
  pages: 10        // 总页数
}
```

#### 添加用户
```javascript
await API.addUser({
  username: "testuser",
  password: "123456",
  name: "测试用户",
  role: "student",
  email: "test@example.com",
  status: "active"
});
```

#### 更新用户
```javascript
await API.updateUser({
  id: 1,
  username: "testuser",
  name: "测试用户",
  email: "test@example.com",
  role: "student"
  // password可选，不提供则不修改
});
```

#### 删除用户
```javascript
await API.deleteUser(userId);
```

#### 切换用户状态
```javascript
await API.toggleUserStatus(userId);
```

### 2. 试题管理 (/question)

#### 分页查询试题
```javascript
const result = await API.getQuestionPage({ 
  pageNum: 1, 
  pageSize: 10, 
  type: 'single',      // 可选: single, multiple, judge, fill, essay
  subject: 'Java编程',  // 可选
  difficulty: 'medium'  // 可选: easy, medium, hard
});
```

#### 获取试题详情
```javascript
const question = await API.getQuestionById(questionId);
```

#### 添加试题
```javascript
await API.addQuestion({
  type: "single",
  content: "题目内容",
  options: JSON.stringify(["选项A", "选项B", "选项C", "选项D"]),
  answer: "A",
  analysis: "答案解析",
  difficulty: "medium",
  subject: "Java编程",
  chapter: "基础语法",
  tags: JSON.stringify(["Java", "基础"]),
  score: 2.00,
  creatorId: 2,
  creatorName: "张教授"
});
```

#### 更新试题
```javascript
await API.updateQuestion(questionData);
```

#### 删除试题
```javascript
await API.deleteQuestion(questionId);
```

### 3. 试卷管理 (/paper)

#### 分页查询试卷
```javascript
const result = await API.getPaperPage({ 
  pageNum: 1, 
  pageSize: 10, 
  subject: 'Java编程',
  status: 'published'  // 可选: draft, published
});
```

#### 获取试卷详情
```javascript
const paper = await API.getPaperById(paperId);
```

#### 添加试卷
```javascript
await API.addPaper({
  name: "Java基础测试卷",
  subject: "Java编程",
  duration: 60,
  totalScore: 100.00,
  passScore: 60.00,
  questionCount: 25,
  difficulty: "medium",
  description: "试卷描述",
  config: JSON.stringify({...}),  // 组卷配置
  status: "draft",
  creatorId: 2,
  creatorName: "张教授"
});
```

#### 更新试卷
```javascript
await API.updatePaper(paperData);
```

#### 删除试卷
```javascript
await API.deletePaper(paperId);
```

#### 自动组卷
```javascript
const result = await API.autoGeneratePaper({
  subject: "Java编程",
  difficulty: "medium",
  singleChoiceCount: 10,
  multipleChoiceCount: 5,
  judgeCount: 5,
  fillCount: 3,
  essayCount: 2
});
```

### 4. 考试管理 (/exam)

#### 分页查询考试
```javascript
const result = await API.getExamPage({ 
  pageNum: 1, 
  pageSize: 10, 
  status: 'ongoing'  // 可选: upcoming, ongoing, ended
});
```

#### 获取考试详情
```javascript
const exam = await API.getExamById(examId);
```

#### 添加考试
```javascript
await API.addExam({
  paperId: 1,
  paperName: "Java基础测试卷",
  subject: "Java编程",
  startTime: "2024-12-20 09:00:00",
  endTime: "2024-12-20 10:00:00",
  duration: 60,
  status: "upcoming",
  allowReview: 1,
  randomOrder: 0,
  antiCheat: 1,
  maxSwitchTimes: 3,
  description: "考试说明",
  creatorId: 2,
  creatorName: "张教授"
});
```

#### 更新考试
```javascript
await API.updateExam(examData);
```

#### 删除考试
```javascript
await API.deleteExam(examId);
```

#### 进入考试
```javascript
const result = await API.enterExam(examId, studentId);
```

#### 提交考试
```javascript
await API.submitExam(examId, studentId, [
  {
    questionId: 1,
    answer: "A",
    questionType: "single"
  },
  {
    questionId: 2,
    answer: "ABC",
    questionType: "multiple"
  }
]);
```

### 5. 成绩管理 (/score)

#### 分页查询成绩
```javascript
const result = await API.getScorePage({ 
  pageNum: 1, 
  pageSize: 10, 
  examId: 1,
  studentNo: '2022001'
});
```

#### 获取我的成绩
```javascript
const scores = await API.getMyScores(studentId);
```

#### 获取考试统计
```javascript
const statistics = await API.getExamStatistics(examId);
```

#### 阅卷
```javascript
await API.gradePaper({
  scoreId: 1,
  subjectiveScore: 40.00,
  graderId: 2,
  graderName: "张教授",
  remark: "评语"
});
```

## 辅助函数

### 加载数据函数

#### 加载用户列表
```javascript
const result = await loadUsers(pageNum, pageSize, username, role, status);
// 数据会自动缓存到 cachedData.users
```

#### 加载试题列表
```javascript
const result = await loadQuestions(pageNum, pageSize, type, subject, difficulty);
```

#### 加载试卷列表
```javascript
const result = await loadPapers(pageNum, pageSize, subject, status);
```

#### 加载考试列表
```javascript
const result = await loadExams(pageNum, pageSize, status);
```

#### 加载成绩列表
```javascript
const result = await loadScores(pageNum, pageSize, examId, studentNo);
```

#### 加载首页数据
```javascript
const dashboardData = await loadDashboardData();
// 返回: { userCount, questionCount, paperCount, examCount }
```

### 操作处理函数

所有操作函数都会显示通知并返回操作结果：

```javascript
// 用户操作
await handleAddUser(userData);
await handleUpdateUser(userData);
await handleDeleteUser(userId);
await handleToggleUserStatus(userId);

// 试题操作
await handleAddQuestion(questionData);
await handleUpdateQuestion(questionData);
await handleDeleteQuestion(questionId);

// 试卷操作
await handleAddPaper(paperData);
await handleUpdatePaper(paperData);
await handleDeletePaper(paperId);
await handleAutoGeneratePaper(config);

// 考试操作
await handleAddExam(examData);
await handleUpdateExam(examData);
await handleDeleteExam(examId);
await handleEnterExam(examId, studentId);
await handleSubmitExam(examId, studentId, answers);

// 成绩操作
await handleGradePaper(gradeInfo);
```

## 数据缓存

所有加载的数据都会缓存在 `cachedData` 对象中：

```javascript
cachedData = {
  users: [],
  questions: [],
  papers: [],
  exams: [],
  scores: []
};
```

## 错误处理

所有API调用都包含统一的错误处理：
- 成功时显示成功通知
- 失败时显示错误通知
- 错误信息会打印到控制台

## 使用示例

### 示例1: 登录
```javascript
document.getElementById('loginForm').addEventListener('submit', async function(e) {
    e.preventDefault();
    const username = document.getElementById('loginUsername').value;
    const password = document.getElementById('loginPassword').value;
    const role = document.querySelector('input[name="role"]:checked').value;
    
    try {
        const result = await API.login({ username, password, role });
        state.token = result.token;
        state.currentUser = result.userInfo;
        localStorage.setItem('token', state.token);
        showMainSystem();
        showNotification('登录成功！');
    } catch (error) {
        showNotification('登录失败：' + error.message, 'error');
    }
});
```

### 示例2: 加载用户列表并渲染
```javascript
async function renderUsers() {
    const result = await loadUsers(1, 10);
    
    const html = result.records.map(user => `
        <tr>
            <td>${user.name}</td>
            <td>${user.role}</td>
            <td>${user.email}</td>
            <td>${user.status}</td>
        </tr>
    `).join('');
    
    document.getElementById('userTableBody').innerHTML = html;
}
```

### 示例3: 添加用户
```javascript
async function saveUser(formData) {
    const success = await handleAddUser(formData);
    if (success) {
        closeGenericModal();
        renderUsers();  // 重新加载列表
    }
}
```

## 注意事项

1. **跨域问题**: 确保后端已配置CORS（已完成）
2. **Token过期**: Token有效期24小时，过期后需要重新登录
3. **数据格式**: 注意JSON字段和字符串字段的转换
4. **异步调用**: 所有API调用都是异步的，需要使用async/await
5. **错误处理**: 建议使用try-catch包裹API调用

## 后续优化建议

1. 添加请求拦截器，统一处理Token刷新
2. 添加响应拦截器，统一处理错误
3. 实现数据预加载和缓存策略
4. 添加请求取消功能
5. 实现离线数据同步

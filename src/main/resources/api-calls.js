// ==================== 前端API调用补充文件 ====================
// 此文件需要插入到HTML文件的<script>标签中，在menuConfig定义之后

// 全局数据存储
let cachedData = {
    users: [],
    questions: [],
    papers: [],
    exams: [],
    scores: []
};

// ==================== 用户管理API调用 ====================
async function loadUsers(pageNum = 1, pageSize = 10, username = '', role = '', status = '') {
    try {
        const result = await API.getUserPage({ pageNum, pageSize, username, role, status });
        cachedData.users = result.records || [];
        return result;
    } catch (error) {
        console.error('加载用户列表失败:', error);
        return { records: [], total: 0 };
    }
}

async function handleAddUser(userData) {
    try {
        await API.addUser(userData);
        showNotification('用户添加成功！');
        return true;
    } catch (error) {
        showNotification('添加失败：' + error.message, 'error');
        return false;
    }
}

async function handleUpdateUser(userData) {
    try {
        await API.updateUser(userData);
        showNotification('用户更新成功！');
        return true;
    } catch (error) {
        showNotification('更新失败：' + error.message, 'error');
        return false;
    }
}

async function handleDeleteUser(userId) {
    if (!confirm('确定要删除该用户吗？')) return false;
    
    try {
        await API.deleteUser(userId);
        showNotification('用户删除成功！');
        return true;
    } catch (error) {
        showNotification('删除失败：' + error.message, 'error');
        return false;
    }
}

async function handleToggleUserStatus(userId) {
    try {
        await API.toggleUserStatus(userId);
        showNotification('状态更新成功！');
        return true;
    } catch (error) {
        showNotification('操作失败：' + error.message, 'error');
        return false;
    }
}

// ==================== 试题管理API调用 ====================
async function loadQuestions(pageNum = 1, pageSize = 10, type = '', subject = '', difficulty = '') {
    try {
        const result = await API.getQuestionPage({ pageNum, pageSize, type, subject, difficulty });
        cachedData.questions = result.records || [];
        return result;
    } catch (error) {
        console.error('加载试题列表失败:', error);
        return { records: [], total: 0 };
    }
}

async function handleAddQuestion(questionData) {
    try {
        await API.addQuestion(questionData);
        showNotification('试题添加成功！');
        return true;
    } catch (error) {
        showNotification('添加失败：' + error.message, 'error');
        return false;
    }
}

async function handleUpdateQuestion(questionData) {
    try {
        await API.updateQuestion(questionData);
        showNotification('试题更新成功！');
        return true;
    } catch (error) {
        showNotification('更新失败：' + error.message, 'error');
        return false;
    }
}

async function handleDeleteQuestion(questionId) {
    if (!confirm('确定要删除该试题吗？')) return false;
    
    try {
        await API.deleteQuestion(questionId);
        showNotification('试题删除成功！');
        return true;
    } catch (error) {
        showNotification('删除失败：' + error.message, 'error');
        return false;
    }
}

// ==================== 试卷管理API调用 ====================
async function loadPapers(pageNum = 1, pageSize = 10, subject = '', status = '') {
    try {
        const result = await API.getPaperPage({ pageNum, pageSize, subject, status });
        cachedData.papers = result.records || [];
        return result;
    } catch (error) {
        console.error('加载试卷列表失败:', error);
        return { records: [], total: 0 };
    }
}

async function handleAddPaper(paperData) {
    try {
        await API.addPaper(paperData);
        showNotification('试卷添加成功！');
        return true;
    } catch (error) {
        showNotification('添加失败：' + error.message, 'error');
        return false;
    }
}

async function handleUpdatePaper(paperData) {
    try {
        await API.updatePaper(paperData);
        showNotification('试卷更新成功！');
        return true;
    } catch (error) {
        showNotification('更新失败：' + error.message, 'error');
        return false;
    }
}

async function handleDeletePaper(paperId) {
    if (!confirm('确定要删除该试卷吗？')) return false;
    
    try {
        await API.deletePaper(paperId);
        showNotification('试卷删除成功！');
        return true;
    } catch (error) {
        showNotification('删除失败：' + error.message, 'error');
        return false;
    }
}

async function handleAutoGeneratePaper(config) {
    try {
        const result = await API.autoGeneratePaper(config);
        showNotification('自动组卷成功！');
        return result;
    } catch (error) {
        showNotification('组卷失败：' + error.message, 'error');
        return null;
    }
}

// ==================== 考试管理API调用 ====================
async function loadExams(pageNum = 1, pageSize = 10, status = '') {
    try {
        const result = await API.getExamPage({ pageNum, pageSize, status });
        cachedData.exams = result.records || [];
        return result;
    } catch (error) {
        console.error('加载考试列表失败:', error);
        return { records: [], total: 0 };
    }
}

async function handleAddExam(examData) {
    try {
        await API.addExam(examData);
        showNotification('考试添加成功！');
        return true;
    } catch (error) {
        showNotification('添加失败：' + error.message, 'error');
        return false;
    }
}

async function handleUpdateExam(examData) {
    try {
        await API.updateExam(examData);
        showNotification('考试更新成功！');
        return true;
    } catch (error) {
        showNotification('更新失败：' + error.message, 'error');
        return false;
    }
}

async function handleDeleteExam(examId) {
    if (!confirm('确定要删除该考试吗？')) return false;
    
    try {
        await API.deleteExam(examId);
        showNotification('考试删除成功！');
        return true;
    } catch (error) {
        showNotification('删除失败：' + error.message, 'error');
        return false;
    }
}

async function handleEnterExam(examId, studentId) {
    try {
        const result = await API.enterExam(examId, studentId);
        return result;
    } catch (error) {
        showNotification('进入考试失败：' + error.message, 'error');
        return null;
    }
}

async function handleSubmitExam(examId, studentId, answers) {
    try {
        const result = await API.submitExam(examId, studentId, answers);
        showNotification('试卷提交成功！');
        return result;
    } catch (error) {
        showNotification('提交失败：' + error.message, 'error');
        return null;
    }
}

// ==================== 成绩管理API调用 ====================
async function loadScores(pageNum = 1, pageSize = 10, examId = '', studentNo = '') {
    try {
        const result = await API.getScorePage({ pageNum, pageSize, examId, studentNo });
        cachedData.scores = result.records || [];
        return result;
    } catch (error) {
        console.error('加载成绩列表失败:', error);
        return { records: [], total: 0 };
    }
}

async function loadMyScores(studentId) {
    try {
        const result = await API.getMyScores(studentId);
        return result;
    } catch (error) {
        console.error('加载我的成绩失败:', error);
        return [];
    }
}

async function loadExamStatistics(examId) {
    try {
        const result = await API.getExamStatistics(examId);
        return result;
    } catch (error) {
        console.error('加载考试统计失败:', error);
        return null;
    }
}

async function handleGradePaper(gradeInfo) {
    try {
        await API.gradePaper(gradeInfo);
        showNotification('阅卷成功！');
        return true;
    } catch (error) {
        showNotification('阅卷失败：' + error.message, 'error');
        return false;
    }
}

// ==================== 首页数据加载 ====================
async function loadDashboardData() {
    try {
        // 并行加载各项统计数据
        const [users, questions, papers, exams] = await Promise.all([
            API.getUserPage({ pageNum: 1, pageSize: 1 }),
            API.getQuestionPage({ pageNum: 1, pageSize: 1 }),
            API.getPaperPage({ pageNum: 1, pageSize: 1 }),
            API.getExamPage({ pageNum: 1, pageSize: 1 })
        ]);
        
        return {
            userCount: users.total || 0,
            questionCount: questions.total || 0,
            paperCount: papers.total || 0,
            examCount: exams.total || 0
        };
    } catch (error) {
        console.error('加载首页数据失败:', error);
        return { userCount: 0, questionCount: 0, paperCount: 0, examCount: 0 };
    }
}

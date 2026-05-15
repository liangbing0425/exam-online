package org.college.examonline.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.college.examonline.common.Result;
import org.college.examonline.entity.*;
import org.college.examonline.mapper.ExamMapper;
import org.college.examonline.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ExamServiceImpl extends ServiceImpl<ExamMapper, Exam> implements ExamService {
    
    @Autowired
    private PaperService paperService;
    
    @Autowired
    private QuestionService questionService;
    
    @Autowired
    private ScoreService scoreService;
    
    @Autowired
    private UserService userService;
    
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    @Override
    public Page<Exam> getExamPage(Integer pageNum, Integer pageSize, String status) {
        Page<Exam> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Exam> wrapper = new LambdaQueryWrapper<>();
        
        if (StrUtil.isNotBlank(status)) {
            wrapper.eq(Exam::getStatus, status);
        }
        
        wrapper.orderByDesc(Exam::getCreateTime);
        return this.page(page, wrapper);
    }
    
    @Override
    public Result getExamDetail(Long id) {
        Exam exam = this.getById(id);
        return exam != null ? Result.success(exam) : Result.error("考试不存在");
    }
    
    @Override
    public Result addExam(Exam exam) {
        // 验证必要参数
        if (exam.getPaperId() == null) {
            return Result.error("试卷ID不能为空");
        }
        if (exam.getStartTime() == null) {
            return Result.error("开始时间不能为空");
        }
        if (exam.getDuration() == null || exam.getDuration() <= 0) {
            return Result.error("考试时长必须大于0");
        }
        
        // 如果没有设置结束时间，根据开始时间和时长计算结束时间
        if (exam.getEndTime() == null) {
            exam.setEndTime(exam.getStartTime().plusMinutes(exam.getDuration()));
        }
        
        // 验证结束时间是否合理（应该晚于开始时间）
        if (exam.getEndTime().isBefore(exam.getStartTime())) {
            return Result.error("结束时间不能早于开始时间");
        }
        
        // 验证结束时间与开始时间的差值是否等于时长
        long actualMinutes = java.time.Duration.between(exam.getStartTime(), exam.getEndTime()).toMinutes();
        if (Math.abs(actualMinutes - exam.getDuration()) > 1) {
            // 允许1分钟的误差
            exam.setEndTime(exam.getStartTime().plusMinutes(exam.getDuration()));
        }
        
        // 设置默认值
        if (exam.getStatus() == null) {
            exam.setStatus("upcoming");
        }
        if (exam.getParticipantCount() == null) {
            exam.setParticipantCount(0);
        }
        if (exam.getSubmittedCount() == null) {
            exam.setSubmittedCount(0);
        }
        if (exam.getAllowReview() == null) {
            exam.setAllowReview(1);
        }
        if (exam.getRandomOrder() == null) {
            exam.setRandomOrder(0);
        }
        if (exam.getAntiCheat() == null) {
            exam.setAntiCheat(0);
        }
        if (exam.getMaxSwitchTimes() == null) {
            exam.setMaxSwitchTimes(3);
        }
        
        return this.save(exam) ? Result.success() : Result.error("添加失败");
    }
    
    @Override
    public Result updateExam(Exam exam) {
        // 如果更新了开始时间或时长，需要重新计算结束时间
        if (exam.getStartTime() != null && exam.getDuration() != null && exam.getDuration() > 0) {
            // 如果没有提供结束时间，或者提供了但与时长不匹配，则重新计算
            if (exam.getEndTime() == null) {
                exam.setEndTime(exam.getStartTime().plusMinutes(exam.getDuration()));
            } else {
                long actualMinutes = java.time.Duration.between(exam.getStartTime(), exam.getEndTime()).toMinutes();
                if (Math.abs(actualMinutes - exam.getDuration()) > 1) {
                    // 允许1分钟的误差，否则重新计算
                    exam.setEndTime(exam.getStartTime().plusMinutes(exam.getDuration()));
                }
            }
        }
        
        return this.updateById(exam) ? Result.success() : Result.error("更新失败");
    }
    
    @Override
    public Result deleteExam(Long id) {
        return this.removeById(id) ? Result.success() : Result.error("删除失败");
    }
    
    @Override
    @Transactional(readOnly = true)
    public Result enterExam(Long examId, Long studentId) {
        // 1. 获取考试信息
        Exam exam = this.getById(examId);
        if (exam == null) {
            return Result.error("考试不存在");
        }
        
        // 2. 检查考试状态
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(exam.getStartTime())) {
            return Result.error("考试尚未开始");
        }
        if (now.isAfter(exam.getEndTime())) {
            return Result.error("考试已结束");
        }
        
        // 3. 获取试卷信息
        Paper paper = paperService.getById(exam.getPaperId());
        if (paper == null) {
            return Result.error("试卷不存在");
        }
        
        // 4. 获取试卷中的所有题目（通过paper_question表）
        List<Map<String, Object>> paperQuestions = baseMapper.getPaperQuestions(exam.getPaperId());
        
        if (paperQuestions == null || paperQuestions.isEmpty()) {
            return Result.error("该试卷暂无题目");
        }
        
        // 5. 获取完整的题目信息
        List<Map<String, Object>> questions = new ArrayList<>();
        for (Map<String, Object> pq : paperQuestions) {
            Long questionId = ((Number) pq.get("question_id")).longValue();
            Question question = questionService.getById(questionId);
            
            if (question != null) {
                Map<String, Object> questionMap = new HashMap<>();
                questionMap.put("id", question.getId());
                questionMap.put("type", question.getType());
                questionMap.put("content", question.getContent());
                
                // 解析options JSON字符串为List
                try {
                    if (question.getOptions() != null && !question.getOptions().isEmpty()) {
                        // 使用Jackson解析JSON字符串
                        com.fasterxml.jackson.databind.ObjectMapper objectMapper = new com.fasterxml.jackson.databind.ObjectMapper();
                        List<String> optionsList = objectMapper.readValue(question.getOptions(), List.class);
                        questionMap.put("options", optionsList);
                    } else {
                        questionMap.put("options", new ArrayList<>());
                    }
                } catch (Exception e) {
                    // 如果解析失败，尝试简单的手动解析
                    try {
                        String optionsStr = question.getOptions();
                        // 移除方括号
                        optionsStr = optionsStr.replaceAll("^\\[|]$", "").trim();
                        if (!optionsStr.isEmpty()) {
                            // 按逗号分割并去除引号
                            String[] optionsArray = optionsStr.split(",");
                            List<String> optionsList = new ArrayList<>();
                            for (String opt : optionsArray) {
                                String cleaned = opt.trim().replaceAll("^\"|\"$", "").trim();
                                if (!cleaned.isEmpty()) {
                                    optionsList.add(cleaned);
                                }
                            }
                            questionMap.put("options", optionsList);
                        } else {
                            questionMap.put("options", new ArrayList<>());
                        }
                    } catch (Exception ex) {
                        questionMap.put("options", new ArrayList<>());
                    }
                }
                
                questionMap.put("difficulty", question.getDifficulty());
                questionMap.put("score", pq.get("question_score")); // 使用试卷中配置的分值
                questionMap.put("sortOrder", pq.get("sort_order"));
                
                // 注意：不返回正确答案，防止作弊
                // questionMap.put("answer", question.getAnswer()); // 这行不要
                
                questions.add(questionMap);
            }
        }
        
        // 6. 如果需要随机顺序，打乱题目
        if (exam.getRandomOrder() != null && exam.getRandomOrder() == 1) {
            Collections.shuffle(questions);
        } else {
            // 否则按排序字段排序
            questions.sort(Comparator.comparing(q -> (Integer) q.get("sortOrder")));
        }
        
        // 7. 构建返回数据
        Map<String, Object> resultData = new HashMap<>();
        resultData.put("examId", exam.getId());
        resultData.put("paperName", exam.getPaperName());
        resultData.put("subject", exam.getSubject());
        resultData.put("duration", exam.getDuration());
        resultData.put("totalScore", paper.getTotalScore());
        resultData.put("allowReview", exam.getAllowReview());
        resultData.put("antiCheat", exam.getAntiCheat());
        resultData.put("maxSwitchTimes", exam.getMaxSwitchTimes());
        resultData.put("questions", questions);
        
        return Result.success(resultData);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result submitExam(Long examId, Long studentId, List<Map<String, Object>> answers) {
        try {
            // 1. 获取考试信息
            Exam exam = this.getById(examId);
            if (exam == null) {
                return Result.error("考试不存在");
            }
            
            // 2. 获取试卷信息
            Paper paper = paperService.getById(exam.getPaperId());
            if (paper == null) {
                return Result.error("试卷不存在");
            }
            
            // 3. 获取学生信息
            User student = userService.getById(studentId);
            if (student == null) {
                return Result.error("学生不存在");
            }
            
            // 4. 获取试卷中的所有题目（通过paper_question表）
            List<Map<String, Object>> paperQuestions = baseMapper.getPaperQuestions(exam.getPaperId());
            if (paperQuestions == null || paperQuestions.isEmpty()) {
                return Result.error("该试卷暂无题目");
            }
            
            // 5. 计算分数
            BigDecimal objectiveScore = BigDecimal.ZERO; // 客观题分数
            BigDecimal subjectiveScore = BigDecimal.ZERO; // 主观题分数（暂时为0，需要人工阅卷）
            
            // 构建答案映射：questionIndex -> answer
            Map<Integer, String> answerMap = new HashMap<>();
            if (answers != null) {
                for (Map<String, Object> ans : answers) {
                    Integer questionIndex = (Integer) ans.get("questionIndex");
                    String answer = (String) ans.get("answer");
                    if (questionIndex != null && answer != null) {
                        answerMap.put(questionIndex, answer);
                    }
                }
            }
            
            // 6. 逐题判分
            int questionIndex = 0;
            for (Map<String, Object> pq : paperQuestions) {
                Long questionId = ((Number) pq.get("question_id")).longValue();
                Question question = questionService.getById(questionId);
                
                if (question != null) {
                    BigDecimal questionScore = new BigDecimal(pq.get("question_score").toString());
                    String studentAnswer = answerMap.get(questionIndex);
                    
                    // 只处理客观题（单选、多选、判断）
                    if ("single".equals(question.getType()) || "multiple".equals(question.getType()) || "judge".equals(question.getType())) {
                        // 对比答案
                        if (studentAnswer != null && question.getAnswer() != null) {
                            // 标准化答案（去除空格、转大写）
                            String normalizedStudentAnswer = studentAnswer.trim().toUpperCase();
                            String normalizedCorrectAnswer = question.getAnswer().trim().toUpperCase();
                            
                            // 多选题需要排序后比较
                            if ("multiple".equals(question.getType())) {
                                // 将答案转换为字符数组并排序
                                char[] studentChars = normalizedStudentAnswer.toCharArray();
                                char[] correctChars = normalizedCorrectAnswer.toCharArray();
                                Arrays.sort(studentChars);
                                Arrays.sort(correctChars);
                                String sortedStudentAnswer = new String(studentChars);
                                String sortedCorrectAnswer = new String(correctChars);
                                
                                if (sortedStudentAnswer.equals(sortedCorrectAnswer)) {
                                    objectiveScore = objectiveScore.add(questionScore);
                                }
                            } else {
                                // 单选题和判断题直接比较
                                if (normalizedStudentAnswer.equals(normalizedCorrectAnswer)) {
                                    objectiveScore = objectiveScore.add(questionScore);
                                }
                            }
                        }
                    }
                    // 主观题（填空、简答）暂不计分，需要人工阅卷
                    
                    questionIndex++;
                }
            }
            
            // 7. 计算总分
            BigDecimal totalScore = objectiveScore.add(subjectiveScore);
            
            // 8. 创建成绩记录
            Score score = new Score();
            score.setExamId(examId);
            score.setExamName(exam.getPaperName());
            score.setStudentId(studentId);
            score.setStudentNo(student.getUsername()); // 使用username作为学号
            score.setStudentName(student.getName());
            score.setTotalScore(totalScore);
            score.setObjectiveScore(objectiveScore);
            score.setSubjectiveScore(subjectiveScore);
            score.setStatus("graded"); // 已阅卷（客观题已自动评分）
            score.setSubmitTime(LocalDateTime.now());
            score.setGradeTime(LocalDateTime.now());
            score.setGraderId(0L); // 系统自动阅卷
            score.setGraderName("系统");
            
            // 9. 保存成绩
            scoreService.save(score);
            
            // 10. 构建返回数据
            Map<String, Object> resultData = new HashMap<>();
            resultData.put("scoreId", score.getId());
            resultData.put("totalScore", totalScore);
            resultData.put("objectiveScore", objectiveScore);
            resultData.put("subjectiveScore", subjectiveScore);
            resultData.put("examName", exam.getPaperName());
            resultData.put("submitTime", score.getSubmitTime());
            
            return Result.success(resultData);
            
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("提交考试失败：" + e.getMessage());
        }
    }
    
    @Override
    public void updateExamStatus() {
        LocalDateTime now = LocalDateTime.now();
        
        // 查询所有需要更新状态的考试
        LambdaQueryWrapper<Exam> wrapper = new LambdaQueryWrapper<>();
        List<Exam> exams = this.list(wrapper);
        
        for (Exam exam : exams) {
            String newStatus = determineExamStatus(exam, now);
            
            // 如果状态发生变化，则更新
            if (!newStatus.equals(exam.getStatus())) {
                exam.setStatus(newStatus);
                this.updateById(exam);
            }
        }
    }
    
    /**
     * 根据当前时间和考试时间确定考试状态
     * @param exam 考试对象
     * @param now 当前时间
     * @return 考试状态: upcoming-未开始, ongoing-进行中, ended-已结束
     */
    private String determineExamStatus(Exam exam, LocalDateTime now) {
        if (exam.getStartTime() != null) {
            // 如果当前时间在开始时间之前，状态为未开始
            if (now.isBefore(exam.getStartTime())) {
                return "upcoming"; // 未开始
            }
            
            // 如果设置了结束时间，优先使用结束时间判断
            if (exam.getEndTime() != null) {
                if (now.isAfter(exam.getEndTime())) {
                    return "ended"; // 已结束
                } else {
                    return "ongoing"; // 进行中
                }
            }
            
            // 如果没有设置结束时间，但有考试时长，则根据开始时间+时长计算结束时间
            if (exam.getDuration() != null && exam.getDuration() > 0) {
                LocalDateTime calculatedEndTime = exam.getStartTime().plusMinutes(exam.getDuration());
                if (now.isAfter(calculatedEndTime)) {
                    return "ended"; // 已结束
                } else {
                    return "ongoing"; // 进行中
                }
            }
            
            // 如果既没有结束时间也没有时长，开始后默认为进行中
            return "ongoing";
        }
        
        // 如果没有设置开始时间，默认为未开始
        return "upcoming";
    }
}

package org.college.examonline.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.college.examonline.common.Result;
import org.college.examonline.entity.Question;
import org.college.examonline.mapper.QuestionMapper;
import org.college.examonline.service.QuestionService;
import org.springframework.stereotype.Service;

@Service
public class QuestionServiceImpl extends ServiceImpl<QuestionMapper, Question> implements QuestionService {
    
    @Override
    public Page<Question> getQuestionPage(Integer pageNum, Integer pageSize, String type, String subject, String difficulty) {
        Page<Question> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Question> wrapper = new LambdaQueryWrapper<>();
        
        if (StrUtil.isNotBlank(type)) {
            wrapper.eq(Question::getType, type);
        }
        if (StrUtil.isNotBlank(subject)) {
            wrapper.eq(Question::getSubject, subject);
        }
        if (StrUtil.isNotBlank(difficulty)) {
            wrapper.eq(Question::getDifficulty, difficulty);
        }
        
        wrapper.orderByDesc(Question::getCreateTime);
        return this.page(page, wrapper);
    }
    
    @Override
    public Result addQuestion(Question question) {
        // 1. 验证必要参数
        if (question.getType() == null || question.getType().trim().isEmpty()) {
            return Result.error("题目类型不能为空");
        }
        
        if (question.getContent() == null || question.getContent().trim().isEmpty()) {
            return Result.error("题目内容不能为空");
        }
        
        // 2. 验证题目类型
        String type = question.getType().toLowerCase();
        if (!"single".equals(type) && !"multiple".equals(type) && 
            !"judge".equals(type) && !"fill".equals(type) && !"essay".equals(type)) {
            return Result.error("题目类型不合法，只能是：single/multiple/judge/fill/essay");
        }
        
        // 3. 选择题和判断题必须有选项
        if (("single".equals(type) || "multiple".equals(type)) && 
            (question.getOptions() == null || question.getOptions().trim().isEmpty())) {
            return Result.error("选择题必须设置选项");
        }
        
        // 4. 所有题目必须有答案
        if (question.getAnswer() == null || question.getAnswer().trim().isEmpty()) {
            return Result.error("答案不能为空");
        }
        
        // 5. 标准化题目类型（转小写）
        question.setType(type);
        
        // 6. 设置默认值
        if (question.getDifficulty() == null || question.getDifficulty().trim().isEmpty()) {
            question.setDifficulty("medium"); // 默认中等难度
        }
        
        if (question.getScore() == null) {
            question.setScore(new java.math.BigDecimal("5.0")); // 默认5分
        }
        
        if (question.getStatus() == null || question.getStatus().trim().isEmpty()) {
            question.setStatus("active"); // 默认启用
        }
        
        // 7. 处理tags字段（如果是数组，转换为JSON字符串）
        if (question.getTags() != null && !question.getTags().trim().isEmpty()) {
            // 如果tags已经是JSON格式，保持不变
            // 如果不是，可以尝试转换
            if (!question.getTags().startsWith("[") && !question.getTags().startsWith("\"")) {
                // 假设是逗号分隔的字符串，转换为JSON数组
                String[] tagArray = question.getTags().split(",");
                StringBuilder jsonTags = new StringBuilder("[");
                for (int i = 0; i < tagArray.length; i++) {
                    if (i > 0) jsonTags.append(",");
                    jsonTags.append("\"").append(tagArray[i].trim()).append("\"");
                }
                jsonTags.append("]");
                question.setTags(jsonTags.toString());
            }
        }
        
        // 8. 保存题目
        try {
            boolean success = this.save(question);
            if (success) {
                return Result.success();
            } else {
                return Result.error("添加试题失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("添加试题失败：" + e.getMessage());
        }
    }
    
    @Override
    public Result updateQuestion(Question question) {
        return this.updateById(question) ? Result.success() : Result.error("更新失败");
    }
    
    @Override
    public Result deleteQuestion(Long id) {
        return this.removeById(id) ? Result.success() : Result.error("删除失败");
    }
}

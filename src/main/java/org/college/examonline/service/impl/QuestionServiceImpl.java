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
        return this.save(question) ? Result.success() : Result.error("添加失败");
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

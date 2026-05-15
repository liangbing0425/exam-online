package org.college.examonline.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.college.examonline.common.Result;
import org.college.examonline.entity.Question;

public interface QuestionService extends IService<Question> {
    
    Page<Question> getQuestionPage(Integer pageNum, Integer pageSize, String type, String subject, String difficulty);
    
    Result addQuestion(Question question);
    
    Result updateQuestion(Question question);
    
    Result deleteQuestion(Long id);
}

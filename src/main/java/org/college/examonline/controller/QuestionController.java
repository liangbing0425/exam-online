package org.college.examonline.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.college.examonline.common.Result;
import org.college.examonline.entity.Question;
import org.college.examonline.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/question")
public class QuestionController {
    
    @Autowired
    private QuestionService questionService;
    
    @GetMapping("/page")
    public Result getQuestionPage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String subject,
            @RequestParam(required = false) String difficulty) {
        Page<Question> page = questionService.getQuestionPage(pageNum, pageSize, type, subject, difficulty);
        return Result.success(page);
    }
    
    @GetMapping("/{id}")
    public Result getQuestionById(@PathVariable Long id) {
        Question question = questionService.getById(id);
        return Result.success(question);
    }
    
    @PostMapping
    public Result addQuestion(@RequestBody Question question) {
        return questionService.addQuestion(question);
    }
    
    @PutMapping
    public Result updateQuestion(@RequestBody Question question) {
        return questionService.updateQuestion(question);
    }
    
    @DeleteMapping("/{id}")
    public Result deleteQuestion(@PathVariable Long id) {
        return questionService.deleteQuestion(id);
    }
}

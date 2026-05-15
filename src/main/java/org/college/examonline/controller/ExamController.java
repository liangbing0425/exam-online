package org.college.examonline.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.college.examonline.common.Result;
import org.college.examonline.entity.Exam;
import org.college.examonline.service.ExamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/exam")
public class ExamController {
    
    @Autowired
    private ExamService examService;
    
    @GetMapping("/page")
    public Result getExamPage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String status) {
        Page<Exam> page = examService.getExamPage(pageNum, pageSize, status);
        return Result.success(page);
    }
    
    @GetMapping("/{id}")
    public Result getExamById(@PathVariable Long id) {
        return examService.getExamDetail(id);
    }
    
    @PostMapping
    public Result addExam(@RequestBody Exam exam) {
        return examService.addExam(exam);
    }
    
    @PutMapping
    public Result updateExam(@RequestBody Exam exam) {
        return examService.updateExam(exam);
    }
    
    @DeleteMapping("/{id}")
    public Result deleteExam(@PathVariable Long id) {
        return examService.deleteExam(id);
    }
    
    @PostMapping("/enter/{examId}")
    public Result enterExam(@PathVariable Long examId, @RequestParam Long studentId) {
        return examService.enterExam(examId, studentId);
    }
    
    @PostMapping("/submit/{examId}")
    public Result submitExam(@PathVariable Long examId, 
                            @RequestParam Long studentId, 
                            @RequestBody List<Map<String, Object>> answers) {
        return examService.submitExam(examId, studentId, answers);
    }
    
    /**
     * 手动触发更新所有考试的状态
     */
    @PostMapping("/update-status")
    public Result updateExamStatus() {
        examService.updateExamStatus();
        return Result.success("考试状态更新成功");
    }
}

package org.college.examonline.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.college.examonline.common.Result;
import org.college.examonline.entity.Score;
import org.college.examonline.service.ScoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/score")
public class ScoreController {
    
    @Autowired
    private ScoreService scoreService;
    
    @GetMapping("/page")
    public Result getScorePage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Long examId,
            @RequestParam(required = false) String studentNo) {
        Page<Score> page = scoreService.getScorePage(pageNum, pageSize, examId, studentNo);
        return Result.success(page);
    }
    
    @GetMapping("/my-scores/{studentId}")
    public Result getMyScores(@PathVariable Long studentId) {
        return scoreService.getMyScores(studentId);
    }
    
    @GetMapping("/statistics/{examId}")
    public Result getExamStatistics(@PathVariable Long examId) {
        return scoreService.getExamStatistics(examId);
    }
    
    @PostMapping("/grade")
    public Result gradePaper(@RequestBody Map<String, Object> gradeInfo) {
        return scoreService.gradePaper(gradeInfo);
    }
}

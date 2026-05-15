package org.college.examonline.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.college.examonline.common.Result;
import org.college.examonline.entity.Paper;
import org.college.examonline.service.PaperService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/paper")
public class PaperController {
    
    @Autowired
    private PaperService paperService;
    
    @GetMapping("/page")
    public Result getPaperPage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String subject,
            @RequestParam(required = false) String status) {
        Page<Paper> page = paperService.getPaperPage(pageNum, pageSize, subject, status);
        return Result.success(page);
    }
    
    @GetMapping("/{id}")
    public Result getPaperById(@PathVariable Long id) {
        return paperService.getPaperDetail(id);
    }
    
    @PostMapping
    public Result addPaper(@RequestBody Paper paper) {
        return paperService.addPaper(paper);
    }
    
    @PutMapping
    public Result updatePaper(@RequestBody Paper paper) {
        return paperService.updatePaper(paper);
    }
    
    @DeleteMapping("/{id}")
    public Result deletePaper(@PathVariable Long id) {
        return paperService.deletePaper(id);
    }
    
    @PostMapping("/auto-generate")
    public Result autoGeneratePaper(@RequestBody Map<String, Object> config) {
        return paperService.autoGeneratePaper(config);
    }
    
    @GetMapping("/{id}/questions")
    public Result getPaperQuestions(@PathVariable Long id) {
        return paperService.getPaperQuestions(id);
    }
}

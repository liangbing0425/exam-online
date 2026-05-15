package org.college.examonline.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.college.examonline.common.Result;
import org.college.examonline.entity.Exam;
import org.college.examonline.mapper.ExamMapper;
import org.college.examonline.service.ExamService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ExamServiceImpl extends ServiceImpl<ExamMapper, Exam> implements ExamService {
    
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
        return this.save(exam) ? Result.success() : Result.error("添加失败");
    }
    
    @Override
    public Result updateExam(Exam exam) {
        return this.updateById(exam) ? Result.success() : Result.error("更新失败");
    }
    
    @Override
    public Result deleteExam(Long id) {
        return this.removeById(id) ? Result.success() : Result.error("删除失败");
    }
    
    @Override
    public Result enterExam(Long examId, Long studentId) {
        // TODO: 实现进入考试逻辑
        return Result.success();
    }
    
    @Override
    public Result submitExam(Long examId, Long studentId, List<Map<String, Object>> answers) {
        // TODO: 实现提交考试逻辑
        return Result.success();
    }
}

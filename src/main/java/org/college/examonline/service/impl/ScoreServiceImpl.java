package org.college.examonline.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.college.examonline.common.Result;
import org.college.examonline.entity.Score;
import org.college.examonline.mapper.ScoreMapper;
import org.college.examonline.service.ScoreService;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class ScoreServiceImpl extends ServiceImpl<ScoreMapper, Score> implements ScoreService {
    
    @Override
    public Page<Score> getScorePage(Integer pageNum, Integer pageSize, Long examId, String studentNo) {
        Page<Score> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Score> wrapper = new LambdaQueryWrapper<>();
        
        if (examId != null) {
            wrapper.eq(Score::getExamId, examId);
        }
        if (StrUtil.isNotBlank(studentNo)) {
            wrapper.eq(Score::getStudentNo, studentNo);
        }
        
        wrapper.orderByDesc(Score::getCreateTime);
        return this.page(page, wrapper);
    }
    
    @Override
    public Result getMyScores(Long studentId) {
        LambdaQueryWrapper<Score> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Score::getStudentId, studentId);
        wrapper.orderByDesc(Score::getCreateTime);
        return Result.success(this.list(wrapper));
    }
    
    @Override
    public Result getExamStatistics(Long examId) {
        // TODO: 实现考试统计逻辑
        return Result.success();
    }
    
    @Override
    public Result gradePaper(Map<String, Object> gradeInfo) {
        // TODO: 实现阅卷逻辑
        return Result.success();
    }
}

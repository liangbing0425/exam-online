package org.college.examonline.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.college.examonline.common.Result;
import org.college.examonline.entity.Paper;
import org.college.examonline.mapper.PaperMapper;
import org.college.examonline.service.PaperService;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class PaperServiceImpl extends ServiceImpl<PaperMapper, Paper> implements PaperService {
    
    @Override
    public Page<Paper> getPaperPage(Integer pageNum, Integer pageSize, String subject, String status) {
        Page<Paper> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Paper> wrapper = new LambdaQueryWrapper<>();
        
        if (StrUtil.isNotBlank(subject)) {
            wrapper.eq(Paper::getSubject, subject);
        }
        if (StrUtil.isNotBlank(status)) {
            wrapper.eq(Paper::getStatus, status);
        }
        
        wrapper.orderByDesc(Paper::getCreateTime);
        return this.page(page, wrapper);
    }
    
    @Override
    public Result getPaperDetail(Long id) {
        Paper paper = this.getById(id);
        return paper != null ? Result.success(paper) : Result.error("试卷不存在");
    }
    
    @Override
    public Result addPaper(Paper paper) {
        return this.save(paper) ? Result.success() : Result.error("添加失败");
    }
    
    @Override
    public Result updatePaper(Paper paper) {
        return this.updateById(paper) ? Result.success() : Result.error("更新失败");
    }
    
    @Override
    public Result deletePaper(Long id) {
        return this.removeById(id) ? Result.success() : Result.error("删除失败");
    }
    
    @Override
    public Result autoGeneratePaper(Map<String, Object> config) {
        // TODO: 实现自动组卷逻辑
        return Result.success();
    }
}

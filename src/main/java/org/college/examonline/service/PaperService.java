package org.college.examonline.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.college.examonline.common.Result;
import org.college.examonline.entity.Paper;
import java.util.Map;

public interface PaperService extends IService<Paper> {
    
    Page<Paper> getPaperPage(Integer pageNum, Integer pageSize, String subject, String status);
    
    Result getPaperDetail(Long id);
    
    Result addPaper(Paper paper);
    
    Result updatePaper(Paper paper);
    
    Result deletePaper(Long id);
    
    Result autoGeneratePaper(Map<String, Object> config);
}

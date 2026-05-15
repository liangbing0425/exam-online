package org.college.examonline.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("chapter")
public class Chapter implements Serializable {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long subjectId;
    
    private String name;
    
    private Long parentId;
    
    private Integer sortOrder;
    
    private String description;
    
    private String status;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    // 暂时禁用逻辑删除功能
    // @TableLogic
    // private Integer deleted;
}

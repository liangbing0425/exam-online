package org.college.examonline.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.college.examonline.entity.User;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}

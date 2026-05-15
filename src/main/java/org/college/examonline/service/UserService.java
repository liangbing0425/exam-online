package org.college.examonline.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.college.examonline.entity.User;
import org.college.examonline.common.Result;
import org.college.examonline.vo.LoginVO;

public interface UserService extends IService<User> {
    
    Result login(LoginVO loginVO);
    
    Page<User> getUserPage(Integer pageNum, Integer pageSize, String username, String role, String status);
    
    Result addUser(User user);
    
    Result updateUser(User user);
    
    Result deleteUser(Long id);
    
    Result toggleStatus(Long id);
}

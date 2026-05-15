package org.college.examonline.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.college.examonline.common.JwtUtil;
import org.college.examonline.common.Result;
import org.college.examonline.entity.User;
import org.college.examonline.mapper.UserMapper;
import org.college.examonline.service.UserService;
import org.college.examonline.vo.LoginVO;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    
    @Override
    public Result login(LoginVO loginVO) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, loginVO.getUsername());
        wrapper.eq(User::getRole, loginVO.getRole());
        wrapper.eq(User::getStatus, "active");
        
        User user = this.getOne(wrapper);
        if (user == null) {
            return Result.error("用户名或密码错误");
        }
        
        if (!SecureUtil.md5(loginVO.getPassword()).equals(user.getPassword())) {
            return Result.error("用户名或密码错误");
        }
        
        Map<String, Object> tokenMap = new HashMap<>();
        tokenMap.put("userId", user.getId());
        tokenMap.put("username", user.getUsername());
        tokenMap.put("role", user.getRole());
        String token = JwtUtil.generateToken(tokenMap);
        
        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("userInfo", user);
        
        return Result.success(result);
    }
    
    @Override
    public Page<User> getUserPage(Integer pageNum, Integer pageSize, String username, String role, String status) {
        Page<User> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        
        if (StrUtil.isNotBlank(username)) {
            wrapper.like(User::getUsername, username)
                   .or()
                   .like(User::getName, username);
        }
        if (StrUtil.isNotBlank(role)) {
            wrapper.eq(User::getRole, role);
        }
        if (StrUtil.isNotBlank(status)) {
            wrapper.eq(User::getStatus, status);
        }
        
        wrapper.orderByDesc(User::getCreateTime);
        return this.page(page, wrapper);
    }
    
    @Override
    public Result addUser(User user) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, user.getUsername());
        if (this.count(wrapper) > 0) {
            return Result.error("用户名已存在");
        }
        
        user.setPassword(SecureUtil.md5(user.getPassword()));
        if (StrUtil.isBlank(user.getStatus())) {
            user.setStatus("active");
        }
        return this.save(user) ? Result.success() : Result.error("添加失败");
    }
    
    @Override
    public Result updateUser(User user) {
        User existUser = this.getById(user.getId());
        if (existUser == null) {
            return Result.error("用户不存在");
        }
        
        if (StrUtil.isNotBlank(user.getPassword())) {
            user.setPassword(SecureUtil.md5(user.getPassword()));
        } else {
            user.setPassword(existUser.getPassword());
        }
        
        return this.updateById(user) ? Result.success() : Result.error("更新失败");
    }
    
    @Override
    public Result deleteUser(Long id) {
        return this.removeById(id) ? Result.success() : Result.error("删除失败");
    }
    
    @Override
    public Result toggleStatus(Long id) {
        User user = this.getById(id);
        if (user == null) {
            return Result.error("用户不存在");
        }
        
        user.setStatus("active".equals(user.getStatus()) ? "inactive" : "active");
        return this.updateById(user) ? Result.success() : Result.error("操作失败");
    }
}

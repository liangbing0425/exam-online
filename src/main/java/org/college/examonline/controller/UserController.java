package org.college.examonline.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.college.examonline.common.Result;
import org.college.examonline.entity.User;
import org.college.examonline.service.UserService;
import org.college.examonline.vo.LoginVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    @PostMapping("/login")
    public Result login(@RequestBody LoginVO loginVO) {
        return userService.login(loginVO);
    }
    
    @GetMapping("/page")
    public Result getUserPage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) String status) {
        Page<User> page = userService.getUserPage(pageNum, pageSize, username, role, status);
        return Result.success(page);
    }
    
    @PostMapping
    public Result addUser(@RequestBody User user) {
        return userService.addUser(user);
    }
    
    @PutMapping
    public Result updateUser(@RequestBody User user) {
        return userService.updateUser(user);
    }
    
    @DeleteMapping("/{id}")
    public Result deleteUser(@PathVariable Long id) {
        return userService.deleteUser(id);
    }
    
    @PutMapping("/toggle/{id}")
    public Result toggleStatus(@PathVariable Long id) {
        return userService.toggleStatus(id);
    }
}

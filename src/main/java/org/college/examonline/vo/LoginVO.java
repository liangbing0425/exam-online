package org.college.examonline.vo;

import lombok.Data;

@Data
public class LoginVO {
    private String username;
    private String password;
    private String role;
}

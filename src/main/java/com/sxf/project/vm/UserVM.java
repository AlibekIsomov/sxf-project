 package com.sxf.project.vm;

 import lombok.Data;

 @Data
public class UserVM {

    private String username;

    private String oldPassword;

    private String newPassword;

    private String confirm;

}

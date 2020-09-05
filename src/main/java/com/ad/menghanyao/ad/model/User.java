package com.ad.menghanyao.ad.model;

import lombok.Data;

@Data
public class User {
    private Long userId;
    private String userName;
    private Long userPhone;
    private String userPassword;
    private String userToken;
    private String userGender;
    private Integer userAge;
    private String userCity;
    private Boolean isStudent;
    private Integer userLevel;
    private Integer userType;
    private Integer userStatus;
    private Long userCash;
    private Long gmtCreate;
    private Long gmtModified;
}

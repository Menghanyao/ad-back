package com.ad.menghanyao.ad.dto;

import lombok.Data;

@Data
public class UserDTO {
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

package com.ad.menghanyao.ad.mapper;

import com.ad.menghanyao.ad.model.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.beans.factory.annotation.Value;

import java.util.ArrayList;
import java.util.List;

@Mapper
public interface UserMapper {

    @Select("select * from user")
    ArrayList<User> UserList();

    @Insert("insert into user" +
            "(user_name,user_phone,user_password,user_token,user_gender," +
            "user_age,user_city,is_student,user_level,user_type," +
            "user_status,user_cash,gmt_create,gmt_modified)" +
            "values (#{userName},#{userPhone},#{userPassword},#{userToken},#{userGender}," +
            "#{userAge},#{userCity},#{isStudent},#{userLevel},#{userType}," +
            "#{userStatus},#{userCash},#{gmtCreate},#{gmtModified})")
    void addUser(User user);

    @Select("select count(1) from user")
    Long userCount();

    @Select("select * from user order by gmt_create desc limit #{limit} offset #{offset}")
    List<User> userList(@Value("offset") Integer offset, @Value("limit") Integer limit);

    @Select("select count(1) from user where gmt_create >= #{todayZeroTimestamp} ")
    Long userCountToday(@Value("todayZeroTimestamp") Long todayZeroTimestamp);

    @Select("select user_type from user where user_id = #{userId}")
    Integer getUserType(@Value("userId") Long userId);

    @Select("select count(1) from user where user_type = 0 or user_type = 3 or user_type = 4")
    Long userCountRootAdminWait();

    @Select("select * from user " +
            "where user_type = 0 or user_type = 3 or user_type = 4 " +
            "order by gmt_create desc " +
            "limit #{limit} offset #{offset}")
    List<User> userListRootAdminWait(@Value("offset") Integer offset, @Value("limit") Integer limit);

    @Update("update user set user_type = #{userType} , gmt_modified = #{gmtModified} where user_id = #{userId}")
    void adminPass(@Value("userId") Long userId, @Value("userType") Integer userType, @Value("gmtModified") Long gmtModified);

    @Select("select * from user where user_id = #{userId}")
    User getUserByUserId(@Value("userId") Long userId);

    @Select("select * from user where user_phone = #{userPhone}")
    User getUserByUserPhone(@Value("userPhone") Long userPhone);

    @Update("update user set user_token = #{userToken} , gmt_modified = #{gmtModified} where user_id = #{userId}")
    void updateUser(@Value("userId") Long userId, @Value("userToken") String userToken, @Value("gmtModified") Long gmtModified);

    @Select("select count(1) from user where gmt_create > #{start} and gmt_create < #{end}")
    Long getUserCountBetween(@Value("#start") Long start, @Value("end") Long end);
    @Select("select count(1) from shop where gmt_create > #{start} and gmt_create < #{end}")
    Long getShopCountBetween(@Value("#start") Long start, @Value("end") Long end);
    @Select("select count(1) from ad where gmt_create > #{start} and gmt_create < #{end}")
    Long getAdCountBetween(@Value("#start") Long start, @Value("end") Long end);

    @Select("select count(1) from user where user_phone = #{userPhone}")
    long getUserCountByUserPhone(@Value("userPhone") Long userPhone);

    @Update("update user set user_cash = #{userCash} where user_id = #{userId}")
    void updateUserCash(Long userId, Long userCash);

    @Update("update user set user_name = #{userName} , user_gender = #{userGender} , user_age = #{userAge} ," +
            " is_student = #{isStudent} , user_city = #{userCity} , user_token = #{userToken} ," +
            " gmt_modified = #{gmtModified} where user_id = #{userId}")
    void changeUser(User user);
}

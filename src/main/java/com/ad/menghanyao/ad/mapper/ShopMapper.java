package com.ad.menghanyao.ad.mapper;

import com.ad.menghanyao.ad.model.Shop;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;

@Mapper
public interface ShopMapper {

    @Insert("insert into shop" +
            "(shop_id,user_id,shop_name,shop_type,shop_description,shop_city," +
            "shop_address,shop_status,shop_phone,shop_arrange,shop_cash," +
            "issue_count,click_count,like_count,dislike_count,gmt_create,gmt_modified)" +
            "values" +
            "(#{shopId},#{userId},#{shopName},#{shopType},#{shopDescription},#{shopCity}," +
            "#{shopAddress},#{shopStatus},#{shopPhone},#{shopArrange},#{shopCash}," +
            "#{issueCount},#{clickCount},#{likeCount},#{dislikeCount},#{gmtCreate},#{gmtModified})")
    void addShop(Shop shop);

    @Select("select count(1) from shop")
    Long shopCountAll();

    @Select("select count(1) from shop where user_id = #{userId}")
    Long shopCountByUserId(@Value("userId") Long userId);

    @Select("select * from shop order by gmt_create desc limit #{limit} offset #{offset}")
    List<Shop> shopListAll(@Value("offset") Integer offset, @Value("limit") Integer limit);

    @Select("select * from shop where user_id = #{userId} order by gmt_create desc limit #{limit} offset #{offset}")
    List<Shop> shopListByUserId(@Value("userId") Long userId, @Value("offset") Integer offset, @Value("limit") Integer limit);

    @Select("select count(1) from shop where gmt_create > #{todayZeroTimestamp}")
    Long shopCountToday(Long todayZeroTimestamp);

    @Select("select shop_cash from shop where shop_id = #{shopId}")
    Long getShopCash(@Value("shopId") Long shopId);

    @Update("update shop set shop_cash = #{newMoney} , gmt_modified = #{gmtModified} where shop_id = #{shopId}")
    void setShopCash(@Value("shopId") Long shopId, @Value("newMoney") Long newMoney, @Value("gmtModified") Long gmtModified);

    @Update("update shop set shop_status = #{shopStatus} , gmt_modified = #{gmtModified} where shop_id = #{shopId}")
    void forbidOrRelieveShop(@Value("shopId") Long shopId, @Value("shopStatus") Integer shopStatus, @Value("gmtModified") Long gmtModified);

    @Select("select * from shop where shop_status = 0")
    List<Shop> shopListForbidden();

    @Update("update shop set shop_status = 0 , gmt_modified = #{gmtModified} where shop_id = #{shopId}")
    void forbidShop(@Value("shopId") Long shopId, @Value("shopStatus") Integer shopStatus, @Value("gmtModified") Long gmtModified);
}

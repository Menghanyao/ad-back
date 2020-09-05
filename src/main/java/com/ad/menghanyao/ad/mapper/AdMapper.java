package com.ad.menghanyao.ad.mapper;

import com.ad.menghanyao.ad.dto.AdDTO;
import com.ad.menghanyao.ad.model.Ad;
import org.apache.ibatis.annotations.*;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;

@Mapper
public interface AdMapper {

    @Insert("insert into ad" +
            "(user_id , shop_id , ad_id , ad_name , ad_description , " +
            "ad_picture , ad_type , ad_time , ad_progress , ad_cash , " +
            "issue_count , click_count , like_count , dislike_count , gmt_create , gmt_modified)" +
            "values" +
            "(#{userId} , #{shopId} , #{adId}, #{adName} , #{adDescription}," +
            "#{adPicture} , #{adType} , #{adTime} , #{adProgress} , #{adCash}," +
            "#{issueCount} , #{clickCount} , #{likeCount} , #{dislikeCount} , #{gmtCreate} , #{gmtModified})")
    void addAd(Ad ad);

    @Select("select count(1) from ad")
    Long adCountAll();

    @Select("select count(1) from ad where user_id = #{userId}")
    Long adCountByUserId(@Value("userId") Long userId);

    @Select("select * from ad order by gmt_create desc limit #{limit} offset #{offset}")
    List<Ad> adListAll(@Value("offset") Integer offset, @Value("limit") Integer limit);

    @Select("select * from ad where user_id = #{userId} order by gmt_create desc limit #{limit} offset #{offset}")
    List<Ad> adListByUserId(@Value("userId") Long userId, @Value("offset") Integer offset, @Value("limit") Integer limit);

    @Select("select count(1) from ad where gmt_create > #{todayZeroTimestamp}")
    Long adCountToday(@Value("todayZeroTimestamp") Long todayZeroTimestamp);

    @Delete("delete from ad where ad_id = #{adId}")
    void deleteAd(@Value("adId") Long adId);

    @Select("select * from ad where ad_id = #{adId}")
    AdDTO getAdByAdId(@Value("adId") Long adId);

    @Update("update ad set issue_count = #{issueCount} , click_count = #{clickCount} , like_count = #{likeCount} , " +
            "dislike_count = #{dislikeCount} , ad_progress = #{adProgress} , gmt_modified = #{gmtModified} where ad_id = #{adId}")
    void updateAd(Ad ad);

    @Select("select * from ad order by click_count desc limit #{size}")
    List<Ad> getTop50(@Value("size") Integer size);

    @Select("select * from ad where gmt_create > #{todayZeroTimestamp} limit 1 offset #{offset} ")
    Ad getNew(@Value("offset") Integer random, @Value("todayZeroTimestamp") Long todayZeroTimestamp);

    @Select("select * from ad limit 1 offset #{offset} ")
    Ad getRandom(@Value("offset") Integer random);

    @Select("select count(1) from ad where ad_type = #{adType}")
    Integer getCountByAdType(@Value("adType") Integer recommendationType);

    @Select("select * from ad where ad_type = #{adType} limit 1 offset #{offset}")
    Ad getAdByAdType(@Value("adType") Integer recommendationType, @Value("offset") Integer random);
}

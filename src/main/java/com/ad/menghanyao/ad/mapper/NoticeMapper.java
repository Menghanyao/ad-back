package com.ad.menghanyao.ad.mapper;

import com.ad.menghanyao.ad.model.Notice;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;

@Mapper
public interface NoticeMapper {

    @Insert("insert into notice" +
            "(notice_id , shop_id , ad_id , from_id , to_id , " +
            "notice_reason , notice_status , notice_type , gmt_create , gmt_modified)" +
            "values" +
            "(#{noticeId} , #{shopId} , #{adId}, #{fromId} , #{toId}," +
            "#{noticeReason} , #{noticeStatus} , #{noticeType} , #{gmtCreate} , #{gmtModified})")
    void addNotice(Notice notice);

    @Select("select count(1) from notice where notice_type = 1")
    Long getAllViolateCount();

    @Select("select count(1) from notice where to_id = #{toId}")
    Long getNoticeCountByToId(@Value("toId") Long userId);

    @Select("select * from notice where notice_type = 1 order by gmt_create desc limit #{limit} offset #{offset}")
    List<Notice> getAllViolateList(@Value("offset") Integer offset, @Value("limit") Integer limit);

    @Select("select * from notice where to_id = #{toId} order by gmt_create desc limit #{limit} offset #{offset}")
    List<Notice> getNoticeListByToId(@Value("toId") Long toId, @Value("offset") Integer offset, @Value("limit") Integer limit);

    @Select("select count(1) from notice where notice_status = 10 or notice_status = 11 or notice_status = 12")
    Long getAllApplyCount();

    @Select("select * from notice " +
            "where notice_status = 10 or notice_status = 11 or notice_status = 12 " +
            "order by gmt_create desc limit #{limit} offset #{offset}")
    List<Notice> getAllApplyList(@Value("offset") Integer offset, @Value("limit") Integer limit);

    @Update("update notice set notice_status = #{noticeStatus} , gmt_modified = #{gmtModified} where notice_id = #{noticeId}")
    void updateNoticeStatus(@Value("noticeId") Long noticeId, @Value("noticeStatus") Integer noticeStatus, @Value("gmtModified") long gmtModified);
}

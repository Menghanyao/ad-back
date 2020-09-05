package com.ad.menghanyao.ad.mapper;

import com.ad.menghanyao.ad.model.Record;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;

@Mapper
public interface RecordMapper {

    @Insert("insert into record" +
            "(user_id, ad_id, ad_type, operation, gmt_create)" +
            "values " +
            "(#{userId}, #{adId}, #{adType}, #{operation}, #{gmtCreate})")
    void addRecord(Record record);

    @Select("select * from record")
    List<Record> getRecord();

    @Select("select count(1) from record where gmt_create > #{time}")
    Long adCountByTime(@Value("time") long time);

    @Select("select user_id,ad_id,operation from record where gmt_create > #{time}")
    List<Record> getRecordAfter(@Value("time") Long time);
}

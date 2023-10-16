package com.apon.chen.mapper;

import com.apon.chen.entity.Hospital;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * @author chen
 */
@Mapper
public interface HospitalMapper extends BaseMapper<Hospital> {

    //查询所有
    @Select("select * from hospital")
    List<Hospital> selectAllHospital();

    @Select("select * from hospital where YEAR(install_time)=2023")
    List<Hospital> select2023();

    @Update("update hospital set ssh_status=0 WHERE ssh_port=#{ssh_port}")
    int updateErrStatusByPort(@Param("ssh_port") Integer ssh_port);

    @Update("update hospital set ssh_status=1 WHERE ssh_port=#{ssh_port}")
    int updateStatusByPort(@Param("ssh_port") Integer ssh_port);

}

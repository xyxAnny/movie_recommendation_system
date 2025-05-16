package cn.zjw.mrs.mapper;

import cn.zjw.mrs.entity.RegionLike;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
* @author 95758
* @description 针对表【region_like】的数据库操作Mapper
* @createDate 2022-04-20 16:13:15
* @Entity cn.zjw.mrs.entity.RegionLike
*/
@Repository
public interface RegionLikeMapper extends BaseMapper<RegionLike> {
    /**
     * 使用SQL直接插入地区喜好记录
     * @param uid 用户ID
     * @param rid 地区ID
     * @param degree 喜好程度
     * @return 影响的行数
     */
    @Insert("INSERT INTO region_like(uid, rid, degree) VALUES(#{uid}, #{rid}, #{degree})")
    int insertDirectly(@Param("uid") Long uid, @Param("rid") Integer rid, @Param("degree") Integer degree);
    
    /**
     * 删除用户的所有地区喜好记录
     * @param uid 用户ID
     * @return 影响的行数
     */
    @Delete("DELETE FROM region_like WHERE uid = #{uid}")
    int deleteByUserId(@Param("uid") Long uid);
}





package cn.zjw.mrs.mapper;

import cn.zjw.mrs.entity.TypeLike;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
* @author 95758
* @description 针对表【type_like】的数据库操作Mapper
* @createDate 2022-04-20 16:13:21
* @Entity cn.zjw.mrs.entity.TypeLike
*/
@Repository
public interface TypeLikeMapper extends BaseMapper<TypeLike> {
    /**
     * 使用SQL直接插入类型喜好记录
     * @param uid 用户ID
     * @param tid 类型ID
     * @param degree 喜好程度
     * @return 影响的行数
     */
    @Insert("INSERT INTO type_like(uid, tid, degree) VALUES(#{uid}, #{tid}, #{degree})")
    int insertDirectly(@Param("uid") Long uid, @Param("tid") Integer tid, @Param("degree") Integer degree);
    
    /**
     * 删除用户的所有类型喜好记录
     * @param uid 用户ID
     * @return 影响的行数
     */
    @Delete("DELETE FROM type_like WHERE uid = #{uid}")
    int deleteByUserId(@Param("uid") Long uid);
}





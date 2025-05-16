package cn.zjw.mrs.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 
 * @author zjw
 * @TableName type_like
 */
@TableName(value ="type_like")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TypeLike implements Serializable {
    /**
     * 用户id
     */
    private Long uid;

    /**
     * 电影类型id
     */
    private Integer tid;

    /**
     * 喜爱程度
     */
    private Integer degree;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
    
    /**
     * 获取用户ID
     * @return 用户ID
     */
    public Long getUid() {
        return uid;
    }

    /**
     * 设置用户ID
     * @param uid 用户ID
     */
    public void setUid(Long uid) {
        this.uid = uid;
    }

    /**
     * 获取类型ID
     * @return 类型ID
     */
    public Integer getTid() {
        return tid;
    }

    /**
     * 设置类型ID
     * @param tid 类型ID
     */
    public void setTid(Integer tid) {
        this.tid = tid;
    }

    /**
     * 获取喜爱程度
     * @return 喜爱程度
     */
    public Integer getDegree() {
        return degree;
    }

    /**
     * 设置喜爱程度
     * @param degree 喜爱程度
     */
    public void setDegree(Integer degree) {
        this.degree = degree;
    }
}
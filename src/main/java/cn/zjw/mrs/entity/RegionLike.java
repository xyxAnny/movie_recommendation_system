package cn.zjw.mrs.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author zjw
 * @TableName region_like
 */
@TableName(value ="region_like")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegionLike implements Serializable {
    /**
     * 用户id
     */
    private Long uid;

    /**
     * 电影地区id
     */
    private Integer rid;

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
     * 获取地区ID
     * @return 地区ID
     */
    public Integer getRid() {
        return rid;
    }

    /**
     * 设置地区ID
     * @param rid 地区ID
     */
    public void setRid(Integer rid) {
        this.rid = rid;
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
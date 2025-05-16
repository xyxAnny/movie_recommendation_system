package cn.zjw.mrs.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

/**
 * @author zjw
 * @Classname SexEnum
 * @Date 2022/4/11 23:40
 * @Description
 */
@Getter
public enum SexEnum {
    // 男
    MALE(1, "男"),
    // 女
    FEMALE(0, "女"),
    // 保密
    SECRET(2, "保密");

    /**
     * 性别标识
     * 将注解所标识的属性的值存储到数据库中
     */
    @EnumValue
    private final Integer sex;
    /**
     * 性别名称
     */
    private final String sexName;

    SexEnum(Integer sex, String sexName) {
        this.sex = sex;
        this.sexName = sexName;
    }

    /**
     * 通过性别名称查找性别标识
     * @param sexName 性别名称
     * @return 性别标识
     */
    public static Integer findSexBySexName(String sexName) {
        System.out.println("接收到的性别参数: '" + sexName + "'");
        
        // 处理可能的JSON格式问题
        sexName = sexName.replace("\"", "").trim();
        
        for (SexEnum sexEnum : SexEnum.values()) {
            if (sexEnum.getSexName().equals(sexName)) {
                return sexEnum.getSex();
            }
        }
        System.out.println("无法匹配性别参数: '" + sexName + "'");
        return SECRET.getSex(); // 默认返回保密而不是抛出异常
    }

    /**
     * 通过性别标识查找性别名称
     * @param sex 性别标识
     * @return 性别名称
     */
    public static String findSexNameBySex(Integer sex) {
        for (SexEnum sexEnum : SexEnum.values()) {
            if (sexEnum.getSex().equals(sex)) {
                return sexEnum.getSexName();
            }
        }
        throw new IllegalArgumentException("sex is invalid");
    }
}

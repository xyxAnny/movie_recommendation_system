package cn.zjw.mrs.service.impl;

import cn.zjw.mrs.entity.RegionLike;
import cn.zjw.mrs.entity.TypeLike;
import cn.zjw.mrs.enums.TypeEnum;
import cn.zjw.mrs.mapper.RegionLikeMapper;
import cn.zjw.mrs.mapper.TypeLikeMapper;
import cn.zjw.mrs.service.TypeLikeService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

/**
* @author zjw
* @description 针对表【type_like】的数据库操作Service实现
* @createDate 2022-04-20 16:13:21
*/
@Service
public class TypeLikeServiceImpl extends ServiceImpl<TypeLikeMapper, TypeLike>
    implements TypeLikeService{

    @Resource
    private TypeLikeMapper typeLikeMapper;

    @Override
    public int updateUserTypeLike(Long id, int[] types) {
        System.out.println("==================================================================");
        System.out.println("开始处理类型喜好更新，用户ID: " + id);
        
        // 防御性检查
        if (id == null || id <= 0) {
            System.err.println("无效的用户ID: " + id);
            return 0;
        }
        
        if (types == null) {
            System.err.println("类型数组为null");
            return 0;
        }
        
        System.out.println("接收到的类型数组: " + Arrays.toString(types));
        
        try {
            // 检查前端传来的类型ID是否有效
            for (int i = 0; i < types.length; i++) {
                try {
                    // 注意：前端传来的就是枚举索引值，范围为0-20
                    // 直接使用这些值作为tid，不需要额外转换
                    if (types[i] < 0 || types[i] > 20) {
                        System.err.println("无效的类型ID: " + types[i] + "，应该在0-20范围内");
                    } else {
                        // 验证类型是否存在于枚举
                        try {
                            String typeName = TypeEnum.findTypeNameByType(types[i]);
                            System.out.println("类型ID: " + types[i] + " 有效，对应名称: " + typeName);
                        } catch (Exception e) {
                            System.err.println("类型ID: " + types[i] + " 在枚举中不存在: " + e.getMessage());
                        }
                    }
                } catch (Exception e) {
                    System.err.println("检查类型ID时出错: " + e.getMessage());
                }
            }
            
            // 查询用户现有的类型喜好
            QueryWrapper<TypeLike> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("uid", id);
            List<TypeLike> existingLikes = typeLikeMapper.selectList(queryWrapper);
            System.out.println("用户现有类型喜好数量: " + existingLikes.size());
            for (TypeLike like : existingLikes) {
                System.out.println("现有喜好 - ID: " + like.getTid() + ", 程度: " + like.getDegree());
            }
            
            int len = Math.min(types.length, 5);
            System.out.println("将处理前" + len + "个类型项");
            
            // 先删除该用户所有已有的类型喜好
            int deleteCount = typeLikeMapper.deleteByUserId(id);
            System.out.println("已删除该用户现有的" + deleteCount + "个类型喜好记录");
            
            if (len == 0) {
                System.out.println("没有收到任何有效的类型喜好，不进行插入操作");
                return 0;
            }
            
            int cnt = 0;
            // 然后逐个插入新的类型喜好
            for (int i = 0; i < len; ++i) {
                // 确保类型ID在有效范围内
                if (types[i] < 0 || types[i] > 20) {
                    System.err.println("跳过无效的类型ID: " + types[i]);
                    continue;
                }
                
                System.out.println("尝试插入类型喜好记录: uid=" + id + ", tid=" + types[i] + ", degree=" + i);
                try {
                    // types[i]是前端传来的枚举索引，直接用作tid
                    cnt += typeLikeMapper.insertDirectly(id, types[i], i);
                    System.out.println("插入成功");
                } catch (Exception e) {
                    System.err.println("插入失败: " + e.getMessage());
                    e.printStackTrace();
                }
            }
            System.out.println("成功插入" + cnt + "条类型喜好记录");
            
            // 验证结果
            List<TypeLike> newLikes = typeLikeMapper.selectList(
                new QueryWrapper<TypeLike>().eq("uid", id)
            );
            System.out.println("插入后用户类型喜好数量: " + newLikes.size());
            for (TypeLike like : newLikes) {
                System.out.println("新喜好 - ID: " + like.getTid() + ", 程度: " + like.getDegree());
            }
            
            System.out.println("==================================================================");
            return cnt;
        } catch (Exception e) {
            System.err.println("更新类型喜好时发生异常: " + e.getMessage());
            e.printStackTrace();
            return 0;
        }
    }
}





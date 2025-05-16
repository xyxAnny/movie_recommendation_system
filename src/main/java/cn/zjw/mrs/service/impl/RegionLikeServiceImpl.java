package cn.zjw.mrs.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.zjw.mrs.entity.RegionLike;
import cn.zjw.mrs.enums.RegionEnum;
import cn.zjw.mrs.service.RegionLikeService;
import cn.zjw.mrs.mapper.RegionLikeMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

/**
* @author zjw
* @description 针对表【region_like】的数据库操作Service实现
* @createDate 2022-04-20 16:13:15
*/
@Service
public class RegionLikeServiceImpl extends ServiceImpl<RegionLikeMapper, RegionLike>
    implements RegionLikeService{

    @Resource
    private RegionLikeMapper regionLikeMapper;

    @Override
    public int updateUserRegionLike(Long id, int[] regions) {
        System.out.println("==================================================================");
        System.out.println("开始处理地区喜好更新，用户ID: " + id);
        
        // 防御性检查
        if (id == null || id <= 0) {
            System.err.println("无效的用户ID: " + id);
            return 0;
        }
        
        if (regions == null) {
            System.err.println("地区数组为null");
            return 0;
        }
        
        System.out.println("接收到的地区数组: " + Arrays.toString(regions));
        
        try {
            // 检查前端传来的地区ID是否有效
            for (int i = 0; i < regions.length; i++) {
                try {
                    // 注意：前端传来的就是枚举索引值，范围为0-20
                    // 直接使用这些值作为rid，不需要额外转换
                    if (regions[i] < 0 || regions[i] > 20) {
                        System.err.println("无效的地区ID: " + regions[i] + "，应该在0-20范围内");
                    } else {
                        // 验证地区是否存在于枚举
                        try {
                            String regionName = RegionEnum.findRegionNameByRegion(regions[i]);
                            System.out.println("地区ID: " + regions[i] + " 有效，对应名称: " + regionName);
                        } catch (Exception e) {
                            System.err.println("地区ID: " + regions[i] + " 在枚举中不存在: " + e.getMessage());
                        }
                    }
                } catch (Exception e) {
                    System.err.println("检查地区ID时出错: " + e.getMessage());
                }
            }
            
            // 查询用户现有的地区喜好
            QueryWrapper<RegionLike> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("uid", id);
            List<RegionLike> existingLikes = regionLikeMapper.selectList(queryWrapper);
            System.out.println("用户现有地区喜好数量: " + existingLikes.size());
            for (RegionLike like : existingLikes) {
                System.out.println("现有喜好 - ID: " + like.getRid() + ", 程度: " + like.getDegree());
            }
            
            int len = Math.min(regions.length, 5);
            System.out.println("将处理前" + len + "个地区项");
            
            // 先删除该用户所有已有的地区喜好
            int deleteCount = regionLikeMapper.deleteByUserId(id);
            System.out.println("已删除该用户现有的" + deleteCount + "个地区喜好记录");
            
            if (len == 0) {
                System.out.println("没有收到任何有效的地区喜好，不进行插入操作");
                return 0;
            }
            
            int cnt = 0;
            // 然后逐个插入新的地区喜好
            for (int i = 0; i < len; ++i) {
                // 确保地区ID在有效范围内
                if (regions[i] < 0 || regions[i] > 20) {
                    System.err.println("跳过无效的地区ID: " + regions[i]);
                    continue;
                }
                
                System.out.println("尝试插入地区喜好记录: uid=" + id + ", rid=" + regions[i] + ", degree=" + i);
                try {
                    // regions[i]是前端传来的枚举索引，直接用作rid
                    cnt += regionLikeMapper.insertDirectly(id, regions[i], i);
                    System.out.println("插入成功");
                } catch (Exception e) {
                    System.err.println("插入失败: " + e.getMessage());
                    e.printStackTrace();
                }
            }
            System.out.println("成功插入" + cnt + "条地区喜好记录");
            
            // 验证结果
            List<RegionLike> newLikes = regionLikeMapper.selectList(
                new QueryWrapper<RegionLike>().eq("uid", id)
            );
            System.out.println("插入后用户地区喜好数量: " + newLikes.size());
            for (RegionLike like : newLikes) {
                System.out.println("新喜好 - ID: " + like.getRid() + ", 程度: " + like.getDegree());
            }
            
            System.out.println("==================================================================");
            return cnt;
        } catch (Exception e) {
            System.err.println("更新地区喜好时发生异常: " + e.getMessage());
            e.printStackTrace();
            return 0;
        }
    }
}





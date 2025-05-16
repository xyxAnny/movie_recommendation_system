package cn.zjw.mrs.controller;

import cn.zjw.mrs.entity.LoginUser;
import cn.zjw.mrs.entity.Result;
import cn.zjw.mrs.entity.User;
import cn.zjw.mrs.entity.RegionLike;
import cn.zjw.mrs.entity.TypeLike;
import cn.zjw.mrs.mapper.UserMapper;
import cn.zjw.mrs.mapper.RegionLikeMapper;
import cn.zjw.mrs.mapper.TypeLikeMapper;
import cn.zjw.mrs.service.*;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zjw
 * @Classname UserPreferenceController
 * @Date 2022/4/20 16:14
 * @Description
 */
@RestController
@RequestMapping("/user/like")
public class UserPreferenceController {
    @Resource
    private RegionLikeService regionLikeService;

    @Resource
    private TypeLikeService typeLikeService;

    @Resource
    private UserService userService;
    
    @Resource
    private UserMapper userMapper;
    
    @Resource
    private RegionLikeMapper regionLikeMapper;
    
    @Resource
    private TypeLikeMapper typeLikeMapper;

    @Resource
    private RecommendationService recommendationService;

    @GetMapping
    public Result<?> getTypesAndRegions(Authentication authentication) {
        try {
            // 直接获取用户名，然后从数据库查询用户ID
            String username = authentication.getName();
            System.out.println("获取用户偏好 - 用户名: " + username);
            
            // 从数据库获取用户ID
            Long userId = getUserIdByUsername(username);
            System.out.println("获取用户偏好 - 用户ID: " + userId);
            
            Map<String, List<?>> typesAndRegions = userService.getTypesAndRegions(userId);
            System.out.println("获取用户偏好 - 结果: " + typesAndRegions);
            
            return Result.success(typesAndRegions);
        } catch (Exception e) {
            System.err.println("获取用户偏好时发生异常: " + e.getMessage());
            e.printStackTrace();
            return Result.error(500, "获取用户偏好失败: " + e.getMessage());
        }
    }
    
    // 添加一个调试端点，用于直接检查数据库中的偏好数据
    @GetMapping("/debug")
    public Result<?> debugPreferences(Authentication authentication) {
        try {
            // 直接获取用户名，然后从数据库查询用户ID
            String username = authentication.getName();
            Long userId = getUserIdByUsername(username);
            System.out.println("调试偏好 - 用户名: " + username + ", 用户ID: " + userId);
            
            // 从数据库直接查询用户的类型和地区喜好
            List<TypeLike> typeLikes = typeLikeMapper.selectList(
                new QueryWrapper<TypeLike>().eq("uid", userId)
            );
            System.out.println("调试偏好 - 类型喜好数量: " + typeLikes.size());
            
            List<RegionLike> regionLikes = regionLikeMapper.selectList(
                new QueryWrapper<RegionLike>().eq("uid", userId)
            );
            System.out.println("调试偏好 - 地区喜好数量: " + regionLikes.size());
            
            // 还获取正常的类型和地区数据，用于比较
            Map<String, List<?>> typesAndRegions = userService.getTypesAndRegions(userId);
            
            Map<String, Object> result = new HashMap<>();
            result.put("userId", userId);
            result.put("typeLikes", typeLikes);
            result.put("regionLikes", regionLikes);
            result.put("typesAndRegions", typesAndRegions);
            
            return Result.success(result);
        } catch (Exception e) {
            System.err.println("调试偏好时发生异常: " + e.getMessage());
            e.printStackTrace();
            return Result.error(500, "调试偏好失败: " + e.getMessage());
        }
    }

    @PostMapping("/update/types")
    public Result<?> updateUserTypeLike(@RequestBody int[] types, Authentication authentication) {
        try {
            System.out.println("收到类型喜好更新请求，参数长度: " + types.length);
            for (int i = 0; i < types.length; i++) {
                System.out.println("类型索引[" + i + "]: " + types[i]);
            }
            
            // 直接获取用户名，然后从数据库查询用户ID
            String username = authentication.getName();
            Long id = getUserIdByUsername(username);
            System.out.println("获取到用户ID: " + id + ", 用户名: " + username);
            
            int updated = typeLikeService.updateUserTypeLike(id, types);
            System.out.println("成功更新类型喜好数量: " + updated);
            
            recommendationService.updateRecommendation(id);
            return Result.success("电影类型喜好更新成功(‾◡◝)");
        } catch (Exception e) {
            System.err.println("更新类型喜好时发生异常: " + e.getMessage());
            e.printStackTrace();
            return Result.error(500, "更新类型喜好失败: " + e.getMessage());
        }
    }

    @PostMapping("/update/regions")
    public Result<?> updateUserRegionLike(@RequestBody int[] regions, Authentication authentication) {
        try {
            System.out.println("收到地区喜好更新请求，参数长度: " + regions.length);
            for (int i = 0; i < regions.length; i++) {
                System.out.println("地区索引[" + i + "]: " + regions[i]);
            }
            
            // 直接获取用户名，然后从数据库查询用户ID
            String username = authentication.getName();
            Long id = getUserIdByUsername(username);
            System.out.println("获取到用户ID: " + id + ", 用户名: " + username);
            
            int updated = regionLikeService.updateUserRegionLike(id, regions);
            System.out.println("成功更新地区喜好数量: " + updated);
            
            recommendationService.updateRecommendation(id);
            return Result.success("电影地区喜好更新成功(‾◡◝)");
        } catch (Exception e) {
            System.err.println("更新地区喜好时发生异常: " + e.getMessage());
            e.printStackTrace();
            return Result.error(500, "更新地区喜好失败: " + e.getMessage());
        }
    }
    
    /**
     * 根据用户名获取用户ID
     * @param username 用户名
     * @return 用户ID
     */
    private Long getUserIdByUsername(String username) {
        try {
            // 通过UserMapper查询用户ID
            Map<String, Object> result = userMapper.selectMaps(
                new QueryWrapper<User>()
                    .select("id")
                    .eq("username", username)
            ).get(0);
            
            Long userId = ((Number) result.get("id")).longValue();
            return userId;
        } catch (Exception e) {
            System.err.println("根据用户名获取ID时发生异常: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
    
    /**
     * 将对象转换为int数组
     */
    private int[] convertToIntArray(Object obj) {
        if (obj == null) {
            return new int[0];
        }
        
        if (obj instanceof List) {
            List<?> list = (List<?>) obj;
            int[] result = new int[list.size()];
            for (int i = 0; i < list.size(); i++) {
                Object item = list.get(i);
                if (item instanceof Number) {
                    result[i] = ((Number) item).intValue();
                } else if (item instanceof String) {
                    try {
                        result[i] = Integer.parseInt((String) item);
                    } catch (NumberFormatException e) {
                        result[i] = 0;
                    }
                } else {
                    result[i] = 0;
                }
            }
            return result;
        } else if (obj instanceof int[]) {
            return (int[]) obj;
        } else {
            return new int[0];
        }
    }
}

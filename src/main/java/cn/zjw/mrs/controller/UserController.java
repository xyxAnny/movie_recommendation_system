package cn.zjw.mrs.controller;

import cn.zjw.mrs.entity.LoginUser;
import cn.zjw.mrs.entity.Result;
import cn.zjw.mrs.entity.User;
import cn.zjw.mrs.mapper.UserMapper;
import cn.zjw.mrs.service.LocalAvatarService;
import cn.zjw.mrs.service.OssService;
import cn.zjw.mrs.service.UserService;
import cn.zjw.mrs.utils.Base64DecodedMultipartFile;
import cn.zjw.mrs.vo.user.UserInfoVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import org.apache.logging.log4j.util.Strings;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

/**
 * @author zjw
 * @Classname UserController
 * @Date 2022/4/10 22:47
 * @Description
 */

@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    UserService userService;

    @Resource
    UserMapper userMapper;

//    @Resource
//    OssService ossService;

    @Resource
    LocalAvatarService localAvatarService;


    @PostMapping("/login")
    public Result<?> login(@RequestBody User user) {
        return userService.login(user);
    }

    @PostMapping("/logout")
    public Result<?> logout() {
        return userService.logout();
    }

    @PostMapping("/register")
    public Result<?> register(@RequestBody User user) {
        return userService.register(user);
    }

    @PostMapping("/update/password")
    public Result<?> updatePassword(@RequestBody Map<String, String> password, Principal principal) {
        int update = userService.updatePassword(principal.getName(),
                password.get("prePassword"),
                password.get("newPassword"),
                false);
        if (update == -1) {
            return Result.error("输入的原密码不正确(┬┬﹏┬┬)");
        } else if (update == 0) {
            return Result.error("密码修改失败(┬┬﹏┬┬)");
        }
        return Result.success("密码修改成功(‾◡◝)");
    }

    @PostMapping("/judge")
    public Result<?> isLogin() {
        return Result.success();
    }

    @GetMapping("/info")
    public Result<?> getUserInfo(Authentication authentication) {
        // 获取当前登录用户
        String username = authentication.getName();
        // 使用SQL查询直接获取id字段
        Map<String, Object> result = userMapper.selectMaps(
            new QueryWrapper<User>()
                .select("id")
                .eq("username", username)
        ).get(0);
        Long userId = ((Number) result.get("id")).longValue();
        UserInfoVo userInfo = userService.getUserInfo(userId);
        return Result.success(userInfo);
    }

    @PutMapping("/update/nickname")
    public Result<?> updateUserNickname(@RequestBody String nickname) {
        if (userService.updateNickname(nickname) == 0) {
            return Result.error("昵称更新失败(┬┬﹏┬┬)");
        }
        return Result.success("昵称更新成功(‾◡◝)");
    }

    @PutMapping("/update/sex")
    public Result<?> updateUserSex(@RequestBody String sex, Principal principal) {
        System.out.println("收到性别修改请求，原始参数: '" + sex + "'");
        
        // 尝试处理可能的JSON引号问题
        if (sex.startsWith("\"") && sex.endsWith("\"")) {
            sex = sex.substring(1, sex.length() - 1);
            System.out.println("处理后的性别参数: '" + sex + "'");
        }
        
        if (userService.updateSex(sex, principal.getName()) == 0) {
            System.out.println("性别更新失败");
            return Result.error("性别更新失败(┬┬﹏┬┬)");
        }
        System.out.println("性别更新成功");
        return Result.success("性别更新成功(‾◡◝)", null);
    }

//    @PostMapping("/update/avatar")
//    public Result<?> updateUserAvatar(@RequestBody String avatar, Principal principal) {
//        if (Strings.isBlank(avatar)) {
//            return Result.error("头像更新失败(┬┬﹏┬┬)");
//        }
//        // base64转MultipartFile文件
//        MultipartFile avatarFile = Base64DecodedMultipartFile.base64ToMultipart(avatar);
//        boolean isSuccess = ossService.updateAvatar(principal.getName(), avatarFile);
//        if (!isSuccess) {
//            return Result.error("头像上传失败(┬┬﹏┬┬)");
//        }
//        return Result.success("头像修改成功(‾◡◝)", null);
//    }
    @PostMapping("/update/avatar")
    public Result<?> updateUserAvatar(@RequestBody String avatar, Principal principal) {
        if (Strings.isBlank(avatar)) {
            return Result.error("头像更新失败:空数据");
        }
        
        System.out.println("收到头像数据，长度: " + avatar.length());
        System.out.println("头像数据前50个字符: " + avatar.substring(0, Math.min(50, avatar.length())));
        
        // 检查是否包含逗号，这是Base64编码的标志
        if (!avatar.contains(",")) {
            System.out.println("头像数据格式错误: 缺少逗号分隔符");
            return Result.error("头像更新失败:格式错误-缺少分隔符");
        }
        
        // 处理收到的JSON格式引号问题
        if (avatar.startsWith("\"") && avatar.endsWith("\"")) {
            avatar = avatar.substring(1, avatar.length() - 1);
            System.out.println("移除了JSON引号，现在长度: " + avatar.length());
        }
        
        // 尝试截取逗号后的内容作为Base64数据
        if (!avatar.startsWith("data:image")) {
            System.out.println("头像数据不以data:image开头，尝试识别其他前缀");
            // 如果前端发送的不是标准格式，尝试添加前缀
            if (!avatar.contains(",")) {
                avatar = "data:image/jpeg;base64," + avatar;
                System.out.println("添加了标准前缀");
            }
        }
        
        // base64转MultipartFile文件
        MultipartFile avatarFile = Base64DecodedMultipartFile.base64ToMultipart(avatar);
        if (avatarFile == null) {
            System.out.println("Base64转换为MultipartFile失败");
            return Result.error("头像更新失败:Base64转换错误");
        }
        
        System.out.println("转换后的文件大小: " + avatarFile.getSize() + " 字节");
        
        boolean isSuccess = localAvatarService.updateAvatar(principal.getName(), avatarFile);
        if (!isSuccess) {
            System.out.println("头像保存到磁盘或数据库更新失败");
            return Result.error("头像上传失败(┬┬﹏┬┬)");
        }
        return Result.success("头像修改成功(‾◡◝)", null);
    }


    @GetMapping("/get/mail")
    public Result<?> getUserMail(Principal principal) {
        String username = principal.getName();
        // 使用SQL查询直接获取mail字段
        Map<String, Object> result = userMapper.selectMaps(
            new QueryWrapper<User>()
                .select("mail")
                .eq("username", username)
        ).get(0);
        return Result.success("成功", result.get("mail"));
    }

    // 添加调试接口，查看Avatar格式
    @PostMapping("/debug/avatar")
    public Result<?> debugAvatar(@RequestBody String avatar) {
        System.out.println("Debug avatar data received, length: " + avatar.length());
        if (avatar.length() > 50) {
            System.out.println("Avatar data starts with: " + avatar.substring(0, 50) + "...");
        } else {
            System.out.println("Avatar data: " + avatar);
        }
        
        // 检查数据格式
        boolean hasImagePrefix = avatar.startsWith("data:image");
        boolean hasComma = avatar.contains(",");
        
        // 使用HashMap替代Map.of()来兼容Java 8
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("length", avatar.length());
        resultMap.put("hasImagePrefix", hasImagePrefix);
        resultMap.put("hasComma", hasComma);
        resultMap.put("firstChars", avatar.substring(0, Math.min(20, avatar.length())));
        
        return Result.success("Avatar debug info", resultMap);
    }
}

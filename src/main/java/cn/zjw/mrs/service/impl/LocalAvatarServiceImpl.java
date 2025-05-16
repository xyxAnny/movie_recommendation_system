package cn.zjw.mrs.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import cn.zjw.mrs.entity.User;
import cn.zjw.mrs.mapper.UserMapper;
import cn.zjw.mrs.service.LocalAvatarService;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.annotation.PostConstruct;

@Service
public class LocalAvatarServiceImpl implements LocalAvatarService {

    @Value("${local.avatar-dir}")
    private String localAvatarPath;

    // 文件分隔符
    private final String slash = File.separator;
    
    // 头像后缀
    private final String suffix = ".webp";

    @Resource
    UserMapper userMapper;

    @PostConstruct
    public void init() {
        // 确保头像目录存在
        File avatarDir = new File(localAvatarPath);
        if (!avatarDir.exists()) {
            boolean created = avatarDir.mkdirs();
            System.out.println("初始化头像目录: " + (created ? "成功" : "失败"));
        }
        System.out.println("头像存储路径: " + localAvatarPath);
    }

    @Override
    public boolean updateAvatar(String username, MultipartFile uploadFile) {
        try {
            // 检测上传的文件是否为空
            if (uploadFile == null || uploadFile.isEmpty()) {
                System.err.println("上传的头像文件为空");
                return false;
            }
            
            System.out.println("\n===== 开始处理头像 =====");
            System.out.println("原始文件名: " + uploadFile.getOriginalFilename());
            System.out.println("文件大小: " + uploadFile.getSize() + " 字节");
            System.out.println("内容类型: " + uploadFile.getContentType());

            if (uploadFile.getSize() <= 0) {
                System.err.println("头像文件大小为0，无法保存");
                return false;
            }

            // 按照年月日来创建目录
            String format = DateUtil.today();
            // 本地保存路径
            String localPath = localAvatarPath + slash + format;
            System.out.println("保存目录: " + localPath);

            // 创建保存目录
            File dirPath = new File(localPath);
            if (!dirPath.exists()) {
                boolean mkdirSuccess = dirPath.mkdirs();
                System.out.println("创建目录结果: " + (mkdirSuccess ? "成功" : "失败"));
                
                // 检查权限
                System.out.println("目录读权限: " + dirPath.canRead());
                System.out.println("目录写权限: " + dirPath.canWrite());
            }

            // 生成唯一文件名（只是文件名部分，不包含路径）
            String fileBaseName = IdUtil.simpleUUID() + suffix;
            // 数据库存储的路径格式（包含日期目录）
            String fileName = format + "/" + fileBaseName;
            System.out.println("数据库存储的文件名: " + fileName);

            // 生成本地文件对象（完整的文件系统路径）
            File localFile = new File(localPath + slash + fileBaseName);
            System.out.println("文件完整路径: " + localFile.getAbsolutePath());
            
            try {
                // 保存文件到本地
                uploadFile.transferTo(localFile);
                System.out.println("文件保存成功，大小: " + localFile.length() + " 字节");
                
                // 验证文件是否成功保存且可读
                if (localFile.exists() && localFile.length() > 0 && localFile.canRead()) {
                    System.out.println("文件验证通过");
                } else {
                    System.err.println("文件验证失败: 存在=" + localFile.exists() + 
                                      ", 大小=" + localFile.length() +
                                      ", 可读=" + localFile.canRead());
                }
            } catch (IOException e) {
                System.err.println("保存头像文件失败: " + e.getMessage());
                e.printStackTrace();
                System.err.println("保存路径: " + localFile.getAbsolutePath());
                System.err.println("目录是否存在: " + localFile.getParentFile().exists());
                System.err.println("目录可写: " + localFile.getParentFile().canWrite());
                return false;
            }

            // 将本地头像路径存入数据库
            try {
                // 使用UpdateWrapper而不是LambdaUpdateWrapper避免方法引用问题
                int updateResult = userMapper.update(null, new UpdateWrapper<User>()
                    .set("avatar", fileName)
                    .eq("username", username));
                
                System.out.println("数据库更新结果: " + (updateResult > 0 ? "成功" : "失败") + 
                                  " (影响行数: " + updateResult + ")");
                
                System.out.println("===== 头像处理完成 =====\n");
                return updateResult > 0;
            } catch (Exception e) {
                System.err.println("更新数据库头像路径失败: " + e.getMessage());
                e.printStackTrace();
                return false;
            }
        } catch (Exception e) {
            System.err.println("头像更新过程中发生异常: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
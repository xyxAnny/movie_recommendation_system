package cn.zjw.mrs.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import cn.zjw.mrs.entity.User;
import cn.zjw.mrs.mapper.UserMapper;
import cn.zjw.mrs.service.LocalAvatarService;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;

@Service
public class LocalAvatarServiceImpl implements LocalAvatarService {

    @Value("${local.avatar-dir}")
    private String localAvatarDir;

    @Resource
    private UserMapper userMapper;

    private String getAvatarFileName() {
        return DateUtil.today() + '/' + IdUtil.simpleUUID() + ".webp";
    }

    @Override
    public boolean updateAvatar(String username, MultipartFile uploadFile) {
        String fileName = getAvatarFileName();
        File localFile = new File(localAvatarDir + File.separator + fileName);

        // 创建目录
        if (!localFile.getParentFile().exists()) {
            localFile.getParentFile().mkdirs();
        }

        try {
            // 保存文件到本地
            uploadFile.transferTo(localFile);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        // 将本地头像路径存入数据库
        userMapper.update(null, new LambdaUpdateWrapper<User>()
                .set(User::getAvatar, fileName).eq(User::getUsername, username));
        return true;
    }
}
package cn.zjw.mrs.service;

import org.springframework.web.multipart.MultipartFile;

public interface LocalAvatarService {
    /**
     * 上传头像到本地目录，并将图片路径存入数据库中
     * @param username 用户名
     * @param uploadFile 待上传头像
     * @return 上传结果
     */
    boolean updateAvatar(String username, MultipartFile uploadFile);
}

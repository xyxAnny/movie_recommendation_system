package cn.zjw.mrs.utils;

import org.springframework.web.multipart.MultipartFile;
import java.io.*;
import java.util.Base64;
import java.util.Random;

/**
 * @author zjw
 * @Classname BASE64DecodedMultipartFile
 * @Date 2022/4/18 0:52
 * @Description
 */
public class Base64DecodedMultipartFile implements MultipartFile {

    private final byte[] imgContent;
    private final String header;

    public Base64DecodedMultipartFile(byte[] imgContent, String header) {
        this.imgContent = imgContent;
        this.header = header.split(";")[0];
    }

    @Override
    public String getName() {
        // implementation depends on your requirements
        return System.currentTimeMillis() + Math.random() + "." + "webp";
    }

    @Override
    public String getOriginalFilename() {
        // implementation depends on your requirements
        return System.currentTimeMillis() + new Random().nextInt() + "." + "webp";
    }

    @Override
    public String getContentType() {
        // implementation depends on your requirements
        return header.split(":")[1];
    }

    @Override
    public boolean isEmpty() {
        return imgContent == null || imgContent.length == 0;
    }

    @Override
    public long getSize() {
        return imgContent.length;
    }

    @Override
    public byte[] getBytes() throws IOException {
        return imgContent;
    }

    @Override
    public java.io.InputStream getInputStream() throws IOException {
        return new ByteArrayInputStream(imgContent);
    }

    @Override
    public void transferTo(File dest) throws IOException, IllegalStateException {
        try (FileOutputStream fos = new FileOutputStream(dest)) {
            fos.write(imgContent);
        }
    }

    /**
     * base64转MultipartFile文件
     *
     * @param base64 base64字符串
     * @return MultipartFile文件
     */
    public static MultipartFile base64ToMultipart(String base64) {
        try {
            String[] baseStrs = base64.split(",");

            // 使用 java.util.Base64 替代 sun.misc.BASE64Decoder
            byte[] b = Base64.getDecoder().decode(baseStrs[1]);

            for (int i = 0; i < b.length; ++i) {
                if (b[i] < 0) {
                    b[i] += 256;
                }
            }

            return new Base64DecodedMultipartFile(b, baseStrs[0]);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 保存MultipartFile到本地
     * @param fileContent 源文件
     * @param dirPath 本地路径
     */
    public static void approvalFile(MultipartFile fileContent, String dirPath) {
        try (InputStream inputStream = fileContent.getInputStream()) {
            String fileName = fileContent.getOriginalFilename();
            String path = dirPath;

            // 2、保存到临时文件
            // 1K的数据缓冲
            byte[] bs = new byte[1024];
            // 读取到的数据长度
            int len;

            // 输出的文件流保存到本地文件
            File tempFile = new File(path);
            if (!tempFile.exists()) {
                tempFile.mkdirs();
            }

            try (FileOutputStream os = new FileOutputStream(tempFile.getPath() + File.separator + fileName)) {
                // 开始读取
                while ((len = inputStream.read(bs)) != -1) {
                    os.write(bs, 0, len);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
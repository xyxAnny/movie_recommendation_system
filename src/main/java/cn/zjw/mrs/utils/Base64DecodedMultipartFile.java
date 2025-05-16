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
        
        // 防止header为null或格式不正确导致异常
        String processedHeader;
        try {
            if (header != null && header.contains(";")) {
                processedHeader = header.split(";")[0];
            } else {
                // 默认MIME类型
                processedHeader = "data:image/jpeg";
            }
        } catch (Exception e) {
            System.out.println("Exception handling header: " + e.getMessage());
            // 默认MIME类型
            processedHeader = "data:image/jpeg";
        }
        
        this.header = processedHeader;
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
        try {
            // 确保header格式正确并包含":"
            if (header != null && header.contains(":")) {
                return header.split(":")[1];
            } else {
                // 默认MIME类型
                return "image/jpeg";
            }
        } catch (Exception e) {
            System.out.println("Exception getting content type: " + e.getMessage());
            return "image/jpeg";
        }
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
            System.out.println("\n===== BASE64 处理开始 =====");
            System.out.println("接收Base64数据，长度: " + base64.length());
            
            // 输出前后10个字符以帮助调试，避免打印太多日志
            if (base64.length() > 20) {
                System.out.println("数据前10个字符: " + base64.substring(0, 10) + "...");
                System.out.println("数据后10个字符: " + base64.substring(base64.length() - 10));
            }
            
            // 处理可能的JSON转义问题
            if (base64.startsWith("\"") && base64.endsWith("\"")) {
                base64 = base64.substring(1, base64.length() - 1);
                System.out.println("移除JSON引号后长度: " + base64.length());
            }
            
            // 检查是否包含逗号
            if (!base64.contains(",")) {
                System.out.println("数据不包含逗号分隔符，将使用默认头");
            }
            
            String[] baseStrs = base64.split(",");
            String header;
            String encodedData;
            
            if (baseStrs.length >= 2) {
                header = baseStrs[0];
                encodedData = baseStrs[1];
                System.out.println("使用标准格式 - 头部: " + header);
                System.out.println("编码数据长度: " + encodedData.length());
            } else {
                // 如果没有标准格式，使用默认的JPEG格式
                header = "data:image/jpeg;base64";
                encodedData = baseStrs[0];
                System.out.println("使用默认头部: " + header);
            }
            
            // 解码前校验base64字符串，确保符合base64编码规则(只包含A-Za-z0-9+/=)
            if (!encodedData.matches("^[A-Za-z0-9+/=]+$")) {
                System.out.println("警告: Base64数据包含非法字符，尝试清理...");
                // 清理非法字符
                encodedData = encodedData.replaceAll("[^A-Za-z0-9+/=]", "");
            }
            
            // 使用 java.util.Base64 替代 sun.misc.BASE64Decoder
            byte[] b;
            try {
                b = Base64.getDecoder().decode(encodedData);
                System.out.println("成功解码Base64数据，长度: " + b.length + " 字节");
            } catch (IllegalArgumentException e) {
                System.out.println("解码Base64数据失败: " + e.getMessage());
                
                // 再次尝试清理所有非base64字符后解码
                System.out.println("尝试清理所有非Base64合法字符...");
                encodedData = encodedData.replaceAll("[^A-Za-z0-9+/=]", "");
                
                // 确保长度是4的倍数
                while (encodedData.length() % 4 != 0) {
                    encodedData += "=";
                    System.out.println("添加填充字符，现在长度为: " + encodedData.length());
                }
                
                b = Base64.getDecoder().decode(encodedData);
                System.out.println("第二次尝试解码后长度: " + b.length + " 字节");
            }
            
            // 转换字节
            for (int i = 0; i < b.length; ++i) {
                if (b[i] < 0) {
                    b[i] += 256;
                }
            }
            
            System.out.println("创建MultipartFile对象...");
            MultipartFile result = new Base64DecodedMultipartFile(b, header);
            System.out.println("===== BASE64 处理完成 =====\n");
            return result;
        } catch (Exception e) {
            System.out.println("\n===== BASE64 处理失败 =====");
            System.out.println("异常类型: " + e.getClass().getName());
            System.out.println("异常信息: " + e.getMessage());
            e.printStackTrace();
            System.out.println("===== 异常堆栈打印完毕 =====\n");
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
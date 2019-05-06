package com.benxiaopao.mobile.common.config;

import com.benxiaopao.mobile.common.constant.UserConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import java.io.*;
import java.util.HashSet;
import java.util.Set;

 /**
 * 读取昵称关键字配置类
 * @author liupoyang
 * @since 2019-05-05
 */
//@Component
@Configuration
@Slf4j
public class ReadNicknameKeywordConfig {
    /**
     * 配置文件名称
     */
    @Value("${local.config.nickname.file-name}")
    private String fileName;
    /**
     * 昵称关键字容器
     */
    private static Set<String> nicknameKeywordSet = new HashSet<String>();

//    /**
//     * 设置配置文件名称
//     * @param fileName 配置文件名称
//     */
//    public void setFileName(String fileName) {
//        this.fileName = fileName;
//        log.info("#设置配置文件名称={}", fileName);
//    }

//    /**
//     * 获得昵称关键字容器
//     */
//    public Set<String> getNicknameKeywordSet() {
//        return nicknameKeywordSet;
//    }

    /**
     * 初始化配置文件
     */
    @Bean
    public Set<String> initConfig() {
        BufferedReader br = null;
        InputStream in = null;
        try {
            String path= this.getClass().getResource("/").getPath() + fileName;
            br = new BufferedReader(new InputStreamReader(new FileInputStream(path), "UTF-8"));
            String keyword = null;
            while ((keyword = br.readLine()) != null) {
                if (!keyword.trim().isEmpty()) {
                    nicknameKeywordSet.add(keyword.trim());
                }
            }
            log.info("#初始化配置文件成功：{}", fileName);
        } catch (Exception e) {
            log.error("#设置配置文件失败：" + fileName, e);
        }
        finally{
            try {
                if(in!=null) in.close();
                if(br!=null) br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        UserConstant.nicknameKeywordSet = nicknameKeywordSet;
        return nicknameKeywordSet;
    }
}
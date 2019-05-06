package com.benxiaopao.mobile.common.constant;

import com.benxiaopao.common.supers.BaseConstant;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.HashSet;
import java.util.Set;

/**
 * 全局常量类
 *
 * Created by liupoyang
 * 2019-05-05
 */
public class UserConstant extends BaseConstant {
//    /**
//     * 找回密码发送邮件，激活链接url的host + port
//     */
//    public static final String HOST_PORT_4_RESET_PWD = "localhost:8080/public";


//    /**
//     * 设置昵称关键字相关配置
//     *
//     * @param readNicknameKeywordConfig 昵称配置读取对象
//     */
//    public void setReadNicknameKeywordConfig(ReadNicknameKeywordConfig readNicknameKeywordConfig){
//        nicknameKeywordSet = readNicknameKeywordConfig.getNicknameKeywordSet();
//    }


    /**
     * 昵称关键字容器
     */
    public static Set<String> nicknameKeywordSet;

    /**
     * 是否命中关键字
     * @param keyword
     * @return
     */
    public static boolean hitKeywordForNickname(String keyword) {
        for (String key : nicknameKeywordSet) {
            if (keyword.toUpperCase().contains(key.toUpperCase())) {
                return true;
            }
        }
        return false;
    }
}

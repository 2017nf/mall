package com.mall.util;

import java.util.Locale;
import java.util.ResourceBundle;

public class PropertiesUtil {
    static Locale locale_cn = new Locale("zh", "CN");
    static ResourceBundle resource_cn = ResourceBundle.getBundle("config", locale_cn);

    public static String AK = resource_cn.getString("AK");
    public static String SK = resource_cn.getString("SK");
    public static String qiniuURL = resource_cn.getString("prefix");

    public static String getDomain(String folderName) {
        return resource_cn.getString(folderName);
    }

    /**
     * aes加密密钥
     */
    public static final String encryptAES = "treespringzou";
}

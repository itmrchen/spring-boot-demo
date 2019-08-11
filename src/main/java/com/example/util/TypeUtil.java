package com.example.util;

import org.springframework.util.ClassUtils;

import java.net.URI;
import java.net.URL;
import java.util.Date;
import java.util.Locale;

public class TypeUtil {

    // 判断是否基本类型或字面量类型
    public static boolean isSimpleValueType(Class<?> clazz) {
        return (ClassUtils.isPrimitiveOrWrapper(clazz) || clazz.isEnum() || CharSequence.class.isAssignableFrom(clazz)
                || Number.class.isAssignableFrom(clazz) || Date.class.isAssignableFrom(clazz) || URI.class==clazz
                || URL.class==clazz || Locale.class==clazz || Class.class==clazz);
    }

}

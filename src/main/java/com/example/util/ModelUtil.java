package com.example.util;

import com.example.exception.ProjectException;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.FatalBeanException;
import org.springframework.util.ClassUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@Slf4j
public class ModelUtil {

    /**
     * 复制对象
     * @see BeanUtils#copyProperties(Object, Object, String...)
     * @see BeanUtils#getPropertyDescriptors(Class)
     * @see PropertyDescriptor
     * @param source 源对象
     * @param mappings 对应关系
     * @return 复制结果
     */
    @SuppressWarnings("unchecked")
    public static Object copy(Object source, Mapping... mappings) {
        if (source==null) {
            return null;
        }
        Class clazz = source.getClass();
        // 集合
        if (Collection.class.isAssignableFrom(clazz)) {
            try {
                Collection collection = (Collection) clazz.newInstance();
                ((Collection) source).forEach(object -> collection.add(copy(object, mappings)));
                return collection;
            } catch (InstantiationException|IllegalAccessException e) {
                log.error(e.getMessage(), e);
                throw new ProjectException("复制对象属性出错", e);
            }
        }
        // 数组
        if (clazz.isArray()) {
            List list = new ArrayList();
            Arrays.stream(((Object[]) source)).forEach(object -> list.add(copy(object, mappings)));
            return list.toArray();
        }
        // 简单类型
        if (TypeUtil.isSimpleValueType(clazz)) {
            return source;
        }
        Class targetClass = clazz;
        List<String> ignoreList = null;
        for (Mapping mapping: mappings) {
            if (clazz.equals(mapping.sourceClass)) {
                targetClass = mapping.targetClass;
                ignoreList = (mapping.ignoreProperties != null ? Arrays.asList(mapping.ignoreProperties) : null);
                break;
            }
        }
        PropertyDescriptor[] targetPds = BeanUtils.getPropertyDescriptors(targetClass);
        Object target;
        try {
            target = targetClass.newInstance();
        } catch (InstantiationException|IllegalAccessException e) {
            log.error(e.getMessage(), e);
            throw new ProjectException("反射创建对象出错", e);
        }
        for (PropertyDescriptor targetPd : targetPds) {
            Method writeMethod = targetPd.getWriteMethod();
            if (writeMethod != null && (ignoreList == null || !ignoreList.contains(targetPd.getName()))) {
                PropertyDescriptor sourcePd = BeanUtils.getPropertyDescriptor(source.getClass(), targetPd.getName());
                if (sourcePd != null) {
                    Method readMethod = sourcePd.getReadMethod();
                    if (readMethod != null) {
                        if (!Modifier.isPublic(readMethod.getDeclaringClass().getModifiers())) {
                            readMethod.setAccessible(true);
                        }
                        if (!Modifier.isPublic(writeMethod.getDeclaringClass().getModifiers())) {
                            writeMethod.setAccessible(true);
                        }
                        Object value;
                        try {
                            value = readMethod.invoke(source);
                        } catch (Throwable e) {
                            log.error(e.getMessage(), e);
                            throw new FatalBeanException(
                                    "无法从"+source.getClass().getName()+"复制属性：'" + targetPd.getName() + "'", e);
                        }
                        if (value != null) {
                            Class sourcePt = sourcePd.getPropertyType();
                            if (Collection.class.isAssignableFrom(sourcePt) || sourcePt.isArray()) {
                                value = copy(value, mappings);
                            } else {
                                for (Mapping mapping: mappings) {
                                    if (sourcePt.equals(mapping.sourceClass)) {
                                        value = copy(value, mappings);
                                        break;
                                    }
                                }
                            }
                            if (value != null && ClassUtils.isAssignable(writeMethod.getParameterTypes()[0], value.getClass())) {
                                try {
                                    writeMethod.invoke(target, value);
                                } catch (Throwable e) {
                                    log.error(e.getMessage(), e);
                                    throw new FatalBeanException(
                                            "无法从"+source.getClass().getName()+"复制属性：'" + targetPd.getName() + "'", e);
                                }
                            }
                        }
                    }
                }
            }
        }
        return target;
    }

    @Data
    public static class Mapping {
        private Class sourceClass;
        private Class targetClass;
        private String[] ignoreProperties;
        public Mapping(Class sourceClass, Class targetClass, String... ignoreProperties) {
            this.sourceClass = sourceClass;
            this.targetClass = targetClass;
            this.ignoreProperties = ignoreProperties;
        }
    }

}

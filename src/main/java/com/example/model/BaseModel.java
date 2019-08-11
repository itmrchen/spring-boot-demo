package com.example.model;

import com.example.exception.ProjectException;
import com.example.util.SerializeUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class BaseModel implements Cloneable, Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * @return 实体类主键与数据库主键对应关系，key实体类主键，value数据库主键
     */
    public Map<String, String> pkMap() {
        Map<String, String> map = new HashMap<>();
        map.put("id", "id");
        return map;
    }

    /**
     * 按照字段名称取值
     * @param fieldName 字段名称
     * @return 字段值
     */
    public Object value(String fieldName) {
        try {
            Field field = this.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(this);
        } catch (Exception e) {
            throw new ProjectException("获取字段"+fieldName+"的值出错", e);
        }
    }

    /**
     * 按照字段名称赋值
     * @param fieldName 字段名称
     * @param value 字段值
     */
    public void value(String fieldName, Object value) {
        try {
            Field field = this.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(this, value);
        } catch (Exception e) {
            throw new ProjectException("设置字段"+fieldName+"的值为"+value+"出错", e);
        }
    }

    @Override
    public BaseModel clone() {
        return SerializeUtil.deserialize(SerializeUtil.serialize(this));
    }

}

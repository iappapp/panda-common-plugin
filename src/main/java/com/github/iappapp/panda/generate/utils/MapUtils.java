package com.github.iappapp.panda.generate.utils;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class MapUtils {

    /**
     * 对象转Map
     * @param obj 要转换的对象
     * @param maxLevel 最大层级 (1 表示只转第一层)
     * @param currentLevel 当前层级 (初始传 1)
     */
    public static Map<String, Object> toMap(Object obj, int maxLevel, int currentLevel)
            throws IllegalAccessException {
        if (obj == null || currentLevel > maxLevel) {
            return null; 
        }

        Map<String, Object> map = new HashMap<>();
        Class<?> clazz = obj.getClass();
        
        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);
            Object value = field.get(obj);
            
            // 判断是否需要继续递归转换（且是非基本类型）
            if (currentLevel < maxLevel && value != null && !isPrimitive(value)) {
                map.put(field.getName(), toMap(value, maxLevel, currentLevel + 1));
            } else {
                map.put(field.getName(), value);
            }
        }
        return map;
    }

    // 简单判断是否为基本类型或包装类/String
    private static boolean isPrimitive(Object obj) {
        Class<?> cls = obj.getClass();
        return cls.isPrimitive() || obj instanceof String || obj instanceof Number || obj instanceof Boolean;
    }
}
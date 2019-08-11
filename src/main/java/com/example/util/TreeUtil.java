package com.example.util;

import com.example.model.Tree;
import io.jsonwebtoken.lang.Collections;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TreeUtil {

    /**
     * 将集合处理成树形结构，顶点为level最小的节点
     */
    public static <T extends Tree<T>> List<T> toTree(List<T> list) {
        if (Collections.isEmpty(list)) {
            return null;
        }
        Integer resultLevel = _toTree(list, getMap(list));
        if (resultLevel!=null) {
            List<T> result = new ArrayList<>();
            final Integer _level = resultLevel;
            list.forEach(t -> {
                if (_level.equals(t.getLevel())) {
                    result.add(t);
                }
            });
            return result;
        }
        return null;
    }

    /**
     * 将集合处理成树形结构，顶点为指定的id
     */
    public static <T extends Tree<T>> T toTree(List<T> list, Integer topId) {
        if (Collections.isEmpty(list)) {
            return null;
        }
        _toTree(list, getMap(list));
        for (T t : list) {
            if (t.getId().equals(topId)) {
                return t;
            }
        }
        return null;
    }

    private static <T extends Tree<T>> Map<Integer, T> getMap(List<T> list) {
        Map<Integer, T> map = new HashMap<>();
        for (T t: list) {
            map.put(t.getId(), t);
        }
        return map;
    }

    private static <T extends Tree<T>> Integer _toTree(List<T> list, Map<Integer, T> map) {
        Integer resultLevel = null;
        for (T t: list) {
            if (resultLevel==null || t.getLevel()<resultLevel) {
                resultLevel = t.getLevel();
            }
            Integer pid = t.getPid();
            if (pid!=null) {
                T pt = map.get(pid);
                if (pt!=null) {
                    List<T> subs = pt.getSubs();
                    if (subs==null) {
                        subs = new ArrayList<>();
                        pt.setSubs(subs);
                    }
                    subs.add(t);
                }
            }
        }
        return resultLevel;
    }

}

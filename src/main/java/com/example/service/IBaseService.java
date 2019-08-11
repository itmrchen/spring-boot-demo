package com.example.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.exception.ProjectException;
import com.example.model.BaseModel;
import com.example.model.po.User;
import com.example.params.Params;

import java.util.Collection;
import java.util.List;

public interface IBaseService<T extends BaseModel> extends IService<T> {

    User currentUser();

    default List<T> customList(Params<T> params) {
        throw new ProjectException("方法未实现！");
    }

    default IPage<T> customPage(Page<T> page, Params<T> params) {
        throw new ProjectException("方法未实现！");
    }

    default T customGetById(Integer id) {
        throw new ProjectException("方法未实现！");
    }

    default T customGetOne(Params<T> params) {
        throw new ProjectException("方法未实现！");
    }

    default void customSave(T model) {
        throw new ProjectException("方法未实现！");
    }

    default void customUpdateById(T model) {
        throw new ProjectException("方法未实现！");
    }

    default void customUpdateBatchById(List<T> models) {
        throw new ProjectException("方法未实现！");
    }

    default void customRemoveById(Integer id) {
        throw new ProjectException("方法未实现！");
    }

    default void customRemoveByIds(List<Integer>ids) {
        throw new ProjectException("方法未实现！");
    }

    /**
     * 类似oracle的merge语法
     * @param collection 目标集合
     * @param queryWrapper 查询条件，用来查询将要覆盖的集合
     */
    void merge(Collection<T> collection, QueryWrapper<T> queryWrapper);

    /**
     * 类似oracle的merge语法
     * @param collection 目标集合
     * @param queryWrapper 查询条件，用来查询将要覆盖的集合
     * @param batchSize 插入批次数量
     */
    void merge(Collection<T> collection, QueryWrapper<T> queryWrapper, int batchSize);

}

package com.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.model.BaseModel;
import com.example.params.Params;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface IBaseMapper<T extends BaseModel> extends BaseMapper<T> {

    List<T> customSelectList(@Param("params") Params<T> params);

    List<T> customSelectPage(Page<T> page, @Param("params") Params<T> params);

    T customSelectById(Integer id);

    T customSelectOne(@Param("params") Params<T> params);

    void customUpdateBatchById(List<T> models);

}

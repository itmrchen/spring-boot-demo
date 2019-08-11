package com.example.mapper;

import com.example.model.po.Resource;

import java.util.List;

public interface ResourceMapper extends IBaseMapper<Resource> {

    List<String> selectAllCategorys();

}

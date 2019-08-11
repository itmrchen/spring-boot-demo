package com.example.service;

import com.example.model.po.Resource;

import java.util.List;

public interface IResourceService extends IBaseService<Resource> {

    /** 根据资源类型查询资源 */
    List<Resource> getByType(String type);

    List<Resource> all();

    List<String> categorys();

}

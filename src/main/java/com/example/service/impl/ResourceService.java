package com.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.mapper.ResourceMapper;
import com.example.model.po.Resource;
import com.example.params.Params;
import com.example.service.IResourceService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.List;

@Service
public class ResourceService extends BaseService<ResourceMapper, Resource> implements IResourceService {

    @Override
    @Cacheable(cacheNames = {"resource:multiple"}, keyGenerator = "defaultPageKeyGenerator")
    public List<Resource> all() {
        return baseMapper.selectList(new QueryWrapper<Resource>().orderByAsc("resource_seq"));
    }

    @Override
    @Cacheable(cacheNames = {"resource:multiple"}, keyGenerator = "defaultPageKeyGenerator")
    public List<String> categorys() {
        return baseMapper.selectAllCategorys();
    }

    @Override
    @Cacheable(cacheNames = {"resource:multiple"}, keyGenerator = "defaultPageKeyGenerator")
    public List<Resource> getByType(String type) {
        QueryWrapper<Resource> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("resource_type", type);
        return baseMapper.selectList(queryWrapper);
    }

    @Override
    @Cacheable(cacheNames = {"resource:multiple"}, keyGenerator = "defaultPageKeyGenerator")
    public IPage<Resource> customPage(Page<Resource> page, Params<Resource> params) {
        return page.setRecords(baseMapper.customSelectPage(page, params));
    }

    @Override
    @Cacheable(cacheNames = {"resource:single"}, key = "'resource:'+#id", unless = "#result==null")
    public Resource getById(Serializable id) {
        return super.getById(id);
    }

    @Override
    @Transactional
    @CacheEvict(cacheNames = {"resource:multiple"}, allEntries = true)
    public boolean save(Resource resource) {
        return super.save(resource);
    }

    @Override
    @Transactional
    @Caching(
            evict = {
                    @CacheEvict(cacheNames = {"resource:multiple"}, allEntries = true),
                    @CacheEvict(cacheNames = {"resource:single"}, key = "'resource:'+#resource.id")
            }
    )
    public boolean updateById(Resource resource) {
        return super.updateById(resource);
    }

    @Override
    @Transactional
    @Caching(
            evict = {
                    @CacheEvict(cacheNames = {"resource:multiple"}, allEntries = true),
                    @CacheEvict(cacheNames = {"resource:single"}, allEntries = true)
            }
    )
    public void customRemoveByIds(List<Integer> ids) {
        super.removeByIds(ids);
    }

}

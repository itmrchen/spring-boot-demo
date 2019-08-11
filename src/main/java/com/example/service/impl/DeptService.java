package com.example.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.mapper.DeptMapper;
import com.example.model.po.Dept;
import com.example.params.Params;
import com.example.service.IDeptService;
import com.example.util.TreeUtil;
import io.jsonwebtoken.lang.Collections;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Service
public class DeptService extends BaseService<DeptMapper, Dept> implements IDeptService {

    @Override
    @Cacheable(cacheNames = {"dept:multiple"}, keyGenerator = "defaultPageKeyGenerator")
    public List<Dept> tree() {
        List<Dept> list = baseMapper.selectList(null);
        return TreeUtil.toTree(list);
    }

    @Override
    @Cacheable(cacheNames = {"dept:multiple"}, keyGenerator = "defaultPageKeyGenerator")
    public List<Dept> customList(Params<Dept> params) {
        return baseMapper.customSelectList(params);
    }

    @Override
    @Cacheable(cacheNames = {"dept:multiple"}, keyGenerator = "defaultPageKeyGenerator")
    public IPage<Dept> customPage(Page<Dept> page, Params<Dept> params) {
        return page.setRecords(baseMapper.customSelectPage(page, params));
    }

    @Override
    @Cacheable(cacheNames = {"dept:single"}, key = "'dept:'+#id", unless = "#result==null")
    public Dept getById(Serializable id) {
        return super.getById(id);
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(cacheNames = {"dept:multiple"}, allEntries = true),
            @CacheEvict(cacheNames = {"dept:single"}, allEntries = true, condition="#dept.isDefault==1")
    })
    public void customSave(Dept dept) {
        if (Integer.valueOf(DEFAULT).equals(dept.getIsDefault())) {
            resetDefault();
        }
        Integer pid = dept.getPid();
        if (pid==null) {
            dept.setLevel(1);
            dept.setFullName(dept.getDeptName());
        } else {
            Dept pDept = baseMapper.selectById(pid);
            dept.setLevel(pDept.getLevel()+1);
            dept.setFullName(pDept.getFullName()+"-"+dept.getDeptName());
        }
        super.save(dept);
    }

    @Override
    @Transactional
    @Caching(
            evict = {
                    @CacheEvict(cacheNames = {"dept:multiple"}, allEntries = true),
                    @CacheEvict(cacheNames = {"dept:single"}, allEntries = true)
            }
    )
    public void customUpdateById(Dept dept) {
        if (Integer.valueOf(DEFAULT).equals(dept.getIsDefault())) {
            resetDefault();
        }
        Integer pid = dept.getPid();
        if (pid==null) {
            dept.setLevel(1);
            dept.setFullName(dept.getDeptName());
        } else {
            Dept pDept = baseMapper.selectById(pid);
            dept.setLevel(pDept.getLevel()+1);
            dept.setFullName(pDept.getFullName()+"-"+dept.getDeptName());
        }
        super.updateById(dept);
        // 查询所有部门
        List<Dept> list = baseMapper.selectList(null);
        // 获得当前修改部门的树形分支
        Dept dept_ = TreeUtil.toTree(list, dept.getId());
        if (dept_!=null) {
            List<Dept> subs = dept_.getSubs();
            // 如果当前修改的部门下有子部门，修改子部门的fullName
            if (!Collections.isEmpty(subs)) {
                List<Dept> allsubs = allsubs(subs, dept.getFullName());
                super.updateBatchById(allsubs);
            }
        }
    }

    @Override
    @Transactional
    public void resetDefault() {
        List<Dept> defaults = baseMapper.customSelectList(new Params<>(new Dept().setIsDefault(DEFAULT)));
        if (!Collections.isEmpty(defaults)) {
            defaults.forEach(d -> d.setIsDefault(NOT_DEFAULT));
            super.updateBatchById(defaults);
        }
    }

    private List<Dept> allsubs(List<Dept> subs, String pFullName) {
        List<Dept> result = new ArrayList<>();
        subs.forEach(sub -> {
            sub.setFullName(pFullName + "-" + sub.getDeptName());
            List<Dept> subs2 = sub.getSubs();
            result.add(sub);
            if (!Collections.isEmpty(subs2)) {
                result.addAll(allsubs(subs2, sub.getFullName()));
            }
        });
        return result;
    }

    @Override
    @Transactional
    @Caching(
            evict = {
                    @CacheEvict(cacheNames = {"dept:multiple"}, allEntries = true),
                    @CacheEvict(cacheNames = {"dept:single"}, allEntries = true)
            }
    )
    public void customRemoveByIds(List<Integer> ids) {
        super.removeByIds(ids);
    }

}

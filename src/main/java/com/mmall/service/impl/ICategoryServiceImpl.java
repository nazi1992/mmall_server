package com.mmall.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mmall.common.ServerResponse;
import com.mmall.dao.CategoryMapper;
import com.mmall.pojo.Category;
import com.mmall.service.ICategoryService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Administrator on 2018/4/4 0004.
 */
@Service("iCategoryService")
public class ICategoryServiceImpl implements ICategoryService {
    @Autowired
    private CategoryMapper categoryMapper;
    private org.slf4j.Logger logger = LoggerFactory.getLogger(ICategoryServiceImpl.class);
    public ServerResponse addCategory(String categroyName,Integer parentId)
    {
        if(parentId ==null|| StringUtils.isBlank(categroyName))
        {
            return ServerResponse.createByError("添加品类参数错误");

        }
        Category category = new Category();
        category.setName(categroyName);
        category.setParentId(parentId);
        category.setStatus(true);
        int result = categoryMapper.insert(category);
        if(result>0)
        {
            return ServerResponse.createBySuccessMessage("添加品类成功");
        }
        return ServerResponse.createByError("添加品类失败");

    }
    public ServerResponse updateCategoryName(Integer parentId,String categoryName)
    {
        if(parentId ==null|| StringUtils.isBlank(categoryName))
        {
            return ServerResponse.createByError("添加品类参数错误");

        }
        Category category = new Category();
        category.setName(categoryName);
        category.setParentId(parentId);
        int rowCount = categoryMapper.updateByPrimaryKeySelective(category);
        if(rowCount>0)
        {
            return ServerResponse.createBySuccess("更新品类名字成功");
        }
        return ServerResponse.createByError("更新品类名字失败");
    }
    //根据品级id查询相应的内容
    public ServerResponse<List<Category>> getChildParallerByParentId(Integer parentId)
    {
            List<Category> categories = categoryMapper.selectChildByParentId(parentId);
        if(CollectionUtils.isEmpty(categories))
        {
            logger.info("未找到当前分类的子分类");
        }
        return ServerResponse.createBySuccess(categories);
    }

    /**
     * 递归查询本节点的id和其子元素的id
     * @param categoryId
     * @return
     */
    public ServerResponse<List<Integer>> selectCategoryAndChildrenByParentId(Integer categoryId)
    {
        Set<Category> sets = Sets.newHashSet();
        Set<Category> setResult = this.findChildCategroy(sets,categoryId);
        List<Integer> categoryIds = Lists.newArrayList();
        if(categoryId!=null)
        {
            for(Category category :setResult)
            {
                categoryIds.add(category.getId());
            }
        }
        return ServerResponse.createBySuccess(categoryIds);
    }
    //递归算法算出子节点
    public Set<Category> findChildCategroy(Set<Category> categorySet, Integer categroyId)
    {
        Category category = categoryMapper.selectByPrimaryKey(categroyId);
        if(category!=null)
        {
            categorySet.add(category);
        }
        List<Category> categories = categoryMapper.selectChildByParentId(categroyId);
        for(Category category1: categories)
        {
            this.findChildCategroy(categorySet,category1.getId());
        }
        return categorySet;
    }
}

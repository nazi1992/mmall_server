package com.mmall.service;

import com.mmall.common.ServerResponse;
import com.mmall.pojo.Category;

import java.util.List;

/**
 * Created by Administrator on 2018/4/4 0004.
 */
public interface ICategoryService {
    ServerResponse addCategory(String categroyName, Integer parentId);
    ServerResponse updateCategoryName(Integer parentId,String categoryName);
    ServerResponse<List<Category>> getChildParallerByParentId(Integer parentId);
    ServerResponse<List<Integer>> selectCategoryAndChildrenByParentId(Integer categoryId);
}

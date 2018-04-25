package com.mmall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.dao.CategoryMapper;
import com.mmall.dao.ProductMapper;
import com.mmall.pojo.Category;
import com.mmall.pojo.Product;
import com.mmall.service.ICategoryService;
import com.mmall.service.IProductService;
import com.mmall.util.DateTimeUtil;
import com.mmall.util.PropertiesUtil;
import com.mmall.vo.ProductDetailVo;
import com.mmall.vo.ProductVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/4/9 0009.
 */
public class IProductServiceImpl implements IProductService {
    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private ICategoryService iCategoryService;
    public ServerResponse updateOrSaveProduct(Product product){
        if(product!=null)
        {
            String subImages = product.getSubImages();
            if(StringUtils.isNotBlank(subImages))
            {
                String[] subImagesAry = subImages.split(",");
                String subMain = subImagesAry[0];
                if(subMain!=null)
                {
                    product.setMainImage(subMain);
                }
            }
                Integer id = product.getId();
                if(id!=null)
                {
                    //更新
                    int updateResult = productMapper.updateByPrimaryKey(product);
                    if(updateResult>0)
                    {
                        return ServerResponse.createBySuccessMessage("更新成功");
                    }
                    else
                    {
                        return ServerResponse.createByError("更新失败");
                    }
                }
                else
                {
                    //保存
                    int insertResult = productMapper.insert(product);
                    if(insertResult>0)
                    {
                        return ServerResponse.createBySuccessMessage("保存成功");

                    }
                    else
                    {
                        return ServerResponse.createByError("保存失败");

                    }
                }

        }
        else {
            return ServerResponse.createByError("更新失败参数错误");
        }
    }
    public ServerResponse setProductStatus(Integer productId,Integer status)
    {
        if(productId==null||status==null)
        {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());//参数异常
        }
        else
        {
            Product product = new Product();
            product.setId(productId);
            product.setStatus(status);
            int i = productMapper.updateByPrimaryKeySelective(product);
            if(i>0)
            {
                return ServerResponse.createBySuccess("更新状态成功");
            }
            else
            {
                return ServerResponse.createByError("更新状态失败");

            }


        }

    }



    public ServerResponse<ProductDetailVo> getDetail(Integer productId)
    {
        if(productId==null)
        {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        else
        {
            Product product = productMapper.selectByPrimaryKey(productId);
            if(product==null)
            {
                return ServerResponse.createByError("产品已下架，或删除");
            }
            else
            {
                ProductDetailVo productDetailVo  = new ProductDetailVo();
                productDetailVo = this.assembleProductDetailVo(product);
                return ServerResponse.createBySuccess(productDetailVo);
            }
        }
    }
    public ProductDetailVo assembleProductDetailVo(Product product)
    {
        ProductDetailVo productDetailVo = new ProductDetailVo();
        productDetailVo.setId(product.getId());
        productDetailVo.setName(product.getName());
        productDetailVo.setStatus(product.getStatus());
        productDetailVo.setMainImage(product.getMainImage());
        productDetailVo.setSubImages(product.getSubImages());
        productDetailVo.setCategoryId(product.getCategoryId());
        productDetailVo.setSubtitle(product.getSubtitle());
        productDetailVo.setStock(product.getStock());
        productDetailVo.setPrice(product.getPrice());
        productDetailVo.setDetail(product.getDetail());

        productDetailVo.setImgHost(PropertiesUtil.getPropertie("ftp.server.http.prefix","http://img.happymmall.com/"));
        Category category = categoryMapper.selectByPrimaryKey(product.getCategoryId());
        if(category==null)
        {
            productDetailVo.setParentCategroyId(0);
        }
        else
        {
            productDetailVo.setParentCategroyId(category.getParentId());
        }
        //imgHost,parentCategoryId,updateTime ,createTime
        productDetailVo.setCreateTime(DateTimeUtil.timeToStr(product.getCreateTime()));
        productDetailVo.setUpdateTime(DateTimeUtil.timeToStr(product.getUpdateTime()));
        return productDetailVo;
    }



    public ServerResponse<PageInfo> getProductList(Integer pageNum, Integer pageSize)
    {
        PageHelper.startPage(pageNum,pageSize);
        List<Product> productList = productMapper.selectList();
        List<ProductVo> productvoList = new ArrayList<ProductVo>();
        for (Product item: productList
             ) {
            ProductVo productVo = this.assembleProductVo(item);
            productvoList.add(productVo);

        }
        PageInfo pageInfo = new PageInfo(productList);
        pageInfo.setList(productvoList);
        return ServerResponse.createBySuccess(pageInfo);
    }
    public ProductVo assembleProductVo(Product product)
    {
        ProductVo productVo = new ProductVo();
        productVo.setCategoryId(product.getCategoryId());
        productVo.setStock(product.getStock());
        productVo.setStatus(product.getStatus());
        productVo.setName(product.getName());
        productVo.setId(product.getId());
        productVo.setPrice(product.getPrice());
        productVo.setSubtitle(product.getSubtitle());
        productVo.setImgHost(PropertiesUtil.getPropertie("ftp.server.http.prefix","http://img.happymmall.com/"));
        return productVo;
    }


    public ServerResponse<PageInfo> searchProductList(String productName,Integer productId,Integer pageNum, Integer pageSize)
    {
        PageHelper.startPage(pageNum,pageSize);
        productName = new StringBuilder().append("%").append(productName).append("%").toString();
        //模糊查询
        List<Product> productList = productMapper.selectByProductNameOrProductId(productName,productId);
        List<ProductVo> productvoList = new ArrayList<ProductVo>();
        for (Product item: productList
                ) {
            ProductVo productVo = this.assembleProductVo(item);
            productvoList.add(productVo);

        }
        PageInfo pageInfo = new PageInfo(productList);
        pageInfo.setList(productvoList);
        return ServerResponse.createBySuccess(pageInfo);
    }

    /**
     * 前端查询产品详情
     * @param productId
     * @return
     */
    public ServerResponse<ProductDetailVo> getPruductDetail(Integer productId)
    {
        if(productId==null)
        {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        else
        {
            Product product = productMapper.selectByPrimaryKey(productId);
            if(product==null)
            {
                return ServerResponse.createByError("产品已下架，或删除");
            }
            else
            {
                if(product.getStatus()!=ResponseCode.ProductDetail.ONSALE.getCode())
                {
                    return ServerResponse.createByError("产品已下架，或删除");

                }
                ProductDetailVo productDetailVo  = new ProductDetailVo();
                productDetailVo = this.assembleProductDetailVo(product);
                return ServerResponse.createBySuccess(productDetailVo);
            }
        }
    }
    //根据关键字或id查询产品列表，并带有排序功能
    public ServerResponse<PageInfo> getProductByKeywordCategory(String orderBy,String keyword,Integer categoryId,int pageNum,int pageSize)
    {
        List<Integer> categoryIds = new ArrayList<Integer>();
        if(StringUtils.isBlank(keyword)&&categoryId==null)
        {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        if(categoryId!=null)
        {
            Category category  = categoryMapper.selectByPrimaryKey(categoryId);
            if(category==null&&StringUtils.isBlank(keyword))
            {
                //没有该分类，并且没有关键字，这个时候返回一个空的集合，不报错
                PageHelper.startPage(pageNum,pageSize);
                List<ProductVo> productVosList = Lists.newArrayList();
                PageInfo pageInfo = new PageInfo(productVosList);
                return ServerResponse.createBySuccess(pageInfo);
            }
            categoryIds = iCategoryService.selectCategoryAndChildrenByParentId(category.getId()).getData();

        }
        if(StringUtils.isNotBlank(keyword))
        {
            keyword = new StringBuilder().append("%").append(keyword).append("%").toString();

        }
        PageHelper.startPage(pageNum,pageSize);
        //排序处理
        if(StringUtils.isNotBlank(orderBy))
        {
            if(Const.ProductListOrderBy.PRICE_ASC_DESC.contains(orderBy))
            {
                String[] orderByArray = orderBy.split("_");
                PageHelper.orderBy(orderByArray[0]+" "+orderByArray[1]);//排序
            }
        }
        //因为查询的时候有为空的 情况，为空是搜索不出结果来的，这样不正确，所以需要判断下
        List<Product> productList = productMapper.selectByProductNameOrProductIds(StringUtils.isBlank(keyword)?null:keyword,categoryIds.size()==0?null:categoryIds);

        List<ProductVo> productVos = Lists.newArrayList();
        for(Product product:productList)
        {
            ProductVo productVo = this.assembleProductVo(product);
            productVos.add(productVo);
        }
        PageInfo pageInfo = new PageInfo();
        pageInfo.setList(productVos);
        return ServerResponse.createBySuccess(pageInfo);
    }
}

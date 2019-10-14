package com.atguigu.gmall.service;

import com.atguigu.gmall.bean.PmsProductInfo;
import com.atguigu.gmall.bean.PmsProductSaleAttr;

import java.util.List;

public interface SpuService {
    List<PmsProductInfo> supList(String catalog3Id);

    List<PmsProductSaleAttr> spuSaleAttrList(String spuId);
}

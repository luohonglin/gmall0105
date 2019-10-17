package com.atguigu.gmall.litem.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.bean.PmsProductSaleAttr;
import com.atguigu.gmall.bean.PmsSkuInfo;
import com.atguigu.gmall.bean.PmsSkuSaleAttrValue;
import com.atguigu.gmall.service.SkuService;
import com.atguigu.gmall.service.SpuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;

@Controller
public class ItemController {

    @Reference
    SkuService skuService;
    @Reference
    SpuService spuService;

    @RequestMapping("{skuId}.html")
    public String item(@PathVariable String skuId, ModelMap modelMap,HttpServletRequest request){

        String remoteaddr=request.getRemoteAddr();

        PmsSkuInfo pmsSkuInfo=skuService.getSkuById(skuId,remoteaddr);
        //sku对象
        modelMap.put("skuInfo",pmsSkuInfo);
        //销售属性列表
        List<PmsProductSaleAttr> pmsProductSaleAttrs=spuService.spuSaleAttrListCheckBySku(pmsSkuInfo.getProductId(),pmsSkuInfo.getId());
        modelMap.put("spuSaleAttrListCheckBySku",pmsProductSaleAttrs);

        HashMap<String, String> skuSaleAttrHash = new HashMap<>();

        List<PmsSkuInfo> pmsSkuInfos=skuService.getSkuSaleAttrValueListBySpu(pmsSkuInfo.getProductId());

        for (PmsSkuInfo skuInfo : pmsSkuInfos){
            String k="";
            String v=skuInfo.getId();
            List<PmsSkuSaleAttrValue> skuSaleAttrValueList=skuInfo.getSkuSaleAttrValueList();
            for (PmsSkuSaleAttrValue pmsSkuSaleAttrValue : skuSaleAttrValueList){
                k+=pmsSkuSaleAttrValue.getSaleAttrId()+"|";
            }
            skuSaleAttrHash.put(k,v);
        }
        //将sku的销售属性hash放到页面
        String skuSaleAttrHashJsonStr= JSON.toJSONString(skuSaleAttrHash);
        modelMap.put("skuSaleAttrHashJsonStr",skuSaleAttrHashJsonStr);
        return "item";
    }


    @RequestMapping("index")
//    @ResponseBody
    public String index(){
        return "index";
    }
}

package com.atguigu.gmall.search.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.gmall.bean.*;
import com.atguigu.gmall.service.AttrService;
import com.atguigu.gmall.service.SearchService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

@Controller
public class SearchController {

    @Reference
    SearchService searchService;

    @Reference
    AttrService attrService;

    @RequestMapping("list.html")
    public String list(PmsSearchParam pmsSearchParam,ModelMap map){//三级分类id,关键字
        //调用搜索服务,放回搜索结果
        List<PmsSearchSkuInfo> pmsSearchSkuInfos=searchService.list(pmsSearchParam);
        map.put("skuLsInfoList",pmsSearchSkuInfos);

        Set<String> valueIdSet=new HashSet<>();

        for (PmsSearchSkuInfo pmsSearchSkuInfo : pmsSearchSkuInfos) {
            List<PmsSkuAttrValue>skuAttrValues=pmsSearchSkuInfo.getSkuAttrValueList();
            for (PmsSkuAttrValue skuAttrValue : skuAttrValues) {
                String valueId= skuAttrValue.getValueId();
                valueIdSet.add(valueId);
            }
        }
        //根据value将属性列表查出来
        List<PmsBaseAttrInfo>pmsBaseAttrInfos=attrService.getAttrValueListByValueId(valueIdSet);
        map.put("attrList",pmsBaseAttrInfos);

        //对平台属性集合进一步处理,去掉条件中value所在的属性组
        String[] delValueIds=pmsSearchParam.getValueId();
        if (delValueIds!=null){
        Iterator<PmsBaseAttrInfo> iterator=pmsBaseAttrInfos.iterator();
        while (iterator.hasNext()){
            PmsBaseAttrInfo pmsBaseAttrInfo=iterator.next();
            List<PmsBaseAttrValue> attrValueList=pmsBaseAttrInfo.getAttrValueList();
            for (PmsBaseAttrValue pmsBaseAttrValue : attrValueList) {
                String valueId=pmsBaseAttrValue.getId();
                for (String delValueId : delValueIds) {
                    if (delValueId.equals(valueId)){
                        iterator.remove();
                    }
                }
            }
        }
    }

        String urlParam=getUrlParam(pmsSearchParam);
        map.put("urlParam",urlParam);
        return "list";
    }

    private String getUrlParam(PmsSearchParam pmsSearchParam) {
        String keyword=pmsSearchParam.getKeyword();
        String catalog3Id=pmsSearchParam.getCatalog3Id();
        String[] pmsSkuAttrValueList=pmsSearchParam.getValueId();
        String urlParam="";
        if (StringUtils.isNotBlank(keyword)){
            if (StringUtils.isNotBlank(urlParam)){
                urlParam+="&";
            }
            urlParam=urlParam+"&keyword"+keyword;
        }

        if (StringUtils.isNotBlank(catalog3Id)){
            if (StringUtils.isNotBlank(catalog3Id)){
                urlParam+="&";
            }
            urlParam=urlParam+"&catalog3Id"+catalog3Id;
        }

        if (pmsSkuAttrValueList!=null){
            for (String pmsSkuAttrValue : pmsSkuAttrValueList) {
                urlParam=urlParam+"valueId"+pmsSkuAttrValue;
            }
        }

        return urlParam;
    }

    @RequestMapping("index")
    public String index(){
        return "index";
    }
}

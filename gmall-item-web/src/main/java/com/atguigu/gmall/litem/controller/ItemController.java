package com.atguigu.gmall.litem.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.gmall.bean.PmsSkuInfo;
import com.atguigu.gmall.service.SkuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ItemController {

    @Reference
    SkuService skuService;

    @RequestMapping("{skuId}.html")
    public String item(@PathVariable String skuId, ModelMap modelMap){
        PmsSkuInfo pmsSkuInfo=skuService.getSkuById(skuId);
        modelMap.put("skuInfo",pmsSkuInfo);
        return "item";
    }


    @RequestMapping("index")
//    @ResponseBody
    public String index(){
        return "index";
    }
}

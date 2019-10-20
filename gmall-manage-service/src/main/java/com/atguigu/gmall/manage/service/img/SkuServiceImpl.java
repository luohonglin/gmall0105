package com.atguigu.gmall.manage.service.img;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.bean.PmsSkuAttrValue;
import com.atguigu.gmall.bean.PmsSkuImage;
import com.atguigu.gmall.bean.PmsSkuInfo;
import com.atguigu.gmall.bean.PmsSkuSaleAttrValue;
import com.atguigu.gmall.manage.mapper.PmsSkuAttrValueMapper;
import com.atguigu.gmall.manage.mapper.PmsSkuImageMapper;
import com.atguigu.gmall.manage.mapper.PmsSkuInfoMapper;
import com.atguigu.gmall.manage.mapper.PmsSkuSaleAttrValueMapper;
import com.atguigu.gmall.service.SkuService;
import com.atguigu.gmall.util.RedisUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;


import javax.swing.*;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
public class SkuServiceImpl implements SkuService {

    @Autowired
    PmsSkuInfoMapper pmsSkuInfoMapper;

    @Autowired
    PmsSkuAttrValueMapper pmsSkuAttrValueMapper;

    @Autowired
    PmsSkuSaleAttrValueMapper pmsSkuSaleAttrValueMapper;

    @Autowired
    PmsSkuImageMapper pmsSkuImageMapper;

    @Autowired
    RedisUtil redisUtil;

    @Override
    public void saveSkuInfo(PmsSkuInfo pmsSkuInfo) {

        // 插入skuInfo
        int i = pmsSkuInfoMapper.insertSelective(pmsSkuInfo);
        String skuId = pmsSkuInfo.getId();

        // 插入平台属性关联
        List<PmsSkuAttrValue> skuAttrValueList = pmsSkuInfo.getSkuAttrValueList();
        for (PmsSkuAttrValue pmsSkuAttrValue : skuAttrValueList) {
            pmsSkuAttrValue.setSkuId(skuId);
            pmsSkuAttrValueMapper.insertSelective(pmsSkuAttrValue);
        }

        // 插入销售属性关联
        List<PmsSkuSaleAttrValue> skuSaleAttrValueList = pmsSkuInfo.getSkuSaleAttrValueList();
        for (PmsSkuSaleAttrValue pmsSkuSaleAttrValue : skuSaleAttrValueList) {
            pmsSkuSaleAttrValue.setSkuId(skuId);
            pmsSkuSaleAttrValueMapper.insertSelective(pmsSkuSaleAttrValue);
        }

        // 插入图片信息
        List<PmsSkuImage> skuImageList = pmsSkuInfo.getSkuImageList();
        for (PmsSkuImage pmsSkuImage : skuImageList) {
            pmsSkuImage.setSkuId(skuId);
            pmsSkuImageMapper.insertSelective(pmsSkuImage);
        }


    }

    public PmsSkuInfo getSkuByIdFromDb(String skuId) {
        //sku商品对象
        PmsSkuInfo pmsSkuInfo = new PmsSkuInfo();
        pmsSkuInfo.setId(skuId);
        PmsSkuInfo skuInfo=pmsSkuInfoMapper.selectOne(pmsSkuInfo);

        //sku的图片集合
        PmsSkuImage pmsSkuImage=new PmsSkuImage();
        pmsSkuImage.setSkuId(skuId);
        List<PmsSkuImage> pmsSkuImages=pmsSkuImageMapper.select(pmsSkuImage);
        skuInfo.setSkuImageList(pmsSkuImages);
        return skuInfo;
    }


    @Override
    public PmsSkuInfo getSkuById(String skuId,String ip) {


        PmsSkuInfo pmsSkuInfo=new PmsSkuInfo();

        //链接缓存
        Jedis jedis= redisUtil.getJedis();
        //查询缓存
        String skuKey="sku:"+skuId+"info";
        String skuJons=jedis.get(skuKey);
        if (StringUtils.isNotBlank(skuJons)){
            System.out.println("ip为"+ip+"的同学:"+Thread.currentThread().getName()+"进入商品详细的请求");
            pmsSkuInfo = JSON.parseObject(skuJons, PmsSkuInfo.class);
        }else {
            System.out.println("ip为"+ip+"的同学:"+Thread.currentThread().getName()+"进入商品详细的请求"+"sku:"+skuId+":lock");
            String token= UUID.randomUUID().toString();
            // 设置分布式锁
            String ok = jedis.set("sku:"+skuId+":lock",token,"nx","px",10*1000);//拿到锁有10秒的过期时间
            if(StringUtils.isNoneBlank(ok)&&ok.equals("ok")){
                System.out.println("ip为"+ip+"的同学:"+Thread.currentThread().getName()+"有权在10秒的过期时间内访问数据库"+"sku:"+skuId+":lock");

                // 设置成功，有权在10秒的过期时间内访问数据库
                pmsSkuInfo =  getSkuByIdFromDb(skuId);
                if(pmsSkuInfo!=null){
                    // mysql查询结果存入redis
                    jedis.set("sku:"+skuId+":info",JSON.toJSONString(pmsSkuInfo));
                }else{
                    // 数据库中不存在该sku
                    // 为了防止缓存穿透将，null或者空字符串值设置给redis
                    jedis.setex("sku:"+skuId+":info",60*3,JSON.toJSONString(""));
                }
                try {
                    Thread.sleep(5*1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("ip为"+ip+"的同学:"+Thread.currentThread().getName()+"使用完毕,将锁归还"+"sku:"+skuId+":lock");
                String script ="if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
                jedis.eval(script, Collections.singletonList("sku:"+skuId+":lock"),Collections.singletonList(token));
//                String lockToken=jedis.get("sku:"+skuId+":lock");
//                if (StringUtils.isNoneBlank(lockToken)&&lockToken.equals(token)){
//                    jedis.del("sku:"+skuId+":lock");
//                }
            }else{
                System.out.println("ip为"+ip+"的同学:"+Thread.currentThread().getName()+"没有拿到锁,开始自选");
                // 设置失败，自旋（该线程在睡眠几秒后，重新尝试访问本方法）
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return getSkuById(skuId,ip);
            }
        }
        jedis.close();
        return pmsSkuInfo;
    }

    @Override
    public List<PmsSkuInfo> getSkuSaleAttrValueListBySpu(String productId) {
        List<PmsSkuInfo> pmsSkuInfos=pmsSkuInfoMapper.selectSkuSaleAttrValueListBySpu(productId);
        return pmsSkuInfos;
    }
}

package com.atguigu.gmall.user.service.impl;


import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.bean.UmsMember;
import com.atguigu.gmall.bean.UmsMemberReceiveAddress;
import com.atguigu.gmall.service.UserService;
import com.atguigu.gmall.user.mapper.UmsMemberReceiveAddressMapper;
import com.atguigu.gmall.user.mapper.UserMapper;
import com.atguigu.gmall.util.RedisUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserMapper userMapper;

    @Autowired
    RedisUtil redisUtil;

    @Autowired
    UmsMemberReceiveAddressMapper umsMemberReceiveAddressMapper;

    @Override
    public List<UmsMember> getAllUser() {
        List<UmsMember> umsMemberList= userMapper.selectAll();//userMapper.selectAllUser();
        return umsMemberList;
    }


    @Override
    public List<UmsMemberReceiveAddress> getReceiveAddressByMemberId(String memberId) {
        Example e=new Example(UmsMemberReceiveAddress.class);
        e.createCriteria().andEqualTo("memberId",memberId);
        return umsMemberReceiveAddressMapper.selectByExample(e);
    }

    @Override
    public UmsMember logn(UmsMember umsMember) {
        Jedis jedis=null;
        try {
            jedis=redisUtil.getJedis();
            if (jedis!=null){
                String umsMemberStr=jedis.get("user:"+umsMember.getPassword()+":info");
                if (StringUtils.isNotBlank(umsMemberStr)){
                    //密码正确
                    UmsMember umsMemberFromCache= JSON.parseObject(umsMemberStr,UmsMember.class);
                    return umsMemberFromCache;
                }
            }
            //密码错误
            //缓存中没有数据
            //在数据库总查询
            UmsMember umsMemberFromDb=loginFromDb(umsMember);
            if (umsMember!=null){
                jedis.setex("user:"+umsMember.getPassword()+":info",60*60*24,JSON.toJSONString(umsMemberFromDb));
            }
            return umsMemberFromDb;
        }finally {
            jedis.close();
        }
    }

    @Override
    public void addUserToken(String token, String memberId) {
        Jedis jedis=redisUtil.getJedis();
        jedis.setex("user:"+memberId+":token",60*60*2,token);
        jedis.close();
    }

    private UmsMember loginFromDb(UmsMember umsMember) {
        List<UmsMember> umsMembers=userMapper.select(umsMember);
        if (umsMembers!=null){
            return umsMembers.get(0);
        }
        return null;
    }
}

package com.atguigu.gmall.user.service.img;



//import com.atguigu.gmall.user.mapper.UmsMemberReceiveAddressMapper;
//import com.atguigu.gmall.user.mapper.UserMapper;

//@Service
//public class UserServiceImpl implements UserService {

//    @Autowired
//    UserMapper userMapper;

//    @Autowired
//    UmsMemberReceiveAddressMapper umsMemberReceiveAddressMapper;

//    @Override
//    public List<UmsMember> getAllUser() {
//        List<UmsMember> umsMemberList= userMapper.selectAllUser();//userMapper.selectAllUser();
//        return umsMemberList;
//    }
//
//
//    @Override
//    public List<UmsMemberReceiveAddress> getReceiveAddressByMemberId(String memberId) {
//
////        UmsMemberReceiveAddress umsMemberReceiveAddress=new UmsMemberReceiveAddress();
////        umsMemberReceiveAddress.setMemberId(memberId);
//        Example e=new Example(UmsMemberReceiveAddress.class);
//        e.createCriteria().andEqualTo("memberId",memberId);
//        return umsMemberReceiveAddressMapper.selectByExample(e);
//    }
//}

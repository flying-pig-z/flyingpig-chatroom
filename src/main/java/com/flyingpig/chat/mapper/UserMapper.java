package com.flyingpig.chat.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.flyingpig.chat.dataobject.eneity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}

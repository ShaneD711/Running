package com.shaned.running.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shaned.running.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户 Mapper，继承 BaseMapper 后无需手写 SQL 即可使用常用 CRUD 方法：
 *
 *   insert(user)                    插入一条记录
 *   selectById(id)                  按 ID 查询
 *   selectOne(queryWrapper)         按条件查询一条
 *   updateById(user)                按 ID 更新非 null 字段
 *   deleteById(id)                  按 ID 删除
 *
 * 需要自定义 SQL 时，在此接口添加方法，并在 resources/mapper/UserMapper.xml 中编写对应 SQL
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
}

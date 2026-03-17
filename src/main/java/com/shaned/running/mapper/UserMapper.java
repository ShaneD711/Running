package com.shaned.running.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shaned.running.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户数据访问层
 *
 * 作用：负责与数据库交互，执行增删改查操作。
 *
 * 继承 BaseMapper<User> 后，MyBatisPlus 自动提供以下常用方法，不需要手写SQL：
 * - insert(user)                    插入一条记录
 * - deleteById(id)                  根据ID删除
 * - updateById(user)                根据ID更新
 * - selectById(id)                  根据ID查询
 * - selectOne(queryWrapper)         根据条件查询一条
 * - selectList(queryWrapper)        根据条件查询多条
 * - selectPage(page, queryWrapper)  分页查询
 *
 * 如果需要自定义复杂SQL，可以在这里添加方法，并在 resources/mapper/UserMapper.xml 中编写SQL。
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
}

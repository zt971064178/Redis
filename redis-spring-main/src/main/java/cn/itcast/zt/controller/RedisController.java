package cn.itcast.zt.controller;

import cn.itcast.zt.domain.RedisModel;
import cn.itcast.zt.sevice.RedisServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Created by zhangtian on 2017/4/25.
 */
@RestController
public class RedisController {
    @Autowired
    private RedisServiceImpl service;

    // 添加
    @GetMapping(value = "add")
    public void test() {
        System.out.println("start.....");
        RedisModel m = new RedisModel();
        m.setName("张三");
        m.setTel("1111");
        m.setAddress("深圳1");
        m.setRedisKey("zhangsanKey01");
        service.put(m.getRedisKey(), m, -1);

        RedisModel m2 = new RedisModel();
        m2.setName("张三2");
        m2.setTel("2222");
        m2.setAddress("深圳2");
        m2.setRedisKey("zhangsanKey02");
        service.put(m2.getRedisKey(), m2, -1);

        RedisModel m3 = new RedisModel();
        m3.setName("张三3");
        m3.setTel("2222");
        m3.setAddress("深圳2");
        m3.setRedisKey("zhangsanKey03");
        service.put(m3.getRedisKey(), m3, -1);

        System.out.println("add success end...");
    }

    // 查询所有对象
    @GetMapping(value = "getAll")
    public Object getAll() {
        return service.getAll();
    }

    // 查询所有key
    @GetMapping(value = "getKeys")
    public Object getKeys() {
        return service.getKeys();
    }

    // 根据key查询
    @GetMapping(value = "get")
    public Object get() {
        RedisModel m = new RedisModel();
        m.setRedisKey("zhangsanKey02");
        return service.get(m.getRedisKey());
    }

    // 删除
    @GetMapping(value = "remove")
    public void remove() {
        RedisModel m = new RedisModel();
        m.setRedisKey("zhangsanKey01");
        service.remove(m.getRedisKey());
    }

    // 判断key是否存在
    @GetMapping(value = "isKeyExists")
    public void isKeyExists() {
        RedisModel m = new RedisModel();
        m.setRedisKey("zhangsanKey01");
        boolean flag = service.isKeyExists(m.getRedisKey());
        System.out.println("zhangsanKey01 是否存在: "+flag);
    }

    // 查询当前缓存的数量
    @GetMapping(value = "count")
    public Object count() {
        return service.count();
    }

    // 清空所有key
    @GetMapping(value = "empty")
    public void empty() {
        service.empty();
    }
}

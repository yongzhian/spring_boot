package cn.zain.dao;

import cn.zain.model.po.SysUser;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SysUserMapperTest {
    private static final Logger logger = LoggerFactory.getLogger(SysUserMapperTest.class);
    @Autowired
    private SysUserMapper sysUserMapper;

    @Test
    public void getAllTest() throws Exception {
        final List<SysUser> all = sysUserMapper.getAll();
        logger.info("....");
    }

    @Test
    public void insertAllTest() throws Exception {
        List<SysUser> list = new ArrayList<>();
        list.add(new SysUser("a", "b"));
        list.add(new SysUser("ca", "cb"));
        sysUserMapper.insertAll(list);
        logger.info("{}", list);
    }

    @Test
    public void insertAllInSysUserMapperProviderTest() throws Exception {
        SysUserMapperProvider sysUserMapperProvider = new SysUserMapperProvider();
        List<SysUser> list = new ArrayList<>();
        list.add(new SysUser("a", "b"));
        list.add(new SysUser("ca", "cb"));
        Map<String, Object> map = new HashMap<>();
        map.put("list", list);
        logger.info(sysUserMapperProvider.insertAll(map));//insert into sys_user(username,password) VALUES('a', 'a'),('ca', 'ca')
    }
}

package cn.zain.dao;

import cn.zain.model.po.SysUser;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

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
}

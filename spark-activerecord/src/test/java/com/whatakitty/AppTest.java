package com.whatakitty;

import com.whatakitty.druid.DruidPlugin;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class AppTest {

    private DruidPlugin druidPlugin;
    private ActiveRecordPlugin activeRecordPlugin;

    @Before
    public void init() {
        druidPlugin = new DruidPlugin("jdbc:mysql://localhost:3306/spark", "spark", "spark");
        druidPlugin.start();
        activeRecordPlugin = new ActiveRecordPlugin(druidPlugin);
        activeRecordPlugin.addMapping("test", TestModel.class);
        activeRecordPlugin.start();
    }

    @After
    public void destory() {
        activeRecordPlugin.stop();
        druidPlugin.stop();
    }

    @Test
    public void test() {
        List<TestModel> list = TestModel.dao.find("select id, name from test");
        Assert.assertEquals(1, list.size());
    }

    static class TestModel extends Model<TestModel> {
        static TestModel dao = new TestModel();
    }
}

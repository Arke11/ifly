package com.wcy.rhapsody.admin;

import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.extra.mail.MailUtil;
import com.wcy.rhapsody.admin.mapper.TopicMapper;
import com.wcy.rhapsody.admin.model.entity.Topic;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class BootApplicationTests {

    @Test
    void contextLoads() {
    }

    @Autowired
    private TopicMapper topicMapper;

    @Test
    void testUpdate() {
        Topic topic = topicMapper.selectById("1331577537143721986");
        topic.setContent("hi test");
        topicMapper.updateById(topic);
    }

    @Test
    void se() {
        ThreadUtil.execAsync(() -> {
            // 发送激活邮件?user=hhh&code=true
            String activeUrl = URLUtil.normalize("http://47.105.186.18" + "?user=mabaoguo&code=" + "1234124");
            String content = "请在30分钟内激活您的账号，如非本人操作，请忽略 </br > " +
                    "<a href=\"" + activeUrl + "\" target =\"_blank\" '>点击激活账号</a>";
            MailUtil.send("1020317774@qq.com", "【滚雪球】账号激活", content, true);
        });
    }

}

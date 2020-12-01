package com.wcy.rhapsody.admin.controller.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.vdurmont.emoji.EmojiParser;
import com.wcy.rhapsody.admin.annotation.RequireLogin;
import com.wcy.rhapsody.admin.controller.BaseController;
import com.wcy.rhapsody.admin.core.R;
import com.wcy.rhapsody.admin.model.dto.CreateTopicDTO;
import com.wcy.rhapsody.admin.model.entity.Topic;
import com.wcy.rhapsody.admin.model.entity.User;
import com.wcy.rhapsody.admin.model.vo.TopicVO;
import com.wcy.rhapsody.admin.service.TopicService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 议题，话题控制器
 *
 * @author Yeeep 2020/11/7
 */
@Api(tags = "话题处理器")
@RestController
@RequestMapping("/topic")
public class TopicController extends BaseController {

    @Resource
    private TopicService topicService;


    /**
     * 发布
     */
    @RequireLogin
    @ApiOperation(value = "发布话题")
    @PostMapping("/create")
    public R create(@RequestBody CreateTopicDTO dto, HttpServletRequest request) {
        User loginUser = getLoginUser(request);
        Assert.isTrue(loginUser.getActive(), "你的帐号还没有激活，请去个人设置页面激活帐号");
        Topic topic = topicService.create(dto, loginUser);
        return R.ok().data(topic);
    }

    /**
     * 删除
     *
     * @param id
     * @return
     */
    @RequireLogin
    @ApiImplicitParam(value = "id", name = "话题ID", required = true, paramType = "path")
    @ApiOperation(value = "删除", notes = "")
    @DeleteMapping("/delete/{id}")
    public R delete(@PathVariable("id") String id, HttpServletRequest request) {
        User loginUser = getLoginUser(request);
        Topic byId = topicService.getById(id);
        Assert.notNull(byId, "来晚一步，话题已不存在");
        Assert.isTrue(byId.getUserId().equals(loginUser.getId()), "你为什么可以删除别人的话题？？？");
        topicService.removeById(id);
        return R.ok().message("删除成功").code(200);
    }

    /**
     * 修改主题
     */
    @RequireLogin
    @PostMapping("/update")
    @ApiOperation(value = "话题更新", notes = "")
    public R update(@Valid @RequestBody Topic topic, HttpServletRequest request) {
        User loginUser = getLoginUser(request);
        Assert.isTrue(loginUser.getId().equals(topic.getUserId()), "非本人无权修改");
        topic.setModifyTime(new Date());
        topic.setContent(EmojiParser.parseToAliases(topic.getContent()));
        topicService.updateById(topic);
        return R.ok().data(topic);
    }

    /**
     * 首页获取话题列表
     */
    @ApiOperation(value = "获取话题列表", notes = "分页查询，默认每页10条数据")
    @GetMapping("/all")
    public R getTopicList(
            @ApiParam("页码，默认值1") @RequestParam(value = "pageNo", defaultValue = "1") Integer pageNo,
            @ApiParam("每页展示数据量，默认10") @RequestParam(value = "size", defaultValue = "10") Integer size,
            @ApiParam("类别，如latest(最新)，hot(热门)，essence(加精)，top(置顶)， 默认查询latest")
            @RequestParam(value = "tab", defaultValue = "latest") String tab) {
        Page<TopicVO> voPage = topicService.getList(new Page<>(pageNo, size), tab);
        return R.ok().data(voPage).message("首页话题列表接口调用成功");
    }

    /**
     * 浏览指定话题
     */
    @GetMapping()
    @ApiOperation(value = "获取指定话题,议题", notes = "输入话题ID获取")
    public R view(@ApiParam(value = "id", name = "话题ID", required = true) @RequestParam("id") String id) {
        Map<String, Object> map = topicService.viewTopic(id);
        return R.ok().data(map);
    }

    /**
     * 详情页推荐
     */
    @ApiOperation(value = "获取详情页推荐")
    @GetMapping("/recommend")
    public R getRecommend(@RequestParam("topicId") String id) {
        List<Topic> topics = topicService.getRecommend(id);
        return R.ok().data(topics);
    }


}

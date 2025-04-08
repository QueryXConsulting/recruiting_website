package com.queryx.recruiting_website.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.queryx.recruiting_website.domain.TDMessageBoard;
import com.queryx.recruiting_website.domain.vo.LastMessageVO;
import com.queryx.recruiting_website.domain.vo.MessageDataVO;

import java.util.List;

/**
 * 留言板表 服务类
 */
public interface MessageBoardService extends IService<TDMessageBoard> {

    Object getMessageData(Integer page, Integer size, Long userId);

    /**
     * 查询用户与公司留言数据
     *
     * @param companyId 公司id
     * @return 留言数据
     */
    List<MessageDataVO> queryMessageData(Long companyId);

    Object lastMessage();

    /**
     * 查询用户所有留言记录
     *
     * @return 留言记录
     */
    List<LastMessageVO> queryMessageListAndLastMessage();

    Object sendMessage(Long userId, String content);

    /**
     * 发送留言
     *
     * @param companyId 公司id
     * @param content   留言内容
     * @return 留言数据保存结果
     */
    Boolean saveMessage(Long companyId, String content);


}

package com.queryx.recruiting_website.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.queryx.recruiting_website.domain.TDMessageBoard;

/**
 * 留言板表 服务类
 */
public interface MessageBoardService extends IService<TDMessageBoard> {

    Object getMessageData(Integer page,Integer size,Long userId);

    Object lastMessage();

    Object sendMessage(Long userId, String content);
}

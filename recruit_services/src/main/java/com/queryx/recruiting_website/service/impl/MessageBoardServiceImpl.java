package com.queryx.recruiting_website.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.queryx.recruiting_website.constant.Common;
import com.queryx.recruiting_website.domain.TDCompanyInfo;
import com.queryx.recruiting_website.domain.TDMessageBoard;
import com.queryx.recruiting_website.domain.TDUser;
import com.queryx.recruiting_website.domain.vo.LastMessageVO;
import com.queryx.recruiting_website.domain.vo.MessageDataVO;
import com.queryx.recruiting_website.mapper.MessageBoardMapper;
import com.queryx.recruiting_website.service.MessageBoardService;
import com.queryx.recruiting_website.service.TDCompanyInfoService;
import com.queryx.recruiting_website.service.TDUserService;
import com.queryx.recruiting_website.utils.SecurityUtils;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Collectors;


@Service
public class MessageBoardServiceImpl extends ServiceImpl<MessageBoardMapper, TDMessageBoard> implements MessageBoardService {

    @Resource
    private TDUserService userService;
    @Resource
    private TDCompanyInfoService companyInfoService;

    @Override
    public Object getMessageData(Integer page,Integer size,Long userId) {
        // 设置分页参数，获取最后一页，每页4条数据
        Page<TDMessageBoard> pageData = new Page<>(page, size);
        LambdaQueryWrapper<TDMessageBoard> queryWrapper = new LambdaQueryWrapper<TDMessageBoard>()
                .eq(TDMessageBoard::getUserId, userId)
                .eq(TDMessageBoard::getCompanyId, SecurityUtils.getLoginUser().getTdUser().getCompanyInfoId())
                .eq(TDMessageBoard::getIsDeleted, Common.NOT_DELETE)
                .orderByDesc(TDMessageBoard::getCreateTime);

        Page<TDMessageBoard> messageBoardPage = page(pageData, queryWrapper);

        List<TDMessageBoard> messageBoards = messageBoardPage.getRecords();
        if (messageBoards.isEmpty()) {
            return null;
        }

        // 设置已读状态并更新
        for (TDMessageBoard messageBoard : messageBoards) {
            messageBoard.setIsRead(Common.READ);
        }
        updateBatchById(messageBoards);

        // 按createTime排序（分页查询已经按createTime倒序）
        messageBoards.sort(Comparator.comparing(TDMessageBoard::getCreateTime));

        return messageBoards.stream().map(messageBoard -> {
            MessageDataVO messageDataVO = new MessageDataVO();
            BeanUtils.copyProperties(messageBoard, messageDataVO);
            if (Common.COMPANY_TYPE.equals(messageBoard.getOwnerUser())) {
                String companyInfoName = companyInfoService.getById(SecurityUtils.getLoginUser().getTdUser().getCompanyInfoId()).getCompanyInfoName();
                messageDataVO.setUser(companyInfoName);
            } else {
                String userName = userService.getById(userId).getUserName();
                messageDataVO.setUser(userName);
            }
            return messageDataVO;
        }).toList();
    }

    @Override
    public List<MessageDataVO> queryMessageData(Long companyId) {
        final Long userId = SecurityUtils.getLoginUser().getTdUser().getUserId();
        LambdaQueryWrapper<TDMessageBoard> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TDMessageBoard::getUserId, userId);
        queryWrapper.eq(TDMessageBoard::getCompanyId, companyId);
        queryWrapper.eq(TDMessageBoard::getIsDeleted, Common.NOT_DELETE);
        List<TDMessageBoard> messageBoards = list(queryWrapper);
        if (messageBoards == null || messageBoards.isEmpty()) {
            return null;
        }

        // 留言标记已读
        for (TDMessageBoard messageBoard : messageBoards) {
            messageBoard.setApplicantReadStatus(Common.READ);
        }
        updateBatchById(messageBoards);
        // 按时间排序
        messageBoards.sort(Comparator.comparing(TDMessageBoard::getCreateTime));

        return messageBoards.stream().map(messageBoard -> {
            MessageDataVO messageDataVO = new MessageDataVO();
            BeanUtils.copyProperties(messageBoard, messageDataVO);
            if ("0".equals(messageBoard.getOwnerUser())) {
                String userName = userService.getById(userId).getUserName();
                messageDataVO.setUser(userName);
            } else {
                String companyInfoName = companyInfoService.getById(companyId).getCompanyInfoName();
                messageDataVO.setUser(companyInfoName);
            }
            return messageDataVO;
        }).toList();
    }

    @Override
    public Object lastMessage() {
        LambdaQueryWrapper<TDMessageBoard> queryWrapper = new LambdaQueryWrapper<TDMessageBoard>()
                .eq(TDMessageBoard::getCompanyId, SecurityUtils.getLoginUser().getTdUser().getCompanyInfoId())
                .eq(TDMessageBoard::getIsDeleted, Common.NOT_DELETE);
        List<TDMessageBoard> messageBoards = list(queryWrapper);
        if (messageBoards.isEmpty()) {
            return null;
        }
        List<Long> userId = messageBoards.stream().map(TDMessageBoard::getUserId).toList();
        Map<Long, String> userNameMap = userService.listByIds(userId).stream().collect(Collectors.toMap(TDUser::getUserId, TDUser::getUserName));

        List<TDMessageBoard> latestMessageBoards = new ArrayList<>(messageBoards.stream()
                .collect(Collectors.toMap(TDMessageBoard::getUserId, Function.identity()
                        , BinaryOperator.maxBy(Comparator.comparing(TDMessageBoard::getCreateTime))))
                .values());

        return latestMessageBoards.stream().map(messageBoard -> {
            LastMessageVO lastMessageVO = new LastMessageVO();
            BeanUtils.copyProperties(messageBoard, lastMessageVO);
            lastMessageVO.setUserName(userNameMap.get(messageBoard.getUserId()));
            return lastMessageVO;
        }).toList();
    }

    @Override
    public List<LastMessageVO> queryMessageListAndLastMessage() {
        LambdaQueryWrapper<TDMessageBoard> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TDMessageBoard::getUserId, SecurityUtils.getLoginUser().getTdUser().getUserId());
        queryWrapper.eq(TDMessageBoard::getIsDeleted, Common.NOT_DELETE);
        List<TDMessageBoard> messageBoards = list(queryWrapper);
        if (messageBoards == null || messageBoards.isEmpty()) {
            return null;
        }
        List<Long> companyId = messageBoards.stream().map(TDMessageBoard::getCompanyId).toList();
        Map<Long, String> companyNameMap = companyInfoService.listByIds(companyId).stream().collect(Collectors.toMap(TDCompanyInfo::getCompanyInfoId, TDCompanyInfo::getCompanyInfoName));

        List<TDMessageBoard> latestMessageBoards = new ArrayList<>(messageBoards.stream()
                .collect(Collectors.toMap(TDMessageBoard::getCompanyId, Function.identity()
                        , BinaryOperator.maxBy(Comparator.comparing(TDMessageBoard::getCreateTime))))
                .values());
        // TODO 留言列表：id和名字对应关系待确认
        return latestMessageBoards.stream().map(messageBoard -> {
            LastMessageVO lastMessageVO = new LastMessageVO();
            BeanUtils.copyProperties(messageBoard, lastMessageVO);
            lastMessageVO.setUserId(messageBoard.getCompanyId());
            lastMessageVO.setUserName(companyNameMap.get(messageBoard.getCompanyId()));
            return lastMessageVO;
        }).toList();
    }

    @Override
    public Object sendMessage(Long userId, String content) {
        Long companyInfoId = SecurityUtils.getLoginUser().getTdUser().getCompanyInfoId();
        TDMessageBoard tdMessageBoard = new TDMessageBoard();
        tdMessageBoard.setCompanyId(companyInfoId);
        tdMessageBoard.setUserId(userId);
        tdMessageBoard.setOwnerUser(Common.COMPANY_TYPE);
        tdMessageBoard.setContent(content);
        tdMessageBoard.setIsRead(Common.READ);
        save(tdMessageBoard);
        return null;
    }

    @Override
    public Boolean saveMessage(Long companyId, String content) {
        TDMessageBoard tdMessageBoard = new TDMessageBoard();
        tdMessageBoard.setCompanyId(companyId);
        tdMessageBoard.setUserId(SecurityUtils.getLoginUser().getTdUser().getUserId());
        tdMessageBoard.setOwnerUser(Common.USER_TYPE);
        tdMessageBoard.setContent(content);
        tdMessageBoard.setApplicantReadStatus(Common.READ);
        return save(tdMessageBoard);
    }
}

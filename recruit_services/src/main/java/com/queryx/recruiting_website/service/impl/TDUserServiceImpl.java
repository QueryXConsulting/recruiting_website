package com.queryx.recruiting_website.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.queryx.recruiting_website.constant.AppHttpCodeEnum;
import com.queryx.recruiting_website.constant.Common;
import com.queryx.recruiting_website.domain.*;
import com.queryx.recruiting_website.domain.dto.LoginDTO;
import com.queryx.recruiting_website.domain.dto.UserCompanyDto;
import com.queryx.recruiting_website.domain.dto.UserDto;
import com.queryx.recruiting_website.domain.dto.UserRegisterDTO;
import com.queryx.recruiting_website.domain.vo.UserCompanyVO;
import com.queryx.recruiting_website.domain.vo.UserInfoVO;
import com.queryx.recruiting_website.domain.vo.UserLoginVO;
import com.queryx.recruiting_website.domain.vo.UserVO;
import com.queryx.recruiting_website.exception.SystemException;
import com.queryx.recruiting_website.mapper.*;
import com.queryx.recruiting_website.service.TDUserService;
import com.queryx.recruiting_website.utils.CommonResp;
import com.queryx.recruiting_website.utils.JwtUtil;
import com.queryx.recruiting_website.utils.SecurityUtils;
import com.queryx.recruiting_website.utils.TokenStorage;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;


@Service("tDUserService")
public class TDUserServiceImpl extends ServiceImpl<TDUserMapper, TDUser> implements TDUserService {
    @Resource
    private TDUserMapper tdUserMapper;
    @Resource
    private PasswordEncoder passwordEncoder;
    @Resource
    private AuthenticationManager authenticationManager;
    @Resource
    private TDResumeMapper resumeMapper;
    @Resource
    private TDCompanyInfoMapper companyInfoMapper;
    @Resource
    private TDAdminMapper adminMapper;
    @Resource
    private TPRoleMapper roleMapper;
    @Resource
    private TPRoleMapper tpRoleMapper;

    @Autowired
    private TDUserMapper userMapper;

    @Value("${file.upload-path-avatar}")
    private String uploadPathAvatar;

    @Override
    public UserCompanyDto selectUserInfo(Long userId, String userRole) {
        LambdaQueryWrapper<TDUser> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userLambdaQueryWrapper.eq(TDUser::getUserId, userId)
                .eq(TDUser::getDelFlag, Common.NOT_DELETE);
        if (StringUtils.hasText(userRole)) {
            userLambdaQueryWrapper.eq(TDUser::getUserRole, userRole)
                    .eq(TDUser::getUserStatus, Common.STATUS_ENABLE);
        }

        TDUser tdUser = tdUserMapper.selectOne(userLambdaQueryWrapper);
        UserCompanyDto userCompanyDto = new UserCompanyDto();
        BeanUtils.copyProperties(tdUser, userCompanyDto);
        TPRole tpRole = roleMapper.selectById(tdUser.getUserRole());
        userCompanyDto.setUserRole(tpRole.getRoleName());
        userCompanyDto.setUserAvatar(Common.getImgURL() + tdUser.getUserAvatar());
        return userCompanyDto;
    }

    @Override
    public UserCompanyDto updateUserCompanyInfo(UserCompanyDto userCompanyDto) {
        TDUser tdUser = SecurityUtils.getLoginUser().getTdUser();
        BeanUtils.copyProperties(userCompanyDto, tdUser);
        tdUser.setUserPassword(passwordEncoder.encode(tdUser.getUserPassword()));
        UpdateWrapper<TDUser> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("user_id", tdUser.getUserId())
                .eq("user_status", Common.STATUS_ENABLE)
                .eq("del_flag", Common.NOT_DELETE);
        if (!update(tdUser, updateWrapper)) {
            throw new SystemException(AppHttpCodeEnum.SYSTEM_ERROR);
        }
        return null;
    }

    @Override
    public UserLoginVO login(LoginDTO loginDTO) {
        //  TODO 用户登录：(验证码待实现) 判断用户名登录方式
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken
                = new UsernamePasswordAuthenticationToken(loginDTO.getUsername(), loginDTO.getUserPassword());
        Authentication authenticate = authenticationManager.authenticate(usernamePasswordAuthenticationToken);
        // 判断是否验证通过
        if (Objects.isNull(authenticate)) {
            throw new SystemException(AppHttpCodeEnum.LOGIN_ERROR);
        }
        LoginUser loginUser = (LoginUser) authenticate.getPrincipal();
        UserLoginVO userLoginVO = new UserLoginVO();
        // 生成token
        String jwt = JwtUtil.createJWT(loginUser.getTdUser().getUserId());
        userLoginVO.setToken(jwt);
        UserInfoVO userInfoVO = new UserInfoVO();
        BeanUtils.copyProperties(loginUser.getTdUser(), userInfoVO);
        userInfoVO.setPermissions(loginUser.getPermissions());
        if (StringUtils.hasText(loginUser.getTdUser().getUserAvatar())) {
            userInfoVO.setUserAvatar(Common.getImgURL() + loginUser.getTdUser().getUserAvatar());
        }
        userLoginVO.setUserInfoVO(userInfoVO);
        TokenStorage.addToken(jwt, loginUser);
        if (!loginUser.getTdUser().getUserRole().equals(Common.STUDENT_USER.toString())) {
            TDCompanyInfo tdCompanyInfo = companyInfoMapper.selectById(loginUser.getTdUser().getCompanyInfoId());
            userInfoVO.setEnterpriseReview(tdCompanyInfo.getEnterpriseReview());
            userInfoVO.setCompanyInfoReview(tdCompanyInfo.getCompanyInfoReview());
        }
        // 更新登录标志
        if (loginUser.getTdUser().getIsFirstLogin().equals("0")) {
            LambdaUpdateWrapper<TDUser> tdUserLambdaQueryWrapper = new LambdaUpdateWrapper<>();
            tdUserLambdaQueryWrapper.eq(TDUser::getUserId, loginUser.getTdUser().getUserId());
            tdUserLambdaQueryWrapper.set(TDUser::getIsFirstLogin, 1);
            update(tdUserLambdaQueryWrapper);
        }
        // 返回前端凭证
        return userLoginVO;
    }

    @Override
    public UserRegisterDTO register(UserRegisterDTO userRegisterDTO) {
        LambdaQueryWrapper<TDUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TDUser::getUserPhone, userRegisterDTO.getUserPhone())
                .eq(TDUser::getDelFlag, Common.NOT_DELETE);
        if (count(queryWrapper) > 0) {
            throw new SystemException(AppHttpCodeEnum.PHONE_EXIST);
        }

        LambdaQueryWrapper<TDResume> Wrapper = new LambdaQueryWrapper<>();
        Wrapper.eq(TDResume::getResumeEmail, userRegisterDTO.getResumeEmail());
        if (!Objects.isNull(resumeMapper.selectOne(Wrapper))) {
            throw new SystemException(AppHttpCodeEnum.EMAIL_EXIST);
        }

        TDUser user = new TDUser();
        user.setUserRegisterTime(Date.from(Instant.now()));
        userRegisterDTO.setUserPassword(passwordEncoder.encode(userRegisterDTO.getUserPassword()));
        BeanUtils.copyProperties(userRegisterDTO, user);
        if (Common.STUDENT_USER.equals(userRegisterDTO.getUserRole())) {
            TDResume userResume = new TDResume();
            BeanUtils.copyProperties(userRegisterDTO, userResume);
            // 插入在线简历
            resumeMapper.insert(userResume);
            user.setResumeId(userResume.getResumeId());
            // 插入用户
            tdUserMapper.insert(user);
            return null;
        }
        // 插入用户
        tdUserMapper.insert(user);
        return null;
    }

    @Override
    public Page<UserVO> selectUserList(Integer page, Integer size, String userName, String userStatus) {
        LambdaUpdateWrapper<TDUser> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(TDUser::getDelFlag, Common.NOT_DELETE)
                .like(userName != null, TDUser::getUserName, userName)
                .eq(userStatus != null, TDUser::getUserStatus, userStatus);
        Page<TDUser> tdUserPage = tdUserMapper.selectPage(new Page<>(page, size), wrapper);
        Page<UserVO> userVOPage = new Page<>(tdUserPage.getCurrent(), tdUserPage.getSize(), tdUserPage.getTotal());

        userVOPage.setRecords(tdUserPage.getRecords().stream().map(user -> {
            UserVO userVO = new UserVO();
            BeanUtils.copyProperties(user, userVO);
            return userVO;
        }).collect(Collectors.toList()));

        return userVOPage;

    }

    @Override
    public Object updateUserInfo(UserDto userDto) {

        TDUser tdUser = new TDUser();
        BeanUtils.copyProperties(userDto, tdUser);
        tdUser.setUserPassword(passwordEncoder.encode(tdUser.getUserPassword()));
        if (!update(tdUser, new LambdaUpdateWrapper<TDUser>().eq(TDUser::getUserId,
                userDto.getUserId()).eq(TDUser::getDelFlag, Common.NOT_DELETE))) {
            throw new SystemException(AppHttpCodeEnum.SYSTEM_ERROR);
        }

        return null;
    }

    @Override
    public Object deleteUser(Long userId) {
        LambdaUpdateWrapper<TDUser> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(TDUser::getUserId, userId)
                .set(TDUser::getDelFlag, Common.DELETE);
        if (!update(wrapper)) {
            throw new SystemException(AppHttpCodeEnum.SYSTEM_ERROR);
        }
        return null;
    }

    @Override
    public Object addUser(UserDto userDto) {
        LambdaQueryWrapper<TDUser> phoneQueryWrapper = new LambdaQueryWrapper<>();
        phoneQueryWrapper.eq(TDUser::getUserPhone, userDto.getUserPhone())
                .eq(TDUser::getDelFlag, Common.NOT_DELETE);
        if (count(phoneQueryWrapper) > 0) {
            throw new SystemException(AppHttpCodeEnum.PHONE_EXIST);
        }
        LambdaQueryWrapper<TDUser> emailQueryWrapper = new LambdaQueryWrapper<>();
        emailQueryWrapper.eq(TDUser::getUserEmail, userDto.getUserEmail())
                .eq(TDUser::getDelFlag, Common.NOT_DELETE);
        if (count(emailQueryWrapper) > 0) {
            throw new SystemException(AppHttpCodeEnum.EMAIL_EXIST);
        }
        TDUser tdUser = new TDUser();
        BeanUtils.copyProperties(userDto, tdUser);
        tdUser.setUserRegisterTime(new Date());
        tdUser.setUserPassword(passwordEncoder.encode(tdUser.getUserPassword()));
        if (!save(tdUser)) {
            throw new SystemException(AppHttpCodeEnum.SYSTEM_ERROR);
        }
        return tdUser.getUserId().toString();
    }

    @Override
    public Object selectUserCompanyList(Integer page, Integer size, String userName) {
        Long companyInfoId = SecurityUtils.getLoginUser().getTdUser().getCompanyInfoId();
        LambdaUpdateWrapper<TDUser> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(TDUser::getCompanyInfoId, companyInfoId)
                .like(StringUtils.hasText(userName), TDUser::getUserName, userName)
                .eq(TDUser::getDelFlag, Common.NOT_DELETE);

        Page<TDUser> tdUserPage = tdUserMapper.selectPage(new Page<>(page, size), wrapper);
        if (tdUserPage.getRecords().isEmpty()){
            return null;
        }
        Page<UserCompanyVO> userCompanyVOPage =
                new Page<>(tdUserPage.getCurrent(), tdUserPage.getSize(), tdUserPage.getTotal());
        List<Long> roleIds = tdUserPage.getRecords().stream().map(TDUser::getUserRole).map(Long::valueOf).toList();
        Map<Long, String> tpRoles = tpRoleMapper.selectByIds(roleIds).stream().collect(Collectors.toMap(TPRole::getRoleId, TPRole::getRoleName));
        userCompanyVOPage.setRecords(tdUserPage.getRecords().stream().map(user -> {
            UserCompanyVO userCompanyVO = new UserCompanyVO();
            BeanUtils.copyProperties(user, userCompanyVO);
            userCompanyVO.setUserRole(tpRoles.get(Long.valueOf(user.getUserRole())));
            userCompanyVO.setUserAvatar(Common.getImgURL() + user.getUserAvatar());
            return userCompanyVO;
        }).toList());
        return userCompanyVOPage;
    }

    @Override
    public Object updateUserCompany(UserDto userDto, MultipartFile userAvatar) throws IOException {
        if (userDto.getUserId() == null) {
            userDto.setUserId(SecurityUtils.getLoginUser().getTdUser().getUserId());
        }
        String userEmail = userDto.getUserEmail();
        String userPhone = userDto.getUserPhone();
        if (StringUtils.hasText(userPhone)) {
            LambdaQueryWrapper<TDUser> phoneQueryWrapper = new LambdaQueryWrapper<>();
            phoneQueryWrapper.eq(TDUser::getUserPhone, userDto.getUserPhone())
                    .eq(TDUser::getDelFlag, Common.NOT_DELETE);
            if (count(phoneQueryWrapper) > 0) {
                throw new SystemException(AppHttpCodeEnum.PHONE_EXIST);
            }
        }
        if (StringUtils.hasText(userEmail)) {
            LambdaQueryWrapper<TDUser> emailQueryWrapper = new LambdaQueryWrapper<>();
            emailQueryWrapper.eq(TDUser::getUserEmail, userDto.getUserEmail())
                    .eq(TDUser::getDelFlag, Common.NOT_DELETE);
            if (count(emailQueryWrapper) > 0) {
                throw new SystemException(AppHttpCodeEnum.EMAIL_EXIST);
            }
        }

        TDUser tdUser = new TDUser();
        BeanUtils.copyProperties(userDto, tdUser);
        LambdaUpdateWrapper<TDUser> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(TDUser::getUserId, tdUser.getUserId());
        if (userDto.getUserPassword() != null) {
            tdUser.setUserPassword(passwordEncoder.encode(tdUser.getUserPassword()));
        }
        if (userAvatar != null) {
            String fileName = userAvatar.getOriginalFilename();
            long currentTimeMillis = System.currentTimeMillis();
            if (!SecurityUtils.isAllowedFileType(SecurityUtils.getFileExtension(fileName))) {
                throw new SystemException(AppHttpCodeEnum.FILE_TYPE_ERROR);
            }
            File uploadDir = new File(Common.uploadPath);
            File destFile = new File(uploadDir, currentTimeMillis + "_" + fileName);
            if (!destFile.getParentFile().exists()) {
                destFile.getParentFile().mkdirs();
            }
            userAvatar.transferTo(destFile.getAbsoluteFile());
            tdUser.setUserAvatar("/avatar_files/" + currentTimeMillis + "_" + fileName);
            // 删除旧的图片
            String oldCompanyLogoURL = getById(tdUser.getUserId()).getUserAvatar();
            int lastIndex = oldCompanyLogoURL.lastIndexOf('/');
            String file = oldCompanyLogoURL.substring(lastIndex + 1);
            File oldFile = new File(Common.uploadPath + file);
            if (oldFile.exists()) {
                oldFile.delete();
            }
        }

        tdUserMapper.update(tdUser, wrapper);
        return null;
    }

    @Override
    public Object addUserCompany(UserDto userDto, MultipartFile userAvatar) throws IOException {
        String userEmail = userDto.getUserEmail();
        String userPhone = userDto.getUserPhone();
        if (StringUtils.hasText(userPhone)) {
            LambdaQueryWrapper<TDUser> phoneQueryWrapper = new LambdaQueryWrapper<>();
            phoneQueryWrapper.eq(TDUser::getUserPhone, userDto.getUserPhone())
                    .eq(TDUser::getDelFlag, Common.NOT_DELETE);
            if (count(phoneQueryWrapper) > 0) {
                throw new SystemException(AppHttpCodeEnum.PHONE_EXIST);
            }
        }
        if (StringUtils.hasText(userEmail)) {
            LambdaQueryWrapper<TDUser> emailQueryWrapper = new LambdaQueryWrapper<>();
            emailQueryWrapper.eq(TDUser::getUserEmail, userDto.getUserEmail())
                    .eq(TDUser::getDelFlag, Common.NOT_DELETE);
            if (count(emailQueryWrapper) > 0) {
                throw new SystemException(AppHttpCodeEnum.EMAIL_EXIST);
            }
        }
        if (StringUtils.hasText(userDto.getUserPassword())) {
            userDto.setUserPassword(passwordEncoder.encode(userDto.getUserPassword()));
        }

        TDUser tdUser = new TDUser();
        BeanUtils.copyProperties(userDto, tdUser);
        if (userAvatar != null) {
            String fileName = userAvatar.getOriginalFilename();
            long currentTimeMillis = System.currentTimeMillis();
            if (!SecurityUtils.isAllowedFileType(SecurityUtils.getFileExtension(fileName))) {
                throw new SystemException(AppHttpCodeEnum.FILE_TYPE_ERROR);
            }
            File uploadDir = new File(Common.uploadPath);
            File destFile = new File(uploadDir, currentTimeMillis + "_" + fileName);
            if (!destFile.getParentFile().exists()) {
                destFile.getParentFile().mkdirs();
            }
            userAvatar.transferTo(destFile.getAbsoluteFile());
            tdUser.setUserAvatar("/avatar_files/" + currentTimeMillis + "_" + fileName);
        }
        tdUser.setCompanyInfoId(SecurityUtils.getLoginUser().getTdUser().getCompanyInfoId());
        tdUser.setUserRegisterTime(new Date());

        save(tdUser);
        return null;
    }


    @Override
    public CommonResp<String> userUploadAvatar(Long userId, MultipartFile image) {
        // 查询用户是否存在头像
        TDUser user = userMapper.selectById(userId);
        // 判断非法用户
        if (user == null) {
            return CommonResp.fail(AppHttpCodeEnum.USER_NOT_EXIST, null);
        }
        if (Common.DELETE.equals(user.getDelFlag()) && Common.STATUS_DISABLE.equals(user.getUserStatus())) {
            return CommonResp.fail(AppHttpCodeEnum.SYSTEM_ERROR, null);
        }
        // 有 -> 删除
        if (user.getUserAvatar() != null && !user.getUserAvatar().trim().isEmpty()) {
            String avatar = Common.getLastPath(user.getUserAvatar(), "/", "");
            File oldFile = new File(uploadPathAvatar + avatar);
            if (!(oldFile.exists() && oldFile.delete())) {
                return CommonResp.fail(AppHttpCodeEnum.AVATAR_DELETE_ERROR, null);
            }
        }
        String path = saveAvatar(uploadPathAvatar, "/", image);
        if (path == null) {
            return CommonResp.fail(AppHttpCodeEnum.AVATAR_UPLOAD_ERROR, null);
        }
        user.setUserAvatar(path);
        userMapper.updateById(user);
        return CommonResp.success(Common.getImgURL() + path);
    }


    @Override
    public CommonResp<String> adminUploadAvatar(Long adminId, MultipartFile image) {
        // 查询用户是否存在头像
        TDAdmin admin = adminMapper.selectById(adminId);
        // 判断非法用户
        if (admin == null) {
            return CommonResp.fail(AppHttpCodeEnum.USER_NOT_EXIST, null);
        }
        if (Common.STATUS_DISABLE.equals(admin.getAdminStatus())) {
            return CommonResp.fail(AppHttpCodeEnum.SYSTEM_ERROR, null);
        }
        // 有 -> 删除
        if (admin.getAdminAvatar() != null && !admin.getAdminAvatar().trim().isEmpty()) {
            String avatar = Common.getLastPath(admin.getAdminAvatar(), "/", "");
            File oldFile = new File(uploadPathAvatar + avatar);
            if (!(oldFile.exists() && oldFile.delete())) {
                return CommonResp.fail(AppHttpCodeEnum.AVATAR_DELETE_ERROR, null);
            }
        }
        String path = saveAvatar(uploadPathAvatar, "/", image);
        if (path == null) {
            return CommonResp.fail(AppHttpCodeEnum.AVATAR_UPLOAD_ERROR, null);
        }
        admin.setAdminAvatar(path);
        adminMapper.updateById(admin);
        return CommonResp.success(Common.getImgURL() + path);
    }


    /**
     * 保存头像图片
     *
     * @param savePath  文件保存路径
     * @param separator 路径分隔符
     * @param image     图片文件
     * @return 保存后的文件相对路径
     */
    private String saveAvatar(String savePath, String separator, MultipartFile image) {
        String path;
        // 保存头像图片
        String fileName = image.getOriginalFilename();
        String newFileName = System.currentTimeMillis() + "_" + fileName;
        File file = new File(savePath + newFileName);
        try {
            image.transferTo(file.getAbsoluteFile());
            if (!file.exists()) {
                return null;
            }
            path = separator + Common.getLastPath(savePath, separator, separator + newFileName);
        } catch (Exception e) {
            return null;
        }
        return path;
    }
}



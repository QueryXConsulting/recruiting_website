package com.queryx.recruiting_website.Aspect;

import com.queryx.recruiting_website.domain.LoginAdmin;
import com.queryx.recruiting_website.domain.OperateLog;
import com.queryx.recruiting_website.service.IOperateLogService;
import com.queryx.recruiting_website.utils.CommonResp;
import com.queryx.recruiting_website.utils.SecurityUtils;

import jakarta.annotation.Resource;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;

import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Component;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.util.Arrays;

@Aspect
@Component
public class OperateLogAspect {

    @Resource
    private IOperateLogService operateLogService;

    @Pointcut(
            "execution(* com.queryx.recruiting_website.controller.AdminController.*(..)) ||"
                    + "execution(* com.queryx.recruiting_website.controller.CompanyManageController.*(..)) ||"
                    + "execution(* com.queryx.recruiting_website.controller.MenuMangeController.*(..)) ||"
                    + "execution(* com.queryx.recruiting_website.controller.PositionManageController.*(..)) ||"
                    + "execution(* com.queryx.recruiting_website.controller.ResumeManageController.*(..)) ||"
                    + "execution(* com.queryx.recruiting_website.controller.RoleManageController.*(..)) ||"
                    + "execution(* com.queryx.recruiting_website.controller.UserManageController.*(..))"
    )
    public void controllerPointcut() {
    }


    @AfterReturning(pointcut = "controllerPointcut()", returning = "returnValue")
    public void doAfterReturning(JoinPoint joinPoint, Object returnValue) {
        // 获取方法签名
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        String operateUser;
        if (SecurityUtils.getAuthentication() instanceof LoginAdmin loginAdmin) {
            operateUser = String.valueOf(loginAdmin);
        } else {
            operateUser = "公司用户登录: " + SecurityUtils.getLoginUser().getTdUser().getUserId();
        }

        String methodName = method.getName();

        Object[] args = joinPoint.getArgs();
        String params = Arrays.toString(args);

        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        String requestUrl = requestAttributes.getRequest().getRequestURI();

        OperateLog operateLog = new OperateLog();
        operateLog.setOperateUser(operateUser);
        operateLog.setOperateName("执行方法: " + methodName);
        operateLog.setMethodName(requestUrl);
        operateLog.setParams(params);
        // 处理返回值
        CommonResp<?> commonResp = (CommonResp<?>) returnValue;
        operateLog.setReturnValue("Code: " + commonResp.getCode() + ",message:"
                + commonResp.getMessage());

        operateLogService.save(operateLog);
    }
}

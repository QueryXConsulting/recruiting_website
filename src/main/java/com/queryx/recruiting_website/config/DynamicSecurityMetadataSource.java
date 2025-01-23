package com.queryx.recruiting_website.config;

import com.queryx.recruiting_website.domain.TPMenu;
import com.queryx.recruiting_website.service.TPMenuService;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class DynamicSecurityMetadataSource implements FilterInvocationSecurityMetadataSource {

    @Resource
    private TPMenuService menuService;

    private Map<String, Collection<ConfigAttribute>> configAttributeMap;

    @PostConstruct
    public void loadDataSource() {
        configAttributeMap = new HashMap<>();
        List<TPMenu> menus = menuService.list();
        configAttributeMap = menus.stream()
                .filter(menu -> StringUtils.hasText(menu.getPath()) && StringUtils.hasText(menu.getPerms()))
                .collect(Collectors.toMap(
                        TPMenu::getPath,
                        menu -> {
                            List<ConfigAttribute> attributes = new ArrayList<>();
                            attributes.add(new SecurityConfig(menu.getPerms()));
                            return attributes;
                        }
                ));
    }

    @Override
    public Collection<ConfigAttribute> getAttributes(Object object) throws IllegalArgumentException {
        String requestUrl = ((FilterInvocation) object).getRequestUrl();
        if (requestUrl.contains("?")) {
            requestUrl = requestUrl.substring(0, requestUrl.indexOf("?"));
        }

        for (Map.Entry<String, Collection<ConfigAttribute>> entry : configAttributeMap.entrySet()) {
            String pattern = entry.getKey();
            if (new AntPathMatcher().match(pattern, requestUrl)) {
                return entry.getValue();
            }
        }

        return null;
    }

    @Override
    public Collection<ConfigAttribute> getAllConfigAttributes() {
        Set<ConfigAttribute> allAttributes = new HashSet<>();
        configAttributeMap.values().forEach(allAttributes::addAll);
        return allAttributes;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return FilterInvocation.class.isAssignableFrom(clazz);
    }

}

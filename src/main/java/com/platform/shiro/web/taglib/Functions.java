package com.platform.shiro.web.taglib;

import com.platform.shiro.entity.App;
import com.platform.shiro.entity.Organization;
import com.platform.shiro.entity.Resource;
import com.platform.shiro.entity.Role;
import com.platform.shiro.entity.User;
import com.platform.shiro.service.AppService;
import com.platform.shiro.service.OrganizationService;
import com.platform.shiro.service.ResourceService;
import com.platform.shiro.service.RoleService;
import com.platform.shiro.service.UserService;
import com.platform.shiro.spring.SpringUtils;

import org.springframework.util.CollectionUtils;

import java.util.Collection;

/**
 * 页面跟你据对应的id信息,显示对应的中文名称信息标签
 * 
 * @Author  LiuBao
 * @Version 2.0
 * @Date 2016年9月1日
 *
 */
public class Functions {

    public static boolean in(Iterable iterable, Object element) {
        if(iterable == null) {
            return false;
        }
        return CollectionUtils.contains(iterable.iterator(), element);
    }

    public static String username(Long userId) {
        User user = getUserService().findOne(userId);
        if(user == null) {
            return "";
        }
        return user.getUsername();
    }
    public static String appName(Long appId) {
        App app = getAppService().findOne(appId);
        if(app == null) {
            return "";
        }
        return app.getName();
    }

    public static String organizationName(Long organizationId) {
        Organization organization = getOrganizationService().findOne(organizationId);
        if(organization == null) {
            return "";
        }
        return organization.getOrgName();
    }

    public static String organizationNames(Collection<Long> organizationIds) {
        if(CollectionUtils.isEmpty(organizationIds)) {
            return "";
        }

        StringBuilder s = new StringBuilder();
        for(Long organizationId : organizationIds) {
            Organization organization = getOrganizationService().findOne(organizationId);
            if(organization == null) {
                return "";
            }
            s.append(organization.getOrgName());
            s.append(",");
        }

        if(s.length() > 0) {
            s.deleteCharAt(s.length() - 1);
        }

        return s.toString();
    }
    public static String roleName(Long roleId) {
        Role role = getRoleService().findOne(roleId);
        if(role == null) {
            return "";
        }
        return role.getDescription();
    }

    public static String roleNames(Collection<Long> roleIds) {
        if(CollectionUtils.isEmpty(roleIds)) {
            return "";
        }

        StringBuilder s = new StringBuilder();
        for(Long roleId : roleIds) {
            Role role = getRoleService().findOne(roleId);
            if(role == null) {
                return "";
            }
            s.append(role.getDescription());
            s.append(",");
        }

        if(s.length() > 0) {
            s.deleteCharAt(s.length() - 1);
        }

        return s.toString();
    }
    public static String resourceName(Long resourceId) {
        Resource resource = getResourceService().findOne(resourceId);
        if(resource == null) {
            return "";
        }
        return resource.getName();
    }
    public static String resourceNames(Collection<Long> resourceIds) {
        if(CollectionUtils.isEmpty(resourceIds)) {
            return "";
        }

        StringBuilder s = new StringBuilder();
        for(Long resourceId : resourceIds) {
            Resource resource = getResourceService().findOne(resourceId);
            if(resource == null) {
                return "";
            }
            s.append(resource.getName());
            s.append(",");
        }

        if(s.length() > 0) {
            s.deleteCharAt(s.length() - 1);
        }

        return s.toString();
    }

    private static OrganizationService organizationService;
    private static RoleService roleService;
    private static ResourceService resourceService;
    private static UserService userService;
    private static AppService appService;

    public static UserService getUserService() {
        if(userService == null) {
            userService = SpringUtils.getBean(UserService.class);
        }
        return userService;
    }

    public static AppService getAppService() {
        if(appService == null) {
            appService = SpringUtils.getBean(AppService.class);
        }
        return appService;
    }

    public static OrganizationService getOrganizationService() {
        if(organizationService == null) {
            organizationService = SpringUtils.getBean(OrganizationService.class);
        }
        return organizationService;
    }

    public static RoleService getRoleService() {
        if(roleService == null) {
            roleService = SpringUtils.getBean(RoleService.class);
        }
        return roleService;
    }

    public static ResourceService getResourceService() {
        if(resourceService == null) {
            resourceService = SpringUtils.getBean(ResourceService.class);
        }
        return resourceService;
    }
}


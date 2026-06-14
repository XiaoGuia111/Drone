package com.uav.system.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 页面路由控制器
 *
 * <p>负责 Thymeleaf 视图的页面导航，所有页面路径映射在此统一管理。</p>
 */
@Controller
public class PageController {

    /**
     * 登录页面
     */
    @GetMapping("/login")
    public String login() {
        return "login";
    }

    /**
     * 首页/UAV 列表页面
     */
    @GetMapping({"/", "/index", "/uav/list"})
    public String index() {
        return "uav/list";
    }

    /**
     * 新增无人机页面
     */
    @GetMapping("/uav/create")
    public String create() {
        return "uav/form";
    }

    /**
     * 编辑无人机页面
     */
    @GetMapping("/uav/edit")
    public String edit() {
        return "uav/form";
    }
}

package me.ohtaeg.securitystudy.controller;

import java.security.Principal;

import me.ohtaeg.securitystudy.service.HelloService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class HelloController {

    private HelloService helloService;

    public HelloController(final HelloService helloService) {
        this.helloService = helloService;
    }

    @GetMapping("/hello")
    public String hello(Model model, Principal principal) {
        if (principal == null) {
            model.addAttribute("message", "Hello~");
        } else {
            model.addAttribute("message", "Hello~ " + principal.getName());
        }

        return "index";
    }

    @GetMapping("/info")
    public String info(Model model) {
        model.addAttribute("message", "Hello~ info");
        return "info";
    }

    // 로그인한 사용자만 접근이 가능해야하기에 인증된 유저인 Principal을 받는다.
    @GetMapping("/dashboard")
    public String dashboard(Model model, Principal principal) {
        helloService.dashboard();
        model.addAttribute("message", "Hello~ " + principal.getName());
        return "dashboard";
    }

    @GetMapping("/admin")
    public String admin(Model model, Principal principal) {
        model.addAttribute("message", "Hello~ Admin! " + principal.getName());
        return "admin";
    }
}

package com.example.carportal.controllers;

import com.example.carportal.repositories.UserRepository;
import com.example.carportal.services.DamageService;
import com.example.carportal.services.SessionService;
import com.example.carportal.services.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.context.request.WebRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Controller
public class HomeController
{

    private DamageService ds = new DamageService();
    private UserService us = new UserService(new UserRepository());
    private SessionService ss = new SessionService();

    @GetMapping("/")
    public String landPage(){
        return "database";
    }

    @GetMapping("/selectDb")
    public String chooseDb() {
        return "database";
    }

    @PostMapping("/selectDb")
    public String chosenDb(WebRequest webrequest, HttpSession session, HttpServletRequest request) {
        int dbSource = Integer.parseInt(webrequest.getParameter("source"));
        session.setAttribute("source", dbSource);
        ss.setDbSource(dbSource);
        us.addUserToSession(request);
        return "redirect:/index";
    }

    @GetMapping("/mainpage")
    public String index(HttpSession session, HttpServletRequest request, Model model){
        ds.addListOfDamagesToSession(request);
        model.addAttribute("sessionUser", us.getUserFromSession(session));
        return "mainpage";
    }
}

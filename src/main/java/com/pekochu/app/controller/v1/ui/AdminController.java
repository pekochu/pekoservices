package com.pekochu.app.controller.v1.ui;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class AdminController {

    @RequestMapping(value = {"/", "/home"})
    public ModelAndView home() {
        ModelAndView view = new ModelAndView("index");
        view.addObject("title", "PekoServices");
        return view;
    }

}

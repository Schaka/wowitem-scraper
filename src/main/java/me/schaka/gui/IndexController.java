package me.schaka.gui;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by Jonas on 09.08.2016.
 */
@Controller
@RequestMapping("")
public class IndexController {

    @GetMapping("")
    public String showIndex(){
        return "views/item-search";
    }
}

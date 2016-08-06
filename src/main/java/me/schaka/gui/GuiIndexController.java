package me.schaka.gui;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class GuiIndexController {

	@RequestMapping
	public String showIndex() {
		return "views/job-search";
	}
}

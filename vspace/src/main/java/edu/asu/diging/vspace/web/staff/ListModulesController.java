package edu.asu.diging.vspace.web.staff;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import edu.asu.diging.vspace.core.data.ModuleRepository;

@Controller
public class ListModulesController {

	@Autowired
	private ModuleRepository moduleRepo;
	
	@RequestMapping("/staff/module/list")
	public String listSpaces(Model model) {
		
		model.addAttribute("vspacMmodules", moduleRepo.findAll());
		
		
		return "staff/modules/modulelist";
	}
}

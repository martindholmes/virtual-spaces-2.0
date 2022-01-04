package edu.asu.diging.vspace.web.publicview;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import edu.asu.diging.vspace.core.model.IModule;
import edu.asu.diging.vspace.core.model.ISpace;
import edu.asu.diging.vspace.core.model.impl.Exhibition;
import edu.asu.diging.vspace.core.services.IExhibitionManager;
import edu.asu.diging.vspace.core.services.IModuleManager;
import edu.asu.diging.vspace.core.services.ISpaceManager;
import edu.asu.diging.vspace.web.exception.ModuleNotFoundException;
import edu.asu.diging.vspace.web.exception.SpaceNotFoundException;

@Controller
public class ExhibitionModuleController {

    @Autowired
    private IModuleManager moduleManager;

    @Autowired
    private ISpaceManager spaceManager;

    @RequestMapping(value = { "/exhibit/{spaceId}/module/{id}", "/preview/{previewId}/{spaceId}/module/{id}" })
    public String module(@PathVariable("id") String id, @PathVariable("spaceId") String spaceId,
            @PathVariable(name = "previewId", required = false) String previewId, Model model)
            throws SpaceNotFoundException, ModuleNotFoundException {

        if (previewId != null) {
            model.addAttribute("isExhPreview", true);
            model.addAttribute("previewId", previewId);
        }
        ISpace space = spaceManager.getSpace(spaceId);
        if (space == null) {
            return "redirect:/exhibit/404";
        }
        IModule module = moduleManager.getModule(id);
        model.addAttribute("module", module);
        if (module == null) {
            return "redirect:/exhibit/404";
        } else if (module.getStartSequence() == null) {
            model.addAttribute("showAlert", true);
            model.addAttribute("message", "Sorry, module has not been configured yet.");
            return "/exhibition/module";
        }
        String startSequenceID = module.getStartSequence().getId();
        if (previewId != null) {
            return "redirect:/preview/" + previewId + "/" + spaceId + "/module/" + id + "/sequence/" + startSequenceID;
        } else {
            return "redirect:/exhibit/" + spaceId + "/module/" + id + "/sequence/" + startSequenceID;
        }
    }
}

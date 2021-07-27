package edu.asu.diging.vspace.web.general.search;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import edu.asu.diging.vspace.core.model.impl.ModuleLink;
import edu.asu.diging.vspace.core.model.impl.Slide;
import edu.asu.diging.vspace.core.model.impl.SlideWithSpace;
import edu.asu.diging.vspace.core.model.impl.StaffSearch;
import edu.asu.diging.vspace.core.services.IModuleLinkManager;
import edu.asu.diging.vspace.core.services.IStaffSearchManager;

@Controller
public class PublicSearchSlideController {
    
    private final Logger logger = LoggerFactory.getLogger(getClass());
    
    @Autowired
    private IStaffSearchManager staffSearchManager;
    
    @Autowired
    private IModuleLinkManager moduleLinkManager;

    @RequestMapping(value = "/exhibit/search/slide")
    public ResponseEntity<StaffSearch> searchInVspace(
            @RequestParam(value = "slidePagenum", required = false, defaultValue = "1") String slidePagenum,
            Model model, @RequestParam(name = "searchText") String searchTerm) {

        HashSet<Slide> slideSet = paginationForSlide(slidePagenum, searchTerm);
        StaffSearch staffSearch = new StaffSearch();
        staffSearch.setSlideSet(slideSet);
        
        Map<String, String> slideFirstImage = new HashMap<>();
        
        for (Slide slide : slideSet) {
            
            String slideFirstImageId = null;
            
            if (slide != null && slide.getFirstImageBlock() != null) {
                slideFirstImageId = slide.getFirstImageBlock().getImage().getId();
            }
            slideFirstImage.put(slide.getId(), slideFirstImageId);
            staffSearch.setSlideFirstImage(slideFirstImage);
        }
        return new ResponseEntity<StaffSearch>(staffSearch, HttpStatus.OK);
    }

    /**
     * This method is used to search the searched string specified in the input
     * parameter(searchTerm) in slide table and return the slides corresponding to
     * the page number specified in the input parameter(spacePagenum) whose name or
     * description contains the search string.
     * 
     * @param slidePagenum current page number sent as request parameter in the URL.
     * @param searchTerm   This is the search string which is being searched.
     */
    private HashSet<Slide> paginationForSlide(String slidePagenum, String searchTerm) {
        Page<Slide> slidePage = staffSearchManager.searchInSlides(searchTerm, Integer.parseInt(slidePagenum));
        HashSet<Slide> slideSet = new LinkedHashSet<>();
        
        for(Slide slide : slidePage.getContent()) {
            ModuleLink moduleLink = moduleLinkManager.findFirstByModule(slide.getModule());
            if(moduleLink!=null) {
                SlideWithSpace slideWithSpace = new SlideWithSpace();
                try {
                    BeanUtils.copyProperties(slideWithSpace, slide);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    logger.error("Could not create moduleWithSpace.", e);
                }
                slideWithSpace.setSpaceId(moduleLink.getSpace().getId());
                slideSet.add(slideWithSpace);
            }
        }
        return slideSet;
    }
}

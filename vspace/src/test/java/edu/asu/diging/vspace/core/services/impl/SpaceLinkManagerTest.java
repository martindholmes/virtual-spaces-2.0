package edu.asu.diging.vspace.core.services.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;

import edu.asu.diging.vspace.core.data.ImageRepository;
import edu.asu.diging.vspace.core.data.SpaceLinkRepository;
import edu.asu.diging.vspace.core.data.display.SpaceLinkDisplayRepository;
import edu.asu.diging.vspace.core.exception.FileStorageException;
import edu.asu.diging.vspace.core.exception.ImageCouldNotBeStoredException;
import edu.asu.diging.vspace.core.exception.ImageDoesNotExistException;
import edu.asu.diging.vspace.core.exception.LinkDoesNotExistsException;
import edu.asu.diging.vspace.core.exception.SpaceDoesNotExistException;
import edu.asu.diging.vspace.core.factory.IImageFactory;
import edu.asu.diging.vspace.core.factory.ISpaceLinkFactory;
import edu.asu.diging.vspace.core.factory.impl.SpaceLinkDisplayFactory;
import edu.asu.diging.vspace.core.file.IStorageEngine;
import edu.asu.diging.vspace.core.model.ISpace;
import edu.asu.diging.vspace.core.model.ISpaceLink;
import edu.asu.diging.vspace.core.model.IVSImage;
import edu.asu.diging.vspace.core.model.display.DisplayType;
import edu.asu.diging.vspace.core.model.display.ISpaceDisplay;
import edu.asu.diging.vspace.core.model.display.ISpaceLinkDisplay;
import edu.asu.diging.vspace.core.model.display.impl.SpaceDisplay;
import edu.asu.diging.vspace.core.model.display.impl.SpaceLinkDisplay;
import edu.asu.diging.vspace.core.model.impl.Space;
import edu.asu.diging.vspace.core.model.impl.SpaceLink;
import edu.asu.diging.vspace.core.model.impl.VSImage;
import edu.asu.diging.vspace.core.services.IImageService;

public class SpaceLinkManagerTest {
    @Mock
    private SpaceLinkRepository spaceLinkRepo;

    @Mock
    private SpaceLinkDisplayRepository spaceLinkDisplayRepo;

    @Mock
    private SpaceManager spaceManager;

    @Mock
    private SpaceDisplayManager spaceDisplayManager;

    @Mock
    private ISpaceLinkFactory spaceLinkFactory;

    @Mock
    private SpaceLinkDisplayFactory spaceLinkDisplayFactory;
    
    @Mock
    private IImageFactory imageFactory;
    
    @Mock
    private IImageService imageService;
    
    @Mock
    private ImageRepository imageRepo;
    
    @Mock
    private IStorageEngine storage;

    @InjectMocks
    private SpaceLinkManager managerToTest = new SpaceLinkManager();

    private String spaceId1, spaceId2, spaceId3, imageId, imageId2, imageFileName, spcDisplayLinkId;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        spaceId1 = "SPA000000001";
        spaceId2 = "SPA000000002";
        spaceId3 = "SPA000000003";
        imageId = "IMG00000001";
        imageId2 = "IMG00000002";
        imageFileName = "Space Image 1";
        spcDisplayLinkId = "SPLD001";
    }

    @Test
    public void test_createLink_success() throws SpaceDoesNotExistException, ImageCouldNotBeStoredException, ImageDoesNotExistException, FileStorageException {

        ISpace space = new Space();
        space.setId(spaceId1);
        IVSImage spaceImage = new VSImage();
        spaceImage.setHeight(700);
        spaceImage.setWidth(1300);
        space.setImage(spaceImage);
        Mockito.when(spaceManager.getSpace(spaceId1)).thenReturn(space);
        ISpaceLink spaceLink = new SpaceLink();
        spaceLink.setId("SPL001");
        Mockito.when(spaceLinkRepo.save((SpaceLink)spaceLink)).thenReturn((SpaceLink) spaceLink);

        Mockito.when(spaceManager.getSpace(spaceId1)).thenReturn(space);
        Mockito.when(spaceLinkFactory.createSpaceLink("New Space Link", space)).thenReturn(spaceLink);

        ISpace target = new Space();
        target.setId(spaceId2);
        Mockito.when(spaceManager.getSpace(spaceId2)).thenReturn(target);

        spaceLink.setTargetSpace(target);
        ISpaceLinkDisplay spaceDisplayLink = new SpaceLinkDisplay();
        spaceDisplayLink.setId("SPLD001");
        spaceDisplayLink.setPositionX(10);
        spaceDisplayLink.setPositionY(30);
        spaceDisplayLink.setRotation(40);
        spaceDisplayLink.setType(DisplayType.ARROW);
        Mockito.when(spaceLinkFactory.createSpaceLink("New Space Link", space)).thenReturn(spaceLink);
        spaceDisplayLink.setLink(spaceLink);

        ISpaceDisplay displayAttributes = new SpaceDisplay();
        displayAttributes.setHeight(700);
        displayAttributes.setWidth(1300);

        Mockito.when(spaceDisplayManager.getBySpace(space)).thenReturn(displayAttributes);

        Mockito.when(spaceLinkDisplayFactory.createSpaceLinkDisplay(spaceLink)).thenReturn(spaceDisplayLink);

        Mockito.when(spaceLinkRepo.save((SpaceLink) spaceLink)).thenReturn((SpaceLink)spaceLink);
        Mockito.when(spaceLinkDisplayRepo.save((SpaceLinkDisplay)spaceDisplayLink)).thenReturn((SpaceLinkDisplay)spaceDisplayLink);

        ISpaceLinkDisplay savedSpaceLinkDisplay = managerToTest.createLink("New Space Link", spaceId1, 10, 30, 40, spaceId2, "New Space Link", DisplayType.ARROW, null, null, null);
        Assert.assertEquals(spaceDisplayLink.getId(), savedSpaceLinkDisplay.getId());
        Assert.assertEquals(spaceDisplayLink.getName(), savedSpaceLinkDisplay.getName());
        Assert.assertEquals(new Double(spaceDisplayLink.getPositionX()), new Double(savedSpaceLinkDisplay.getPositionX()));
        Assert.assertEquals(new Double(spaceDisplayLink.getPositionY()), new Double(savedSpaceLinkDisplay.getPositionY()));
        Assert.assertEquals(spaceDisplayLink.getRotation(), savedSpaceLinkDisplay.getRotation());
        Assert.assertEquals(spaceDisplayLink.getLink().getId(), savedSpaceLinkDisplay.getLink().getId());
        Assert.assertEquals(spaceDisplayLink.getType(), savedSpaceLinkDisplay.getType());
        Assert.assertEquals(spaceDisplayLink.getLink().getTargetSpace(), savedSpaceLinkDisplay.getLink().getTargetSpace());
        Mockito.verify(spaceLinkDisplayRepo).save((SpaceLinkDisplay)spaceDisplayLink);
        
        //For spaceLink as Image 
        IVSImage spcImage = new VSImage();
        spcImage.setId(imageId);
        spcImage.setFilename(imageFileName);
        spcImage.setHeight(200);
        spcImage.setWidth(400);
        
        ISpaceLinkDisplay spaceDisplayLinkImage = new SpaceLinkDisplay();
        spaceDisplayLinkImage.setId(spcDisplayLinkId);
        spaceDisplayLinkImage.setPositionX(10);
        spaceDisplayLinkImage.setPositionY(30);
        spaceDisplayLinkImage.setRotation(40);
        spaceDisplayLinkImage.setType(DisplayType.IMAGE);
        spaceDisplayLinkImage.setImage(spcImage);
        Mockito.when(spaceLinkFactory.createSpaceLink("New Module Link", space)).thenReturn(spaceLink);
        spaceDisplayLinkImage.setLink(spaceLink);
        
        Mockito.when(spaceLinkDisplayFactory.createSpaceLinkDisplay(spaceLink)).thenReturn(spaceDisplayLinkImage);
        
        Mockito.when(imageFactory.createImage(Mockito.anyString(), Mockito.anyString())).thenReturn(spcImage);
        Mockito.when(imageRepo.save((VSImage) spcImage)).thenReturn((VSImage) spcImage);
        Mockito.when(storage.storeFile(new byte[20], imageFileName, spcImage.getId())).thenReturn("Dummy File Path");
        
        Mockito.when(spaceLinkRepo.save((SpaceLink) spaceLink)).thenReturn((SpaceLink)spaceLink);
        Mockito.when(spaceLinkDisplayRepo.save((SpaceLinkDisplay)spaceDisplayLinkImage)).thenReturn((SpaceLinkDisplay)spaceDisplayLinkImage);
        
        ISpaceLinkDisplay savedSpaceLinkDisplay2 = managerToTest.createLink("New Space Link", spaceId1, 10, 30, 40, spaceId2, "New Space Link", DisplayType.IMAGE, new byte[20], imageFileName, null);
        Assert.assertEquals(spaceDisplayLinkImage.getId(), savedSpaceLinkDisplay2.getId());
        Assert.assertEquals(spaceDisplayLinkImage.getName(), savedSpaceLinkDisplay2.getName());
        Assert.assertEquals(new Double(spaceDisplayLinkImage.getPositionX()), new Double(savedSpaceLinkDisplay2.getPositionX()));
        Assert.assertEquals(new Double(spaceDisplayLinkImage.getPositionY()), new Double(savedSpaceLinkDisplay2.getPositionY()));
        Assert.assertEquals(spaceDisplayLinkImage.getRotation(), savedSpaceLinkDisplay2.getRotation());
        Assert.assertEquals(spaceDisplayLinkImage.getLink().getId(), savedSpaceLinkDisplay2.getLink().getId());
        Assert.assertEquals(spaceDisplayLinkImage.getType(), savedSpaceLinkDisplay2.getType());
        Assert.assertEquals(spaceDisplayLinkImage.getLink().getTargetSpace(), savedSpaceLinkDisplay2.getLink().getTargetSpace());
        Assert.assertEquals(spaceDisplayLinkImage.getImage().getHeight(), savedSpaceLinkDisplay2.getImage().getHeight());
        Assert.assertEquals(spaceDisplayLinkImage.getImage().getWidth(), savedSpaceLinkDisplay2.getImage().getWidth());
        Assert.assertEquals(spaceDisplayLinkImage.getImage().getId(), savedSpaceLinkDisplay2.getImage().getId());
        Mockito.verify(spaceLinkDisplayRepo).save((SpaceLinkDisplay)spaceDisplayLinkImage);
    }

    @Test
    public void test_deleteLink_linkPresent() {
        ISpace space = new Space();
        space.setId("SPA001");
        SpaceLinkDisplay spaceLinkDisplay = new SpaceLinkDisplay();
        ISpaceLink spaceLink = new SpaceLink();
        spaceLink.setId("SPL001");
        spaceLink.setSourceSpace(space);

        List<ISpaceLink> spaceLinks = new ArrayList<ISpaceLink>();
        spaceLinks.add(spaceLink);

        space.setSpaceLinks(spaceLinks);
        ISpace target = new Space();
        target.setId(spaceId2);
        spaceLink.setTargetSpace(target);

        spaceLinkDisplay.setId("SPLD001");
        spaceLinkDisplay.setLink(spaceLink);

        spaceLinkDisplay.setName("TestSpace");
        spaceLinkDisplay.setPositionX(10);
        spaceLinkDisplay.setPositionY(30);
        spaceLinkDisplay.setPositionY(20);
        spaceLinkDisplay.setType(DisplayType.ARROW);

        Optional<SpaceLink> mockSpaceLink = Optional.of((SpaceLink)spaceLink);
        Mockito.when(spaceLinkRepo.findById(spaceLink.getId())).thenReturn(mockSpaceLink);

        Optional<SpaceLinkDisplay> mockSpaceLinkDisplay = Optional.of((SpaceLinkDisplay)spaceLinkDisplay);
        Mockito.when(spaceLinkDisplayRepo.findById(spaceLinkDisplay.getId())).thenReturn(mockSpaceLinkDisplay);

        managerToTest.deleteLink("SPL001");

        Mockito.verify(spaceLinkDisplayRepo).deleteByLink(spaceLink);
        Mockito.verify(spaceLinkRepo).delete((SpaceLink)spaceLink);
    }

    @Test
    public void test_updateLink_success() throws SpaceDoesNotExistException, LinkDoesNotExistsException, ImageCouldNotBeStoredException, ImageDoesNotExistException {
        ISpace space = new Space();
        space.setId(spaceId1);
        IVSImage spaceImage = new VSImage();
        spaceImage.setHeight(700);
        spaceImage.setWidth(1300);
        space.setImage(spaceImage);
        SpaceLinkDisplay spaceLinkDisplay = new SpaceLinkDisplay();
        ISpaceLink spaceLink = new SpaceLink();
        spaceLink.setId("SPL002");
        spaceLink.setSourceSpace(space);


        ISpace target = new Space();
        target.setId(spaceId2);

        Mockito.when(spaceManager.getSpace(spaceId2)).thenReturn(target);

        spaceLink.setTargetSpace(target);

        spaceLinkDisplay.setId("SPLD001");
        spaceLinkDisplay.setLink(spaceLink);

        spaceLinkDisplay.setName("TestSpace");
        spaceLinkDisplay.setPositionX(10);
        spaceLinkDisplay.setPositionY(30);
        spaceLinkDisplay.setPositionY(20);
        spaceLinkDisplay.setType(DisplayType.ARROW);

        Mockito.when(spaceManager.getSpace(spaceId1)).thenReturn(space);
        SpaceLink newSpaceLink = new SpaceLink();
        newSpaceLink.setId("spaceLink1");
        Optional<SpaceLink> mockSpaceLink = Optional.of((SpaceLink)spaceLink);
        Mockito.when(spaceLinkRepo.findById(spaceLink.getId())).thenReturn(mockSpaceLink);

        Optional<SpaceLinkDisplay> mockSpaceLinkDisplay = Optional.of((SpaceLinkDisplay)spaceLinkDisplay);
        Mockito.when(spaceLinkDisplayRepo.findById(spaceLinkDisplay.getId())).thenReturn(mockSpaceLinkDisplay);


        SpaceLinkDisplay spaceLinkDisplayUpdated = spaceLinkDisplay;
        spaceLinkDisplayUpdated.setName("TestSpaceEdited");
        spaceLinkDisplayUpdated.setPositionX(100);
        spaceLinkDisplayUpdated.setPositionY(300);
        spaceLinkDisplayUpdated.setRotation(180);
        spaceLinkDisplayUpdated.setType(DisplayType.ALERT);

        ISpace newTarget = new Space();
        newTarget.setId(spaceId3);

        Mockito.when(spaceManager.getSpace(spaceId3)).thenReturn(newTarget);

        spaceLinkDisplayUpdated.getLink().setTargetSpace(newTarget);

        ISpaceDisplay displayAttributes = new SpaceDisplay();
        displayAttributes.setHeight(700);
        displayAttributes.setWidth(1300);

        Mockito.when(spaceDisplayManager.getBySpace(space)).thenReturn(displayAttributes);

        Mockito.when(spaceLinkRepo.save((SpaceLink) spaceLink)).thenReturn((SpaceLink)spaceLink);
        Mockito.when(spaceLinkDisplayRepo.save((SpaceLinkDisplay)spaceLinkDisplay)).thenReturn((SpaceLinkDisplay)spaceLinkDisplayUpdated);

        ISpaceLinkDisplay actualUpdatedLink = managerToTest.updateLink("TestSpaceEdited", spaceId1, 100, 300, 180, spaceId3, "TestSpaceEdited", "SPL002", "SPLD001", DisplayType.ALERT, null, null, null);
        Assert.assertEquals(spaceLinkDisplayUpdated.getId(), actualUpdatedLink.getId());
        Assert.assertEquals(spaceLinkDisplayUpdated.getName(), actualUpdatedLink.getName());
        Assert.assertEquals(new Double(spaceLinkDisplayUpdated.getPositionX()), new Double(actualUpdatedLink.getPositionX()));
        Assert.assertEquals(new Double(spaceLinkDisplayUpdated.getPositionY()), new Double(actualUpdatedLink.getPositionY()));
        Assert.assertEquals(spaceLinkDisplayUpdated.getLink().getTargetSpace(), actualUpdatedLink.getLink().getTargetSpace());
        Assert.assertEquals(spaceLinkDisplayUpdated.getType(), actualUpdatedLink.getType());
        
        
        //For spaceLink updating with existing Image 
        IVSImage spcImage = new VSImage();
        spcImage.setId(imageId2);
        spcImage.setFilename(imageFileName);
        spcImage.setHeight(300);
        spcImage.setWidth(600);
        
        spaceLinkDisplayUpdated.setType(DisplayType.IMAGE);
        spaceLinkDisplayUpdated.setImage(spcImage);
        
        Mockito.when(spaceDisplayManager.getBySpace(space)).thenReturn(displayAttributes);
        Mockito.when(spaceLinkRepo.save((SpaceLink) spaceLink)).thenReturn((SpaceLink)spaceLink);
        Mockito.when(spaceLinkDisplayRepo.save((SpaceLinkDisplay)spaceLinkDisplay)).thenReturn((SpaceLinkDisplay)spaceLinkDisplayUpdated);
        
        Mockito.when(imageService.getImageById(Mockito.anyString())).thenReturn(spcImage);
        
        ISpaceLinkDisplay savedSpaceLinkDisplay2 = managerToTest.updateLink("Updated Space Link", spaceId1, 10, 30, 40, spaceId2, "Updated Space Link", "SPL002", "SPLD001", DisplayType.IMAGE, null, null, imageId2);
        Assert.assertEquals(spaceLinkDisplayUpdated.getId(), savedSpaceLinkDisplay2.getId());
        Assert.assertEquals(spaceLinkDisplayUpdated.getName(), savedSpaceLinkDisplay2.getName());
        Assert.assertEquals(new Double(spaceLinkDisplayUpdated.getPositionX()), new Double(savedSpaceLinkDisplay2.getPositionX()));
        Assert.assertEquals(new Double(spaceLinkDisplayUpdated.getPositionY()), new Double(savedSpaceLinkDisplay2.getPositionY()));
        Assert.assertEquals(spaceLinkDisplayUpdated.getRotation(), savedSpaceLinkDisplay2.getRotation());
        Assert.assertEquals(spaceLinkDisplayUpdated.getLink().getId(), savedSpaceLinkDisplay2.getLink().getId());
        Assert.assertEquals(spaceLinkDisplayUpdated.getType(), savedSpaceLinkDisplay2.getType());
        Assert.assertEquals(spaceLinkDisplayUpdated.getLink().getTargetSpace(), savedSpaceLinkDisplay2.getLink().getTargetSpace());
        Assert.assertEquals(spaceLinkDisplayUpdated.getImage().getHeight(), savedSpaceLinkDisplay2.getImage().getHeight());
        Assert.assertEquals(spaceLinkDisplayUpdated.getImage().getWidth(), savedSpaceLinkDisplay2.getImage().getWidth());
        Assert.assertEquals(spaceLinkDisplayUpdated.getImage().getId(), savedSpaceLinkDisplay2.getImage().getId());
        
    }
}

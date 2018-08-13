package edu.asu.diging.vspace.core.services.impl;

import java.util.ArrayList;

import javax.transaction.Transactional;

import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import edu.asu.diging.vspace.core.data.ImageRepository;
import edu.asu.diging.vspace.core.data.SpaceLinkDisplayRepository;
import edu.asu.diging.vspace.core.data.SpaceLinkRepository;
import edu.asu.diging.vspace.core.data.SpaceRepository;
import edu.asu.diging.vspace.core.exception.FileStorageException;
import edu.asu.diging.vspace.core.factory.IImageFactory;
import edu.asu.diging.vspace.core.factory.ISpaceLinkDisplayFactory;
import edu.asu.diging.vspace.core.factory.ISpaceLinkFactory;
import edu.asu.diging.vspace.core.file.IStorageEngine;
import edu.asu.diging.vspace.core.model.ISpace;
import edu.asu.diging.vspace.core.model.ISpaceLink;
import edu.asu.diging.vspace.core.model.IVSImage;
import edu.asu.diging.vspace.core.model.display.ISpaceLinkDisplay;
import edu.asu.diging.vspace.core.model.display.impl.SpaceLinkDisplay;
import edu.asu.diging.vspace.core.model.impl.Space;
import edu.asu.diging.vspace.core.model.impl.SpaceLink;
import edu.asu.diging.vspace.core.model.impl.VSImage;
import edu.asu.diging.vspace.core.services.ISpaceManager;

@Transactional
@Service
@PropertySource("classpath:/config.properties")
public class SpaceManager implements ISpaceManager {

	@Autowired
	private SpaceRepository spaceRepo;

	@Autowired
	private ImageRepository imageRepo;
	
	@Autowired
	private SpaceLinkRepository spaceLinkRepo;
	
	@Autowired
	private SpaceLinkDisplayRepository spaceLinkDisplayRepo;

	@Autowired
	private IStorageEngine storage;

	@Autowired
	private IImageFactory imageFactory;
	
	@Autowired
	private ISpaceLinkFactory spaceLinkFactory;
	
	@Autowired
	private ISpaceLinkDisplayFactory spaceLinkDisplayFactory;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * edu.asu.diging.vspace.core.services.impl.ISpaceManager#storeSpace(edu.asu.
	 * diging.vspace.core.model.ISpace, java.lang.String)
	 */
	@Override
	public CreationReturnValue storeSpace(ISpace space, byte[] image, String filename) {
		IVSImage bgImage = null;
		if (image != null && image.length > 0) {
			Tika tika = new Tika();
			String contentType = tika.detect(image);

			bgImage = imageFactory.createImage(filename, contentType);
			bgImage = imageRepo.save((VSImage) bgImage);
		}

		CreationReturnValue returnValue = new CreationReturnValue();
		returnValue.setErrorMsgs(new ArrayList<>());
		
		if (bgImage != null) {
			String relativePath = null;
			try {
				relativePath = storage.storeFile(image, filename, bgImage.getId());
			} catch (FileStorageException e) {
				returnValue.getErrorMsgs().add("Background image could not be stored: " + e.getMessage());
			}
			bgImage.setParentPath(relativePath);
			imageRepo.save((VSImage) bgImage);
			space.setImage(bgImage);
		}

		space = spaceRepo.save((Space) space);
		returnValue.setElement(space);
		return returnValue;
	}

	@Override
	public ISpace getSpace(String id) {
		return spaceRepo.findById(id).get();
	}
	
	@Override
	public ISpaceLinkDisplay createSpaceLink(String title, ISpace source, float positionX, float positionY) {
		source = spaceRepo.findById(source.getId()).get();
		ISpaceLink link = spaceLinkFactory.createSpaceLink(title, source);
		spaceLinkRepo.save((SpaceLink) link);
		
		ISpaceLinkDisplay display = spaceLinkDisplayFactory.createSpaceLinkDisplay(link);
		display.setPositionX(positionX);
		display.setPositionY(positionY);
		spaceLinkDisplayRepo.save((SpaceLinkDisplay)display);
		return display;
	}
}

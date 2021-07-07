package edu.asu.diging.vspace.core.services.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.asu.diging.vspace.core.data.ReferenceRepository;
import edu.asu.diging.vspace.core.model.IBiblioBlock;
import edu.asu.diging.vspace.core.model.IReference;
import edu.asu.diging.vspace.core.model.impl.BiblioBlock;
import edu.asu.diging.vspace.core.model.impl.Reference;
import edu.asu.diging.vspace.core.services.IReferenceManager;

@Service
public class ReferenceManager implements IReferenceManager {

    @Autowired
    private ReferenceRepository referenceRepo;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public IReference createReference(IBiblioBlock biblio, Reference reference) {
        reference.getBiblioList().add((BiblioBlock) biblio);
        return referenceRepo.save((Reference) reference);
    }

    @Override
    public IReference getReference(String referenceId) {
        Optional<Reference> reference = referenceRepo.findById(referenceId);
        if (reference.isPresent()) {
            return reference.get();
        }
        return null;
    }

    @Override
    public void updateReference(Reference reference) {
        referenceRepo.save((Reference) reference);
    }

    @Override
    public void deleteReferenceById(String referenceId, String BiblioId) {
        if (referenceId == null) {
            logger.error("Reference Id cannot be null.");
            return;
        }

        try {
            referenceRepo.delete((Reference) getReference(referenceId));
        } catch (IllegalArgumentException exception) {
            logger.error("Unable to delete reference" + referenceId + ". ", exception);
        }
    }

    @Override
    public void deleteReferences(List<IReference> references, String BiblioId) {
        for(IReference ref : references) {
            deleteReferenceById(ref.getId(), BiblioId);
        }
        
    }

    @Override
    public List<IReference> getReferencesForBiblio(String biblioId) {
        return new ArrayList<>(referenceRepo.findByBiblioId(biblioId));
    }
}

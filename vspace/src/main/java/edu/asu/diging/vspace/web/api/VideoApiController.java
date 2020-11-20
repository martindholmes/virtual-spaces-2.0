package edu.asu.diging.vspace.web.api;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.asu.diging.vspace.core.data.VideoRepository;
import edu.asu.diging.vspace.core.file.IStorageEngine;
import edu.asu.diging.vspace.core.model.IVSVideo;

@RestController
public class VideoApiController {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private VideoRepository videoRepo;

    @Autowired
    private IStorageEngine storage;

    @RequestMapping(value="/api/video/{id}", produces="video/mp4")
    public ResponseEntity<byte[]> getVideo(@PathVariable String id, HttpServletResponse response,
            HttpServletRequest request) {
        IVSVideo video = videoRepo.findById(id).get();
        byte[] videoContent = null;

        try {
            videoContent = storage.getVideoContent(video.getId(), video.getFilename());
        } catch (IOException e) {
            logger.error("Could not retrieve video.", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

//        try {
//            if (videoContent != null) {
//                response.setContentLength(videoContent.length);
//                response.getOutputStream().write(videoContent);
//                response.getOutputStream().close();
//            }
//        } catch (IOException e) {
//            logger.error("Could not write to output stream.", e);
//            return new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR);
//        }
        
//        response.setContentType("video/mp4");
        return ResponseEntity.status(HttpStatus.OK)
        .header("Content-Type", "video/mp4")
        .header("Content-Length", String.valueOf(videoContent.length - 1)).body(videoContent);
//        return new ResponseEntity<>(HttpStatus.OK);
    }
}

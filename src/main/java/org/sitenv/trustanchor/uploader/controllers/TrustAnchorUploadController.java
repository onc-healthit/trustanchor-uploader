package org.sitenv.trustanchor.uploader.controllers;

import org.sitenv.trustanchor.uploader.dto.TrustAnchorUploadResult;
import org.sitenv.trustanchor.uploader.services.TrustAnchorUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * Created by Brian on 1/16/2017.
 */
@RestController
public class TrustAnchorUploadController {
    private TrustAnchorUploadService trustbundleUploadService;

    @Autowired
    public TrustAnchorUploadController(TrustAnchorUploadService trustbundleUploadService) {
        this.trustbundleUploadService = trustbundleUploadService;
    }

    @RequestMapping(value = "/uploadtrustanchor", headers = "content-type=multipart/*", method = RequestMethod.POST)
    public TrustAnchorUploadResult uploadTrustAnchor(@RequestParam(value = "anchoruploadfile", required = true) MultipartFile anchoruploadfile){
        return trustbundleUploadService.uploadTrustBundle(anchoruploadfile);
    }
}

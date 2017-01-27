package org.sitenv.trustanchor.uploader.controllers;

import org.sitenv.trustanchor.uploader.dto.TrustAnchorUploadResult;
import org.sitenv.trustanchor.uploader.services.TrustAnchorUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by Brian on 1/16/2017.
 */
@RestController
public class TrustAnchorUploadController {
    private TrustAnchorUploadService trustbundleUploadService;
    @Value("${trustBundleFile}")
    private String trustbundleFilepath;
    @Autowired
    ServletContext context;

    @Autowired
    public TrustAnchorUploadController(TrustAnchorUploadService trustbundleUploadService) {
        this.trustbundleUploadService = trustbundleUploadService;
    }

    @RequestMapping(value = "/uploadtrustanchor", headers = "content-type=multipart/*", method = RequestMethod.POST)
    public TrustAnchorUploadResult uploadTrustAnchor(@RequestParam(value = "anchoruploadfile", required = true) MultipartFile anchoruploadfile){
        return trustbundleUploadService.uploadTrustBundle(anchoruploadfile);
    }

    @RequestMapping(value = "/downloadtrustbundle", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE, method = RequestMethod.GET)
    public void downloadTrustBundle(HttpServletRequest request, HttpServletResponse response) {
        try {
            File file = new File(trustbundleFilepath);

            if (file.exists()) {
                String mimeType = context.getMimeType(file.getPath());

                if (mimeType == null) {
                    mimeType = "application/octet-stream";
                }

                response.setContentType(mimeType);
                response.addHeader("Content-Disposition", "attachment; filename=" + file.getName());
                response.setContentLength((int) file.length());

                OutputStream os = response.getOutputStream();
                FileInputStream fis = new FileInputStream(file);
                byte[] buffer = new byte[4096];
                int b = -1;

                while ((b = fis.read(buffer)) != -1) {
                    os.write(buffer, 0, b);
                }

                fis.close();
                os.close();
            } else {
                System.err.println("Requested " + file.getName() + " file not found!!");
            }
        } catch (IOException e) {
            System.err.println("Error:- " + e.getMessage());
        }
    }
}

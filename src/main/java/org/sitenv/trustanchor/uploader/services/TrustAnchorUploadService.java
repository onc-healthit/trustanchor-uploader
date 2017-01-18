package org.sitenv.trustanchor.uploader.services;

import org.apache.commons.io.IOUtils;
import org.bouncycastle.openssl.PEMReader;
import org.sitenv.trustanchor.uploader.dto.TrustAnchorUploadResult;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStreamReader;
import java.security.cert.X509Certificate;

/**
 * Created by Brian on 1/17/2017.
 */
@Service
public class TrustAnchorUploadService {
    public TrustAnchorUploadResult uploadTrustBundle(MultipartFile anchor)  {
        TrustAnchorUploadResult trustbundleUploadResult= new TrustAnchorUploadResult();
        trustbundleUploadResult.setUploadedTrustAnchorFileName(anchor.getOriginalFilename());

        try {
            byte[] fileBytes = IOUtils.toByteArray(anchor.getInputStream());

            PEMReader pemReader = new PEMReader(new InputStreamReader(anchor.getInputStream()));
            Object pemObject = pemReader.readObject();
            if (pemObject instanceof X509Certificate) {
                X509Certificate cert = (X509Certificate)pemObject;
                System.out.println("DEBUG ---> is an instance of X509");
            }else{
                System.out.println("DEBUG ---> is NOT an instance of X509");
            }
        } catch (IOException e) {
            trustbundleUploadResult.setMessage("IO Exception reading anchor");
        }
        return trustbundleUploadResult;
    }
}

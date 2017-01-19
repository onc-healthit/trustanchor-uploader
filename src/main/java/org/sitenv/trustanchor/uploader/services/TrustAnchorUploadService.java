package org.sitenv.trustanchor.uploader.services;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.DEROutputStream;
import org.bouncycastle.openssl.PEMReader;
import org.nhindirect.trustbundle.core.CreateUnSignedPKCS7;
import org.sitenv.trustanchor.uploader.dto.TrustAnchorUploadResult;
import org.sitenv.trustanchor.uploader.exception.GenerateTrustBundleException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.security.NoSuchProviderException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

/**
 * Created by Brian on 1/17/2017.
 */
@Service
public class TrustAnchorUploadService {
    @Value("${trustBundleFile}")
    private String trustBundleFile;

    @Value("${trustAnchorDir}")
    private String trustAnchorDir;

    public TrustAnchorUploadResult uploadTrustBundle(MultipartFile anchor)  {
        TrustAnchorUploadResult trustbundleUploadResult= new TrustAnchorUploadResult();
        trustbundleUploadResult.setUploadedTrustAnchorFileName(anchor.getOriginalFilename());
        trustbundleUploadResult.setSuccess(false);
        PEMReader pemReader = null;
        X509Certificate x509cert = null;
        String savedFilePath = trustAnchorDir + File.separator + anchor.getOriginalFilename() + ".der";
        try {
            pemReader = new PEMReader(new InputStreamReader(anchor.getInputStream()));
            Object pemObject = pemReader.readObject();
            if (pemObject instanceof X509Certificate) {
                x509cert = (X509Certificate)pemObject;
                saveEncodedDerFile(x509cert, savedFilePath);
            }else{
                CertificateFactory cf = CertificateFactory.getInstance("X.509", "BC");
                x509cert = (X509Certificate)cf.generateCertificate(new ByteArrayInputStream(IOUtils.toByteArray(anchor.getInputStream())));
                saveEncodedDerFile(x509cert, savedFilePath);
            }
            generateTrustBundle(trustAnchorDir, trustBundleFile);
            trustbundleUploadResult.setSuccess(true);
            trustbundleUploadResult.setMessage("Upload successful. File saved as : " + savedFilePath);
        } catch (IOException e) {
            trustbundleUploadResult.setMessage("IO Exception reading anchor : " + e.getMessage());
        } catch (CertificateEncodingException e) {
            trustbundleUploadResult.setMessage("Exception getting encoded file reading anchor : " + e.getMessage());
        } catch (CertificateException e) {
            trustbundleUploadResult.setMessage("Exception with certificate : " + e.getMessage());
        } catch (NoSuchProviderException e) {
            trustbundleUploadResult.setMessage("No Provider found : " + e.getMessage());
        } catch (GenerateTrustBundleException e) {
            trustbundleUploadResult.setMessage("Error generating trust bundle with uploaded file : " + e.getMessage());
            deleteFile(savedFilePath);
        } finally {
            try {
                if(pemReader != null){
                    pemReader.close();
                }
            }catch (IOException ioEx){
                ioEx.printStackTrace();
            }
        }
        return trustbundleUploadResult;
    }

    private void saveEncodedDerFile(X509Certificate x509cert, String savedFilePath) throws IOException, CertificateEncodingException {
        ASN1InputStream in = null;
        ByteArrayOutputStream bOut = null;
        DEROutputStream dOut = null;
        FileOutputStream fos = null;
        if(x509cert == null){
            throw new CertificateEncodingException("Certificates must be in binary or base64 encoded X.509 format.");
        }else{
            try{
                in = new ASN1InputStream(x509cert.getEncoded());
                bOut = new ByteArrayOutputStream();
                dOut = new DEROutputStream(bOut);
                dOut.writeObject(in.readObject());
                fos = new FileOutputStream(savedFilePath);
                fos.write(bOut.toByteArray());
            }finally {
                try {
                    if (in != null) {
                        in.close();
                    }
                    if (bOut != null) {
                        bOut.close();
                    }
                    if(dOut != null){
                        dOut.close();
                    }
                    if(fos != null){
                        fos.close();
                    }
                }catch (IOException ioEx){
                    ioEx.printStackTrace();
                }
            }
        }
    }

    private static synchronized void generateTrustBundle(String anchorDir, String trustBundleFilePath) throws GenerateTrustBundleException {
        CreateUnSignedPKCS7 generator = new CreateUnSignedPKCS7();
        File file = new File(trustBundleFilePath);
        String generationResults = generator.getParameters(anchorDir, "Select Meta Data File", file.getParent(), file.getName());
        if(generationResults.contains("Error")){
            throw new GenerateTrustBundleException("Failed to include the certificate into the bundle, please verify the format of the anchor file uploaded.");
        }
    }

    private void deleteFile(String filePath){
        try {
            FileUtils.forceDelete(new File(filePath));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

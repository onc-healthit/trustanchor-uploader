package org.sitenv.trustanchor.uploader.dto;

/**
 * Created by Brian on 1/17/2017.
 */
public class TrustAnchorUploadResult {
    private boolean success;
    private String uploadedTrustAnchorFileName;
    private String message;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUploadedTrustAnchorFileName() {
        return uploadedTrustAnchorFileName;
    }

    public void setUploadedTrustAnchorFileName(String uploadedTrustAnchorFileName) {
        this.uploadedTrustAnchorFileName = uploadedTrustAnchorFileName;
    }
}

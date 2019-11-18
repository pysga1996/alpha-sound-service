package com.lambda.properties;

import org.springframework.stereotype.Component;

@Component
//@ConfigurationProperties(prefix = "avatar")
public class AvatarStorageProperties {
    private String uploadDir;

    public String getUploadDir() {
        return uploadDir;
    }

    public void setUploadDir(String uploadDir) {
        this.uploadDir = uploadDir;
    }
}
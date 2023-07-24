package com.nodejszip.NodeJSAutoUpdater.requests;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class UploadRequest {
    private String type;
    private MultipartFile file;
}

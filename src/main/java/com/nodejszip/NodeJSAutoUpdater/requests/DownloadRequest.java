package com.nodejszip.NodeJSAutoUpdater.requests;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class DownloadRequest {
    private String type;
}

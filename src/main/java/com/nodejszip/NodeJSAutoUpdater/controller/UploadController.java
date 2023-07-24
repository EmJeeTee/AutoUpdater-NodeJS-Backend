package com.nodejszip.NodeJSAutoUpdater.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

/*public class UploadController {
    @RestController
    @RequestMapping("/api/files")
    public class FileController {

        private static final String UPLOAD_DIR = "C:\\Users\\staj_metin.topcuoglu\\Desktop\\TempServer";


        @PostMapping("/upload")
        public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
            try {
                String fileName = generateUniqueFileName(file.getOriginalFilename());
                System.out.println("hola");
                Path targetLocation = Paths.get(UPLOAD_DIR + fileName);
                Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
                return ResponseEntity.ok("File uploaded successfully. File URL: " + targetLocation.toString());
            } catch (IOException e) {
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("File upload failed.");
            }
        }


        private String generateUniqueFileName(String originalFilename) {
            return UUID.randomUUID().toString() + "_" + originalFilename;
        }
    }
}*/

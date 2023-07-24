package com.nodejszip.NodeJSAutoUpdater.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nodejszip.NodeJSAutoUpdater.requests.UploadRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.minidev.json.JSONObject;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class GetFileController {

    private static final String DOWNLOAD_DIRECTORY = "C:/Users/staj_metin.topcuoglu/Desktop/v1.0.8";

    @GetMapping("/update/{fileName:.+}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileName) {
        try {
            Path filePath = Paths.get(DOWNLOAD_DIRECTORY).resolve(fileName);
            File file = filePath.toFile();

            if (file.exists()) {
                Resource resource = new FileSystemResource(file);

                HttpHeaders headers = new HttpHeaders();
                headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + resource.getFilename());

                return ResponseEntity.ok()
                        .headers(headers)
                        .contentType(MediaType.APPLICATION_OCTET_STREAM)
                        .body(resource);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ResponseEntity.notFound().build();
    }


    @GetMapping("/version")
    public String getVersion() throws IOException {
        String filePath = DOWNLOAD_DIRECTORY + "/package.json";
        Path file = Paths.get(filePath);
        if (Files.exists(file)) {
            String content = Files.readString(file);
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(content);
            String version = jsonNode.get("version").asText();
            return version;
        } else {
            throw new IOException("package.json dosyası bulunamadı: " + filePath);
        }
    }


    @GetMapping("/name")
    public String getName() throws IOException {
        String filePath = DOWNLOAD_DIRECTORY + "/package.json";
        Path file = Paths.get(filePath);
        if (Files.exists(file)) {
            String content = Files.readString(file);
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(content);
            String name = jsonNode.get("name").asText();
            return name;
        } else {
            throw new IOException("package.json dosyası bulunamadı: " + filePath);
        }
    }
    private static final String UPLOAD_DIR = "C:\\Users\\staj_metin.topcuoglu\\Desktop\\TempServer\\";


    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@ModelAttribute UploadRequest uploadRequest) {
        try {
            //String fileName = generateUniqueFileName(uploadRequest.getFile().getOriginalFilename());
            String fileName = uploadRequest.getFile().getOriginalFilename();
            Path targetLocation = Paths.get(UPLOAD_DIR + fileName);
            Files.copy(uploadRequest.getFile().getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            return ResponseEntity.ok("File uploaded successfully. File URL: " + targetLocation.toString());
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("File upload failed.");
        }
    }


    private String generateUniqueFileName(String originalFilename) {
        return UUID.randomUUID().toString() + "_" + originalFilename;
    }


    /*@GetMapping("/download")
    public ResponseEntity<Resource> downloadFiles(@RequestParam String versionNumber) {
        Path filePath;
        try {
            if("asar".equals(type))
            {
                filePath = Paths.get(UPLOAD_DIR +"app."+ type);
                System.out.println("asar dosya");

            }
            else if("exe".equals(type))
            {
                filePath = Paths.get(UPLOAD_DIR +"electron-app."+ type);
                System.out.println("exe dosya");

            } else {
                filePath = Paths.get(UPLOAD_DIR + type);
                System.out.println("normal dosya");
            }
            System.out.println(filePath);
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists()) {
                HttpHeaders headers = new HttpHeaders();
                headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"");

                return ResponseEntity.ok()
                        .headers(headers)
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }*/

    private static final String VERSIONS_FOLDER = "C:\\Users\\staj_metin.topcuoglu\\Desktop\\TempServer\\Versions";

    @PostMapping("/update")
    public ResponseEntity<String> updateVersion(@RequestParam("versionNumber") String versionNumber,
                                                @RequestParam("updateType") String updateType,
                                                @RequestParam("zipName") String zipName,
                                                @RequestParam("files") MultipartFile file) {
        try {
            String folderPath = VERSIONS_FOLDER + File.separator + versionNumber;
            if (!Files.exists(Paths.get(folderPath))) {
                Files.createDirectories(Paths.get(folderPath));
            }

            String fileName = folderPath + File.separator + "update_info.json";
            try (FileWriter fileWriter = new FileWriter(fileName)) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("updateType", updateType);
                jsonObject.put("zipName",zipName);
                fileWriter.write(jsonObject.toString());
            }

            File fileToSave = new File(folderPath + File.separator + zipName);
            file.transferTo(fileToSave);

            return ResponseEntity.ok("Versiyon bilgisi ve dosyalar başarıyla sunucuya gönderildi.");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Versiyon güncelleme sırasında bir hata oluştu.");
        }
    }


    /*@GetMapping("/download")
    public void downloadUpdate(@RequestParam("versionNumber") String versionNumber,
                               HttpServletResponse response) throws IOException {
        String folderPath = VERSIONS_FOLDER + File.separator + versionNumber;

        String updateInfoFilePath = folderPath + File.separator + "update_info.json";
        String fileNameToDownload = null;
        try {
            String updateInfoContent = new String(Files.readAllBytes(Paths.get(updateInfoFilePath)));
            ObjectMapper objectMapper = new ObjectMapper();
            fileNameToDownload = objectMapper.readTree(updateInfoContent).get("updateType").asText();
        } catch (IOException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return;
        }
        String extractionPath = System.getProperty("user.home") + File.separator + "Desktop" + File.separator + "CraneAirport";

        if (fileNameToDownload.equals("app.asar")) {
            deleteDirectory(new File(extractionPath));
            String zipFilePath = folderPath + File.separator + "CraneAirport.zip";

            if (Files.exists(Paths.get(zipFilePath))) {
                try (FileInputStream inputStream = new FileInputStream(zipFilePath);
                     ZipInputStream zipInputStream = new ZipInputStream(inputStream)) {
                    ZipEntry entry = zipInputStream.getNextEntry();
                    while (entry != null) {
                        if (!entry.isDirectory()) {
                            String fileName = entry.getName();
                            System.out.println(fileName);
                            System.out.println(fileNameToDownload);
                            if (fileName.equals(fileNameToDownload)) {

                                new File(extractionPath).mkdirs();
                                String filePath = extractionPath + File.separator + fileName;

                                try (FileOutputStream outputStream = new FileOutputStream(filePath)) {
                                    byte[] buffer = new byte[4096];
                                    int bytesRead;
                                    while ((bytesRead = zipInputStream.read(buffer)) != -1) {
                                        outputStream.write(buffer, 0, bytesRead);
                                    }
                                } catch (IOException e) {
                                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                                    return;
                                }

                                response.setStatus(HttpServletResponse.SC_OK);
                                return;
                            }
                        }
                        zipInputStream.closeEntry();
                        entry = zipInputStream.getNextEntry();
                    }
                } catch (IOException e) {
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    return;
                }
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            }
        } else if (fileNameToDownload.equals("fullpackage")) {
            deleteDirectory(new File(extractionPath));
            String zipFilePath = folderPath + File.separator + "CraneAirport.zip";

            if (Files.exists(Paths.get(zipFilePath))) {
                try (FileInputStream inputStream = new FileInputStream(zipFilePath);
                     ZipInputStream zipInputStream = new ZipInputStream(inputStream)) {
                    ZipEntry entry = zipInputStream.getNextEntry();
                    while (entry != null) {
                        if (!entry.isDirectory()) {
                            String fileName = entry.getName();
                            String filePath = extractionPath + File.separator + fileName;

                            new File(filePath).getParentFile().mkdirs();

                            try (FileOutputStream outputStream = new FileOutputStream(filePath)) {
                                byte[] buffer = new byte[4096];
                                int bytesRead;
                                while ((bytesRead = zipInputStream.read(buffer)) != -1) {
                                    outputStream.write(buffer, 0, bytesRead);
                                }
                            } catch (IOException e) {
                                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                                return;
                            }
                        }
                        zipInputStream.closeEntry();
                        entry = zipInputStream.getNextEntry();
                    }
                } catch (IOException e) {
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    return;
                }
                response.setStatus(HttpServletResponse.SC_OK);
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            }
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }*/

    /*@GetMapping("/download")
    public void downloadUpdate(@RequestParam("versionNumber") String versionNumber, HttpServletResponse response) {
        String folderPath = VERSIONS_FOLDER + File.separator + versionNumber;
        String zipFilePath = folderPath + File.separator + "CraneAirport.zip";

        String updateInfoFilePath = folderPath + File.separator + "update_info.json";

        String tempPath = System.getProperty("user.home") + File.separator + "Desktop" + File.separator + "CraneAirport";
        if (Files.exists(Paths.get(zipFilePath))) {
            try (FileInputStream inputStream = new FileInputStream(zipFilePath);
                 ZipInputStream zipInputStream = new ZipInputStream(inputStream)) {
                ZipEntry entry = zipInputStream.getNextEntry();
                deleteDirectory(new File(tempPath));
                while (entry != null) {
                    if (!entry.isDirectory()) {
                        try (FileOutputStream outputStream = new FileOutputStream(tempPath,true)) {
                            byte[] buffer = new byte[4096];
                            int bytesRead;
                            while ((bytesRead = zipInputStream.read(buffer)) != -1) {
                                outputStream.write(buffer, 0, bytesRead);
                            }
                        } catch (IOException e) {
                            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                            return;
                        }
                    }
                    zipInputStream.closeEntry();
                    entry = zipInputStream.getNextEntry();
                }
            } catch (IOException e) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                return;
            }
            response.setStatus(HttpServletResponse.SC_OK);
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
    }*/


    @GetMapping("/download")
    public ResponseEntity<Resource> downloadUpdate(@RequestParam("versionNumber") String versionNumber, HttpServletResponse response) throws MalformedURLException {
        String folderPath = VERSIONS_FOLDER + File.separator + versionNumber;
        String zipFilePath = folderPath + File.separator + "CraneAirport.zip";


        String updateInfoFilePath = folderPath + File.separator + "update_info.json";
        String fileNameToDownload = null;
        try {
            String updateInfoContent = new String(Files.readAllBytes(Paths.get(updateInfoFilePath)));
            ObjectMapper objectMapper = new ObjectMapper();
            fileNameToDownload = objectMapper.readTree(updateInfoContent).get("updateType").asText();
        } catch (IOException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return null;
        }
        if(fileNameToDownload.equals("app.asar") || fileNameToDownload.equals("fullpackage")) {
            try {
                Path tempPath = Paths.get(zipFilePath);
                Resource resource = new UrlResource(tempPath.toUri());

                if (resource.exists()) {
                    HttpHeaders headers = new HttpHeaders();
                    headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"");

                    return ResponseEntity.ok()
                            .headers(headers)
                            .body(resource);
                } else {
                    return ResponseEntity.notFound().build();
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        }
        else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        /*if (fileNameToDownload.equals("app.asar") || fileNameToDownload.equals("fullpackage")) {
            File zipFile = new File(zipFilePath);
            if (zipFile.exists()) {
                try (FileInputStream inputStream = new FileInputStream(zipFile);
                     ZipInputStream zipInputStream = new ZipInputStream(inputStream)) {




                    response.setContentType("application/octet-stream");
                    response.setHeader("Content-Disposition", "attachment; filename=\"CraneAirport.zip\"");

                    System.out.println(zipFile.length());

                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    while ((bytesRead = zipInputStream.read(buffer)) != -1) {
                        response.getOutputStream().write(buffer, 0, bytesRead);
                    }


                    response.setStatus(HttpServletResponse.SC_OK);
                    return;
                } catch (IOException e) {
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    return;
                }
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }*/
    }








    @GetMapping("/latestVersion")
    public ResponseEntity<?> getLatestVersion() throws IOException {
        JSONObject jsonObject = new JSONObject();
        File versionsFolder = new File(VERSIONS_FOLDER);
        if (!versionsFolder.exists() || !versionsFolder.isDirectory()) {
            return ResponseEntity.badRequest().body("Versions folder not found or not a directory.");
        }

        List<String> versionNumbers = Arrays.stream(Objects.requireNonNull(versionsFolder.list()))
                .filter(fileName -> !fileName.startsWith("."))
                .collect(Collectors.toList());

        if (versionNumbers.isEmpty()) {
            return ResponseEntity.badRequest().body("No versions found in the versions folder.");
        }

        String latestVersion = versionNumbers.stream()
                .max(String::compareToIgnoreCase)
                .orElse(null);

        if (latestVersion != null) {
            String updateInfoFilePath = VERSIONS_FOLDER + File.separator + latestVersion + File.separator + "update_info.json";
            String updateInfoContent = new String(Files.readAllBytes(Paths.get(updateInfoFilePath)));
            ObjectMapper objectMapper = new ObjectMapper();
            String fileNameToDownload = objectMapper.readTree(updateInfoContent).get("updateType").asText();
            jsonObject.put("version",latestVersion);
            jsonObject.put("updateType",fileNameToDownload);
            return new ResponseEntity<String>(jsonObject.toString(),HttpStatus.CREATED);
        } else {
            return ResponseEntity.badRequest().body("Failed to determine the latest version.");
        }
    }

}







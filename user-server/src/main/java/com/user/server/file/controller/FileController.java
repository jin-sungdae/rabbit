package com.user.server.file.controller;

import com.common.config.api.apidto.APIDataResponse;
import com.user.server.file.entity.File;
import com.user.server.file.service.FileService;
import com.user.server.product.entity.Product;
import com.user.server.product.service.ProductService;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/files")
@Slf4j
public class FileController {

    private final FileService fileService;
    private final ProductService productService;

//    @Value("${app.upload-dir}")
    private String dir ="/Users/jinseongdae/Desktop/ditto/ditto/file";

    @PostMapping("/upload")
    public APIDataResponse<String> upload(
            HttpServletRequest request,
            @RequestParam String filename,
            @RequestParam String parentUid,
            @RequestParam String parentType
    ) {
        try (ServletInputStream inputStream = request.getInputStream()) {

            String uploadDir = dir + "/" + parentUid;
            Path uploadPath = Paths.get(uploadDir);
            if (Files.notExists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            String fileExtension = FilenameUtils.getExtension(filename);
            String uniqueName = RandomStringUtils.randomAlphanumeric(16) + "." + fileExtension;
            Path filePath = Paths.get(uploadDir, uniqueName);

            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);

            Product product = productService.getProduct(parentUid);

            File file = File.builder()
                    .fileName(filename)
                    .parentUid(parentUid)
                    .uniqueName(uniqueName)
                    .fileUrl(filePath.toString())
                    .filePath(filePath.toString())
                    .fileExtension(fileExtension)
                    .fileSize(Files.size(filePath))
                    .parentType(parentType)
                    .product(product)
                    .build();

            fileService.upload(file);
            return APIDataResponse.of(Boolean.toString(true));

        } catch (IOException e) {
            log.error("파일 업로드 실패", e);
            return APIDataResponse.of(Boolean.toString(false));
        }
    }
}

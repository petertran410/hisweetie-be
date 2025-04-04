package com.enterprise.backend.controller;

import com.enterprise.backend.auth.AuthoritiesConstants;
import com.enterprise.backend.service.FileService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/file")
@RequiredArgsConstructor
public class FileController {
    private final FileService fileService;

    @PostMapping("/upload")
    public String uploadFiles(HttpServletRequest request, MultipartFile file) {
        return fileService.uploadFiles(request, file);
    }

    @GetMapping("/images/{filename:.+}")
    public ResponseEntity<Resource> getFile(@PathVariable String filename) {
        return fileService.getFile(filename);
    }

    @PostMapping("/import/product")
    @Secured({AuthoritiesConstants.ROLE_ADMIN, AuthoritiesConstants.ROLE_SUPER_ADMIN})
    @ApiOperation(value = "Import and process product data from Excel file", authorizations = {@Authorization(value = "Bearer")})
    public ResponseEntity<byte[]> importProduct(@RequestParam("file") MultipartFile file) {
        return fileService.importProduct(file);
    }
}

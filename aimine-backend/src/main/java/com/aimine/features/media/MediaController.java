package com.aimine.features.media;

import com.aimine.core.common.dto.ApiResponse;
import com.aimine.core.common.util.FileUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;

@RestController
@RequestMapping("/api/media")
public class MediaController {
    private final FileUtil fileUtil;
    @Value("${app.upload-dir:./uploads}")
    private String uploadDir;

    public MediaController(FileUtil fileUtil) {
        this.fileUtil = fileUtil;
    }

    @PostMapping("/upload")
    public ResponseEntity<ApiResponse<UploadResponse>> upload(@RequestParam("file") MultipartFile file) throws Exception {
        Path dir = fileUtil.ensureDir(uploadDir + "/misc");
        Path dest = dir.resolve(file.getOriginalFilename());
        file.transferTo(dest.toFile());
        return ResponseEntity.ok(ApiResponse.ok(new UploadResponse("/uploads/misc/" + file.getOriginalFilename())));
    }
}

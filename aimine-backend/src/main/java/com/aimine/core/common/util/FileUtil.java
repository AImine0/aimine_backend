package com.aimine.core.common.util;

import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
public class FileUtil {
    public Path ensureDir(String dir) throws Exception {
        Path p = Paths.get(dir);
        if (!Files.exists(p)) Files.createDirectories(p);
        return p;
    }
}

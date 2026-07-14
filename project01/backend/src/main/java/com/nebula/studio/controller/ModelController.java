package com.nebula.studio.controller;

import com.nebula.studio.common.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Slf4j
@RestController
@RequestMapping("/api/text2image")
@RequiredArgsConstructor
public class ModelController {

    @Value("${ai.local-model.model-dir:../sd-models}")
    private String modelDir;

    @GetMapping("/models")
    public Result<List<Map<String, Object>>> listModels() {
        List<Map<String, Object>> models = new ArrayList<>();
        File dir = new File(modelDir);
        if (!dir.exists() || !dir.isDirectory()) {
            log.warn("Model directory not found: {}", dir.getAbsolutePath());
            return Result.success(models);
        }
        scanDirectory(dir, models);
        // Filter out SD3 models (gated repo, not usable without HF auth)
        models.removeIf(m -> {
            String type = detectModelType((String) m.get("filepath"));
            return "sd3".equals(type);
        });
        models.sort(Comparator.comparing(m -> (String) m.get("name")));
        return Result.success(models);
    }

    private String detectModelType(String filepath) {
        try (RandomAccessFile raf = new RandomAccessFile(filepath, "r")) {
            byte[] lenBytes = new byte[8];
            raf.read(lenBytes);
            long headerLen = 0;
            for (int i = 0; i < 8; i++) {
                headerLen |= ((long) (lenBytes[i] & 0xFF)) << (8 * i);
            }
            byte[] headerBytes = new byte[(int) headerLen];
            raf.read(headerBytes);
            String header = new String(headerBytes, StandardCharsets.UTF_8);
            if (header.contains("joint_blocks") || header.contains("x_embedder")) {
                return "sd3";
            }
            if (header.contains("\"conditioner.")) {
                return "sdxl";
            }
            return "sd15";
        } catch (Exception e) {
            return "sd15";
        }
    }

    private void scanDirectory(File dir, List<Map<String, Object>> models) {
        File[] files = dir.listFiles();
        if (files == null) return;
        for (File file : files) {
            if (file.isDirectory()) {
                scanDirectory(file, models);
            } else if (file.getName().toLowerCase().endsWith(".safetensors")) {
                Map<String, Object> model = new LinkedHashMap<>();
                String filename = file.getName();
                String name = filename.substring(0, filename.lastIndexOf('.'));
                model.put("name", name);
                model.put("filename", filename);
                model.put("filepath", file.getName());
                model.put("sizeMb", Math.round(file.length() / (1024.0 * 1024.0) * 100.0) / 100.0);
                model.put("size", file.length());
                models.add(model);
            }
        }
    }
}

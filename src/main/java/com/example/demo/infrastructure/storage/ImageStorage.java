package com.example.demo.infrastructure.storage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.domain.value.TransactionId;

@Component
public class ImageStorage {

    @Value("${app.upload.dir}")
    private String uploadDir;

    private static final Logger log =
            LoggerFactory.getLogger(ImageStorage.class);

    /**
     * 儲存圖片並回傳「相對路徑」
     *
     * 結構：
     * uploads/
     *   └── 2026-02/
     *       └── 20260201_tx_123.jpg
     */
    public String save(
            YearMonth month,
            TransactionId transactionId,
            LocalDate transactionDate,
            MultipartFile file
    ) {
        try {
            String filename =
                    month + "/"
                  + transactionDate.format(DateTimeFormatter.BASIC_ISO_DATE)
                  + "_tx_" + transactionId.value()
                  + ".jpg";

            Path path = Paths.get(uploadDir, filename);
            Files.createDirectories(path.getParent());

            Files.copy(
                    file.getInputStream(),
                    path,
                    StandardCopyOption.REPLACE_EXISTING
            );

            return filename; // ✅ DB 存這個（相對路徑）

        } catch (IOException e) {
            throw new IllegalStateException("圖片儲存失敗", e);
        }
    }

    /**
     * 刪除圖片（不影響交易刪除流程）
     */
    public void delete(String imagePath) {
        if (imagePath == null || imagePath.isBlank()) return;

        try {
            Path path = Paths.get(uploadDir, imagePath);
            Files.deleteIfExists(path);
        } catch (IOException e) {
            // ⚠️ 不要讓刪交易失敗
            log.warn("Failed to delete image: {}", imagePath, e);
        }
    }
}

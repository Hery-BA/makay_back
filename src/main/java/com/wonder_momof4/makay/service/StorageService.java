package com.wonder_momof4.makay.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Slf4j
@Service
public class StorageService {

    private final Cloudinary cloudinary;

    public StorageService(
            @Value("${cloudinary.cloud-name}") String cloudName,
            @Value("${cloudinary.api-key}") String apiKey,
            @Value("${cloudinary.api-secret}") String apiSecret
    ) {
        this.cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", cloudName,
                "api_key", apiKey,
                "api_secret", apiSecret,
                "secure", true
        ));
    }

    /**
     * Upload un fichier vers Cloudinary
     * @param file   le fichier uploadé
     * @param folder le sous-dossier : "expeditions", "blog", "gallery", "testimonials"
     * @return Map contenant "url" et "path"
     */
    public Map<String, String> upload(MultipartFile file, String folder) throws IOException {
        Map<?, ?> result = cloudinary.uploader().upload(
                file.getBytes(),
                ObjectUtils.asMap(
                        "folder", "makay/" + folder,
                        "resource_type", "auto"
                )
        );

        String url = (String) result.get("secure_url");
        String path = (String) result.get("public_id");

        log.info("Fichier uploadé : {}", url);

        return Map.of(
                "url", url,
                "path", path
        );
    }

    /**
     * Supprime un fichier de Cloudinary
     * @param path le public_id retourné lors de l'upload
     */
    public void delete(String path) {
        try {
            cloudinary.uploader().destroy(path, ObjectUtils.emptyMap());
            log.info("Fichier supprimé : {}", path);
        } catch (Exception e) {
            log.warn("Impossible de supprimer le fichier {}: {}", path, e.getMessage());
        }
    }
}
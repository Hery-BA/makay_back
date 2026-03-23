package com.wonder_momof4.makay.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
public class StorageService {

    @Value("${supabase.url}")
    private String supabaseUrl;

    @Value("${supabase.service-role-key}")
    private String serviceRoleKey;

    @Value("${supabase.storage.bucket}")
    private String bucket;

    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * Upload un fichier vers Supabase Storage
     * @param file     le fichier uploadé
     * @param folder   le sous-dossier : "expeditions", "blog", "gallery", "testimonials"
     * @return Map contenant "url" et "path"
     */
    public Map<String, String> upload(MultipartFile file, String folder) throws IOException {
        String extension = getExtension(file.getOriginalFilename());
        String fileName  = folder + "/" + UUID.randomUUID() + "." + extension;
        String uploadUrl = supabaseUrl + "/storage/v1/object/" + bucket + "/" + fileName;

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + serviceRoleKey);
        headers.setContentType(MediaType.parseMediaType(
                file.getContentType() != null ? file.getContentType() : "application/octet-stream"
        ));

        HttpEntity<byte[]> entity = new HttpEntity<>(file.getBytes(), headers);
        restTemplate.exchange(uploadUrl, HttpMethod.POST, entity, String.class);

        String publicUrl = supabaseUrl + "/storage/v1/object/public/" + bucket + "/" + fileName;
        log.info("Fichier uploadé : {}", publicUrl);

        return Map.of(
                "url",  publicUrl,
                "path", fileName
        );
    }

    /**
     * Supprime un fichier de Supabase Storage
     * @param path  le chemin retourné lors de l'upload (ex: "expeditions/uuid.jpg")
     */
    public void delete(String path) {
        String deleteUrl = supabaseUrl + "/storage/v1/object/" + bucket + "/" + path;
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + serviceRoleKey);
        try {
            restTemplate.exchange(deleteUrl, HttpMethod.DELETE, new HttpEntity<>(headers), String.class);
            log.info("Fichier supprimé : {}", path);
        } catch (Exception e) {
            log.warn("Impossible de supprimer le fichier {}: {}", path, e.getMessage());
        }
    }

    private String getExtension(String filename) {
        if (filename == null || !filename.contains(".")) return "bin";
        return filename.substring(filename.lastIndexOf('.') + 1).toLowerCase();
    }
}
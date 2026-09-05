package com.example.demo.services;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CloudinaryService {
    private final Cloudinary cloudinary;

    public String uploadImage(MultipartFile file) {
        try {
            Map<?, ?> result = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap("folder", "travelplanner")
            );
            return (String) result.get("secure_url");
        } catch (IOException e) {
            throw new RuntimeException("Error uploading image to Cloudinary", e);
        }
    }

    public void deleteImage(String imageUrl) {
        String publicId = extractPublicId(imageUrl);
        if (publicId == null) {
            return;
        }
        try {
            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
        } catch (IOException e) {
            throw new RuntimeException("Error deleting image from Cloudinary", e);
        }
    }

    private String extractPublicId(String imageUrl) {
        if (imageUrl == null) return null;

        int dotIndex = imageUrl.lastIndexOf('.');
        String withoutExtension = dotIndex != -1 ? imageUrl.substring(0, dotIndex) : imageUrl;

        int folderIndex = withoutExtension.indexOf("travelplanner/");
        if (folderIndex == -1) return null;

        return withoutExtension.substring(folderIndex);
    }
}

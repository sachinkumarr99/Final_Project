package com.LegalMeterology.Online_Verification.Services;

import org.springframework.stereotype.Service;

import com.cloudinary.Cloudinary;

import lombok.RequiredArgsConstructor;

@Service 
@RequiredArgsConstructor 
public class CloudinaryService {
    
    private final Cloudinary cloudinary ;

     public String generateSignedUrl(
            String publicId,
            String resourceType
    ) {

        if (publicId == null || publicId.isBlank()) {
            throw new RuntimeException("Cloudinary public ID not found");
        }

        if (resourceType == null || resourceType.isBlank()) {
            resourceType = "raw";
        }

        return cloudinary.url()
                .secure(true)
                .resourceType(resourceType)
                .signed(true)
                .publicId(publicId)
                .generate();
    }
}

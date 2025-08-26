package com.example.nesta.service;

import com.example.nesta.dto.ApartmentImageDto;
import com.example.nesta.model.Apartment;
import com.example.nesta.model.ApartmentImage;
import com.example.nesta.repository.ApartmentImageRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.*;

@Service
public class ApartmentImageService {
    private final com.example.nesta.repository.apartment.ApartmentRepository apartmentRepository;
    private final ApartmentImageRepository imageRepository;

    @Value("${app.storage.root:uploads}")
    private String storageRoot;
    @Value("${app.storage.public-base-path:/uploads}")
    private String publicBasePath;
    @Value("${app.storage.max-images-per-apartment:20}")
    private int maxImagesPerApartment;

    private static final Set<String> ALLOWED_MIME = Set.of(
            "image/jpeg",
            "image/png"
    );

    public ApartmentImageService(
            com.example.nesta.repository.apartment.ApartmentRepository apartmentRepository,
            ApartmentImageRepository imageRepository
    ) {
        this.apartmentRepository = apartmentRepository;
        this.imageRepository = imageRepository;
    }

    @Transactional
    public ApartmentImageDto uploadSingle(long apartmentId, MultipartFile file, Jwt jwt) {
        Apartment apartment = apartmentRepository.findById(apartmentId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Apartment %d not found".formatted(apartmentId)));

        String subject = getJwtSubjectOrThrow(jwt);
        authorizeOwnerOrThrow(apartment, subject);

        enforceApartmentImageLimit(apartmentId, 1);

        ApartmentImage saved = storeOne(apartment, file);

        return new ApartmentImageDto(
                saved.getId(),
                saved.getPublicUrl(),
                saved.getWidth(),
                saved.getHeight(),
                saved.getContentType(),
                saved.getSizeBytes()
        );
    }

    private ApartmentImage storeOne(Apartment apartment, MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Empty file");
        }

        String contentType = Optional.ofNullable(file.getContentType()).orElse("");
        validateMimeOrThrow(contentType);

        BufferedImage img = readImageOrThrow(file);
        int width = img.getWidth();
        int height = img.getHeight();
        validateDimensionsOrThrow(width, height);

        String ext = extensionFor(contentType);
        String relative = buildRelativePath(apartment.getId(), ext);

        Path root = Path.of(storageRoot).toAbsolutePath().normalize();
        Path target = resolveTargetPath(root, relative);
        ensurePathWithinRoot(root, target);
        writeMultipartTo(file, target);

        String publicUrl = publicUrlFor(relative);

        ApartmentImage entity = ApartmentImage.builder()
                .apartment(apartment)
                .relativePath(relative)
                .publicUrl(publicUrl)
                .contentType(contentType)
                .sizeBytes(file.getSize())
                .width(width)
                .height(height)
                .build();

        return imageRepository.save(entity);
    }

    private String getJwtSubjectOrThrow(Jwt jwt) {
        return Optional.ofNullable(jwt)
                .map(Jwt::getSubject)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing JWT"));
    }

    private void authorizeOwnerOrThrow(Apartment apartment, String subject) {
        if (!Objects.equals(subject, apartment.getLandlordId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not your apartment");
        }
    }

    private void enforceApartmentImageLimit(long apartmentId, int toAdd) {
        int current = imageRepository.countByApartmentId(apartmentId);
        if (current + toAdd > maxImagesPerApartment) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Too many images for this apartment (limit %d)".formatted(maxImagesPerApartment));
        }
    }

    private void validateMimeOrThrow(String contentType) {
        if (!ALLOWED_MIME.contains(contentType)) {
            throw new ResponseStatusException(HttpStatus.UNSUPPORTED_MEDIA_TYPE,
                    "Only JPEG and PNG are allowed");
        }
    }

    private BufferedImage readImageOrThrow(MultipartFile file) {
        try {
            BufferedImage img = ImageIO.read(file.getInputStream());
            if (img == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid image data");
            return img;
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unable to read image", e);
        }
    }

    private void validateDimensionsOrThrow(int width, int height) {
        if (width <= 0 || height <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid image dimensions");
        }
        if (width > 10_000 || height > 10_000) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Image is too large in dimensions");
        }
    }

    private String extensionFor(String contentType) {
        return "image/png".equals(contentType) ? "png" : "jpg";
    }

    private String buildRelativePath(long apartmentId, String ext) {
        LocalDate now = LocalDate.now();
        String fileName = UUID.randomUUID() + "." + ext;
        return "apartments/%d/%04d/%02d/%02d/%s"
                .formatted(apartmentId, now.getYear(), now.getMonthValue(), now.getDayOfMonth(), fileName);
    }

    private Path resolveTargetPath(Path root, String relative) {
        return root.resolve(relative).normalize();
    }

    private void ensurePathWithinRoot(Path root, Path target) {
        if (!target.startsWith(root)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Illegal path");
        }
    }

    private void writeMultipartTo(MultipartFile file, Path target) {
        try {
            Files.createDirectories(target.getParent());
            try (var in = file.getInputStream()) {
                Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to write file", e);
        }
    }

    private String publicUrlFor(String relative) {
        String base = publicBasePath.endsWith("/")
                ? publicBasePath.substring(0, publicBasePath.length() - 1)
                : publicBasePath;
        return (base + "/" + relative).replace("\\", "/");
    }
}

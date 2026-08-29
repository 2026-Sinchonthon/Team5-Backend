package com.sinchonthon.team5.odyssey.global.storage;

public record StoredFile(
        String originalName,
        String fileUrl,
        String contentType,
        Long fileSize
) {
}

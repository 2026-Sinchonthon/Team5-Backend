package com.sinchonthon.team5.odyssey.global.storage;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {

    StoredFile upload(MultipartFile file, String directory);

    void delete(String fileUrl);
}

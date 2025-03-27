package com.user.server.file.dto;

import com.user.server.file.entity.File;
import lombok.*;

@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
public class FileDto {
    private Long id;
    private String fileName;
    private String uniqueName;
    private String filePath;
    private String parentUid;
    private String fileUrl;
    private String parentType;
    private String fileExtension;
    private Long fileSize;
    private boolean isTemp;
    private String createdAt;

    public boolean getIsTemp() {
        return this.isTemp;
    }


    public static FileDto from(File file) {
        return FileDto.builder()
                .id(file.getId())
                .fileName(file.getFileName())
                .uniqueName(file.getUniqueName())
                .filePath(file.getFilePath())
                .parentUid(file.getParentUid())
                .fileUrl(file.getFileUrl())
                .parentType(file.getParentType())
                .fileExtension(file.getFileExtension())
                .fileSize(file.getFileSize())
                .isTemp(file.getIsTemp())
                .createdAt(file.getCreatedAt().toString())
                .build();
    }
}

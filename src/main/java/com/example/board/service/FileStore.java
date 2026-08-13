package com.example.board.service;

import com.example.board.domain.Attachment;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 로컬 파일 시스템에 실제로 파일을 물리적으로 저장하고 삭제하는 작업을 수행하는 컴포넌트입니다.
 */
@Component
public class FileStore {

    @Value("${file.upload-dir}")
    private String uploadDir;

    /**
     * 웹 브라우저에서 접근 시 활용할 자원 식별 경로 접두사
     * 예: /uploads/파일명.png
     */
    private static final String RESOURCE_PATH_PREFIX = "/uploads/";

    /**
     * 업로드된 파일 여러 개를 일괄 처리하여 로컬 디렉토리에 저장합니다.
     *
     * @param multipartFiles 클라이언트로부터 전송받은 MultipartFile 리스트
     * @return DB에 영속화하기 위한 Attachment 도메인 리스트
     * @throws IOException 파일 입출력 과정에서 예외가 발생할 경우
     */
    public List<Attachment> storeFiles(List<MultipartFile> multipartFiles) throws IOException {
        List<Attachment> storeFileResult = new ArrayList<>();
        if (multipartFiles != null) {
            for (MultipartFile multipartFile : multipartFiles) {
                if (!multipartFile.isEmpty()) {
                    storeFileResult.add(storeFile(multipartFile));
                }
            }
        }
        return storeFileResult;
    }

    /**
     * 업로드된 단일 파일을 처리하여 로컬 디렉토리에 저장합니다.
     *
     * @param multipartFile 업로드된 단일 파일 객체
     * @return 파일 정보가 채워진 Attachment 도메인 객체
     * @throws IOException 파일 저장 과정에서 예외가 발생할 경우
     */
    public Attachment storeFile(MultipartFile multipartFile) throws IOException {
        if (multipartFile.isEmpty()) {
            return null;
        }

        // 디렉토리가 존재하지 않는다면 자동 생성
        File directory = new File(uploadDir);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        //원본 파일이름
        String originalFilename = multipartFile.getOriginalFilename();
        //UUID로 중복안되는 파일 이름을 만듬
        String storedFileName = createStoreFileName(originalFilename);

        // 로컬 물리 경로에 파일 저장
        File destination = new File(uploadDir + storedFileName);
        multipartFile.transferTo(destination); // 파일을 저장

        // 웹 브라우저가 정적 경로를 통해 바로 접근 가능하도록 가공된 URI 정의
        String accessUrl = RESOURCE_PATH_PREFIX + storedFileName;

        return Attachment.builder()
                .originalName(originalFilename)
                .storedName(storedFileName)
                .filePath(accessUrl)
                .fileSize(multipartFile.getSize())
                .build();
    }

    /**
     * 로컬 저장소에 저장된 실제 물리 파일을 삭제합니다.
     *
     * @param storedName 삭제할 서버 고유 파일명 (UUID명)
     */
    public void deleteFile(String storedName) {
        if (storedName == null || storedName.isBlank()) {
            return;
        }
        File file = new File(uploadDir + storedName);
        if (file.exists()) {
            file.delete();
        }
    }

    /**
     * 파일명 중복을 피하기 위하여 UUID를 활용한 고유 파일 이름을 생성합니다.
     */
    private String createStoreFileName(String originalFilename) {
        String ext = extractExt(originalFilename);
        String uuid = UUID.randomUUID().toString();
        return uuid + "." + ext;
    }

    /**
     * 원본 파일명으로부터 확장자(Extension)를 추출합니다.
     */
    private String extractExt(String originalFilename) {
        if (originalFilename == null) {
            return "";
        }
        int pos = originalFilename.lastIndexOf(".");
        if (pos == -1) {
            return "";
        }
        return originalFilename.substring(pos + 1);
    }
}

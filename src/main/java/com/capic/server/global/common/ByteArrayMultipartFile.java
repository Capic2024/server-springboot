package com.capic.server.global.common;

import org.apache.commons.io.IOUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public class ByteArrayMultipartFile implements MultipartFile {

    private byte[] imgContent;
    private String fileName;

    public ByteArrayMultipartFile(byte[] imgContent, String fileName) {
        this.imgContent = imgContent;
        this.fileName = fileName;
    }

    @Override
    public String getName() {
        return this.fileName;
    }

    @Override
    public String getOriginalFilename() {
        return this.fileName;
    }

    @Override
    public String getContentType() {
        return "image/jpeg"; // 적절한 MIME 타입 설정
    }

    @Override
    public boolean isEmpty() {
        return this.imgContent == null || this.imgContent.length == 0;
    }

    @Override
    public long getSize() {
        return this.imgContent.length;
    }

    @Override
    public byte[] getBytes() throws IOException {
        return this.imgContent;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return new ByteArrayInputStream(this.imgContent);
    }

    @Override
    public void transferTo(java.io.File dest) throws IOException, IllegalStateException {
        try (InputStream in = new ByteArrayInputStream(this.imgContent)) {
            IOUtils.copy(in, new java.io.FileOutputStream(dest));
        }
    }
}

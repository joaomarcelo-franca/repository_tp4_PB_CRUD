package org.example.service;

import io.javalin.http.UploadedFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class FileService {
    public String saveImage (UploadedFile file){
        if (file == null || file.filename() == null || file.filename().isBlank()) {
            return null;
        }

        try {
            String uploadDir = "src/main/resources/public/uploads/";
            File dir = new File(uploadDir);
            if (!dir.exists()) dir.mkdirs();

            String fileName = System.currentTimeMillis() + "_" + file.filename();
            String filePath = uploadDir + fileName;

            try (InputStream is = file.content();
                 FileOutputStream fos = new FileOutputStream(filePath)) {

                byte[] buffer = new byte[1024];
                int bytes;
                while ((bytes = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, bytes);
                }
            }

            return "src/main/resources/public/uploads/" + fileName;
//            return "/uploads/" + fileName;

        } catch (Exception e) {
            System.out.println("Erro ao salvar imagem: " + e.getMessage());
            return null;
        }
    }
}

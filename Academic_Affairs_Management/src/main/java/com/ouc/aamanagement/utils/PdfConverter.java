

package com.ouc.aamanagement.utils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import java.io.*;
import java.nio.file.*;
import java.util.concurrent.*;
public class PdfConverter {
    private static final String LIBRE_OFFICE_PATH = "G:\\lbo\\program\\soffice.exe";
    private static final ExecutorService executor = Executors.newCachedThreadPool();

    public static File convertToPdf(File wordFile) throws Exception {
        Path tempDir = Files.createTempDirectory("pdf-convert-");
        File pdfFile = null;

        try {
            Process process = new ProcessBuilder(
                    LIBRE_OFFICE_PATH,
                    "--headless",
                    "--convert-to", "pdf:writer_pdf_Export",
                    "--outdir", tempDir.toString(),
                    wordFile.getAbsolutePath()
            ).start();

            consumeStream(process.getInputStream());
            consumeStream(process.getErrorStream());

            boolean success = process.waitFor(1, TimeUnit.MINUTES);
            if (!success || process.exitValue() != 0) {
                throw new IOException("PDF conversion failed, exit code: " + process.exitValue());
            }

            // 修正：使用 Apache Commons IO 获取无扩展名文件名
            String baseName = FilenameUtils.removeExtension(wordFile.getName());
            pdfFile = tempDir.resolve(baseName + ".pdf").toFile();

            // 等待文件系统同步
            int retry = 0;
            while (!pdfFile.exists() && retry++ < 5) {
                Thread.sleep(500);
            }

            if (!pdfFile.exists()) {
                throw new FileNotFoundException("PDF file not generated: " + pdfFile);
            }
            System.out.println("生成的PDF路径: " + pdfFile.getAbsolutePath());
            return pdfFile;
        } finally {
            executor.submit(() -> {
                try {
                    Thread.sleep(5000);
                    /*FileUtils.deleteDirectory(tempDir.toFile());*/
                } catch (Exception e) {
                    System.err.println("Failed to clean temp dir: " + tempDir);
                }
            });
        }
    }

    private static void consumeStream(InputStream inputStream) {
        executor.submit(() -> {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println("[LibreOffice] " + line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
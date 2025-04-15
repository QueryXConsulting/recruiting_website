package com.queryx.recruiting_website.utils;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;
import org.apache.pdfbox.pdmodel.interactive.form.PDTextField;
import org.springframework.util.StringUtils;

import java.io.*;
import java.util.List;
import java.util.Map;

public class PDFFormUtils {
    private static final String DEFAULT_FONT_PATH = "files/font/STXIHEI.TTF";
    private static final String DEFAULT_FONT_NAME = "F1";
    private static final int DEFAULT_FONT_SIZE = 12;


    public static PDDocument fillPDFForm(String templatePath, Map<String, String> formData, String fontPath, Integer sendFont) throws IOException {
        fontPath = StringUtils.hasText(fontPath) ? fontPath : DEFAULT_FONT_PATH;
        int fontSize = sendFont == null ? DEFAULT_FONT_SIZE : sendFont;
        PDDocument document = Loader.loadPDF(new File(templatePath));
        if (!new File(fontPath).exists()) {
            throw new FileNotFoundException("不存在的路径: " + fontPath);
        }


        PDAcroForm acroForm = document.getDocumentCatalog().getAcroForm();
        if (acroForm == null) {
            throw new IllegalStateException("没有该pdf");
        }


        PDType0Font font = loadFont(document, fontPath);
        PDResources resources = setupResources(acroForm, font);


        configureFormFields(acroForm, formData, fontSize);

        acroForm.setNeedAppearances(true);
        acroForm.flatten();

        return document;


    }

    private static PDType0Font loadFont(PDDocument document, String fontPath) throws IOException {
        try (InputStream fontStream = new BufferedInputStream(new FileInputStream(fontPath))) {
            return PDType0Font.load(document, fontStream, true);
        }
    }

    private static PDResources setupResources(PDAcroForm acroForm, PDType0Font font) {
        PDResources resources = acroForm.getDefaultResources();
        if (resources == null) {
            resources = new PDResources();
            acroForm.setDefaultResources(resources);
        }
        resources.put(COSName.getPDFName(DEFAULT_FONT_NAME), font);
        return resources;
    }

    private static void configureFormFields(PDAcroForm acroForm, Map<String, String> formData,Integer fontSize) {
        formData.forEach((fieldName, value) -> {
            List<PDField> fields = acroForm.getFields();
            for (PDField field : fields) {
                String fieldQualifiedName = field.getFullyQualifiedName();
                if (fieldQualifiedName != null && fieldQualifiedName.equals(fieldName)) {
                    if (field instanceof PDTextField) {
                        PDTextField textField = (PDTextField) field;
                        try {
                            textField.setDefaultAppearance("/" + DEFAULT_FONT_NAME + " " + fontSize + " Tf 0 g");
                            textField.setValue(value != null ? value : "");
                            textField.setReadOnly(true);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        textField.setReadOnly(true);
                    }
                    break;
                }

            }
        });


    }

    public static byte[] saveToByteArray(PDDocument document) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        document.save(baos);
        return baos.toByteArray();
    }

}

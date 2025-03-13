package com.queryx.recruiting_website.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.queryx.recruiting_website.domain.TDRegistration;
import com.queryx.recruiting_website.domain.vo.RegistrationListVO;
import com.queryx.recruiting_website.domain.vo.ReservationListVO;
import com.queryx.recruiting_website.mapper.TDRegistrationMapper;
import com.queryx.recruiting_website.service.TDRegistrationService;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;
import org.apache.pdfbox.pdmodel.interactive.form.PDTextField;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.*;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 员工入职信息服务实现类
 */
@Service
public class TDRegistrationServiceImpl extends ServiceImpl<TDRegistrationMapper, TDRegistration> implements TDRegistrationService {

    @Override
    public Object selectRegistration(Integer page, Integer size, Long jobId, String status) {
        Page<TDRegistration> registrationPage = page(
                new Page<>(page, size)
                , new LambdaQueryWrapper<TDRegistration>().eq(TDRegistration::getJobId, jobId)
                        .eq("2".equals(status), TDRegistration::getRegistrationStatus, status)
        );
        if (registrationPage.getRecords().isEmpty()) {
            return null;
        }
        Page<Object> registrationListVOPage = new Page<>(registrationPage.getCurrent(), registrationPage.getSize(), registrationPage.getTotal());
        registrationListVOPage.setRecords(
                registrationPage.getRecords().stream().map(registration -> {
                    if ("2".equals(status)) {
                        ReservationListVO reservationListVO = new ReservationListVO();
                        BeanUtils.copyProperties(registration, reservationListVO);
                        return reservationListVO;
                    } else {
                        RegistrationListVO registrationListVO = new RegistrationListVO();
                        BeanUtils.copyProperties(registration, registrationListVO);
                        return registrationListVO;
                    }
                }).toList()
        );
        return registrationListVOPage;
    }

    @Override
    public Object updateRegistrationStatus(Long registrationId, String status, Date date) {
        LambdaUpdateWrapper<TDRegistration> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(TDRegistration::getId, registrationId)
                .set(StringUtils.hasText(status), TDRegistration::getRegistrationStatus, status);

        if (date != null) {
            updateWrapper.set(TDRegistration::getReservationStatus, "1")
                    .set(TDRegistration::getHireDate, date);
        }

        update(updateWrapper);
        return null;
    }

    @Override
    public byte[] downloadPdf(Long id) {
        TDRegistration registration = getById(id);
        String inputPath = "D:/maven/offer_files/1741588126573_18.pdf";// TODO 后续添加入职信息模板进行修改
        String fontPath = "C:\\Windows\\Fonts\\STXIHEI.TTF";// +1

        try (PDDocument document = Loader.loadPDF(new File(inputPath))) {
            if (!new File(fontPath).exists()) {
                throw new FileNotFoundException("找不到字体文件: " + fontPath);
            }

            PDAcroForm acroForm = document.getDocumentCatalog().getAcroForm();
            if (acroForm == null) {
                log.error("PDF文件中没有表单域");
                return null;
            }

            // 加载字体
            PDType0Font font;
            try (InputStream fontStream = new BufferedInputStream(new FileInputStream(fontPath))) {
                font = PDType0Font.load(document, fontStream, true);
            }

            // 创建资源
            PDResources resources = acroForm.getDefaultResources();
            if (resources == null) {
                resources = new PDResources();
                acroForm.setDefaultResources(resources);
            }

            // 添加字体到资源
            String fontName = "F1";
            resources.put(COSName.getPDFName(fontName), font);

            // 遍历所有表单域并设置字体
            for (PDField field : acroForm.getFields()) {
                if (field instanceof PDTextField textField) {
                    System.out.println("字段名: " + field.getFullyQualifiedName());
                    textField.setDefaultAppearance("/" + fontName + " 12 Tf 0 g");
                    textField.setReadOnly(false);
                }
            }

            // 设置表单域的值 TODO 后面有模板后对此进行修改
            Map<String, String> fieldValues = new HashMap<>();
            fieldValues.put("Text1", "听不懂思密达");
            fieldValues.put("Text2", "不是哥们");
            fieldValues.put("Text3", "对3");
            fieldValues.put("Text4",registration.getUserName());

            // 批量设置表单域的值
            for (Map.Entry<String, String> entry : fieldValues.entrySet()) {
                setFormFieldValue(acroForm, entry.getKey(), entry.getValue(), fontName);
            }

            // 设置表单
            acroForm.setNeedAppearances(true);
            // 取消表单域设置
            acroForm.flatten();

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            document.save(baos);
            return baos.toByteArray();

        } catch (IOException e) {
            log.error("PDF处理失败: ", e);
            return null;
        }
    }

    /**
     * 设置表单域的值
     */
    private void setFormFieldValue(PDAcroForm acroForm, String fieldName, String value, String fontName) {
        try {
            PDTextField field = (PDTextField) acroForm.getField(fieldName);
            if (field != null) {
                field.setDefaultAppearance("/" + fontName + " 12 Tf 0 g");
                // 设置字段值
                field.setValue(value);
                field.setReadOnly(true);
                log.warn("字段设置成功: ");
            } else {
                log.warn("字段不存在");
            }
        } catch (IOException e) {
            log.error("设置字段失败: ", e);
        }
    }
}


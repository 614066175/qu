package com.hand.hdsp.quality.infra.export;

import com.hand.hdsp.quality.api.dto.DataFieldDTO;
import com.hand.hdsp.quality.api.dto.DataStandardDTO;
import com.hand.hdsp.quality.api.dto.NameStandardDTO;
import com.hand.hdsp.quality.api.dto.StandardDocDTO;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hzero.mybatis.helper.DataSecurityHelper;

import java.util.List;

/**
 * @Title: ExportUtils
 * @Description:
 * @author: lgl
 * @date: 2023/2/8 11:37
 */
public class ExportUtils {


    public static void decryptDataStandard(List<DataStandardDTO> dataStandards) {
        //解密责任人相关信息
        if (DataSecurityHelper.isTenantOpen() && CollectionUtils.isNotEmpty(dataStandards)) {
            dataStandards.forEach(dataStandardDTO -> {
                if (StringUtils.isNotEmpty(dataStandardDTO.getChargeName())) {
                    dataStandardDTO.setChargeName(DataSecurityHelper.decrypt(dataStandardDTO.getChargeName()));
                }
                if (StringUtils.isNotEmpty(dataStandardDTO.getChargeEmail())) {
                    dataStandardDTO.setChargeEmail(DataSecurityHelper.decrypt(dataStandardDTO.getChargeEmail()));
                }
                if (StringUtils.isNotEmpty(dataStandardDTO.getChargeTel())) {
                    dataStandardDTO.setChargeTel(DataSecurityHelper.decrypt(dataStandardDTO.getChargeTel()));
                }
                if (StringUtils.isNotEmpty(dataStandardDTO.getChargeDeptName())) {
                    dataStandardDTO.setChargeDeptName(DataSecurityHelper.decrypt(dataStandardDTO.getChargeDeptName()));
                }
            });
        }
    }




    public static void decryptFieldStandard(List<DataFieldDTO> dataFieldDTOList) {
        //解密责任人相关信息
        if (DataSecurityHelper.isTenantOpen() && CollectionUtils.isNotEmpty(dataFieldDTOList)) {
            dataFieldDTOList.forEach(dataFieldDTO -> {
                if (StringUtils.isNotEmpty(dataFieldDTO.getChargeName())) {
                    dataFieldDTO.setChargeName(DataSecurityHelper.decrypt(dataFieldDTO.getChargeName()));
                }
                if (StringUtils.isNotEmpty(dataFieldDTO.getChargeEmail())) {
                    dataFieldDTO.setChargeEmail(DataSecurityHelper.decrypt(dataFieldDTO.getChargeEmail()));
                }
                if (StringUtils.isNotEmpty(dataFieldDTO.getChargeTel())) {
                    dataFieldDTO.setChargeTel(DataSecurityHelper.decrypt(dataFieldDTO.getChargeTel()));
                }
                if (StringUtils.isNotEmpty(dataFieldDTO.getChargeDeptName())) {
                    dataFieldDTO.setChargeDeptName(DataSecurityHelper.decrypt(dataFieldDTO.getChargeDeptName()));
                }
            });
        }
    }



    public static void decryptNameStandard(List<NameStandardDTO> nameStandardDTOS) {
        //解密责任人相关信息
        if (DataSecurityHelper.isTenantOpen() && CollectionUtils.isNotEmpty(nameStandardDTOS)) {
            nameStandardDTOS.forEach(nameStandardDTO -> {
                if (StringUtils.isNotEmpty(nameStandardDTO.getChargeName())) {
                    nameStandardDTO.setChargeName(DataSecurityHelper.decrypt(nameStandardDTO.getChargeName()));
                }
                if (StringUtils.isNotEmpty(nameStandardDTO.getChargeEmail())) {
                    nameStandardDTO.setChargeEmail(DataSecurityHelper.decrypt(nameStandardDTO.getChargeEmail()));
                }
                if (StringUtils.isNotEmpty(nameStandardDTO.getChargeTel())) {
                    nameStandardDTO.setChargeTel(DataSecurityHelper.decrypt(nameStandardDTO.getChargeTel()));
                }
                if (StringUtils.isNotEmpty(nameStandardDTO.getChargeDeptName())) {
                    nameStandardDTO.setChargeDeptName(DataSecurityHelper.decrypt(nameStandardDTO.getChargeDeptName()));
                }
            });
        }
    }

    public static void decryptDocStandard(List<StandardDocDTO> standardDocDTOS) {
        //解密责任人相关信息
        if (DataSecurityHelper.isTenantOpen() && CollectionUtils.isNotEmpty(standardDocDTOS)) {
            standardDocDTOS.forEach(standardDocDTO -> {
                if (StringUtils.isNotEmpty(standardDocDTO.getChargeName())) {
                    standardDocDTO.setChargeName(DataSecurityHelper.decrypt(standardDocDTO.getChargeName()));
                }
                if (StringUtils.isNotEmpty(standardDocDTO.getChargeEmail())) {
                    standardDocDTO.setChargeEmail(DataSecurityHelper.decrypt(standardDocDTO.getChargeEmail()));
                }
                if (StringUtils.isNotEmpty(standardDocDTO.getChargeTel())) {
                    standardDocDTO.setChargeTel(DataSecurityHelper.decrypt(standardDocDTO.getChargeTel()));
                }
                if (StringUtils.isNotEmpty(standardDocDTO.getChargeDeptName())) {
                    standardDocDTO.setChargeDeptName(DataSecurityHelper.decrypt(standardDocDTO.getChargeDeptName()));
                }
            });
        }
    }
}

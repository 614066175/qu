package com.hand.hdsp.quality.infra.export;

import java.util.*;
import java.util.stream.Collectors;

import com.hand.hdsp.core.api.dto.CommonGroupDTO;
import com.hand.hdsp.core.domain.repository.CommonGroupRepository;
import com.hand.hdsp.quality.api.dto.ReferenceDataDTO;
import com.hand.hdsp.quality.api.dto.ReferenceDataValueDTO;
import com.hand.hdsp.quality.domain.repository.ReferenceDataRepository;
import com.hand.hdsp.quality.domain.repository.ReferenceDataValueRepository;
import com.hand.hdsp.quality.infra.export.dto.ReferenceDataExportDTO;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import org.hzero.mybatis.helper.DataSecurityHelper;
import org.hzero.starter.driver.core.infra.constant.BaseConstant;


/**
 * @author fuqiang.luo@hand-china.com
 */
@Component
public class ReferenceDataExporter implements Exporter<ReferenceDataDTO, List<ReferenceDataExportDTO>>{

    private final ReferenceDataRepository referenceDataRepository;
    private final ReferenceDataValueRepository referenceDataValueRepository;
    private final CommonGroupRepository commonGroupRepository;

    private final static long DEFAULT_GROUP_ID = -100L;

    public ReferenceDataExporter(ReferenceDataRepository referenceDataRepository,
                                 ReferenceDataValueRepository referenceDataValueRepository,
                                 CommonGroupRepository commonGroupRepository) {
        this.referenceDataRepository = referenceDataRepository;
        this.referenceDataValueRepository = referenceDataValueRepository;
        this.commonGroupRepository = commonGroupRepository;
    }


    @Override
    public List<ReferenceDataExportDTO> export(ReferenceDataDTO referenceDataDTO) {
        List<ReferenceDataDTO> referenceDataDTOList = referenceDataRepository.list(referenceDataDTO);
        Long projectId = referenceDataDTO.getProjectId();
        Long tenantId = referenceDataDTO.getTenantId();
        for (ReferenceDataDTO dataDTO : referenceDataDTOList) {
            decryptReferenceData(dataDTO);
            // 设置参考值
            List<ReferenceDataValueDTO> referenceDataValueDTOList = referenceDataValueRepository.list(ReferenceDataValueDTO
                    .builder()
                            .projectId(projectId)
                            .tenantId(tenantId)
                            .dataId(dataDTO.getDataId())
                    .build());
            dataDTO.setReferenceDataValueDTOList(referenceDataValueDTOList);
        }
        Map<Long, List<ReferenceDataDTO>> referenceDataMap = referenceDataDTOList
                .stream()
                .collect(Collectors.groupingBy(data -> Optional.ofNullable(data.getDataGroupId()).orElse(DEFAULT_GROUP_ID)));
        // 提取出分组
        List<Long> commonGroupIds = referenceDataDTOList
                .stream()
                .map(ReferenceDataDTO::getDataGroupId)
                .distinct()
                .collect(Collectors.toList());
        List<CommonGroupDTO> commonGroupDTOList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(commonGroupIds)) {
            commonGroupDTOList = commonGroupRepository.selectDTOByIds(commonGroupIds);
        }
        List<ReferenceDataExportDTO> referenceDataExportDTOList = commonGroupDTOList
                .stream()
                .map(group -> {
                    ReferenceDataExportDTO referenceDataExportDTO = new ReferenceDataExportDTO();
                    referenceDataExportDTO.setGroupName(group.getGroupName());
                    String groupPath = group.getGroupPath();
                    String parentGroupPath = null;
                    if (StringUtils.contains(groupPath, BaseConstant.Symbol.SLASH)) {
                        parentGroupPath = StringUtils.substringBeforeLast(groupPath, BaseConstant.Symbol.SLASH);
                    }
                    referenceDataExportDTO.setParentGroupPath(parentGroupPath);
                    referenceDataExportDTO.setReferenceDataDTOList(referenceDataMap.get(group.getGroupId()));
                    return referenceDataExportDTO;
                }).collect(Collectors.toList());
        if (referenceDataMap.containsKey(DEFAULT_GROUP_ID)) {
            ReferenceDataExportDTO referenceDataExportDTO = new ReferenceDataExportDTO();
            referenceDataExportDTO.setReferenceDataDTOList(referenceDataMap.get(DEFAULT_GROUP_ID));
            referenceDataExportDTOList.add(referenceDataExportDTO);
        }

        return referenceDataExportDTOList;
    }

    private void decryptReferenceData(ReferenceDataDTO referenceDataDTO) {
        referenceDataDTO.setResponsibleDeptName(decrypt(referenceDataDTO.getResponsibleDeptName()));
        referenceDataDTO.setResponsiblePersonName(decrypt(referenceDataDTO.getResponsiblePersonName()));
        referenceDataDTO.setResponsiblePersonCode(decrypt(referenceDataDTO.getResponsiblePersonCode()));
        referenceDataDTO.setResponsiblePersonTel(decrypt(referenceDataDTO.getResponsiblePersonTel()));
        referenceDataDTO.setResponsiblePersonEmail(decrypt(referenceDataDTO.getResponsiblePersonEmail()));
    }

    private String decrypt(String encrypted) {
        try {
            if (DataSecurityHelper.isTenantOpen() && StringUtils.isNotBlank(encrypted)) {
                return DataSecurityHelper.decrypt(encrypted);
            }
        } catch (Exception e) {
            // ignore
        }
        return encrypted;
    }
}

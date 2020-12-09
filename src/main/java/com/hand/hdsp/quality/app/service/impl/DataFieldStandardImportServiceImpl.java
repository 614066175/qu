package com.hand.hdsp.quality.app.service.impl;

import static com.hand.hdsp.quality.infra.constant.StandardConstant.Status.CREATE;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hand.hdsp.quality.api.dto.DataFieldDTO;
import com.hand.hdsp.quality.api.dto.StandardGroupDTO;
import com.hand.hdsp.quality.domain.entity.StandardGroup;
import com.hand.hdsp.quality.domain.repository.DataFieldRepository;
import com.hand.hdsp.quality.domain.repository.StandardGroupRepository;
import com.hand.hdsp.quality.infra.constant.TemplateCodeConstants;
import io.choerodon.core.oauth.DetailsHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.hzero.boot.imported.app.service.IDoImportService;
import org.hzero.boot.imported.infra.validator.annotation.ImportService;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.util.Sqls;

/**
 * <p>
 * description
 * </p>
 *
 * @author wsl 2020/12/04 15:20
 * @since 1.0
 */
@Slf4j
@ImportService(templateCode = TemplateCodeConstants.TEMPLATE_CODE_DATA_FIELD)
public class DataFieldStandardImportServiceImpl implements IDoImportService {

    private final ObjectMapper objectMapper;

    private final StandardGroupRepository standardGroupRepository;

    private final DataFieldRepository dataFieldRepository;

    public DataFieldStandardImportServiceImpl(ObjectMapper objectMapper, StandardGroupRepository standardGroupRepository, DataFieldRepository dataFieldRepository) {
        this.objectMapper = objectMapper;
        this.standardGroupRepository = standardGroupRepository;
        this.dataFieldRepository = dataFieldRepository;
    }

    @Override
    public Boolean doImport(String data) {
        DataFieldDTO dataFieldDTO;
        try {
            dataFieldDTO = objectMapper.readValue(data, DataFieldDTO.class);
        } catch (IOException e) {
            log.error("data:{}", data);
            log.error("Read Json Error", e);
            // 失败
            return false;
        }
        // 设置租户Id
        Long tenantId = DetailsHelper.getUserDetails().getTenantId();
        if (tenantId != 0) {
            dataFieldDTO.setTenantId(tenantId);
        }
        List<StandardGroupDTO> standardGroupDTOS = standardGroupRepository.selectDTOByCondition(Condition.builder(StandardGroup.class)
                .andWhere(Sqls.custom()
                        .andEqualTo(StandardGroup.FIELD_GROUP_CODE, dataFieldDTO.getGroupCode())
                        .andEqualTo(StandardGroup.FIELD_TENANT_ID, dataFieldDTO.getTenantId()))
                .build());
        if (CollectionUtils.isNotEmpty(standardGroupDTOS)) {
            dataFieldDTO.setGroupId(standardGroupDTOS.get(0).getGroupId());
        }
        dataFieldDTO.setStandardStatus(CREATE);
        //插入数据
        dataFieldRepository.insertDTOSelective(dataFieldDTO);
        return true;
    }
}

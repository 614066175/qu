package com.hand.hdsp.quality.infra.batchimport;

import static com.hand.hdsp.quality.infra.constant.StandardConstant.Status.CREATE;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hand.hdsp.quality.api.dto.DataStandardDTO;
import com.hand.hdsp.quality.api.dto.StandardGroupDTO;
import com.hand.hdsp.quality.domain.entity.StandardGroup;
import com.hand.hdsp.quality.domain.repository.DataStandardRepository;
import com.hand.hdsp.quality.domain.repository.StandardGroupRepository;
import com.hand.hdsp.quality.infra.constant.StandardConstant;
import com.hand.hdsp.quality.infra.constant.TemplateCodeConstants;
import com.hand.hdsp.quality.infra.mapper.DataStandardMapper;
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
 * @author lgl 2020/12/04 15:20
 * @since 1.0
 */
@Slf4j
@ImportService(templateCode = TemplateCodeConstants.TEMPLATE_CODE_DATA_STANDARD)
public class DataStandardImportServiceImpl implements IDoImportService {

    private final ObjectMapper objectMapper;

    private final StandardGroupRepository standardGroupRepository;

    private final DataStandardRepository dataStandardRepository;

    private final DataStandardMapper dataStandardMapper;

    public DataStandardImportServiceImpl(ObjectMapper objectMapper, StandardGroupRepository standardGroupRepository, DataStandardRepository dataStandardRepository, DataStandardMapper dataStandardMapper) {
        this.objectMapper = objectMapper;
        this.standardGroupRepository = standardGroupRepository;
        this.dataStandardRepository = dataStandardRepository;
        this.dataStandardMapper = dataStandardMapper;
    }

    @Override
    public Boolean doImport(String data) {
        DataStandardDTO dataStandardDTO;
        try {
            dataStandardDTO = objectMapper.readValue(data, DataStandardDTO.class);
        } catch (IOException e) {
            log.error("data:{}", data);
            log.error("Read Json Error", e);
            // 失败
            return false;
        }
        // 设置租户Id
        Long tenantId = DetailsHelper.getUserDetails().getTenantId();
        dataStandardDTO.setTenantId(tenantId);
        Long chargeTenantId = dataStandardMapper.selectTenantIdByChargeName(dataStandardDTO.getChargeName());
        if (tenantId.compareTo(chargeTenantId) != 0) {
            return false;
        }
        Long chargeId = dataStandardMapper.selectIdByChargeName(dataStandardDTO.getChargeName());
        Long chargeDeptId = dataStandardMapper.selectIdByChargeDeptName(dataStandardDTO.getChargeDeptName());
        if (Objects.isNull(chargeId) || Objects.isNull(chargeDeptId)) {
            return false;
        }
        dataStandardDTO.setChargeId(chargeId);
        dataStandardDTO.setChargeDeptId(chargeDeptId);
        List<StandardGroupDTO> standardGroupDTOList = standardGroupRepository.selectDTOByCondition(Condition.builder(StandardGroup.class)
                .andWhere(Sqls.custom()
                        .andEqualTo(StandardGroup.FIELD_GROUP_CODE, dataStandardDTO.getGroupCode())
                        .andEqualTo(StandardGroup.FIELD_TENANT_ID, dataStandardDTO.getTenantId()))
                .build());
        if (CollectionUtils.isNotEmpty(standardGroupDTOList)) {
            dataStandardDTO.setGroupId(standardGroupDTOList.get(0).getGroupId());
        } else {
            //创建分组
            StandardGroupDTO standardGroupDTO = StandardGroupDTO.builder()
                    .groupCode(dataStandardDTO.getGroupCode())
                    .groupName(dataStandardDTO.getGroupName())
                    .groupDesc(dataStandardDTO.getGroupDesc())
                    .standardType(StandardConstant.StandardType.DATA)
                    .tenantId(dataStandardDTO.getTenantId())
                    .build();
            standardGroupRepository.insertDTOSelective(standardGroupDTO);
            dataStandardDTO.setGroupId(standardGroupDTO.getGroupId());
        }
        dataStandardDTO.setStandardStatus(CREATE);
        //插入数据
        dataStandardRepository.insertDTOSelective(dataStandardDTO);
        return true;
    }
}

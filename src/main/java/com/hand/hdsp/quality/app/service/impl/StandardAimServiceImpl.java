package com.hand.hdsp.quality.app.service.impl;

import com.hand.hdsp.quality.api.dto.StandardAimDTO;
import com.hand.hdsp.quality.api.dto.StandardAimRelationDTO;
import com.hand.hdsp.quality.app.service.StandardAimService;
import com.hand.hdsp.quality.domain.entity.StandardAim;
import com.hand.hdsp.quality.domain.entity.StandardAimRelation;
import com.hand.hdsp.quality.domain.repository.*;
import com.hand.hdsp.quality.infra.mapper.StandardAimMapper;
import com.hand.hdsp.quality.infra.vo.ColumnVO;
import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.hzero.boot.driver.app.service.DriverSessionService;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.util.Sqls;
import org.hzero.starter.driver.core.infra.meta.Column;
import org.hzero.starter.driver.core.session.DriverSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>标准落标表应用服务默认实现</p>
 *
 * @author guoliangli01@hand-china.com 2020-11-30 10:23:51
 */
@Service
@Slf4j
public class StandardAimServiceImpl implements StandardAimService {

    private final DriverSessionService driverSessionService;
    private final DataStandardRepository dataStandardRepository;
    private final DataFieldRepository dataFieldRepository;
    private final StandardAimRepository standardAimRepository;
    private final StandardAimMapper standardAimMapper;
    private final BatchPlanRepository batchPlanRepository;
    private final StandardAimRelationRepository standardAimRelationRepository;

    public StandardAimServiceImpl(DriverSessionService driverSessionService, DataStandardRepository dataStandardRepository, DataFieldRepository dataFieldRepository, StandardAimRepository standardAimRepository, BatchPlanRepository batchPlanRepository, StandardAimRelationRepository standardAimRelationRepository, StandardAimMapper standardAimMapper) {

        this.driverSessionService = driverSessionService;
        this.dataStandardRepository = dataStandardRepository;
        this.dataFieldRepository = dataFieldRepository;
        this.standardAimRepository = standardAimRepository;
        this.batchPlanRepository = batchPlanRepository;
        this.standardAimRelationRepository = standardAimRelationRepository;
        this.standardAimMapper = standardAimMapper;
    }

    @Override
    public List<ColumnVO> unAimField(StandardAimDTO standardAimDTO) {
        DriverSession driverSession = driverSessionService.getDriverSession(standardAimDTO.getTenantId(), standardAimDTO.getDatasourceCode());
        List<Column> columns = driverSession.columnMetaData(standardAimDTO.getSchemaName(), standardAimDTO.getTableName());
        List<ColumnVO> columnVOS = new ArrayList<>();
        //判断字段是否已经在此标准落标过
        if (CollectionUtils.isNotEmpty(columns)) {
            columns.forEach(column -> {
                ColumnVO columnVO = ColumnVO.builder()
                        .columnName(column.getColumnName())
                        .typeName(column.getTypeName())
                        .columnDesc(column.getRemarks())
                        .selectable(true)
                        .build();
                List<StandardAimDTO> standardAimDTOS = standardAimRepository.selectDTOByCondition(Condition.builder(StandardAim.class)
                        .andWhere(Sqls.custom()
                                .andEqualTo(StandardAim.FIELD_STANDARD_ID, standardAimDTO.getStandardId())
                                .andEqualTo(StandardAim.FIELD_STANDARD_TYPE, standardAimDTO.getStandardType())
                                .andEqualTo(StandardAim.FIELD_DATASOURCE_TYPE, standardAimDTO.getDatasourceType())
                                .andEqualTo(StandardAim.FIELD_DATASOURCE_CODE, standardAimDTO.getDatasourceCode())
                                .andEqualTo(StandardAim.FIELD_SCHEMA_NAME, standardAimDTO.getSchemaName())
                                .andEqualTo(StandardAim.FIELD_TABLE_NAME, standardAimDTO.getTableName())
                                .andEqualTo(StandardAim.FIELD_FIELD_NAME, String.format("%s(%s)", column.getColumnName(), column.getTypeName()))
                                .andEqualTo(StandardAim.FIELD_TENANT_ID, standardAimDTO.getTenantId())
                                .andEqualTo(StandardAim.FIELD_PROJECT_ID, standardAimDTO.getProjectId()))
                        .build());
                if (CollectionUtils.isNotEmpty(standardAimDTOS)) {
                    columnVO.setSelectable(false);
                }
                columnVOS.add(columnVO);
            });
        }
        return columnVOS;
    }

    @Override
    public Page<StandardAimDTO> list(PageRequest pageRequest, StandardAimDTO standardAimDTO) {
        Page<StandardAimDTO> list = PageHelper.doPageAndSort(pageRequest, () -> standardAimMapper.selectByConditionLike(standardAimDTO));
        List<StandardAimDTO> content = list.getContent();
        if (CollectionUtils.isNotEmpty(content)) {
            content.forEach(dto -> {
                dto.setNameLevelPath(String.format("%s/%s/%s/%s",
                        dto.getDatasourceCode(),
                        dto.getSchemaName(),
                        dto.getTableName(),
                        dto.getFieldName()));
            });
        }
        return list;
    }

    @Override
    public void batchDelete(List<StandardAimDTO> standardAimDTOList, Long tenantId, Long projectId) {
        if (CollectionUtils.isNotEmpty(standardAimDTOList)) {
            standardAimRepository.batchDTODeleteByPrimaryKey(standardAimDTOList);
            standardAimDTOList.forEach(standardAimDTO -> {
                List<StandardAimRelationDTO> standardAimRelationDTOS = standardAimRelationRepository.selectDTOByCondition(Condition.builder(StandardAimRelation.class)
                        .andWhere(Sqls.custom()
                                .andEqualTo(StandardAimRelation.FIELD_AIM_ID, standardAimDTO.getAimId())
                                .andEqualTo(StandardAimRelation.FIELD_TENANT_ID, tenantId)
                                .andEqualTo(StandardAimRelation.FIELD_PROJECT_ID, projectId))
                        .build());
                standardAimRelationRepository.batchDTODeleteByPrimaryKey(standardAimRelationDTOS);
            });
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<StandardAimDTO> reverseAim(Long tenantId, List<StandardAimDTO> standardAimDTOList) {
        if (CollectionUtils.isNotEmpty(standardAimDTOList)) {
            List<StandardAimDTO> dtos = standardAimDTOList
                    .stream()
                    .filter(standardAimDTO -> {
                        List<StandardAimDTO> standardAimDTOS = standardAimRepository.selectDTOByCondition(Condition.builder(StandardAim.class)
                                .andWhere(Sqls.custom()
                                        .andEqualTo(StandardAim.FIELD_STANDARD_ID, standardAimDTO.getStandardId())
                                        .andEqualTo(StandardAim.FIELD_STANDARD_TYPE, standardAimDTO.getStandardType())
                                        .andEqualTo(StandardAim.FIELD_DATASOURCE_TYPE, standardAimDTO.getDatasourceType())
                                        .andEqualTo(StandardAim.FIELD_DATASOURCE_CODE, standardAimDTO.getDatasourceCode())
                                        .andEqualTo(StandardAim.FIELD_SCHEMA_NAME, standardAimDTO.getSchemaName())
                                        .andEqualTo(StandardAim.FIELD_TABLE_NAME, standardAimDTO.getTableName())
                                        .andEqualTo(StandardAim.FIELD_FIELD_NAME, standardAimDTO.getFieldName())
                                        .andEqualTo(StandardAim.FIELD_TENANT_ID, standardAimDTO.getTenantId())
                                        .andEqualTo(StandardAim.FIELD_PROJECT_ID, standardAimDTO.getProjectId()))
                                .build());
                        if (CollectionUtils.isNotEmpty(standardAimDTOS)) {
                            log.info("此字段已经落标了，不重复落标");
                            return false;
                        } else {
                            return true;
                        }
                    }).collect(Collectors.toList());
            standardAimRepository.batchInsertDTOSelective(dtos);
        }
        return standardAimDTOList;
    }
}

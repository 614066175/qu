package com.hand.hdsp.quality.app.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.hand.hdsp.quality.api.dto.StandardAimDTO;
import com.hand.hdsp.quality.app.service.StandardAimService;
import com.hand.hdsp.quality.domain.entity.BatchPlan;
import com.hand.hdsp.quality.domain.entity.StandardAim;
import com.hand.hdsp.quality.domain.repository.BatchPlanRepository;
import com.hand.hdsp.quality.domain.repository.DataFieldRepository;
import com.hand.hdsp.quality.domain.repository.DataStandardRepository;
import com.hand.hdsp.quality.domain.repository.StandardAimRepository;
import com.hand.hdsp.quality.infra.vo.ColumnVO;
import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.apache.commons.collections4.CollectionUtils;
import org.hzero.boot.driver.app.service.DriverSessionService;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.util.Sqls;
import org.hzero.starter.driver.core.infra.meta.Column;
import org.hzero.starter.driver.core.session.DriverSession;
import org.springframework.stereotype.Service;

/**
 * <p>标准落标表应用服务默认实现</p>
 *
 * @author guoliangli01@hand-china.com 2020-11-30 10:23:51
 */
@Service
public class StandardAimServiceImpl implements StandardAimService {

    private final DriverSessionService driverSessionService;
    private final DataStandardRepository dataStandardRepository;
    private final DataFieldRepository dataFieldRepository;
    private final StandardAimRepository standardAimRepository;
    private final BatchPlanRepository batchPlanRepository;

    public StandardAimServiceImpl(DriverSessionService driverSessionService, DataStandardRepository dataStandardRepository, DataFieldRepository dataFieldRepository, StandardAimRepository standardAimRepository, BatchPlanRepository batchPlanRepository) {

        this.driverSessionService = driverSessionService;
        this.dataStandardRepository = dataStandardRepository;
        this.dataFieldRepository = dataFieldRepository;
        this.standardAimRepository = standardAimRepository;
        this.batchPlanRepository = batchPlanRepository;
    }

    @Override
    public List<ColumnVO> unAimField(StandardAimDTO standardAimDTO) {
        DriverSession driverSession = driverSessionService.getDriverSession(standardAimDTO.getTenantId(), standardAimDTO.getDatasourceCode());
        List<Column> columns = driverSession.columnMetaData(standardAimDTO.getSchemaName(), standardAimDTO.getTableName());
        List<ColumnVO> columnVOS=new ArrayList<>();
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
                                .andEqualTo(StandardAim.FIELD_FIELD_NAME, String.format("%s(%s)",column.getColumnName(),column.getTypeName()))
                                .andEqualTo(StandardAim.FIELD_TENANT_ID, standardAimDTO.getTenantId()))
                        .build());
                if(CollectionUtils.isNotEmpty(standardAimDTOS)){
                    columnVO.setSelectable(false);
                }
                columnVOS.add(columnVO);
            });
        }
        return columnVOS;
    }

    @Override
    public Page<StandardAimDTO> list(PageRequest pageRequest, StandardAimDTO standardAimDTO) {
        Page<StandardAimDTO> list = standardAimRepository.pageAndSortDTO(pageRequest, standardAimDTO);
        List<StandardAimDTO> content = list.getContent();
        if (CollectionUtils.isNotEmpty(content)) {
            content.forEach(dto -> {
                dto.setNameLevelPath(String.format("%s/%s/%s/%s",
                        dto.getDatasourceCode(),
                        dto.getSchemaName(),
                        dto.getTableName(),
                        dto.getFieldName()));
                if(Objects.nonNull(dto.getPlanId())){
                    BatchPlan batchPlan = batchPlanRepository.selectByPrimaryKey(dto.getPlanId());
                    dto.setPlanName(batchPlan.getPlanName());
                }
            });
        }
        return list;
    }
}

package org.xdsp.quality.app.service.impl;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.domain.AuditDomain;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.xdsp.quality.api.dto.BatchPlanTableConDTO;
import org.xdsp.quality.api.dto.BatchPlanTableDTO;
import org.xdsp.quality.api.dto.BatchPlanTableLineDTO;
import org.xdsp.quality.app.service.BatchPlanTableService;
import org.xdsp.quality.domain.entity.BatchPlanTableCon;
import org.xdsp.quality.domain.entity.BatchPlanTableLine;
import org.xdsp.quality.domain.repository.BatchPlanTableConRepository;
import org.xdsp.quality.domain.repository.BatchPlanTableLineRepository;
import org.xdsp.quality.domain.repository.BatchPlanTableRepository;
import org.xdsp.quality.infra.dataobject.BatchPlanTableDO;
import org.xdsp.quality.infra.mapper.BatchPlanTableMapper;
import org.xdsp.quality.infra.util.JsonUtils;

import java.util.List;

/**
 * <p>批数据方案-表级规则表应用服务默认实现</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:52
 */
@Service
public class BatchPlanTableServiceImpl implements BatchPlanTableService {

    private final BatchPlanTableMapper batchPlanTableMapper;
    private final BatchPlanTableRepository batchPlanTableRepository;
    private final BatchPlanTableLineRepository batchPlanTableLineRepository;
    private final BatchPlanTableConRepository batchPlanTableConRepository;


    public BatchPlanTableServiceImpl(BatchPlanTableMapper batchPlanTableMapper, BatchPlanTableRepository batchPlanTableRepository,
                                     BatchPlanTableLineRepository batchPlanTableLineRepository,
                                     BatchPlanTableConRepository batchPlanTableConRepository) {
        this.batchPlanTableMapper = batchPlanTableMapper;
        this.batchPlanTableRepository = batchPlanTableRepository;
        this.batchPlanTableLineRepository = batchPlanTableLineRepository;
        this.batchPlanTableConRepository = batchPlanTableConRepository;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int delete(BatchPlanTableDTO batchPlanTableDTO) {
        List<BatchPlanTableLineDTO> batchPlanTableLineDTOList =
                batchPlanTableLineRepository.selectDTO(
                        BatchPlanTableLine.FIELD_PLAN_RULE_ID, batchPlanTableDTO.getPlanRuleId());
        if (CollectionUtils.isNotEmpty(batchPlanTableLineDTOList)) {
            batchPlanTableLineRepository.deleteByPlanRuleId(batchPlanTableDTO.getPlanRuleId());
        }
        return batchPlanTableRepository.deleteByPrimaryKey(batchPlanTableDTO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insert(BatchPlanTableDTO batchPlanTableDTO) {
        Long tenantId = batchPlanTableDTO.getTenantId();
        Long projectId = batchPlanTableDTO.getProjectId();
        batchPlanTableRepository.insertDTOSelective(batchPlanTableDTO);
        if (batchPlanTableDTO.getBatchPlanTableLineDTOList() != null) {

            for (BatchPlanTableLineDTO batchPlanTableLineDTO : batchPlanTableDTO.getBatchPlanTableLineDTOList()) {
                batchPlanTableLineDTO.setPlanRuleId(batchPlanTableDTO.getPlanRuleId());
                batchPlanTableLineDTO.setTenantId(tenantId);
                batchPlanTableLineDTO.setProjectId(projectId);
                batchPlanTableLineRepository.insertDTOSelective(batchPlanTableLineDTO);

                if (CollectionUtils.isNotEmpty(batchPlanTableLineDTO.getBatchPlanTableConDTOList())) {
                    for (BatchPlanTableConDTO batchPlanTableConDTO : batchPlanTableLineDTO.getBatchPlanTableConDTOList()) {
                        batchPlanTableConDTO.setPlanLineId(batchPlanTableLineDTO.getPlanLineId());
                        batchPlanTableConDTO.setTenantId(tenantId);
                        batchPlanTableConDTO.setProjectId(projectId);
                        //todo 范围重叠判断
                        batchPlanTableConDTO.setWarningLevel(JsonUtils.object2Json(batchPlanTableConDTO.getWarningLevelList()));
                        batchPlanTableConRepository.insertDTOSelective(batchPlanTableConDTO);
                    }
                }
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(BatchPlanTableDTO batchPlanTableDTO) {
        Long tenantId = batchPlanTableDTO.getTenantId();
        Long projectId = batchPlanTableDTO.getProjectId();
        batchPlanTableRepository.updateDTOAllColumnWhereTenant(batchPlanTableDTO, tenantId);
        if (batchPlanTableDTO.getBatchPlanTableLineDTOList() != null) {
            for (BatchPlanTableLineDTO batchPlanTableLineDTO : batchPlanTableDTO.getBatchPlanTableLineDTOList()) {
                if (AuditDomain.RecordStatus.update.equals(batchPlanTableLineDTO.get_status())) {
                    batchPlanTableLineDTO.setProjectId(projectId);
                    batchPlanTableLineRepository.updateDTOAllColumnWhereTenant(batchPlanTableLineDTO, tenantId);
                } else if (AuditDomain.RecordStatus.create.equals(batchPlanTableLineDTO.get_status())) {
                    batchPlanTableLineDTO.setProjectId(projectId);
                    batchPlanTableLineDTO.setPlanRuleId(batchPlanTableDTO.getPlanRuleId());
                    batchPlanTableLineDTO.setTenantId(tenantId);
                    batchPlanTableLineRepository.insertDTOSelective(batchPlanTableLineDTO);
                } else if (AuditDomain.RecordStatus.delete.equals(batchPlanTableLineDTO.get_status())) {
                    batchPlanTableLineRepository.deleteByPrimaryKey(batchPlanTableLineDTO);
                }

                if (CollectionUtils.isNotEmpty(batchPlanTableLineDTO.getBatchPlanTableConDTOList())) {
                    for (BatchPlanTableConDTO batchPlanTableConDTO : batchPlanTableLineDTO.getBatchPlanTableConDTOList()) {
                        if (AuditDomain.RecordStatus.update.equals(batchPlanTableConDTO.get_status())) {
                            //todo 范围重叠判断
                            batchPlanTableConDTO.setProjectId(projectId);
                            batchPlanTableConDTO.setWarningLevel(JsonUtils.object2Json(batchPlanTableConDTO.getWarningLevelList()));
                            batchPlanTableConRepository.updateDTOAllColumnWhereTenant(batchPlanTableConDTO, tenantId);
                        } else if (AuditDomain.RecordStatus.create.equals(batchPlanTableConDTO.get_status())) {
                            batchPlanTableConDTO.setProjectId(projectId);
                            batchPlanTableConDTO.setPlanLineId(batchPlanTableLineDTO.getPlanLineId());
                            batchPlanTableConDTO.setTenantId(tenantId);
                            //todo 范围重叠判断
                            batchPlanTableConDTO.setWarningLevel(JsonUtils.object2Json(batchPlanTableConDTO.getWarningLevelList()));
                            batchPlanTableConRepository.insertDTOSelective(batchPlanTableConDTO);
                        } else if (AuditDomain.RecordStatus.delete.equals(batchPlanTableConDTO.get_status())) {
                            batchPlanTableConRepository.deleteByPrimaryKey(batchPlanTableConDTO);
                        }
                    }
                }
            }
        }
    }

    @Override
    public List<BatchPlanTableDO> list(BatchPlanTableDO batchPlanTableDO) {
        return batchPlanTableRepository.list(batchPlanTableDO);
    }

    @Override
    public BatchPlanTableDTO detail(Long planRuleId) {
        BatchPlanTableDTO batchPlanTableDTO = batchPlanTableRepository.selectDTOByPrimaryKey(planRuleId);
        List<BatchPlanTableLineDTO> batchPlanTableLineDTOList = batchPlanTableLineRepository.selectDTO(BatchPlanTableLine.FIELD_PLAN_RULE_ID, planRuleId);
        for (BatchPlanTableLineDTO batchPlanTableLineDTO : batchPlanTableLineDTOList) {
            List<BatchPlanTableConDTO> conDTOList = batchPlanTableConRepository.selectDTO(BatchPlanTableCon.FIELD_PLAN_LINE_ID, batchPlanTableLineDTO.getPlanLineId());
            conDTOList.forEach(dto -> dto.setWarningLevelList(JsonUtils.json2WarningLevel(dto.getWarningLevel())));
            batchPlanTableLineDTO.setBatchPlanTableConDTOList(conDTOList);
        }
        batchPlanTableDTO.setBatchPlanTableLineDTOList(batchPlanTableLineDTOList);
        return batchPlanTableDTO;
    }

    @Override
    public Page<BatchPlanTableDTO> selectDetailList(PageRequest pageRequest, BatchPlanTableDTO batchPlanTableDTO) {
        Page<BatchPlanTableDTO> pages = PageHelper.doPage(pageRequest, () -> batchPlanTableRepository.selectDetailList(batchPlanTableDTO));
        for (BatchPlanTableDTO dto : pages.getContent()) {
            dto.setWarningLevelList(JsonUtils.json2WarningLevel(dto.getWarningLevel()));
        }
        return pages;
    }

    @Override
    public Page<BatchPlanTableDTO> selectTableList(PageRequest pageRequest, BatchPlanTableDTO batchPlanTableDTO) {
        Page<BatchPlanTableDTO> pages = PageHelper.doPage(pageRequest, () -> batchPlanTableRepository.selectTableList(batchPlanTableDTO));
        return pages;
    }


}

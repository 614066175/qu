package com.hand.hdsp.quality.app.service.impl;

import java.util.List;

import com.hand.hdsp.quality.api.dto.StandardApprovalDTO;
import com.hand.hdsp.quality.app.service.StandardApprovalService;
import com.hand.hdsp.quality.domain.entity.StandardApproval;
import com.hand.hdsp.quality.domain.repository.StandardApprovalRepository;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.util.Sqls;

/**
 * <p>各种标准审批表应用服务默认实现</p>
 *
 * @author fuqiang.luo@hand-china.com 2022-08-31 10:30:34
 */
@Service
public class StandardApprovalServiceImpl implements StandardApprovalService {
    private final StandardApprovalRepository standardApprovalRepository;

    public StandardApprovalServiceImpl(StandardApprovalRepository standardApprovalRepository) {
        this.standardApprovalRepository = standardApprovalRepository;
    }

    @Override
    public StandardApprovalDTO createOrUpdate(StandardApprovalDTO standardApprovalDTO) {
        Condition condition = Condition
                .builder(StandardApproval.class)
                .andWhere(Sqls.custom()
                        .andEqualTo(StandardApproval.FIELD_STANDARD_ID, standardApprovalDTO.getStandardId())
                        .andEqualTo(StandardApproval.FIELD_STANDARD_TYPE, standardApprovalDTO.getStandardType())
                        .andEqualTo(StandardApproval.FIELD_APPLICANT_ID, standardApprovalDTO.getApplicantId())
                        .andEqualTo(StandardApproval.FIELD_APPLY_TYPE, standardApprovalDTO.getApplyType()))
                .build();
        List<StandardApprovalDTO> standardApprovalDTOList = standardApprovalRepository.selectDTOByCondition(condition);
        if (CollectionUtils.isNotEmpty(standardApprovalDTOList)) {
            StandardApprovalDTO approvalDTO = standardApprovalDTOList.get(0);
            approvalDTO.setApplyTime(standardApprovalDTO.getApplyTime());
            approvalDTO.setApproverId(standardApprovalDTO.getApproverId());
            approvalDTO.setApprovalStatus(standardApprovalDTO.getApprovalStatus());
            approvalDTO.setInstanceId(standardApprovalDTO.getInstanceId());
            approvalDTO.setTenantId(standardApprovalDTO.getTenantId());
            standardApprovalRepository.updateByDTOPrimaryKeySelective(approvalDTO);
            return approvalDTO;
        } else {
            standardApprovalRepository.insertDTOSelective(standardApprovalDTO);
            return standardApprovalDTO;
        }
    }

    @Override
    public void delete(StandardApprovalDTO standardApprovalDTO) {
        standardApprovalRepository.deleteDTO(standardApprovalDTO);
    }
}

package com.hand.hdsp.quality.app.service.impl;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import com.hand.hdsp.quality.api.dto.*;
import com.hand.hdsp.quality.app.service.NameStandardService;
import com.hand.hdsp.quality.domain.entity.NameAim;
import com.hand.hdsp.quality.domain.entity.NameExecHistory;
import com.hand.hdsp.quality.domain.entity.NameStandard;
import com.hand.hdsp.quality.domain.repository.*;
import com.hand.hdsp.quality.infra.converter.NameStandardConverter;
import io.choerodon.core.exception.CommonException;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.util.Sqls;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>命名标准表应用服务默认实现</p>
 *
 * @author 张鹏 2020-11-27 16:35:40
 */
@Service
public class NameStandardServiceImpl implements NameStandardService {

    private final NameStandardRepository nameStandardRepository;
    private final NameAimRepository nameAimRepository;
    private final NameAimIncludeRepository nameAimIncludeRepository;
    private final NameAimExcludeRepository nameAimExcludeRepository;
    private final NameExecHisDetailRepository nameExecHisDetailRepository;
    private final NameExecHistoryRepository nameExecHistoryRepository;
    private final NameStandardConverter nameStandardConverter;

    public NameStandardServiceImpl(NameStandardRepository nameStandardRepository,
                                   NameAimRepository nameAimRepository,
                                   NameAimIncludeRepository nameAimIncludeRepository,
                                   NameAimExcludeRepository nameAimExcludeRepository,
                                   NameExecHisDetailRepository nameExecHisDetailRepository,
                                   NameExecHistoryRepository nameExecHistoryRepository,
                                   NameStandardConverter nameStandardConverter) {
        this.nameStandardRepository = nameStandardRepository;
        this.nameAimRepository = nameAimRepository;
        this.nameAimIncludeRepository = nameAimIncludeRepository;
        this.nameAimExcludeRepository = nameAimExcludeRepository;
        this.nameExecHisDetailRepository = nameExecHisDetailRepository;
        this.nameExecHistoryRepository = nameExecHistoryRepository;
        this.nameStandardConverter = nameStandardConverter;
    }


    @Transactional(rollbackFor = Exception.class)
    @Override
    public void remove(NameStandardDTO nameStandardDTO) {
        if (nameStandardRepository.selectCount(nameStandardConverter.dtoToEntity(nameStandardDTO))<=0){
            throw new CommonException("hdsp.xsta.err.not_exist");
        }
        List<NameExecHistoryDTO> historyList = nameExecHistoryRepository.selectDTO(NameExecHistory.FIELD_STANDARD_ID,
                nameStandardDTO.getStandardId());
        //删除执行历史
        if (CollectionUtils.isNotEmpty(historyList)){
            List<NameExecHisDetailDTO> detailList= historyList.stream()
                    .map(x->NameExecHisDetailDTO.builder()
                            .historyId(x.getHistoryId())
                            .tenantId(nameStandardDTO.getTenantId())
                            .build())
                    .collect(Collectors.toList());
            nameExecHisDetailRepository.batchDTODelete(detailList);
            nameExecHistoryRepository.batchDTODelete(historyList);
        }
        List<NameAimDTO> aimDtoList = nameAimRepository.selectDTO(NameAim.FIELD_STANDARD_ID,
                nameStandardDTO.getStandardId());
        //删除落标
        if (CollectionUtils.isNotEmpty(aimDtoList)){
            //删除落标排除项
            List<NameAimExcludeDTO> excludeDtoList = aimDtoList.stream()
                    .map(x->NameAimExcludeDTO.builder()
                            .aimId(x.getAimId())
                            .tenantId(nameStandardDTO.getTenantId())
                            .build())
                    .collect(Collectors.toList());
            nameAimExcludeRepository.batchDTODelete(excludeDtoList);

            //删除落标包含项
            List<NameAimIncludeDTO> includeDtoList = aimDtoList.stream()
                    .map(x->NameAimIncludeDTO.builder()
                            .aimId(x.getAimId())
                            .tenantId(nameStandardDTO.getTenantId())
                            .build())
                    .collect(Collectors.toList());
            nameAimIncludeRepository.batchDTODelete(includeDtoList);
            //删除落标
            nameAimRepository.batchDTODelete(aimDtoList);
        }
        //删除标准
        nameStandardRepository.deleteDTO(nameStandardDTO);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void bitchRemove(List<NameStandardDTO> standardDtoList) {
        if (CollectionUtils.isEmpty(standardDtoList)){
            throw new CommonException("hdsp.xsta.err.is_empty");
        }
        standardDtoList.forEach(this::remove);
    }

    @Override
    public NameStandardDTO update(NameStandardDTO nameStandardDTO) {
        NameStandardDTO exist = Optional.ofNullable(nameStandardRepository
                .selectDTOByPrimaryKey(nameStandardDTO.getStandardId()))
                .orElseThrow(()->new CommonException("hdsp.xsta.err.not_exist"));
        if (StringUtils.isNotEmpty(nameStandardDTO.getStandardCode())&&!exist.getStandardCode().equals(nameStandardDTO.getStandardCode())){
            throw new CommonException("hdsp.xsta.err.cannot_update_field", NameStandard.FIELD_STANDARD_CODE);
        }
        nameStandardRepository.updateByDTOPrimaryKeySelective(nameStandardDTO);
        return nameStandardDTO;
    }
}

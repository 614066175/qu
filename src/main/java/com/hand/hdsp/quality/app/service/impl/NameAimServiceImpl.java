package com.hand.hdsp.quality.app.service.impl;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.hand.hdsp.quality.api.dto.NameAimDTO;
import com.hand.hdsp.quality.api.dto.NameAimExcludeDTO;
import com.hand.hdsp.quality.api.dto.NameAimIncludeDTO;
import com.hand.hdsp.quality.app.service.NameAimService;
import com.hand.hdsp.quality.domain.entity.NameAim;
import com.hand.hdsp.quality.domain.entity.NameAimExclude;
import com.hand.hdsp.quality.domain.entity.NameAimInclude;
import com.hand.hdsp.quality.domain.repository.NameAimExcludeRepository;
import com.hand.hdsp.quality.domain.repository.NameAimIncludeRepository;
import com.hand.hdsp.quality.domain.repository.NameAimRepository;
import com.hand.hdsp.quality.infra.constant.ErrorCode;
import io.choerodon.core.exception.CommonException;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>命名落标表应用服务默认实现</p>
 *
 * @author 张鹏 2020-11-27 16:35:40
 */
@Service
public class NameAimServiceImpl implements NameAimService {

    private final NameAimRepository nameAimRepository;
    private final NameAimIncludeRepository nameAimIncludeRepository;
    private final NameAimExcludeRepository nameAimExcludeRepository;

    public NameAimServiceImpl(NameAimRepository nameAimRepository,
                              NameAimIncludeRepository nameAimIncludeRepository,
                              NameAimExcludeRepository nameAimExcludeRepository) {
        this.nameAimRepository = nameAimRepository;
        this.nameAimIncludeRepository = nameAimIncludeRepository;
        this.nameAimExcludeRepository = nameAimExcludeRepository;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public List<NameAimDTO> batchCreate(List<NameAimDTO> nameAimDtoList) {
        if(CollectionUtils.isEmpty(nameAimDtoList)){
            throw new CommonException(ErrorCode.NAME_STANDARD_PARAMS_EMPTY);
        }
        //批量插入主键无法回写，这里选择单个插入
        nameAimDtoList.forEach(nameAimRepository::insertDTOSelective);
        nameAimDtoList.forEach(x->{
            if(CollectionUtils.isNotEmpty(x.getNameAimIncludeDTOList())){
                x.getNameAimIncludeDTOList().forEach(o->{
                    o.setAimId(x.getAimId());
                    o.setTenantId(x.getTenantId());
                });
            }
            if(CollectionUtils.isNotEmpty(x.getNameAimExcludeDTOList())){
                x.getNameAimExcludeDTOList().forEach(o->{
                    o.setTenantId(x.getTenantId());
                    o.setAimId(x.getAimId());
                });
            }
        });
        List<NameAimIncludeDTO> nameAimIncludeDTOList = nameAimDtoList.stream().map(NameAimDTO::getNameAimIncludeDTOList)
                .flatMap(Collection::stream).collect(Collectors.toList());
        List<NameAimExcludeDTO> nameAimExcludeDTOList = nameAimDtoList.stream().map(NameAimDTO::getNameAimExcludeDTOList)
                .flatMap(Collection::stream).collect(Collectors.toList());
        nameAimIncludeRepository.batchInsertDTOSelective(nameAimIncludeDTOList);
        nameAimExcludeRepository.batchInsertDTOSelective(nameAimExcludeDTOList);
        return nameAimDtoList;
    }

    @Override
    public void remove(Long primaryKey) {
        NameAimDTO nameAimDTO = nameAimRepository.selectDTOByPrimaryKey(primaryKey);
        if (Objects.isNull(nameAimDTO)) {
            throw new CommonException(ErrorCode.NAME_STANDARD_NOT_EXIST);
        }
        nameAimIncludeRepository.deleteDTO(NameAimIncludeDTO.builder().aimId(nameAimDTO.getAimId()).build());
        nameAimExcludeRepository.deleteDTO(NameAimExcludeDTO.builder().aimId(nameAimDTO.getAimId()).build());
        nameAimRepository.deleteByPrimaryKey(primaryKey);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public NameAimDTO update(NameAimDTO nameAimDTO) {
        if(Objects.isNull(nameAimDTO)){
            throw new CommonException(ErrorCode.NAME_STANDARD_NOT_EXIST);
        }
       nameAimRepository.updateDTOOptional(nameAimDTO,
               NameAim.FIELD_ENABLED_FLAG,
               NameAim.FIELD_EXCLUDE_DESC,
               NameAim.FIELD_EXCLUDE_RULE);
        List<Long> existIncludeIdList = nameAimIncludeRepository
                .selectDTO(NameAimInclude.FIELD_AIM_ID,nameAimDTO.getAimId())
                .stream().map(NameAimIncludeDTO::getIncludeId)
                .collect(Collectors.toList());
        List<NameAimIncludeDTO> test = nameAimDTO.getNameAimIncludeDTOList().stream()
                .filter(x->Objects.isNull(x.getIncludeId()))
                .map(x->NameAimIncludeDTO.builder()
                        .aimId(nameAimDTO.getAimId())
                        .schemaName(x.getSchemaName())
                        .tenantId(nameAimDTO.getTenantId())
                        .build())
                .collect(Collectors.toList());
        nameAimIncludeRepository.batchInsertDTOSelective(test);
        List<Long> newIncludeIdList = nameAimDTO.getNameAimIncludeDTOList().stream()
                .filter(x->!Objects.isNull(x.getIncludeId()))
                .map(NameAimIncludeDTO::getIncludeId)
                .collect(Collectors.toList());
        existIncludeIdList.forEach(x->{
            if(!newIncludeIdList.contains(x)){
                nameAimIncludeRepository.deleteByPrimaryKey(x);
            }
        });
        List<Long> existExcludeIdList = nameAimExcludeRepository
                .selectDTO(NameAimExclude.FIELD_AIM_ID,nameAimDTO.getAimId()).
                        stream().map(NameAimExcludeDTO::getExcludeId)
                .collect(Collectors.toList());
        //修改排除数据，没有ID视为新增数据
        nameAimExcludeRepository.batchInsertDTOSelective(nameAimDTO.getNameAimExcludeDTOList().stream()
                .filter(x->Objects.isNull(x.getExcludeId()))
                .map(x->NameAimExcludeDTO.builder()
                        .aimId(nameAimDTO.getAimId())
                        .schemaName(x.getSchemaName())
                        .tableName(x.getTableName())
                        .tenantId(nameAimDTO.getTenantId())
                        .build()).collect(Collectors.toList()));
        //修改排除数据,数据库中数据若不在提交的数据列表中，视为删除的数据

        List<Long> newExcludeIdList = nameAimDTO.getNameAimExcludeDTOList().stream()
                .filter(x->!Objects.isNull(x.getExcludeId()))
                .map(NameAimExcludeDTO::getExcludeId)
                .collect(Collectors.toList());
        existExcludeIdList.forEach(x->{
            if(!newExcludeIdList.contains(x)){
                nameAimExcludeRepository.deleteByPrimaryKey(x);
            }
        });
        return nameAimDTO;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public List<NameAimDTO> bitchUpdate(List<NameAimDTO> nameAimDTOList) {
        if(CollectionUtils.isEmpty(nameAimDTOList)){
            throw new CommonException(ErrorCode.NAME_STANDARD_PARAMS_EMPTY);
        }
        List<NameAimDTO> existNameAimDTOList = nameAimDTOList.stream()
                .filter(x->!Objects.isNull(x.getAimId()))
                .collect(Collectors.toList());
        List<NameAimDTO> newNameAimDTOList = nameAimDTOList.stream()
                .filter(x->Objects.isNull(x.getAimId()))
                .collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(newNameAimDTOList)){
            this.batchCreate(newNameAimDTOList);
        }
        existNameAimDTOList.forEach(this::update);
        return nameAimDTOList;
    }

}

package com.hand.hdsp.quality.app.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.hand.hdsp.quality.api.dto.*;
import com.hand.hdsp.quality.app.service.NameStandardService;
import com.hand.hdsp.quality.domain.entity.NameAim;
import com.hand.hdsp.quality.domain.entity.NameExecHistory;
import com.hand.hdsp.quality.domain.entity.NameStandard;
import com.hand.hdsp.quality.domain.repository.*;
import com.hand.hdsp.quality.infra.constant.ErrorCode;
import com.hand.hdsp.quality.infra.converter.NameStandardConverter;
import com.hand.hdsp.quality.infra.vo.NameStandardDatasourceVO;
import com.hand.hdsp.quality.infra.vo.NameStandardTableVO;
import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.oauth.DetailsHelper;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hzero.boot.driver.app.service.DriverSessionService;
import org.hzero.boot.platform.lov.adapter.LovAdapter;
import org.hzero.boot.platform.lov.dto.LovValueDTO;
import org.hzero.export.vo.ExportParam;
import org.hzero.starter.driver.core.session.DriverSession;
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
    private final DriverSessionService driverSessionService;
    private final LovAdapter lovAdapter;
    private static final String ERROR_MESSAGE = "table name cannot match rule: %s";


    public NameStandardServiceImpl(NameStandardRepository nameStandardRepository,
                                   NameAimRepository nameAimRepository,
                                   NameAimIncludeRepository nameAimIncludeRepository,
                                   NameAimExcludeRepository nameAimExcludeRepository,
                                   NameExecHisDetailRepository nameExecHisDetailRepository,
                                   NameExecHistoryRepository nameExecHistoryRepository,
                                   NameStandardConverter nameStandardConverter,
                                   DriverSessionService driverSessionService,
                                   LovAdapter lovAdapter) {
        this.nameStandardRepository = nameStandardRepository;
        this.nameAimRepository = nameAimRepository;
        this.nameAimIncludeRepository = nameAimIncludeRepository;
        this.nameAimExcludeRepository = nameAimExcludeRepository;
        this.nameExecHisDetailRepository = nameExecHisDetailRepository;
        this.nameExecHistoryRepository = nameExecHistoryRepository;
        this.nameStandardConverter = nameStandardConverter;
        this.driverSessionService = driverSessionService;
        this.lovAdapter = lovAdapter;
    }


    @Transactional(rollbackFor = Exception.class)
    @Override
    public void remove(NameStandardDTO nameStandardDTO) {
        if (nameStandardRepository.selectCount(nameStandardConverter.dtoToEntity(nameStandardDTO))<=0){
            throw new CommonException(ErrorCode.NAME_STANDARD_NOT_EXIST);
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
            throw new CommonException(ErrorCode.NAME_STANDARD_PARAMS_EMPTY);
        }
        standardDtoList.forEach(this::remove);
    }

    @Override
    public NameStandardDTO update(NameStandardDTO nameStandardDTO) {
        NameStandardDTO exist = Optional.ofNullable(nameStandardRepository
                .selectDTOByPrimaryKey(nameStandardDTO.getStandardId()))
                .orElseThrow(()->new CommonException(ErrorCode.NAME_STANDARD_NOT_EXIST));
        if (StringUtils.isNotEmpty(nameStandardDTO.getStandardCode())&&!exist.getStandardCode().equals(nameStandardDTO.getStandardCode())){
            throw new CommonException("hdsp.xsta.err.cannot_update_field", NameStandard.FIELD_STANDARD_CODE);
        }
        nameStandardRepository.updateByDTOPrimaryKeySelective(nameStandardDTO);
        return nameStandardDTO;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void executeStandard(Long standardId) {
        NameStandardDTO nameStandardDTO = Optional
                .ofNullable(nameStandardRepository.selectDTOByPrimaryKey(standardId))
                .orElseThrow(() -> new CommonException(ErrorCode.NAME_STANDARD_NOT_EXIST));
        List<LovValueDTO> lovValueDTOList = lovAdapter.queryLovValue("HDSP.XSTA.EXEC_STATUS",
                DetailsHelper.getUserDetails().getTenantId());
        List<NameAimDTO> nameAimDTOList = nameAimRepository.list(nameStandardDTO.getStandardId());
        NameExecHistoryDTO nameExecHistoryDTO = new NameExecHistoryDTO();
        nameExecHistoryDTO.setExecStartTime(new Date());
        nameExecHistoryDTO.setStandardId(nameStandardDTO.getStandardId());
        try {
            nameStandardDTO.setLatestCheckedStatus(lovValueDTOList.get(1).getValue());
            nameStandardDTO.setLatestAbnormalNum(-1L);
            nameStandardRepository.updateDTOOptional(nameStandardDTO,NameStandard.FIELD_LATEST_CHECKED_STATUS,
                    NameStandard.FIELD_LATEST_ABNORMAL_NUM);
            List<NameExecHisDetailDTO> nameExecHisDetailDTOList = this.getAimTable(nameAimDTOList);
            nameExecHistoryDTO.setCheckedNum((long) nameExecHisDetailDTOList.size());
            List<NameExecHisDetailDTO> abnormalList = new ArrayList<>();
            nameExecHisDetailDTOList.forEach(x -> {
                if (!Pattern.matches(nameStandardDTO.getStandardRule(), x.getTableName())) {
                    x.setErrorMessage(String.format(ERROR_MESSAGE,nameStandardDTO.getStandardRule()));
                    abnormalList.add(x);
                }
            });
            nameExecHistoryDTO.setAbnormalNum((long) abnormalList.size());
            nameExecHistoryDTO.setExecEndTime(new Date());
            nameExecHistoryDTO.setExecStatus(lovValueDTOList.get(2).getValue());
            nameExecHistoryRepository.insertDTOSelective(nameExecHistoryDTO);
            abnormalList.forEach(x->x.setHistoryId(nameExecHistoryDTO.getHistoryId()));
            nameExecHisDetailRepository.batchInsertDTOSelective(abnormalList);

            nameStandardDTO.setLatestCheckedStatus(lovValueDTOList.get(2).getValue());
            nameStandardDTO.setLatestAbnormalNum((long)abnormalList.size());
            nameStandardRepository.updateDTOOptional(nameStandardDTO,NameStandard.FIELD_LATEST_CHECKED_STATUS,
                    NameStandard.FIELD_LATEST_ABNORMAL_NUM);

        }catch (Exception e){
            nameStandardDTO.setLatestCheckedStatus(lovValueDTOList.get(1).getValue());
            nameStandardRepository.updateDTOOptional(nameStandardDTO,NameStandard.FIELD_LATEST_CHECKED_STATUS);
            nameExecHistoryDTO.setExecStatus(lovValueDTOList.get(3).getValue());
            nameExecHistoryRepository.insertDTOSelective(nameExecHistoryDTO);
            e.printStackTrace();
        }
    }

    @Override
    public void batchExecuteStandard(List<Long> standardIdList) {
        if (CollectionUtils.isEmpty(standardIdList)){
            throw new CommonException(ErrorCode.NAME_STANDARD_PARAMS_EMPTY);
        }
        standardIdList.forEach(this::executeStandard);
    }

    @Override
    public Page<NameStandardDTO> export(NameStandardDTO dto, ExportParam exportParam, PageRequest pageRequest) {
        return PageHelper.doPageAndSort(pageRequest,()->nameStandardRepository.list(dto));
    }

    @Override
    public List<NameStandardTableVO> getTables(NameStandardDatasourceVO nameStandardDatasourceVO) {
        if(StringUtils.isEmpty(nameStandardDatasourceVO.getDatasource())
                ||CollectionUtils.isEmpty(nameStandardDatasourceVO.getSchemas())){
            throw new CommonException(ErrorCode.NAME_STANDARD_PARAMS_EMPTY);
        }
        List<NameStandardTableVO> nameStandardTableVoList = new ArrayList<>(nameStandardDatasourceVO.getSchemas().size());
        DriverSession driverSession = driverSessionService.getDriverSession(DetailsHelper.getUserDetails().getTenantId(),
                nameStandardDatasourceVO.getDatasource());
        nameStandardDatasourceVO.getSchemas().forEach(x-> nameStandardTableVoList.add(NameStandardTableVO
                .builder().schema(x).tables(driverSession.tableList(x)).build()));
        return nameStandardTableVoList;
    }

    /**
     * 获取要校验的表
     *
     * @param nameAimDTO 落标
     * @param aimTableList 接收结果的list
     */
    private void getAimTableFromNameAimDTO(NameAimDTO nameAimDTO,List<NameExecHisDetailDTO> aimTableList){
        DriverSession driverSession = driverSessionService.getDriverSession(nameAimDTO.getTenantId(),nameAimDTO.getDatasourceCode());
        nameAimDTO.getNameAimIncludeDTOList().forEach(x->{
            List<String> tables = driverSession.tableList(x.getSchemaName()).stream()
                    .filter(o->!Pattern.matches(nameAimDTO.getExcludeRule(),o))
                    .collect(Collectors.toList());
            List<String> excludeTables = nameAimDTO.getNameAimExcludeDTOList().stream()
                    .filter(o->o.getSchemaName().equals(x.getSchemaName()))
                    .map(NameAimExcludeDTO::getTableName)
                    .collect(Collectors.toList());
            tables.removeAll(excludeTables);
            aimTableList.addAll(tables.stream()
                    .map(o-> NameExecHisDetailDTO.builder()
                            .tenantId(nameAimDTO.getTenantId())
                            .tableName(o)
                            .schemaName(x.getSchemaName())
                            .sourcePath(nameAimDTO.getDatasourceCode()+"/"+x.getSchemaName()+"/"+o)
                            .build())
                    .collect(Collectors.toList())
            );
        });
    }

    private List<NameExecHisDetailDTO> getAimTable(List<NameAimDTO> nameAimDTOList) {
        List<NameExecHisDetailDTO> aimTableList = new ArrayList<>();
        nameAimDTOList.forEach(x -> this.getAimTableFromNameAimDTO(x, aimTableList));
        return aimTableList;
    }
}

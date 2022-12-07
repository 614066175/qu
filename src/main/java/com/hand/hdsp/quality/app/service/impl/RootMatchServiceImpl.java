package com.hand.hdsp.quality.app.service.impl;

import com.hand.hdsp.quality.api.dto.RootMatchDTO;
import com.hand.hdsp.quality.app.service.RootMatchService;
import com.hand.hdsp.quality.app.service.RootService;
import com.hand.hdsp.quality.domain.entity.DataField;
import com.hand.hdsp.quality.domain.entity.RootMatch;
import com.hand.hdsp.quality.domain.repository.DataFieldRepository;
import com.hand.hdsp.quality.domain.repository.RootMatchRepository;
import com.hand.hdsp.quality.infra.constant.ErrorCode;
import com.hand.hdsp.quality.infra.constant.StandardConstant;
import com.hand.hdsp.quality.infra.util.CustomThreadPool;
import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.oauth.CustomUserDetails;
import io.choerodon.core.oauth.DetailsHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.util.Sqls;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

import static com.hand.hdsp.quality.infra.constant.RootMatchConstants.MatchStatus.*;
import static com.hand.hdsp.quality.infra.constant.RootMatchConstants.SourceType.ROOT;
import static com.hand.hdsp.quality.infra.constant.RootMatchConstants.SourceType.STANDARD;

/**
 * <p>字段标准匹配表应用服务默认实现</p>
 *
 * @author shijie.gao@hand-china.com 2022-12-07 10:42:30
 */
@Service
@Slf4j
public class RootMatchServiceImpl implements RootMatchService {

    private final RootMatchRepository rootMatchRepository;
    private final DataFieldRepository dataFieldRepository;
    private final RootService rootService;

    private static final String NO_MATCH = "{未匹配}";

    public RootMatchServiceImpl(RootMatchRepository rootMatchRepository, DataFieldRepository dataFieldRepository, RootService rootService) {
        this.rootMatchRepository = rootMatchRepository;
        this.dataFieldRepository = dataFieldRepository;
        this.rootService = rootService;
    }

    /**
     * @param pageRequest  分页条件
     * @param rootMatchDTO 查询条件
     * @return 分页后的字段标准匹配记录
     */
    @Override
    public Page<RootMatchDTO> pageRootMatch(PageRequest pageRequest, RootMatchDTO rootMatchDTO) {
        return rootMatchRepository.pageRootMatchDTOList(pageRequest, rootMatchDTO);
    }

    /**
     * @param rootMatchDTO 条件参数
     * @param file         文件
     */
    @Override
    public void upload(RootMatchDTO rootMatchDTO, MultipartFile file) throws IOException {
        Workbook wb;
        String fileName = file.getOriginalFilename();
        try {
            //获取文件流
            InputStream is = file.getInputStream();
            assert fileName != null;
            //通过文件流读取已有的 Workbook工作簿
            if (fileName.endsWith("xls")) {
                POIFSFileSystem poifsFileSystem = new POIFSFileSystem(is);
                wb = new HSSFWorkbook(poifsFileSystem);
            } else if (fileName.endsWith("xlsx")) {
                wb = new XSSFWorkbook(is);
            } else {
                throw new CommonException(ErrorCode.FILE_TYPE_NOT_SUPPORTED);
            }
            Sheet sheet = wb.getSheetAt(0);
            int rowCount = sheet.getPhysicalNumberOfRows();
            for (int i = 1; i < rowCount - 1; i++) {
                Row row = sheet.getRow(i + 1);
                Cell cell = row.getCell(0);
                if (StringUtils.isNotEmpty(cell.getStringCellValue())) {
                    rootMatchRepository.insertDTOSelective(RootMatchDTO.builder()
                            .fieldName(cell.getStringCellValue())
                            .tenantId(rootMatchDTO.getTenantId())
                            .projectId(rootMatchDTO.getProjectId())
                            .matchingStatus(IMPORT)
                            .build());
                }
            }
        } catch (IOException e) {
            log.info(e.getMessage());
        }
    }

    /**
     * @param rootMatchDTO 条件参数
     * @param exportType   导出类型
     * @return 字段标准匹配集合
     */
    @Override
    public List<RootMatchDTO> export(RootMatchDTO rootMatchDTO, String exportType) {
        List<RootMatchDTO> rootMatchDTOList = rootMatchRepository.selectDTOByCondition(Condition.builder(RootMatch.class).andWhere(Sqls.custom()
                        .andEqualTo(RootMatch.FIELD_BATCH_NUMBER, rootMatchDTO.getBatchNumber()))
                .build());
        //
        return rootMatchDTOList;
    }

    @Override
    public List<RootMatch> smartMatch(RootMatchDTO rootMatchDTO) {
        //获取当前批次的所有数据
        List<RootMatch> rootMatches = rootMatchRepository.select(RootMatch.builder().batchNumber(rootMatchDTO.getBatchNumber())
                .tenantId(rootMatchDTO.getTenantId())
                .projectId(rootMatchDTO.getProjectId())
                .build());
        if (CollectionUtils.isEmpty(rootMatches)) {
            return rootMatches;
        }
        //判断匹配操作是否是重新匹配
        if (rootMatchDTO.getReMatchFlag() == 1) {
            //重新匹配除导入中的所有数据
            List<RootMatch> reMatches = rootMatches.stream()
                    .filter(rootMatch -> !MATCHING.equals(rootMatch.getMatchingStatus()))
                    .collect(Collectors.toList());
            if (CollectionUtils.isEmpty(reMatches)) {
                return reMatches;
            }
            //修改状态
            reMatches.forEach(reMatch -> reMatch.setMatchingStatus(MATCHING));
            //修改状态
            rootMatchRepository.batchUpdateOptional(reMatches, RootMatch.FIELD_MATCHING_STATUS);
            //异步匹配
            ThreadPoolExecutor executor = CustomThreadPool.getExecutor();
            for (RootMatch reMatch : reMatches) {
                CustomUserDetails userDetails = DetailsHelper.getUserDetails();
                executor.execute(() -> doMatch(reMatch, userDetails));
            }
        } else {
            //匹配导入状态的数据
            List<RootMatch> importMatches = rootMatches.stream()
                    .filter(rootMatch -> IMPORT.equals(rootMatch.getMatchingStatus()))
                    .collect(Collectors.toList());
            if (CollectionUtils.isEmpty(importMatches)) {
                return importMatches;
            }
            //修改状态
            importMatches.forEach(importMatch -> importMatch.setMatchingStatus(MATCHING));
            //修改状态
            rootMatchRepository.batchUpdateOptional(importMatches, RootMatch.FIELD_MATCHING_STATUS);
            //异步匹配
            ThreadPoolExecutor executor = CustomThreadPool.getExecutor();
            for (RootMatch importMatch : importMatches) {
                CustomUserDetails userDetails = DetailsHelper.getUserDetails();
                executor.execute(() -> doMatch(importMatch, userDetails));
            }
        }
        return rootMatches;
    }

    private void doMatch(RootMatch importMatch, CustomUserDetails userDetails) {
        DetailsHelper.setCustomUserDetails(userDetails);
        //匹配
        //字段标准匹配
        String fieldComment = importMatch.getFieldComment();
        //匹配已发布的字段标准
        List<DataField> dataFields = dataFieldRepository.selectByCondition(Condition.builder(DataField.class)
                .andWhere(Sqls.custom()
                        .andEqualTo(DataField.FIELD_FIELD_COMMENT, fieldComment)
                        .andEqualTo(DataField.FIELD_TENANT_ID, importMatch.getTenantId())
                        .andEqualTo(DataField.FIELD_PROJECT_ID, importMatch.getProjectId())
                        .andIn(DataField.FIELD_STANDARD_STATUS, Arrays.asList(StandardConstant.Status.ONLINE, StandardConstant.Status.OFFLINE_APPROVING))
                )
                .build());
        if (CollectionUtils.isNotEmpty(dataFields)) {
            DataField dataField = dataFields.get(0);
            importMatch.setMatchingStatus(SUCCESS);
            importMatch.setFieldType(dataField.getFieldType());
            importMatch.setFieldName(dataField.getFieldName());
            importMatch.setFieldDescription(dataField.getFieldComment());
            importMatch.setSource(STANDARD);
            rootMatchRepository.updateByPrimaryKey(importMatch);
            return;
        }
        //词根匹配
        String analyzerWord;
        try {
            analyzerWord = rootService.analyzerWord(importMatch.getTenantId(), importMatch.getProjectId(), importMatch.getFieldComment());
        } catch (Exception e) {
            //匹配失败
            log.error(e.getMessage());
            importMatch.setMatchingStatus(FAILED);
            rootMatchRepository.updateByPrimaryKey(importMatch);
            return;
        }
        if (StringUtils.isEmpty(analyzerWord)) {
            importMatch.setMatchingStatus(FAILED);
            rootMatchRepository.updateByPrimaryKey(importMatch);
            return;
        }

        if (analyzerWord.contains(NO_MATCH)) {
            //部分匹配
            importMatch.setMatchingStatus(PART_MATCH);
            importMatch.setFieldName(ROOT);
        } else {
            importMatch.setMatchingStatus(SUCCESS);
            importMatch.setFieldName(ROOT);
        }
        rootMatchRepository.updateByPrimaryKey(importMatch);
    }
}

package com.hand.hdsp.quality.app.service.impl;

import com.hand.hdsp.quality.api.dto.DataFieldDTO;
import com.hand.hdsp.quality.api.dto.RootMatchDTO;
import com.hand.hdsp.quality.api.dto.RootMatchHisDTO;
import com.hand.hdsp.quality.api.dto.StandardTeamDTO;
import com.hand.hdsp.quality.app.service.RootMatchService;
import com.hand.hdsp.quality.app.service.RootService;
import com.hand.hdsp.quality.domain.entity.DataField;
import com.hand.hdsp.quality.domain.entity.RootMatch;
import com.hand.hdsp.quality.domain.entity.RootMatchHis;
import com.hand.hdsp.quality.domain.entity.StandardRelation;
import com.hand.hdsp.quality.domain.repository.*;
import com.hand.hdsp.quality.infra.constant.ErrorCode;
import com.hand.hdsp.quality.infra.constant.RootMatchConstants;
import com.hand.hdsp.quality.infra.constant.StandardConstant;
import com.hand.hdsp.quality.infra.mapper.DataFieldMapper;
import com.hand.hdsp.quality.infra.util.CustomThreadPool;
import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.oauth.CustomUserDetails;
import io.choerodon.core.oauth.DetailsHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.hzero.boot.message.MessageClient;
import org.hzero.boot.message.entity.Receiver;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.helper.DataSecurityHelper;
import org.hzero.mybatis.util.Sqls;
import org.hzero.starter.driver.core.infra.util.UUIDUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;
import java.util.concurrent.Future;
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
    private final DataFieldMapper dataFieldMapper;
    private final StandardRelationRepository standardRelationRepository;
    private final StandardTeamRepository standardTeamRepository;
    private final RootMatchHisRepository rootMatchHisRepository;
    private final MessageClient messageClient;

    private static final String NO_MATCH = "{未匹配}";

    public RootMatchServiceImpl(RootMatchRepository rootMatchRepository, DataFieldRepository dataFieldRepository, RootService rootService, DataFieldMapper dataFieldMapper, StandardRelationRepository standardRelationRepository, StandardTeamRepository standardTeamRepository, RootMatchHisRepository rootMatchHisRepository, MessageClient messageClient) {
        this.rootMatchRepository = rootMatchRepository;
        this.dataFieldRepository = dataFieldRepository;
        this.rootService = rootService;
        this.dataFieldMapper = dataFieldMapper;
        this.standardRelationRepository = standardRelationRepository;
        this.standardTeamRepository = standardTeamRepository;
        this.rootMatchHisRepository = rootMatchHisRepository;
        this.messageClient = messageClient;
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
    @Transactional(rollbackFor = Exception.class)
    public String upload(RootMatchDTO rootMatchDTO, MultipartFile file) {
        List<RootMatchHis> rootMatchHis = new ArrayList<>();
        Long importRowCount = 0L;
        //生成批次号
        if (StringUtils.isEmpty(rootMatchDTO.getBatchNumber())) {
            rootMatchDTO.setBatchNumber("XSTA.ROOT_MATCH." + UUIDUtils.generateShortUUID() + System.currentTimeMillis());
        } else {
            //目前没有追加上传逻辑。上传都是一个新批次
            //记录数据数量
            rootMatchHis = rootMatchHisRepository.selectByCondition(Condition.builder(RootMatch.class).andWhere(Sqls.custom()
                            .andEqualTo(RootMatch.FIELD_BATCH_NUMBER, rootMatchDTO.getBatchNumber()))
                    .build());
            if (CollectionUtils.isNotEmpty(rootMatchHis)) {
                importRowCount = rootMatchHis.get(0).getDataCount();
            }
        }
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
                    //存匹配表
                    rootMatchRepository.insertDTOSelective(RootMatchDTO.builder()
                            .batchNumber(rootMatchDTO.getBatchNumber())
                            .fieldComment(cell.getStringCellValue())
                            .tenantId(rootMatchDTO.getTenantId())
                            .projectId(rootMatchDTO.getProjectId())
                            .matchingStatus(UN_MATCH)
                            .build());
                }
            }
            //存匹配记录表
            if (importRowCount == 0) {
                rootMatchHisRepository.insertDTOSelective(RootMatchHisDTO.builder()
                        .batchNumber(rootMatchDTO.getBatchNumber())
                        .dataCount((long) (rowCount - 2))
                        .status("UPLOADED")
                        .tenantId(rootMatchDTO.getTenantId())
                        .projectId(rootMatchDTO.getProjectId())
                        .build());
            } else {
                RootMatchHis matchHis = rootMatchHis.get(0);
                matchHis.setTenantId(rootMatchDTO.getTenantId());
                matchHis.setProjectId(rootMatchDTO.getProjectId());
                matchHis.setDataCount(rowCount - 2 + importRowCount);
                rootMatchHisRepository.updateByPrimaryKeySelective(matchHis);
            }
        } catch (IOException e) {
            throw new CommonException("上传失败");
        }
        return rootMatchDTO.getBatchNumber();
    }

    /**
     * @param rootMatchDTO 条件参数
     */
    @Override
    public void export(RootMatchDTO rootMatchDTO, HttpServletResponse response) {
        List<RootMatchDTO> rootMatchDTOList = rootMatchRepository.selectDTOByCondition(Condition.builder(RootMatch.class).andWhere(Sqls.custom()
                        .andEqualTo(RootMatch.FIELD_BATCH_NUMBER, rootMatchDTO.getBatchNumber()))
                .build());
        String exportType = "";
        List<DataFieldDTO> result = new ArrayList<>();
        //筛选字段标准来源的
        List<RootMatchDTO> sortRootMatchDTOList = rootMatchDTOList.stream().filter(dto -> STANDARD.equals(dto.getSource()) && ObjectUtils.isNotEmpty(dto.getFieldId())).collect(Collectors.toList());
        for (RootMatchDTO matchDTO : sortRootMatchDTOList) {
            List<DataFieldDTO> list = dataFieldMapper.list(DataFieldDTO.builder().fieldId(matchDTO.getFieldId()).build());
            //查询标准组
            List<StandardRelation> standardRelations = standardRelationRepository.select(StandardRelation.builder().fieldStandardId(matchDTO.getFieldId()).build());
            List<Long> standardTeamIds = standardRelations.stream()
                    .map(StandardRelation::getStandardTeamId)
                    .collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(standardTeamIds)) {
                List<StandardTeamDTO> standardTeamDTOS = standardTeamRepository.selectDTOByIds(standardTeamIds);
                String standardTeamCodes = standardTeamDTOS.stream().map(StandardTeamDTO::getStandardTeamCode).collect(Collectors.joining(","));
                list.forEach(dataFieldDTO -> {
                    dataFieldDTO.setStandardTeamCodes(standardTeamCodes);
                });
            }
            result.addAll(list);
        }
        Map<String, String> fieldMap = new LinkedHashMap<>();
        createHead(fieldMap);
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Match");
        int rowNum = 0;
        Row row = sheet.createRow(rowNum);
        Cell cell;
        int cellNum = 0;
        for (String name : fieldMap.values()) {
            cell = row.createCell(cellNum);
            cell.setCellValue(name);
            cellNum++;
        }
        if (ObjectUtils.isEmpty(rootMatchDTO.getExportType())) {
            exportType = ".xlsx";
        } else if ("csv".equals(rootMatchDTO.getExportType())) {
            exportType = ".csv";
        }
        createCells(result, sheet);
        String fileName = "RootMatch";
        OutputStream out;
        try {
            out = response.getOutputStream();
            response.reset();
            response.addHeader("Content-Disposition", "attachment; filename=" + fileName + exportType);
            response.setContentType("application/vnd.ms-excel;charset=utf-8");
            workbook.write(out);
            out.flush();
            out.close();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void createHead(Map<String, String> fieldMap) {
        fieldMap.put("fieldComment", "字段注释");
        fieldMap.put("fieldName", "字段名称");
        fieldMap.put("groupName", "分组名称");
        fieldMap.put("standardDesc", "标准描述");
        fieldMap.put("standardTeamCodes", "字段标准组");
        fieldMap.put("dataStandardName", "引用数据标准");
        fieldMap.put("fieldAccuracy", "字段精度");
        fieldMap.put("fieldType", "字段类型");
        fieldMap.put("fieldLength", "字段长度");
        fieldMap.put("dataPattern", "数据格式");
        fieldMap.put("valueType", "值域类型");
        fieldMap.put("valueRange", "值域范围");
        fieldMap.put("nullFlag", "是否可为空");
        fieldMap.put("defaultValue", "默认值");
        fieldMap.put("sysCommonName", "系统常用名");
        fieldMap.put("chargeDeptName", "责任部门");
        fieldMap.put("chargeName", "责任人");
        fieldMap.put("chargeTel", "责任人电话");
        fieldMap.put("chargeEmail", "责任人邮箱");
    }

    private void createCells(List<DataFieldDTO> result, XSSFSheet sheet) {
        int rows = 1;
        for (DataFieldDTO dto : result) {
            Row row = sheet.createRow(rows);
            int col = 0;
            row.createCell(col).setCellValue(dto.getFieldComment());
            row.createCell(col + 1).setCellValue(dto.getFieldName());
            row.createCell(col + 2).setCellValue(dto.getGroupName());
            row.createCell(col + 3).setCellValue(dto.getStandardDesc());
            row.createCell(col + 4).setCellValue(dto.getStandardTeamCodes());
            row.createCell(col + 5).setCellValue(dto.getDataStandardName());
            row.createCell(col + 6).setCellValue(dto.getFieldAccuracy());
            row.createCell(col + 7).setCellValue(dto.getFieldType());
            row.createCell(col + 8).setCellValue(dto.getFieldLength());
            row.createCell(col + 9).setCellValue(dto.getDataPattern());
            row.createCell(col + 10).setCellValue(dto.getValueType());
            row.createCell(col + 11).setCellValue(dto.getValueRange());
            row.createCell(col + 12).setCellValue(dto.getNullFlag());
            row.createCell(col + 13).setCellValue(dto.getDefaultValue());
            row.createCell(col + 14).setCellValue(dto.getSysCommonName());
            //加密租户则解密
            if (DataSecurityHelper.isTenantOpen()) {
                row.createCell(col + 15).setCellValue(DataSecurityHelper.decrypt(dto.getChargeDeptName()));
                row.createCell(col + 16).setCellValue(DataSecurityHelper.decrypt(dto.getChargeTel()));
                row.createCell(col + 17).setCellValue(DataSecurityHelper.decrypt(dto.getChargeEmail()));
            } else {
                row.createCell(col + 15).setCellValue(dto.getChargeDeptName());
                row.createCell(col + 16).setCellValue(dto.getChargeTel());
                row.createCell(col + 17).setCellValue(dto.getChargeEmail());
            }
            rows++;
        }
    }

    @Override
    public List<RootMatch> smartMatch(RootMatchDTO rootMatchDTO) {
        RootMatchHis rootMatchHis = rootMatchHisRepository.selectOne(RootMatchHis.builder().batchNumber(rootMatchDTO.getBatchNumber()).build());
        if (MATCHING.equals(rootMatchHis.getStatus())) {
            //如果当前批次记录正在匹配中，无法进行操作
            throw new CommonException("当前批次正在匹配中，无法进行操作");
        }
        //获取当前批次的除手动维护状态外的所有数据
        List<RootMatch> rootMatches = rootMatchRepository.selectByCondition(Condition.builder(RootMatch.class)
                .andWhere(Sqls.custom()
                        .andEqualTo(RootMatch.FIELD_BATCH_NUMBER, rootMatchDTO.getBatchNumber())
                        .andEqualTo(RootMatch.FIELD_TENANT_ID, rootMatchDTO.getTenantId())
                        .andEqualTo(RootMatch.FIELD_PROJECT_ID, rootMatchDTO.getProjectId())
                        .andNotEqualTo(RootMatch.FIELD_MATCHING_STATUS, MANUAL_MATCH))
                .build());
        if (CollectionUtils.isEmpty(rootMatches)) {
            //如果当前批次记录正在匹配中，无法进行操作
            throw new CommonException("无可智能匹配数据！");
        }
        List<Future> futures = new ArrayList<>();
        //修改批次记录状态
        //批次唯一
        rootMatchHis.setStatus(RootMatchConstants.HisStatus.MATCHING);
        rootMatchHisRepository.updateByPrimaryKey(rootMatchHis);
        //修改数据状态
        rootMatches.forEach(rootMatch -> rootMatch.setMatchingStatus(MATCHING));
        rootMatchRepository.batchUpdateOptional(rootMatches, RootMatch.FIELD_MATCHING_STATUS);

        //异步匹配
        ThreadPoolExecutor executor = CustomThreadPool.getExecutor();
        for (RootMatch rootMatch : rootMatches) {
            CustomUserDetails userDetails = DetailsHelper.getUserDetails();
            Future<?> future = executor.submit(() -> doMatch(rootMatch, userDetails));
            futures.add(future);
        }

        if (CollectionUtils.isNotEmpty(futures)) {
            for (Future future : futures) {
                try {
                    future.get();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            //修改批次状态
            rootMatchHis.setStatus(RootMatchConstants.HisStatus.MATCHED);
            rootMatchHisRepository.updateByPrimaryKey(rootMatchHis);
            //匹配完成，进行通知
            Receiver receiver = new Receiver();
            receiver.setTargetUserTenantId(rootMatchDTO.getTenantId());
            //模板内容 智能标准匹配【${batchNumber}】${successNum}条数据匹配成功，${partMatchNum}条数据部分匹配，${failNum}条数据匹配失败!
            Map<String, String> args = new HashMap<>();
            args.put("batchNumber", rootMatchHis.getBatchNumber());
            //获取成功数量
            rootMatches = rootMatchRepository.select(RootMatch.builder().batchNumber(rootMatchDTO.getBatchNumber())
                    .tenantId(rootMatchDTO.getTenantId())
                    .projectId(rootMatchDTO.getProjectId())
                    .build());
            long successNum = rootMatches.stream().filter(rootMatch -> SUCCESS.equals(rootMatch.getMatchingStatus())).count();
            long partMatchNum = rootMatches.stream().filter(rootMatch -> PART_MATCH.equals(rootMatch.getMatchingStatus())).count();
            long failNum = rootMatches.stream().filter(rootMatch -> FAILED.equals(rootMatch.getMatchingStatus())).count();
            args.put("successNum", String.valueOf(successNum));
            args.put("partMatchNum", String.valueOf(partMatchNum));
            args.put("failNum", String.valueOf(failNum));
            //发送站内信
            messageClient.sendWebMessage(rootMatchDTO.getTenantId(), "HDSP.XQUA.ROOT_MATCH", null, Collections.singletonList(receiver), args);
        }
        return rootMatches;
    }

    @Override
    public RootMatchDTO update(RootMatchDTO rootMatchDTO) {
        //两种修改，一种只是修改字段注释，一种是手工维度，维护对应字段标准
        if (rootMatchDTO.getFieldId() == null) {
            rootMatchDTO.setMatchingStatus(UN_MATCH);
            rootMatchDTO.setFieldId(null);
            rootMatchDTO.setSource(null);
            rootMatchRepository.updateDTOAllColumnWhereTenant(rootMatchDTO, rootMatchDTO.getTenantId());
        } else {
            rootMatchDTO.setMatchingStatus(MANUAL_MATCH);
            rootMatchDTO.setSource(STANDARD);
        }
        rootMatchRepository.updateDTOAllColumnWhereTenant(rootMatchDTO, rootMatchDTO.getTenantId());
        return rootMatchDTO;
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
            importMatch.setFieldId(dataField.getFieldId());
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
        String[] split = analyzerWord.split("_");
        List<String> matchTerms = Arrays.asList(split).stream()
                .filter(term -> !term.equals(NO_MATCH)).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(matchTerms)) {
            importMatch.setFieldId(null);
            importMatch.setFieldName(null);
            importMatch.setSource(null);
            importMatch.setMatchingStatus(FAILED);
            rootMatchRepository.updateByPrimaryKey(importMatch);
            return;
        }
        if (analyzerWord.contains(NO_MATCH)) {
            //部分匹配
            importMatch.setMatchingStatus(PART_MATCH);
        } else {
            importMatch.setMatchingStatus(SUCCESS);
        }
        importMatch.setFieldId(null);
        importMatch.setFieldName(analyzerWord);
        importMatch.setSource(ROOT);
        rootMatchRepository.updateByPrimaryKey(importMatch);
    }
}

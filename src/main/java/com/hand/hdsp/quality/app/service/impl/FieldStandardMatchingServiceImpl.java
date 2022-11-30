package com.hand.hdsp.quality.app.service.impl;

import com.hand.hdsp.quality.api.dto.FieldStandardMatchingDTO;
import com.hand.hdsp.quality.app.service.FieldStandardMatchingService;
import com.hand.hdsp.quality.domain.entity.FieldStandardMatching;
import com.hand.hdsp.quality.domain.repository.FieldStandardMatchingRepository;
import com.hand.hdsp.quality.infra.constant.ErrorCode;
import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.util.Sqls;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.util.*;

import static com.hand.hdsp.quality.infra.constant.FieldMatchingConstants.MatchingStatus.IMPORT;

/**
 * <p>字段标准匹配表应用服务默认实现</p>
 *
 * @author shijie.gao@hand-china.com 2022-11-30 10:31:02
 */
@Service
@Slf4j
public class FieldStandardMatchingServiceImpl implements FieldStandardMatchingService {

    private final FieldStandardMatchingRepository fieldStandardMatchingRepository;

    public FieldStandardMatchingServiceImpl(FieldStandardMatchingRepository fieldStandardMatchingRepository) {
        this.fieldStandardMatchingRepository = fieldStandardMatchingRepository;
    }

    /**
     * @param pageRequest              分页条件
     * @param fieldStandardMatchingDTO 查询条件
     * @return 分页后的字段标准匹配记录
     */
    @Override
    public Page<FieldStandardMatchingDTO> pageFieldStandardMatching(PageRequest pageRequest, FieldStandardMatchingDTO fieldStandardMatchingDTO) {
        return fieldStandardMatchingRepository.pageFieldStandardMatching(pageRequest, fieldStandardMatchingDTO);
    }

    /**
     * @param fieldStandardMatchingDTO 条件
     * @param file 上传文件
     */
    @Override
    public void upload(FieldStandardMatchingDTO fieldStandardMatchingDTO, MultipartFile file)  {
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
                    fieldStandardMatchingRepository.insertDTOSelective(FieldStandardMatchingDTO.builder()
                            .fieldName(cell.getStringCellValue())
                            .tenantId(fieldStandardMatchingDTO.getTenantId())
                            .projectId(fieldStandardMatchingDTO.getProjectId())
                            .matchingStatus(IMPORT)
                            .build());
                }
            }
        }catch (IOException e){
            log.info(e.getMessage());
        }
    }

    /**
     * @param fieldStandardMatchingDTO 条件参数
     * @param exportType               导出类型
     * @return 字段标准匹配集合
     */
    @Override
    public List<FieldStandardMatchingDTO> export(FieldStandardMatchingDTO fieldStandardMatchingDTO, String exportType) {
        List<FieldStandardMatchingDTO> fieldStandardMatchings = fieldStandardMatchingRepository.selectDTOByCondition(Condition.builder(FieldStandardMatching.class).andWhere(Sqls.custom()
                        .andEqualTo(FieldStandardMatching.FIELD_BATCH_NUMBER,fieldStandardMatchingDTO.getBatchNumber()))
                .build());
//        if("xls".equals(exportType)){
//            HSSFWorkbook workbook = new HSSFWorkbook();
//
//        }else if ("xlsx".equals(exportType)){
//            try {
//
//            }catch (IOException e){
//                log.info(e.getMessage());
//            }
//        }
        //导出字段标准匹配
        return fieldStandardMatchings;
    }
}

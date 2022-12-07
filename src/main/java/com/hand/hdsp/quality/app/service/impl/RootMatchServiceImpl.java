package com.hand.hdsp.quality.app.service.impl;

import com.hand.hdsp.quality.api.dto.RootMatchDTO;
import com.hand.hdsp.quality.app.service.RootMatchService;
import com.hand.hdsp.quality.domain.entity.RootMatch;
import com.hand.hdsp.quality.domain.repository.RootMatchRepository;
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
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.util.Sqls;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static com.hand.hdsp.quality.infra.constant.FieldMatchingConstants.MatchingStatus.IMPORT;

/**
 * <p>字段标准匹配表应用服务默认实现</p>
 *
 * @author shijie.gao@hand-china.com 2022-12-07 10:42:30
 */
@Service
@Slf4j
public class RootMatchServiceImpl implements RootMatchService {

    private final RootMatchRepository rootMatchRepository;

    public RootMatchServiceImpl(RootMatchRepository rootMatchRepository) {
        this.rootMatchRepository = rootMatchRepository;
    }

    /**
     * @param pageRequest  分页条件
     * @param rootMatchDTO 查询条件
     * @return 分页后的字段标准匹配记录
     */
    @Override
    public Page<RootMatchDTO> pageRootMatch(PageRequest pageRequest, RootMatchDTO rootMatchDTO) {
        return rootMatchRepository.pageRootMatchDTOList(pageRequest,rootMatchDTO);
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
        }catch (IOException e){
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
                        .andEqualTo(RootMatch.FIELD_BATCH_NUMBER,rootMatchDTO.getBatchNumber()))
                .build());
        //
        return rootMatchDTOList;
    }
}

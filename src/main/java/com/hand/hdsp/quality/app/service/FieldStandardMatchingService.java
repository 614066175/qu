package com.hand.hdsp.quality.app.service;

import com.hand.hdsp.quality.api.dto.FieldStandardMatchingDTO;
import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * <p>字段标准匹配表应用服务</p>
 *
 * @author shijie.gao@hand-china.com 2022-11-30 10:31:02
 */
public interface FieldStandardMatchingService {

    /**
     * @param pageRequest 分页条件
     * @param fieldStandardMatchingDTO 查询条件
     * @return 分页后的字段标准匹配记录
     */
    Page<FieldStandardMatchingDTO> pageFieldStandardMatching(PageRequest pageRequest, FieldStandardMatchingDTO fieldStandardMatchingDTO);

    /**
     * 上传导入excel
     * @param fieldStandardMatchingDTO 条件参数
     * @param file 文件
     */
    void upload(FieldStandardMatchingDTO fieldStandardMatchingDTO, MultipartFile file) throws IOException;

    /**
     * 导出excel
     * @param fieldStandardMatchingDTO 条件参数
     * @param exportType 导出类型
     * @return 导出excel
     */
    List<FieldStandardMatchingDTO> export(FieldStandardMatchingDTO fieldStandardMatchingDTO, String exportType);
}

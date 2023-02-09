package com.hand.hdsp.quality.app.service;

import com.hand.hdsp.quality.api.dto.StandardDocDTO;
import com.hand.hdsp.quality.infra.export.dto.DocStandardExportDTO;
import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.hzero.export.vo.ExportParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * <p>标准文档管理表应用服务</p>
 *
 * @author hua.shi@hand-china.com 2020-11-24 17:50:14
 */
public interface StandardDocService {

    /**
     * 查询标准文档列表
     *
     * @param pageRequest    PageRequest
     * @param standardDocDTO StandardDocDTO
     * @return List<StandardDocDTO>
     */
    Page<StandardDocDTO> list(PageRequest pageRequest, StandardDocDTO standardDocDTO);

    /**
     * 查询标准文档集合
     * @param standardDocDTO 查询条件
     * @return 标准文档集合
     */
    List<StandardDocDTO> findLists(StandardDocDTO standardDocDTO);

    /**
     * 创建标准文档
     *
     * @param standardDocDTO StandardDocDTO
     * @param multipartFile  standard doc file
     * @return StandardDocDTO
     */
    StandardDocDTO create(StandardDocDTO standardDocDTO, MultipartFile multipartFile);

    /**
     * 更新标准文档
     *
     * @param standardDocDTO StandardDocDTO
     * @param multipartFile  standard doc file
     * @return StandardDocDTO
     */
    StandardDocDTO update(StandardDocDTO standardDocDTO, MultipartFile multipartFile);

    /**
     * 删除标准文档
     *
     * @param standardDocDTOList List<StandardDocDTO>
     * @param tenantId           租户ID
     */
    void remove(List<StandardDocDTO> standardDocDTOList, Long tenantId);

    /**
     * 下载标准文档
     *
     * @param standardDocDTO StandardDocDTO
     * @param response       HttpServletResponse
     */
    void downloadStandardDoc(StandardDocDTO standardDocDTO, HttpServletResponse response);

    /**
     * 标准文档导出
     *
     * @param dto         StandardDocDTO
     * @param exportParam ExportParam
     * @return List<StandardDocDTO>
     */
    List<DocStandardExportDTO> export(StandardDocDTO dto, ExportParam exportParam);

    /**
     * 获取预览接口url
     * @return url
     */
    String previewUrl();
}

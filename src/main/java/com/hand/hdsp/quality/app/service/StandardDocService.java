package com.hand.hdsp.quality.app.service;

import java.util.List;
import javax.servlet.http.HttpServletResponse;

import com.hand.hdsp.quality.api.dto.StandardDocDTO;
import org.springframework.web.multipart.MultipartFile;

/**
 * <p>标准文档管理表应用服务</p>
 *
 * @author hua.shi@hand-china.com 2020-11-24 17:50:14
 */
public interface StandardDocService {
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
     * @param tenantId 租户ID
     */
    void remove(List<StandardDocDTO> standardDocDTOList, Long tenantId);

    /**
     * 下载标准文档
     *
     * @param standardDocDTO StandardDocDTO
     * @param response  响应
     */
    void downloadStandardDoc(StandardDocDTO standardDocDTO, HttpServletResponse response);
}

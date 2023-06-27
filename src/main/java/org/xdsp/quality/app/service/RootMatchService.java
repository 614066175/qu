package org.xdsp.quality.app.service;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.springframework.web.multipart.MultipartFile;
import org.xdsp.quality.api.dto.RootMatchDTO;
import org.xdsp.quality.domain.entity.RootMatch;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * <p>字段标准匹配表应用服务</p>
 *
 * @author shijie.gao@hand-china.com 2022-12-07 10:42:30
 */
public interface RootMatchService {

    /**
     * 分页查询匹配结果
     * @param pageRequest 分页条件
     * @param rootMatchDTO 查询条件
     * @return 分页后的字段标准匹配记录
     */
    Page<RootMatchDTO> pageRootMatch(PageRequest pageRequest, RootMatchDTO rootMatchDTO);

    /**
     * 上传导入excel
     * @param rootMatchDTO 条件参数
     * @param file 文件
     */
    String upload(RootMatchDTO rootMatchDTO, MultipartFile file) throws IOException;

    /**
     * 导出excel
     * @param rootMatchDTO 条件参数
     *
     */
    void export(RootMatchDTO rootMatchDTO, HttpServletResponse response);

    /**
     * 智能匹配
     * @param rootMatchDTO
     * @return
     */
    List<RootMatch> smartMatch(RootMatchDTO rootMatchDTO);

    /**
     *
     * @param rootMatchDTO
     * @return
     */
    RootMatchDTO update(RootMatchDTO rootMatchDTO);
}

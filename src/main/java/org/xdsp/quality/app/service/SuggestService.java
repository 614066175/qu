package org.xdsp.quality.app.service;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.xdsp.quality.api.dto.SuggestDTO;

/**
 * <p>问题知识库表应用服务</p>
 *
 * @author lei.song@hand-china.com 2021-08-17 11:47:17
 */
public interface SuggestService {

    /**
     * 新建建议
     * @param suggestDTO  新建的建议数据
     * @return 返回值
     */
    SuggestDTO createSuggest(SuggestDTO suggestDTO);

    /**
     * 整改建议列表加条件查询
     * @param suggestDTO 查询条件
     * @return 返回值
     */
    Page<SuggestDTO> list(PageRequest pageRequest, SuggestDTO suggestDTO);

}

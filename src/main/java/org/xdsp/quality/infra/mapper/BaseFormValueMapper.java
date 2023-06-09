package org.xdsp.quality.infra.mapper;

import io.choerodon.mybatis.common.BaseMapper;
import org.xdsp.quality.api.dto.BaseFormValueDTO;
import org.xdsp.quality.domain.entity.BaseFormValue;

import java.util.List;

/**
 * <p>质检项表单值Mapper</p>
 *
 * @author guoliang.li01@hand-china.com 2022-05-20 15:36:10
 */
public interface BaseFormValueMapper extends BaseMapper<BaseFormValue> {

    /**
     * 根据质检项id批量删除表单值
     * @param planBaseIds
     */
    void deleteFormValueByPlanBaseIds(List<Long> planBaseIds);

    /**
     * 根据质检项id查询动态表单code和value
     * @param planBaseId
     * @return
     */
    List<BaseFormValueDTO> selectByPlanBaseId(Long planBaseId);

    List<BaseFormValueDTO> selectFormItem();

}

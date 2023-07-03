package org.xdsp.quality.infra.batchimport.group;


import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.hzero.boot.imported.infra.validator.annotation.ImportService;
import org.hzero.core.util.JsonUtils;
import org.xdsp.core.domain.entity.CommonGroup;
import org.xdsp.core.infra.constant.CommonGroupConstants;
import org.xdsp.quality.infra.constant.TemplateCodeConstants;

import java.util.ArrayList;
import java.util.List;

/**
 * 参考数据 分组数据导入
 * @author fuqiang.luo@hand-china.com
 */
@Slf4j
@ImportService(templateCode = TemplateCodeConstants.TEMPLATE_CODE_REFERENCE_DATA, sheetIndex = 0)
public class ReferenceDataGroupImportImpl extends AbstractCommonGroupImportImpl{

    private final ObjectMapper objectMapper;

    public ReferenceDataGroupImportImpl(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public Boolean doImport(List<String> data) {
        List<String> dataList = new ArrayList<>();
        for (String json : data) {
            try {
                CommonGroup commonGroup = objectMapper.readValue(json, CommonGroup.class);
                commonGroup.setGroupType(CommonGroupConstants.GroupType.REFERENCE_DATA);
                dataList.add(JsonUtils.toJson(commonGroup));
            } catch (Exception e) {
                log.error("导入参考数据分组失败", e);
                return Boolean.FALSE;
            }
        }
        return super.doImport(dataList);
    }
}

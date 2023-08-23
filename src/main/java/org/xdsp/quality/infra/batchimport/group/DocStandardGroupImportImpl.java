package org.xdsp.quality.infra.batchimport.group;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.hzero.boot.imported.infra.validator.annotation.ImportService;
import org.hzero.starter.driver.core.infra.util.JsonUtil;
import org.xdsp.core.domain.entity.CommonGroup;
import org.xdsp.quality.infra.constant.TemplateCodeConstants;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.xdsp.core.infra.constant.CommonGroupConstants.GroupType.DOC_STANDARD;

/**
 * @Title: DataStandardGroupImportImpl
 * @Description:
 * @author: lgl
 * @date: 2023/2/6 11:09
 */
@Slf4j
@ImportService(templateCode = TemplateCodeConstants.TEMPLATE_CODE_STANDARD_DOC, sheetIndex = 0)
public class DocStandardGroupImportImpl extends AbstractCommonGroupImportImpl {
    private ObjectMapper objectMapper;


    public DocStandardGroupImportImpl(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public Boolean doImport(List<String> data) {
        //循环添加类型
        List<String> commonGroups = new ArrayList<>();
        for (String json : data) {
            try {
                CommonGroup commonGroup = objectMapper.readValue(json, CommonGroup.class);
                commonGroup.setGroupType(DOC_STANDARD);
                commonGroups.add(JsonUtil.toJson(commonGroup));
            } catch (IOException e) {
                log.error("导入分组失败");
                return false;
            }
        }
        return super.doImport(commonGroups);
    }
}

package com.hand.hdsp.quality.infra.batchimport.group;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hand.hdsp.core.domain.entity.CommonGroup;
import com.hand.hdsp.quality.infra.constant.TemplateCodeConstants;
import lombok.extern.slf4j.Slf4j;
import org.hzero.boot.imported.infra.validator.annotation.ImportService;
import org.hzero.starter.driver.core.infra.util.JsonUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.hand.hdsp.core.infra.constant.CommonGroupConstants.GroupType.FIELD_STANDARD;

/**
 * @Title: DataStandardGroupImportImpl
 * @Description:
 * @author: lgl
 * @date: 2023/2/6 11:09
 */
@Slf4j
@ImportService(templateCode = TemplateCodeConstants.TEMPLATE_CODE_FIELD_STANDARD, sheetIndex = 0)
public class FieldStandardGroupImportImpl extends AbstractCommonGroupImportImpl {
    private ObjectMapper objectMapper;


    public FieldStandardGroupImportImpl(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public Boolean doImport(List<String> data) {
        //循环添加类型
        List<String> commonGroups = new ArrayList<>();
        for (String json : data) {
            try {
                CommonGroup commonGroup = objectMapper.readValue(json, CommonGroup.class);
                commonGroup.setGroupType(FIELD_STANDARD);
                commonGroups.add(JsonUtil.toJson(commonGroup));
            } catch (IOException e) {
                log.error("导入分组失败");
                return false;
            }
        }
        return super.doImport(commonGroups);
    }
}

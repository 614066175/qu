package org.xdsp.quality.infra.validation;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.choerodon.core.oauth.DetailsHelper;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import org.hzero.boot.imported.app.service.BatchValidatorHandler;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.util.Sqls;
import org.xdsp.core.util.ProjectHelper;
import org.xdsp.quality.api.dto.RuleDTO;
import org.xdsp.quality.domain.entity.Rule;
import org.xdsp.quality.domain.repository.RuleRepository;
import org.xdsp.quality.infra.constant.TemplateCodeConstants;

import org.hzero.boot.imported.infra.validator.annotation.ImportValidator;
import org.hzero.boot.imported.infra.validator.annotation.ImportValidators;

import java.io.IOException;
import java.util.List;

/**
 * <p>
 * description
 * </p>
 *
 * @author lgl 2020/12/24 9:25
 * @since 1.0
 */
@Slf4j
@ImportValidators(value = {@ImportValidator(templateCode = TemplateCodeConstants.TEMPLATE_CODE_RULE, sheetIndex = 1)})
public class RuleValidator extends BatchValidatorHandler {

    private final ObjectMapper objectMapper;
    private final RuleRepository ruleRepository;

    public RuleValidator(ObjectMapper objectMapper, RuleRepository ruleRepository) {
        this.objectMapper = objectMapper;
        this.ruleRepository = ruleRepository;
    }

    @Override
    public boolean validate(List<String> data) {
        // 设置租户Id
        Long tenantId = DetailsHelper.getUserDetails().getTenantId();
        boolean flag = true;
        try {
            for (int i = 0; i < data.size(); i++) {
                RuleDTO ruleDTO = objectMapper.readValue(data.get(i), RuleDTO.class);
                ruleDTO.setTenantId(tenantId);

                // 校验方式是否正确
                if (!"REGULAR".equals(ruleDTO.getCheckWay()) && !"COMMON".equals(ruleDTO.getCheckWay())) {
                    addErrorMsg(i, "校验方式不存在");
                    flag = false;
                }

                // 根据校验方式判断必输项
                if ("REGULAR".equals(ruleDTO.getCheckWay()) && StringUtils.isEmpty(ruleDTO.getRegularExpression())) {
                    addErrorMsg(i, "正则表达式必输");
                    flag = false;
                }
                if ("COMMON".equals(ruleDTO.getCheckWay()) && StringUtils.isEmpty(ruleDTO.getCheckItem())) {
                    addErrorMsg(i, "校验项必输");
                    flag = false;
                }

                // 根据校验项判断必输项
                String checkItem = ruleDTO.getCheckItem();
                // 除了这三个校验项外，其他校验项的比较方式和校验类型必输
                boolean isExceptCheckItem = "CONSISTENCY".equals(checkItem) || "DATA_LENGTH".equals(checkItem) || "FIELD_NOT_EMPTY".equals(checkItem);
                boolean isEmptyCountType = StringUtils.isEmpty(ruleDTO.getCountType());
                boolean isEmptyCompareWay = StringUtils.isEmpty(ruleDTO.getCompareWay());

                if (StringUtils.isNotEmpty(checkItem)
                        && !isExceptCheckItem
                        && (isEmptyCountType || isEmptyCompareWay)) {
                    addErrorMsg(i, "该校验项下校验类型和比较方式必输");
                    flag = false;
                }
                // 若为数据长度的校验项，比较方式必输
                if(StringUtils.isNotEmpty(checkItem) && "DATA_LENGTH".equals(checkItem) && isEmptyCompareWay){
                    addErrorMsg(i, "该校验项下比较方式必输");
                    flag = false;
                }
            }
        } catch (IOException e) {
            return false;
        }
        return flag;
    }
}

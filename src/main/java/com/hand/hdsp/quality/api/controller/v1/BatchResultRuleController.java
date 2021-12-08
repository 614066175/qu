package com.hand.hdsp.quality.api.controller.v1;

import com.hand.hdsp.core.constant.HdspConstant;
import com.hand.hdsp.quality.api.dto.BatchResultRuleDTO;
import com.hand.hdsp.quality.config.SwaggerTags;
import com.hand.hdsp.quality.domain.entity.BatchResultRule;
import com.hand.hdsp.quality.domain.repository.BatchResultRuleRepository;
import io.choerodon.core.domain.Page;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.mybatis.pagehelper.annotation.SortDefault;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.mybatis.pagehelper.domain.Sort;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.*;
import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

/**
 * <p>批数据方案结果表-规则信息 管理 API</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:52
 */
@Api(tags = SwaggerTags.BATCH_RESULT_RULE)
@RestController("batchResultRuleController.v1")
@RequestMapping("/v1/{organizationId}/batch-result-rules")
public class BatchResultRuleController extends BaseController {

    private final BatchResultRuleRepository batchResultRuleRepository;

    public BatchResultRuleController(BatchResultRuleRepository batchResultRuleRepository) {
        this.batchResultRuleRepository = batchResultRuleRepository;
    }

    @ApiOperation(value = "批数据方案结果表-规则信息列表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping
    public ResponseEntity<?> list(@PathVariable(name = "organizationId") Long tenantId,
                                  @RequestParam(name = "projectId", defaultValue = HdspConstant.DEFAULT_PROJECT_ID_STR) Long projectId,
                                  BatchResultRuleDTO batchResultRuleDTO, @ApiIgnore @SortDefault(value = BatchResultRule.FIELD_RESULT_RULE_ID,
            direction = Sort.Direction.DESC) PageRequest pageRequest) {
        batchResultRuleDTO.setTenantId(tenantId);
        batchResultRuleDTO.setProjectId(projectId);
        Page<BatchResultRuleDTO> list = batchResultRuleRepository.pageAndSortDTO(pageRequest, batchResultRuleDTO);
        return Results.success(list);
    }

    @ApiOperation(value = "批数据方案结果表-规则信息明细")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    ), @ApiImplicitParam(
            name = "resultRuleId",
            value = "批数据方案结果表-规则信息主键",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/{resultRuleId}")
    public ResponseEntity<?> detail(@PathVariable Long resultRuleId) {
        BatchResultRuleDTO batchResultRuleDTO = batchResultRuleRepository.selectDTOByPrimaryKeyAndTenant(resultRuleId);
        return Results.success(batchResultRuleDTO);
    }

    @ApiOperation(value = "创建批数据方案结果表-规则信息")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping
    public ResponseEntity<?> create(@PathVariable("organizationId") Long tenantId,
                                    @RequestParam(name = "projectId", defaultValue = HdspConstant.DEFAULT_PROJECT_ID_STR) Long projectId,
                                    @RequestBody BatchResultRuleDTO batchResultRuleDTO) {
        batchResultRuleDTO.setTenantId(tenantId);
        batchResultRuleDTO.setProjectId(projectId);
        this.validObject(batchResultRuleDTO);
        batchResultRuleRepository.insertDTOSelective(batchResultRuleDTO);
        return Results.success(batchResultRuleDTO);
    }

    @ApiOperation(value = "修改批数据方案结果表-规则信息")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PutMapping
    public ResponseEntity<?> update(@PathVariable("organizationId") Long tenantId,
                                    @RequestParam(name = "projectId", defaultValue = HdspConstant.DEFAULT_PROJECT_ID_STR) Long projectId,
                                    @RequestBody BatchResultRuleDTO batchResultRuleDTO) {
        batchResultRuleDTO.setProjectId(projectId);
        batchResultRuleRepository.updateDTOAllColumnWhereTenant(batchResultRuleDTO, tenantId);
        return Results.success(batchResultRuleDTO);
    }

    @ApiOperation(value = "删除批数据方案结果表-规则信息")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @DeleteMapping
    public ResponseEntity<?> remove(@ApiParam(value = "租户id", required = true) @PathVariable(name = "organizationId") Long tenantId,
                                    @RequestBody BatchResultRuleDTO batchResultRuleDTO) {
        batchResultRuleDTO.setTenantId(tenantId);
        batchResultRuleRepository.deleteByPrimaryKey(batchResultRuleDTO);
        return Results.success();
    }
}

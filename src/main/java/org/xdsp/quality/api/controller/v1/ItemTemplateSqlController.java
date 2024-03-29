package org.xdsp.quality.api.controller.v1;

import io.choerodon.core.domain.Page;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.mybatis.pagehelper.annotation.SortDefault;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.mybatis.pagehelper.domain.Sort;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.xdsp.core.constant.XdspConstant;
import org.xdsp.quality.api.dto.ItemTemplateSqlDTO;
import org.xdsp.quality.domain.entity.ItemTemplateSql;
import org.xdsp.quality.domain.repository.ItemTemplateSqlRepository;
import springfox.documentation.annotations.ApiIgnore;

/**
 * <p>校验项模板SQL表 管理 API</p>
 *
 * @author feng.liu01@hand-china.com 2020-06-08 20:08:41
 */
@RestController("itemTemplateSqlController.v1")
@RequestMapping("/v1/{organizationId}/item-template-sqls")
public class ItemTemplateSqlController extends BaseController {

    private final ItemTemplateSqlRepository itemTemplateSqlRepository;

    public ItemTemplateSqlController(ItemTemplateSqlRepository itemTemplateSqlRepository) {
        this.itemTemplateSqlRepository = itemTemplateSqlRepository;
    }

    @ApiOperation(value = "校验项模板SQL表列表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping
    public ResponseEntity<?> list(@PathVariable(name = "organizationId") Long tenantId,
                                  @RequestParam(name = "projectId", defaultValue = XdspConstant.DEFAULT_PROJECT_ID_STR) Long projectId,
                                  ItemTemplateSqlDTO itemTemplateSqlDTO, @ApiIgnore @SortDefault(value = ItemTemplateSql.FIELD_SQL_ID,
            direction = Sort.Direction.DESC) PageRequest pageRequest) {
        itemTemplateSqlDTO.setTenantId(tenantId);
        itemTemplateSqlDTO.setProjectId(projectId);
        Page<ItemTemplateSqlDTO> list = itemTemplateSqlRepository.pageAndSortDTO(pageRequest, itemTemplateSqlDTO);
        return Results.success(list);
    }

    @ApiOperation(value = "校验项模板SQL表明细")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    ), @ApiImplicitParam(
            name = "sqlId",
            value = "校验项模板SQL表主键",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/{sqlId}")
    public ResponseEntity<?> detail(@PathVariable Long sqlId) {
        ItemTemplateSqlDTO itemTemplateSqlDTO = itemTemplateSqlRepository.selectDTOByPrimaryKeyAndTenant(sqlId);
        return Results.success(itemTemplateSqlDTO);
    }

    @ApiOperation(value = "创建校验项模板SQL表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping
    public ResponseEntity<?> create(@PathVariable("organizationId") Long tenantId,
                                    @RequestParam(name = "projectId", defaultValue = XdspConstant.DEFAULT_PROJECT_ID_STR) Long projectId,
                                    @RequestBody ItemTemplateSqlDTO itemTemplateSqlDTO) {
        itemTemplateSqlDTO.setTenantId(tenantId);
        itemTemplateSqlDTO.setProjectId(projectId);
        this.validObject(itemTemplateSqlDTO);
        itemTemplateSqlRepository.insertDTOSelective(itemTemplateSqlDTO);
        return Results.success(itemTemplateSqlDTO);
    }

    @ApiOperation(value = "修改校验项模板SQL表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PutMapping
    public ResponseEntity<?> update(@PathVariable("organizationId") Long tenantId,
                                    @RequestParam(name = "projectId", defaultValue = XdspConstant.DEFAULT_PROJECT_ID_STR) Long projectId,
                                    @RequestBody ItemTemplateSqlDTO itemTemplateSqlDTO) {
        itemTemplateSqlDTO.setProjectId(projectId);
        itemTemplateSqlRepository.updateDTOAllColumnWhereTenant(itemTemplateSqlDTO, tenantId);
        return Results.success(itemTemplateSqlDTO);
    }

    @ApiOperation(value = "删除校验项模板SQL表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @DeleteMapping
    public ResponseEntity<?> remove(@ApiParam(value = "租户id", required = true) @PathVariable(name = "organizationId") Long tenantId,
                                    @RequestBody ItemTemplateSqlDTO itemTemplateSqlDTO) {
        itemTemplateSqlDTO.setTenantId(tenantId);
        itemTemplateSqlRepository.deleteByPrimaryKey(itemTemplateSqlDTO);
        return Results.success();
    }
}

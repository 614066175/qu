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
import org.hzero.export.annotation.ExcelExport;
import org.hzero.export.vo.ExportParam;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.xdsp.core.constant.XdspConstant;
import org.xdsp.quality.api.dto.AssigneeUserDTO;
import org.xdsp.quality.api.dto.StandardApprovalDTO;
import org.xdsp.quality.app.service.RootService;
import org.xdsp.quality.domain.entity.Root;
import org.xdsp.quality.domain.repository.RootRepository;
import org.xdsp.quality.infra.export.dto.RootExportDTO;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 词根 管理 API
 *
 * @author xin.sheng01@china-hand.com 2022-11-21 14:28:19
 */
@RestController("rootController.v1")
@RequestMapping("/v1/{organizationId}/roots")
public class RootController extends BaseController {

    private final RootRepository rootRepository;
    private final RootService rootService;

    public RootController(RootRepository rootRepository, RootService rootService) {
        this.rootRepository = rootRepository;
        this.rootService = rootService;
    }

    @ApiOperation(value = "词根列表")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping
    public ResponseEntity<Page<Root>> list(@PathVariable(name = "organizationId") Long tenantId,
                                           @RequestParam(name = "projectId", defaultValue = XdspConstant.DEFAULT_PROJECT_ID_STR) Long projectId,
                                           @ApiIgnore @SortDefault(value = Root.FIELD_ID, direction = Sort.Direction.DESC) PageRequest pageRequest,
                                           Root root) {
        root.setTenantId(tenantId);
        root.setProjectId(projectId);
        Page<Root> list = rootService.list(pageRequest, root);
        return Results.success(list);
    }

    @ApiOperation(value = "词根明细")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/{id}")
    public ResponseEntity<Root> detail(@PathVariable Long id) {
        return Results.success(rootService.detail(id));
    }

    @ApiOperation(value = "创建词根")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping
    public ResponseEntity<Root> create(@PathVariable(name = "organizationId") Long tenantId,
                                       @RequestParam(name = "projectId", defaultValue = XdspConstant.DEFAULT_PROJECT_ID_STR) Long projectId,
                                       @RequestBody Root root) {
        root.setTenantId(tenantId);
        root.setProjectId(projectId);
        validObject(root);
        rootService.create(root);
        return Results.success(root);
    }

    @ApiOperation(value = "修改词根")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PutMapping
    public ResponseEntity<Root> update(@RequestBody Root root) {
        rootService.update(root);
        return Results.success(root);
    }

    @ApiOperation(value = "删除词根")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @DeleteMapping
    public ResponseEntity<?> remove(@RequestBody Root root) {
        rootService.delete(root);
        return Results.success();
    }

    @ApiOperation(value = "批量删除词根")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @DeleteMapping("/batch-delete")
    public ResponseEntity<?> batchDelete(@RequestBody List<Root> rootList) {
        rootService.batchDelete(rootList);
        return Results.success();
    }

    @ApiOperation(value = "导出")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/export")
    @ExcelExport(value = RootExportDTO.class)
    public ResponseEntity<List<RootExportDTO>> export(@ApiParam(value = "租户id", required = true) @PathVariable(name = "organizationId") Long tenantId,
                                                     @RequestParam(name = "projectId", defaultValue = XdspConstant.DEFAULT_PROJECT_ID_STR) Long projectId,
                                                     Root root,
                                                     ExportParam exportParam,
                                                     HttpServletResponse response) {
        response.addHeader("Access-Control-Expose-Headers", "Content-Disposition");
        root.setTenantId(tenantId);
        root.setProjectId(projectId);
        return Results.success(rootService.export(root, exportParam));
    }

    @ApiOperation(value = "发布 下线")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PutMapping("/publish-off")
    public ResponseEntity<List<Root>> publishOrOff(@PathVariable(name = "organizationId") Long tenantId,
                                                   @RequestParam(name = "projectId", defaultValue = XdspConstant.DEFAULT_PROJECT_ID_STR) Long projectId,
                                                   @RequestBody List<Root> rootList) {
        rootList.forEach(root -> {
            root.setTenantId(tenantId);
            root.setProjectId(projectId);
            rootService.publishOrOff(root);
        });
        return Results.success(rootList);
    }

    @ApiOperation(value = "词根发布工作流回调事件接口")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/online-workflow-callback/{rootId}")
    public ResponseEntity<Root> onlineWorkflowCallback(@PathVariable(name = "organizationId") Long tenantId,
                                                      @PathVariable Long rootId,@RequestParam String nodeApproveResult) {
        rootService.onlineWorkflowCallback(rootId,nodeApproveResult);
        return Results.success();
    }

    @ApiOperation(value = "词根下线工作流回调事件接口")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/offline-workflow-callback/{rootId}")
    public ResponseEntity<Root> offlineWorkflowCallback(@PathVariable(name = "organizationId") Long tenantId,
                                                       @PathVariable Long rootId,@RequestParam String nodeApproveResult) {
        rootService.offlineWorkflowCallback(rootId,nodeApproveResult);
        return Results.success();
    }

    @ApiOperation(value = "根据词根ID查询责任人，用于审批规则")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/find-charger/{chargeId}")
    public ResponseEntity<List<AssigneeUserDTO>> findCharger(@PathVariable Long chargeId) {
        return Results.success(rootService.findCharger(chargeId));
    }

    @ApiOperation(value = "申请信息-审批表单用")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/root-apply-info")
    public ResponseEntity<StandardApprovalDTO> rootApplyInfo(@PathVariable("organizationId") Long tenantId,
                                                             Long approvalId) {
        return Results.success(rootService.rootApplyInfo(tenantId, approvalId));
    }

    @ApiOperation(value = "词根信息-审批表单用")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/root-info")
    public ResponseEntity<Root> rootInfo(@PathVariable(name = "organizationId") Long tenantId,
                                         Long approvalId) {
        return Results.success(rootService.rootInfo(approvalId));
    }

    @ApiOperation(value = "词根匹配")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/analyzer-word")
    public ResponseEntity<String> analyzerWord(@PathVariable(name = "organizationId") Long tenantId,
                                               @RequestParam(name = "projectId", defaultValue = XdspConstant.DEFAULT_PROJECT_ID_STR) Long projectId,
                                               String word) {
        return Results.success(rootService.analyzerWord(tenantId, projectId, word));
    }

    @ApiOperation(value = "词根翻译")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/root-translate")
    public ResponseEntity<List<String>> rootTranslate(@PathVariable(name = "organizationId") Long tenantId,
                                             @RequestParam(name = "projectId", defaultValue = XdspConstant.DEFAULT_PROJECT_ID_STR) Long projectId,
                                             String word) {
        return Results.success(rootService.rootTranslate(tenantId, projectId, word));
    }


}

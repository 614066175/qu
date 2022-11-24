package com.hand.hdsp.quality.api.controller.v1;

import org.hzero.core.util.Results;
import org.hzero.core.base.BaseController;

import com.hand.hdsp.core.constant.HdspConstant;
import com.hand.hdsp.core.util.DataSecurityUtil;
import com.hand.hdsp.quality.api.dto.RootGroupDTO;
import com.hand.hdsp.quality.app.service.RootService;
import com.hand.hdsp.quality.domain.entity.Root;
import com.hand.hdsp.quality.domain.repository.RootRepository;
import com.hand.hdsp.quality.infra.mapper.RootMapper;
import io.swagger.annotations.ApiParam;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.hzero.export.annotation.ExcelExport;
import org.hzero.export.vo.ExportParam;
import org.hzero.starter.driver.core.security.SecurityHelper;

import io.choerodon.core.domain.Page;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.mybatis.pagehelper.annotation.SortDefault;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.mybatis.pagehelper.domain.Sort;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.ApiOperation;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;
import javax.servlet.http.HttpServletResponse;

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
                                              @RequestParam(name = "projectId", defaultValue = HdspConstant.DEFAULT_PROJECT_ID_STR) Long projectId,
                                              @ApiIgnore @SortDefault(value = Root.FIELD_ID, direction = Sort.Direction.DESC) PageRequest pageRequest,
                                              Root root) {
        root.setTenantId(tenantId);
        root.setProjectId(projectId);
        Page<Root> list = rootService.list(pageRequest,root);
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
                                       @RequestParam(name = "projectId", defaultValue = HdspConstant.DEFAULT_PROJECT_ID_STR) Long projectId,
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

    @ApiOperation(value = "导出")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/export")
    @ExcelExport(value = RootGroupDTO.class)
    public ResponseEntity<List<RootGroupDTO>> export(@ApiParam(value = "租户id", required = true) @PathVariable(name = "organizationId") Long tenantId,
                                                          @RequestParam(name = "projectId", defaultValue = HdspConstant.DEFAULT_PROJECT_ID_STR) Long projectId,
                                                          Root root,
                                                          ExportParam exportParam,
                                                          HttpServletResponse response) {
        response.addHeader("Access-Control-Expose-Headers", "Content-Disposition");
        root.setTenantId(tenantId);
        root.setProjectId(projectId);
        return Results.success(rootService.export(root,exportParam));
    }

}

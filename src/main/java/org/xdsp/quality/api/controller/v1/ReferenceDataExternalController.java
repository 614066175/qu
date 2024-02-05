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
import org.hzero.core.base.Result;
import org.hzero.core.util.Results;
import org.hzero.export.annotation.ExcelExport;
import org.hzero.export.vo.ExportParam;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.xdsp.core.constant.XdspConstant;
import org.xdsp.quality.api.dto.ReferenceDataDTO;
import org.xdsp.quality.app.service.ReferenceDataService;
import org.xdsp.quality.domain.entity.ReferenceData;
import org.xdsp.quality.infra.export.dto.ReferenceDataExportDTO;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * <p>参考数据头表 管理 API</p>
 *
 * @author fuqiang.luo@hand-china.com 2023-04-14 16:23:35
 */
@RestController("referenceDataExternalController.v1")
@RequestMapping("/v1/reference-data/external")
public class ReferenceDataExternalController extends BaseController {

    private final ReferenceDataService referenceDataService;

    public ReferenceDataExternalController(ReferenceDataService referenceDataService) {
        this.referenceDataService = referenceDataService;
    }


    @ApiOperation(value = "发布参考数据的回调接口-工作流使用")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(permissionLogin = true)
    @PostMapping("/release-callback")
    public ResponseEntity<?> releaseCallback(@RequestParam(name = "organizationId") Long tenantId,
                                             @RequestParam Long recordId,
                                             @RequestParam String nodeApproveResult) {
        Result<?> result = new Result<>();
        try {
            referenceDataService.releaseCallback(tenantId, recordId, nodeApproveResult);
            result.setSuccess(true);
        } catch (Exception e) {
            result.setSuccess(false);
        }
        return Results.success(result);
    }

    @ApiOperation(value = "下线参考数据的回调接口-工作流使用")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(permissionLogin = true)
    @PostMapping("/offline-callback")
    public ResponseEntity<?> offlineCallback(@RequestParam(name = "organizationId") Long tenantId,
                                             @RequestParam Long recordId,
                                             @RequestParam String nodeApproveResult) {

        Result<?> result = new Result<>();
        try {
            referenceDataService.offlineCallback(tenantId, recordId, nodeApproveResult);
            result.setSuccess(true);
        } catch (Exception e) {
            result.setSuccess(false);
        }
        return Results.success(result);
    }
}

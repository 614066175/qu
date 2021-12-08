package com.hand.hdsp.quality.api.controller.v1;

import com.hand.hdsp.core.constant.HdspConstant;
import org.hzero.core.util.Results;
import org.hzero.core.base.BaseController;
import com.hand.hdsp.quality.domain.entity.NameExecHisDetail;
import com.hand.hdsp.quality.api.dto.NameExecHisDetailDTO;
import com.hand.hdsp.quality.domain.repository.NameExecHisDetailRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.choerodon.core.domain.Page;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.mybatis.pagehelper.annotation.SortDefault;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.mybatis.pagehelper.domain.Sort;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import springfox.documentation.annotations.ApiIgnore;

/**
 * <p>命名标准执行历史详情表 管理 API</p>
 *
 * @author 张鹏 2020-11-27 16:35:40
 */
@RestController("nameExecHisDetailController.v1")
@RequestMapping("/v1/{organizationId}/name-exec-his-details")
public class NameExecHisDetailController extends BaseController {

    private NameExecHisDetailRepository nameExecHisDetailRepository;

    public NameExecHisDetailController(NameExecHisDetailRepository nameExecHisDetailRepository) {
        this.nameExecHisDetailRepository = nameExecHisDetailRepository;
    }

    @ApiOperation(value = "命名标准执行历史详情表列表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping
    public ResponseEntity<Page<NameExecHisDetailDTO>> list(@PathVariable(name = "organizationId") Long tenantId,
                                                           @RequestParam(name = "projectId", defaultValue = HdspConstant.DEFAULT_PROJECT_ID_STR) Long projectId,
                                                           NameExecHisDetailDTO nameExecHisDetailDTO, @ApiIgnore @SortDefault(value = NameExecHisDetail.FIELD_DETAIL_ID,
            direction = Sort.Direction.DESC) PageRequest pageRequest) {
        nameExecHisDetailDTO.setTenantId(tenantId);
        nameExecHisDetailDTO.setProjectId(projectId);
        Page<NameExecHisDetailDTO> list = nameExecHisDetailRepository.pageAndSortDTO(pageRequest, nameExecHisDetailDTO);
        return Results.success(list);
    }

    @ApiOperation(value = "命名标准执行历史详情表明细")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "organizationId",
            value = "租户",
            paramType = "path",
            required = true
    ), @ApiImplicitParam(
            name = "detailId",
            value = "命名标准执行历史详情表主键",
            paramType = "path",
            required = true
    )})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/{detailId}")
    public ResponseEntity<NameExecHisDetailDTO> detail(@PathVariable Long detailId) {
        NameExecHisDetailDTO nameExecHisDetailDTO = nameExecHisDetailRepository.selectDTOByPrimaryKeyAndTenant(detailId);
        return Results.success(nameExecHisDetailDTO);
    }

}

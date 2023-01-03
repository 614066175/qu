package com.hand.hdsp.quality.api.controller.v1;

import org.hzero.core.util.Results;
import org.hzero.core.base.BaseController;

import com.hand.hdsp.quality.app.service.RootVersionService;
import com.hand.hdsp.quality.domain.entity.RootVersion;
import com.hand.hdsp.quality.domain.repository.RootVersionRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.hzero.mybatis.helper.SecurityTokenHelper;

import io.choerodon.core.domain.Page;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.mybatis.pagehelper.annotation.SortDefault;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.mybatis.pagehelper.domain.Sort;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.ApiOperation;
import springfox.documentation.annotations.ApiIgnore;

/**
 * 词根版本 管理 API
 *
 * @author xin.sheng01@china-hand.com 2022-11-21 14:28:19
 */
@RestController("rootVersionController.v1")
@RequestMapping("/v1/{organizationId}/root-versions")
public class RootVersionController extends BaseController {

    private final RootVersionRepository rootVersionRepository;
    private final RootVersionService rootVersionService;

    public RootVersionController(RootVersionRepository rootVersionRepository, RootVersionService rootVersionService) {
        this.rootVersionRepository = rootVersionRepository;
        this.rootVersionService = rootVersionService;
    }

    @ApiOperation(value = "词根版本列表")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping
    public ResponseEntity<Page<RootVersion>> list(RootVersion rootVersion, @ApiIgnore @SortDefault(value = RootVersion.FIELD_ID,
            direction = Sort.Direction.DESC) PageRequest pageRequest) {
        return Results.success(rootVersionService.list(pageRequest,rootVersion));
    }

    @ApiOperation(value = "词根版本明细")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/{id}")
    public ResponseEntity<RootVersion> detail(@PathVariable Long id) {
        RootVersion rootVersion = rootVersionRepository.detail(id);
        return Results.success(rootVersion);
    }

    @ApiOperation(value = "创建词根版本")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping
    public ResponseEntity<RootVersion> create(@RequestBody RootVersion rootVersion) {
        validObject(rootVersion);
        rootVersionRepository.insertSelective(rootVersion);
        return Results.success(rootVersion);
    }

    @ApiOperation(value = "修改词根版本")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PutMapping
    public ResponseEntity<RootVersion> update(@RequestBody RootVersion rootVersion) {
        SecurityTokenHelper.validToken(rootVersion);
        rootVersionRepository.updateByPrimaryKeySelective(rootVersion);
        return Results.success(rootVersion);
    }

    @ApiOperation(value = "删除词根版本")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @DeleteMapping
    public ResponseEntity<?> remove(@RequestBody RootVersion rootVersion) {
        SecurityTokenHelper.validToken(rootVersion);
        rootVersionRepository.deleteByPrimaryKey(rootVersion);
        return Results.success();
    }

}

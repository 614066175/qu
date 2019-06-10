package com.hand.hdsp.quantity.infra.config;

import io.choerodon.core.swagger.ChoerodonRouteData;
import io.choerodon.swagger.annotation.ChoerodonExtraData;
import io.choerodon.swagger.swagger.extra.ExtraData;
import io.choerodon.swagger.swagger.extra.ExtraDataManager;
import lombok.NoArgsConstructor;

/**
 * <p>适配config服务</p>
 *
 * @author aaron.yi
 */
@ChoerodonExtraData
@NoArgsConstructor
public class QuantityExtraDataManager implements ExtraDataManager {

    @Override
    public ExtraData getData() {
        ChoerodonRouteData choerodonRouteData = new ChoerodonRouteData();
        choerodonRouteData.setName("xqty");
        choerodonRouteData.setPath("/xqty/**");
        choerodonRouteData.setServiceId("hdsp-quantity");
        extraData.put(ExtraData.ZUUL_ROUTE_DATA, choerodonRouteData);
        return extraData;
    }
}
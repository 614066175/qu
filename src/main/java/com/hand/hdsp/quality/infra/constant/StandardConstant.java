package com.hand.hdsp.quality.infra.constant;

/**
 * <p>
 * description
 * </p>
 *
 * @author lgl 2020/11/25 14:14
 * @since 1.0
 */
public interface StandardConstant {

    interface Status{
        /**
         * 标准状态
         */
        String CREATE="CREATE";

        String ONLINE="ONLINE";

        String OFFLINE="OFFLINE";

        String OFFLINE_APPROVING="OFFLINE_APPROVING";

        String ONLINE_APPROVING="ONLINE_APPROVING";

        //----------

        /**
         * 审核状态（申请中，通过，驳回）
         */
        String APPROVING="APPROVING";
        String ADOPT="ADOPT";
        String SUBJET="SUBJET";
    }

   interface StandardType{
        String DATA="DATA";
        String FIELD="FIELD";
        String NAME="NAME";
        String DOC="DOC";
   }




}

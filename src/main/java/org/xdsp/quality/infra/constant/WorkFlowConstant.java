package org.xdsp.quality.infra.constant;

/**
 * <p>
 * description
 * </p>
 *
 * @author lgl 2021/8/17 17:25
 * @since 1.0
 */
public interface WorkFlowConstant {

    interface OpenConfig{
        // 词根工作流开关
        String ROOT_ONLINE = "XQUA.ROOT_ONLINE";

        String ROOT_OFFLINE = "XQUA.ROOT_OFFLINE";


        // 数据标准工作流开关
        String DATA_STANDARD_ONLINE= "XQUA.DATA_STANDARD_ONLINE";
        String DATA_STANDARD_OFFLINE= "XQUA.DATA_STANDARD_OFFLINE";

        //字段标准
        String FIELD_STANDARD_ONLINE="XQUA.FIELD_STANDARD_ONLINE";
        String FIELD_STANDARD_OFFLINE="XQUA.FIELD_STANDARD_OFFLINE";
    }



    interface DataStandard{
        String ONLINE_WORKFLOW_KEY="FLOW1364824631562809345";
        String OFFLINE_WORKFLOW_KEY="FLOW1365186609020002306";
    }

    interface ExternalDataStandard{
        String ONLINE_WORKFLOW_KEY="FLOW1753317103999483906";
        String OFFLINE_WORKFLOW_KEY="FLOW1753320016478044161";
    }

    interface ExternalFieldStandard{
        String ONLINE_WORKFLOW_KEY="FLOW1753322685741498369";
        String OFFLINE_WORKFLOW_KEY="FLOW1753320928206487554";
    }
    interface FieldStandard{
        String ONLINE_WORKFLOW_KEY="FLOW1427814945561071617";
        String OFFLINE_WORKFLOW_KEY="FLOW1427816537739841538";
    }

    interface Root{
        String ONLINE_WORKFLOW_KEY="FLOW1596066386452271105";
        String OFFLINE_WORKFLOW_KEY="FLOW1596068051993612289";
    }
}

package org.xdsp.quality.infra.constant;

/**
 * @author lgl
 */
public interface RootMatchConstants {

    interface MatchStatus {
        /**
         * 匹配中
         */
        String MATCHING = "MATCHING";
        /**
         * 成功
         */
        String SUCCESS = "SUCCESS";
        /**
         * 匹配失败
         */
        String FAILED = "FAILED";
        /**
         * 未匹配
         */
        String UN_MATCH = "UN_MATCH";

        /**
         * 部分匹配
         */
        String PART_MATCH = "PART_MATCH";

        /**
         * 手动维护
         */
        String MANUAL_MATCH = "MANUAL_MATCH";
    }

    interface HisStatus {
        String UPLOADED = "UPLOADED";
        String UPLOADING = "UPLOADING";
        String MATCHED = "MATCHED";
        String MATCHING = "MATCHING";
    }

    interface SourceType {
        String STANDARD = "STANDARD";
        String ROOT = "ROOT";
    }
}

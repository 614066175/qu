package com.hand.hdsp.quality.infra.constant;

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
         * excel导入
         */
        String IMPORT = "IMPORT";

        /**
         * 部分匹配
         */
        String PART_MATCH = "PART_MATCH";
    }

    interface SourceType {
        String STANDARD = "STANDARD";
        String ROOT = "ROOT";
    }
}

INSERT INTO `hdsp_dispatch`.`xdis_dispatch_theme`(`theme_id`, `theme_name`, `theme_description`, `display_sequence`, `enabled_flag`, `tenant_id`) VALUES (-7, 'QUALITY_LABEL_DEFAULT', '数据质量任务默认主题', 0, 1, 0);
INSERT INTO `hdsp_dispatch`.`xdis_dispatch_layer`(`layer_id`, `layer_name`, `layer_description`, `display_sequence`, `theme_id`, `enabled_flag`, `tenant_id`) VALUES (-7, 'QUALITY_DEFAULT_LAYER', '数据质量任务默认层次', 0, -7, 1, 0);
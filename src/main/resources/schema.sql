CREATE TABLE `image` (
	`id` bigint unsigned NOT NULL AUTO_INCREMENT PRIMARY KEY,
	`fid` varchar(50) NOT NULL,
	`width` int(11) unsigned NOT NULL,
	`height` int(11) unsigned NOT NULL,
	`format` varchar(30) NOT NULL,
	`size` int(11) unsigned NOT NULL,
	`status` tinyint unsigned NOT NULL DEFAULT 0,
	`create_time` datetime NOT NULL,
	`bucket` varchar(50) NOT NULL,
	unique key `uidx_fid` (`fid`)
) ENGINE=InnoDB ROW_FORMAT=COMPRESSED KEY_BLOCK_SIZE=4;

CREATE TABLE `message` (
	`id` int(11) unsigned NOT NULL PRIMARY KEY COMMENT '消息编号',
	`code` varchar(50) NOT NULL COMMENT '消息编码',
	`message` text COMMENT '消息正文',
	`comment` text COMMENT '注释说明',
	unique key `uidx_code` (`code`)
) ENGINE=InnoDB ROW_FORMAT=COMPRESSED KEY_BLOCK_SIZE=4;
-- --------------------------------------------------------

--
-- 表的结构 `dev_demand`
--

CREATE TABLE IF NOT EXISTS `dev_demand` (
  `id` int(11) NOT NULL auto_increment,
  `title` text NOT NULL,
  `status` varchar(20) default 'add',
  `project_id` int(11) NOT NULL,
  `bug_id` int(11) default NULL,
  `addtime` int(11) NOT NULL,
  `adduserid` int(11) NOT NULL,
  `adduser` varchar(80) NOT NULL,
  `message` text,
  PRIMARY KEY  (`id`)
) ENGINE=MyISAM  DEFAULT CHARSET=utf8 AUTO_INCREMENT=9637 ;

-- --------------------------------------------------------

--
-- 表的结构 `dev_project`
--

CREATE TABLE IF NOT EXISTS `dev_project` (
  `id` int(11) NOT NULL auto_increment,
  `name` varchar(120) NOT NULL COMMENT '项目名称',
  `svn_address` text NOT NULL COMMENT 'svn目录',
  `svn_username` varchar(50) NOT NULL COMMENT 'svn用户名',
  `svn_password` varchar(50) NOT NULL COMMENT 'svn密码',
  `upload_type` varchar(10) default NULL,
  `ftp_username` varchar(50) default NULL,
  `ftp_password` varchar(50) default NULL,
  `ftp_port` varchar(7) default NULL,
  `ftp_address` varchar(100) default NULL,
  `ftp_rootdir` varchar(200) default NULL,
  `rsync_username` varchar(50) default NULL,
  `rsync_password` varchar(50) default NULL,
  `rsync_port` varchar(7) default NULL,
  `rsync_address` varchar(200) default NULL,
  `addTime` int(11) default '0',
  `uploadTime` int(11) default '0',
  PRIMARY KEY  (`id`)
) ENGINE=MyISAM  DEFAULT CHARSET=utf8 AUTO_INCREMENT=111 ;

-- --------------------------------------------------------

--
-- 表的结构 `dev_uploadfile`
--

CREATE TABLE IF NOT EXISTS `dev_uploadfile` (
  `id` int(11) NOT NULL auto_increment,
  `demand_id` int(11) NOT NULL,
  `project_id` int(11) NOT NULL default '0',
  `upload_file` varchar(200) NOT NULL,
  `upload_version` int(11) NOT NULL,
  `upload_status` tinyint(4) default '0',
  PRIMARY KEY  (`id`)
) ENGINE=MyISAM  DEFAULT CHARSET=utf8 AUTO_INCREMENT=36363 ;

-- --------------------------------------------------------

--
-- 表的结构 `dev_upload_server`
--

CREATE TABLE IF NOT EXISTS `dev_upload_server` (
  `id` int(11) NOT NULL auto_increment,
  `upload_type` varchar(20) default NULL,
  `ftp_address` varchar(200) default NULL,
  `ftp_port` varchar(200) default NULL,
  `ftp_username` varchar(50) default NULL,
  `ftp_password` varchar(50) default NULL,
  `ftp_root_dir` varchar(50) default NULL,
  `project_id` int(11) default NULL,
  `rsync_port` varchar(200) default NULL,
  `rsync_address` varchar(200) default NULL,
  PRIMARY KEY  (`id`)
) ENGINE=MyISAM  DEFAULT CHARSET=utf8 AUTO_INCREMENT=170 ;

-- --------------------------------------------------------

--
-- 表的结构 `dev_user`
--

CREATE TABLE IF NOT EXISTS `dev_user` (
  `id` int(11) NOT NULL auto_increment,
  `username` varchar(80) NOT NULL,
  `password` varchar(40) NOT NULL,
  `email` varchar(80) NOT NULL,
  `projects` varchar(300) default '',
  `svn_username` varchar(80) NOT NULL,
  `svn_password` varchar(40) default NULL,
  `perm` text,
  PRIMARY KEY  (`id`)
) ENGINE=MyISAM  DEFAULT CHARSET=utf8 AUTO_INCREMENT=24 ;

-- --------------------------------------------------------

--
-- 表的结构 `play_evolutions`
--

CREATE TABLE IF NOT EXISTS `play_evolutions` (
  `id` int(11) NOT NULL,
  `hash` varchar(255) NOT NULL,
  `applied_at` timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  `apply_script` text,
  `revert_script` text,
  `state` varchar(255) default NULL,
  `last_problem` text,
  PRIMARY KEY  (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- 表的结构 `user`
--

CREATE TABLE IF NOT EXISTS `user` (
  `id` int(11) NOT NULL auto_increment,
  `username` varchar(80) NOT NULL,
  `password` varchar(40) NOT NULL,
  `email` varchar(80) NOT NULL,
  `svn_username` varchar(80) NOT NULL,
  PRIMARY KEY  (`id`)
) ENGINE=MyISAM  DEFAULT CHARSET=utf8 AUTO_INCREMENT=5 ;

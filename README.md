#isvn-upload
***
isvn-upload是一个基于SVN和rsync的简易上传程序，可以将svn指定版本的文件通过rsync上传到制定的服务器。<br />

###特性
*** 
 1. 支持多台服务器同步
 2. 支持回滚操作

###安装
***
 1. 安装sbt<br />
    官网: http://www.scala-sbt.org/
    版本：< 13.0
 2. 下载play2web开发框架<br />
    官网: http://www.playframework.com/
    版本：2.10
 3. 进入isvn-upload源代码目录执行play,进入控制台后运行start {port} <br />

> 运行方式可参考[playframework官方说明文档][play2run]

###上传步骤
***
 1. 创建项目: 项目管理 -> 添加新项目<br />
  ![Alt][step1]<br />
 2. 添加上传服务器：项目管理 -> 选择已创建好的项目 -> 添加服务器<br />
  ![Alt][step2]<br />
 3. 选择要上传的文件：上线单 -> 添加上线单 <br />
  ![Alt][step3]<br />
 4. 同步上线单：进入上线单列表 -> 同步
  ![Alt][step4]<br />

提交同步后会返回rsync同步的相关信息，点击上线单详情可以查看。
 

[step1]: https://raw.github.com/icecode/ISVN-upload/master/md-images/st_1.jpg
[step2]: https://raw.github.com/icecode/ISVN-upload/master/md-images/st_22.jpg
[step3]: https://raw.github.com/icecode/ISVN-upload/master/md-images/st_2.jpg
[step4]: https://raw.github.com/icecode/ISVN-upload/master/md-images/st_223.jpg
[play2run]: http://www.playframework.com/documentation/2.2.x/Production



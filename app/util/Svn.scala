package util

import org.tmatesoft.svn.core.io.SVNRepositoryFactory
import org.tmatesoft.svn.core.SVNURL
import org.tmatesoft.svn.core.wc.{SVNUpdateClient, SVNWCUtil}
import org.tmatesoft.svn.core.SVNException
import org.tmatesoft.sqljet.core.SqlJetException

/**
 * Created with IntelliJ IDEA.
 * User: bing19880122@gmail.com
 * Date: 13-1-8
 * Time: 下午2:52
 */
object Svn {

  def getUpdateClient(svnUsername:String, svnPassword:String) = {
    val svnOption = SVNWCUtil.createDefaultOptions(true)
    val authManager = SVNWCUtil.createDefaultAuthenticationManager(svnUsername, svnPassword)
    new SVNUpdateClient(authManager, svnOption)
  }
}

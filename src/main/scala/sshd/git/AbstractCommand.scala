/*
 * Copyright (c) 2011 Denis Bardadym
 * Distributed under Apache License.
 */

package sshd.git

import org.apache.sshd.server.{Environment, ExitCallback, Command}
import org.eclipse.jgit.lib.RepositoryCache.FileKey
import org.eclipse.jgit.util.FS
import org.eclipse.jgit.lib.{Repository, RepositoryCache}
import java.io.{File, OutputStream, InputStream}
import actors.Actor
import org.eclipse.jgit.transport.{ReceivePack, UploadPack}
import main.Main
import net.liftweb.common.Loggable

abstract sealed class AbstractCommand extends Command with Loggable {

  protected var in: InputStream = null
  protected var out: OutputStream = null
  protected var err: OutputStream = null

  protected var callback: ExitCallback = null

  def setInputStream(in: InputStream) {
    this.in = in
  }

  def destroy() {}

  def setExitCallback(callback: ExitCallback) {
    this.callback = callback
  }

  def start(env: Environment) = {
    new Actor {

      def act() {
        try {
          run(env)
        } finally {
          in.close();
          out.close();
          err.close();
          callback.onExit(0);
        }
      }

    }.start();
  }


  def run(env: Environment)

  def setErrorStream(err: OutputStream) {
    this.err = err
  }

  def setOutputStream(out: OutputStream) {
    this.out = out
  }
}


case class Upload(repoPath: String) extends AbstractCommand {
  def run(env: Environment) = {
    val repo: Repository = RepositoryCache.open(
      FileKey.lenient(new File(Main.repoDir + repoPath), FS.DETECTED))
    val up = new UploadPack(repo)
    up.upload(in, out, err)
  }
}

case class Receive(repoPath: String) extends AbstractCommand {
  def run(env: Environment) = {
    val repo: Repository = RepositoryCache.open(
      FileKey.lenient(new File(Main.repoDir + repoPath), FS.DETECTED))
    val rp = new ReceivePack(repo)

    rp.setAllowCreates(true)
    rp.setAllowDeletes(true)
    rp.setAllowNonFastForwards(true)
    rp.setCheckReceivedObjects(true)

    rp.receive(in, out, err)
  }
}
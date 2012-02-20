/*
   Copyright 2012 Denis Bardadym

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/
package code.snippet

import bootstrap.liftweb._
import net.liftweb._
import common._
import record.Field
import util.Helpers._
import http._
import code.model._
import com.foursquare.rogue.Rogue._
import js.jquery.JqJE._
import js.JsCmds._
import util._
import xml._
import SnippetHelper._

/**
 * User: denis.bardadym
 * Date: 9/27/11
 * Time: 5:29 PM
 */

class AdminUserOps(up: WithUser) extends Loggable with SshKeyUI with UserUI {
  
  

  def renderUserForm = w(up.user) { user =>
    userForm(user, "Update", updateUser(user, u => S.redirectTo("/admin/" + u.login.get)))
  }
  
  def renderSshKeysTable = w(up.user) {user =>
    keysTable(user.keys)
  }

 
  def renderAddKeyForm = w(up.user) {user => {
  	val newKey = SshKeyUserDoc.createRecord.ownerId(user.id.get)
  	sshKeyForm(newKey, "Add", saveSshKey(newKey))
  }}
  

  

  def renderDeleteUser = w(up.user) {user => 
    "button" #> SHtml.button("Delete", deleteUser(user), "class" -> "button")}

  

}
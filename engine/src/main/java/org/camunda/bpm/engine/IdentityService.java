/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.camunda.bpm.engine;

import java.util.List;
import java.util.Map;

import org.camunda.bpm.engine.identity.Group;
import org.camunda.bpm.engine.identity.GroupQuery;
import org.camunda.bpm.engine.identity.Picture;
import org.camunda.bpm.engine.identity.User;
import org.camunda.bpm.engine.identity.UserQuery;
import org.camunda.bpm.engine.impl.identity.Account;


/**
 * Service to manage {@link User}s and {@link Group}s.
 * 
 * @author Tom Baeyens
 * @author Daniel Meyer
 * 
 */
public interface IdentityService {

  /**
   * <p>Allows to inquire whether this identity service implementation provides 
   * read-only access to the user repository, false otherwise.</p>
   * 
   * Read only identity service implementations do not support the following methods:
   * <ul>
   * <li> {@link #newUser(String)} </li>
   * <li> {@link #saveUser(User)} </li>
   * <li> {@link #deleteUser(String)} </li>

   * <li> {@link #newGroup(String)} </li>
   * <li> {@link #saveGroup(Group)} </li>
   * <li> {@link #deleteGroup(String)} </li>
   *  
   * <li> {@link #createMembership(String, String)} </li>
   * <li> {@link #deleteMembership(String, String)} </li>
   * </ul>
   * 
   * <p>If these methods are invoked on a read-only identity service implementation, 
   * the invocation will throw an {@link UnsupportedOperationException}.</p>
   * 
   * @return true if this identity service implementation provides read-only
   *         access to the user repository, false otherwise.
   */
  public boolean isReadOnly();
  
  /**
   * Creates a new user. The user is transient and must be saved using 
   * {@link #saveUser(User)}.
   * @param userId id for the new user, cannot be null.
   * @throws UnsupportedOperationException if identity service implementation is read only. See {@link #isReadOnly()}
   */
  User newUser(String userId);
  
  /**
   * Saves the user. If the user already existed, the user is updated.
   * @param user user to save, cannot be null.
   * @throws RuntimeException when a user with the same name already exists.
   * @throws UnsupportedOperationException if identity service implementation is read only. See {@link #isReadOnly()}
   */
  void saveUser(User user);
  
  /**
   * Creates a {@link UserQuery} that allows to programmatically query the users.
   */
  UserQuery createUserQuery();
  
  /**
   * @param userId id of user to delete, cannot be null. When an id is passed
   * for an unexisting user, this operation is ignored.
   * @throws UnsupportedOperationException if identity service implementation is read only. See {@link #isReadOnly()}
   */
  void deleteUser(String userId);
  
  /**
   * Creates a new group. The group is transient and must be saved using 
   * {@link #saveGroup(Group)}.
   * @param groupId id for the new group, cannot be null.
   * @throws UnsupportedOperationException if identity service implementation is read only. See {@link #isReadOnly()}
   */
  Group newGroup(String groupId);
  
  /**
   * Creates a {@link GroupQuery} thats allows to programmatically query the groups.
   */
  GroupQuery createGroupQuery();
  
  /**
   * Saves the group. If the group already existed, the group is updated.
   * @param group group to save. Cannot be null.
   * @throws RuntimeException when a group with the same name already exists.
   * @throws UnsupportedOperationException if identity service implementation is read only. See {@link #isReadOnly()}
   */
  void saveGroup(Group group);
  
  /**
   * Deletes the group. When no group exists with the given id, this operation
   * is ignored.
   * @param groupId id of the group that should be deleted, cannot be null.
   * @throws UnsupportedOperationException if identity service implementation is read only. See {@link #isReadOnly()}
   */
  void deleteGroup(String groupId);

  /**
   * @param userId the userId, cannot be null.
   * @param groupId the groupId, cannot be null.
   * @throws RuntimeException when the given user or group doesn't exist or when the user
   * is already member of the group.
   * @throws UnsupportedOperationException if identity service implementation is read only. See {@link #isReadOnly()}
   */
  void createMembership(String userId, String groupId);
  
  /**
   * Delete the membership of the user in the group. When the group or user don't exist 
   * or when the user is not a member of the group, this operation is ignored.
   * @param userId the user's id, cannot be null.
   * @param groupId the group's id, cannot be null.
   * @throws UnsupportedOperationException if identity service implementation is read only. See {@link #isReadOnly()}
   */
  void deleteMembership(String userId, String groupId);

  /**
   * Checks if the password is valid for the given user. Arguments userId
   * and password are nullsafe.
   */
  boolean checkPassword(String userId, String password);

  /** 
   * Passes the authenticated user id for this particular thread.
   * All service method (from any service) invocations done by the same
   * thread will have access to this authenticatedUserId. 
   */
  void setAuthenticatedUserId(String authenticatedUserId);
  
  /** Sets the picture for a given user.
   * @throws ProcessEngineException if the user doesn't exist.
   * @param picture can be null to delete the picture. */
  void setUserPicture(String userId, Picture picture);

  /** Retrieves the picture for a given user.
   * @throws ProcessEngineException if the user doesn't exist.
   * @returns null if the user doesn't have a picture. */
  Picture getUserPicture(String userId);
  
  /** Deletes the picture for a given user. If the user does not have a picture or if the user doesn't exists the call is ignored. 
   * @throws ProcessEngineException if the user doesn't exist. */
  void deleteUserPicture(String userId);

  /** Generic extensibility key-value pairs associated with a user */
  void setUserInfo(String userId, String key, String value);
  
  /** Generic extensibility key-value pairs associated with a user */
  String getUserInfo(String userId, String key);

  /** Generic extensibility keys associated with a user */
  List<String> getUserInfoKeys(String userId);

  /** Delete an entry of the generic extensibility key-value pairs associated with a user */
  void deleteUserInfo(String userId, String key);

  /** Store account information for a remote system */
  @Deprecated
  void setUserAccount(String userId, String userPassword, String accountName, String accountUsername, String accountPassword, Map<String, String> accountDetails);
  
  /** Get account names associated with the given user */
  @Deprecated
  List<String> getUserAccountNames(String userId);

  /** Get account information associated with a user */
  @Deprecated
  Account getUserAccount(String userId, String userPassword, String accountName);

  /** Delete an entry of the generic extensibility key-value pairs associated with a user */
  @Deprecated
  void deleteUserAccount(String userId, String accountName);
}

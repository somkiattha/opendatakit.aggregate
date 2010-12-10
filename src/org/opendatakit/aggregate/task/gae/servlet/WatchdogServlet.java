/*
 * Copyright (C) 2010 University of Washington
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.opendatakit.aggregate.task.gae.servlet;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.opendatakit.aggregate.ContextFactory;
import org.opendatakit.aggregate.constants.BeanDefs;
import org.opendatakit.aggregate.constants.ServletConsts;
import org.opendatakit.aggregate.exception.ODKExternalServiceException;
import org.opendatakit.aggregate.exception.ODKFormNotFoundException;
import org.opendatakit.aggregate.servlet.ServletUtilBase;
import org.opendatakit.aggregate.task.WatchdogWorkerImpl;
import org.opendatakit.common.persistence.Datastore;
import org.opendatakit.common.persistence.exception.ODKDatastoreException;
import org.opendatakit.common.security.User;
import org.opendatakit.common.security.UserService;

/**
 * 
 * @author wbrunette@gmail.com
 * @author mitchellsundt@gmail.com
 * 
 */
public class WatchdogServlet extends ServletUtilBase{
  /**
   * Serial number for serialization
   */
  private static final long serialVersionUID = 4295412985320942609L;

  /**
   * URI from base
   */
  public static final String ADDR = "watchdog";
  
  /**
   * Handler for HTTP Get request to run watchdog task
   * 
   * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest,
   *      javax.servlet.http.HttpServletResponse)
   */
  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

    // get parameter
    String checkIntervalString = getParameter(req, ServletConsts.CHECK_INTERVAL_PARAM);
    if (checkIntervalString == null) {
      errorMissingKeyParam(resp);
      return;
    }
    long checkIntervalMilliseconds = Long.parseLong(checkIntervalString);
    
    UserService userService = (UserService) ContextFactory.get().getBean(BeanDefs.USER_BEAN);
    User user = userService.getDaemonAccountUser();

    System.out.println("STARTING WATCHDOG TASK");
    Datastore ds = (Datastore) ContextFactory.get().getBean(BeanDefs.DATASTORE_BEAN);
    
    WatchdogWorkerImpl worker = new WatchdogWorkerImpl(checkIntervalMilliseconds, getServerURL(req), ds, user);
    try {
      worker.checkTasks();
    } catch (ODKExternalServiceException e) {
      e.printStackTrace();
      resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.toString());
      return;
    } catch (ODKFormNotFoundException e) {
      odkIdNotFoundError(resp);
      return;
    } catch (ODKDatastoreException e) {
      e.printStackTrace();
      resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.toString());
      return;
    }
  }
}

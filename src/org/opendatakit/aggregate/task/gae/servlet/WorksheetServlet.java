/*
 * Copyright (C) 2009 Google Inc. 
 * Copyright (C) 2010 University of Washington.
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
import org.opendatakit.aggregate.constants.externalservice.ExternalServiceConsts;
import org.opendatakit.aggregate.constants.externalservice.ExternalServiceOption;
import org.opendatakit.aggregate.exception.ODKExternalServiceException;
import org.opendatakit.aggregate.exception.ODKFormNotFoundException;
import org.opendatakit.aggregate.form.Form;
import org.opendatakit.aggregate.servlet.ServletUtilBase;
import org.opendatakit.aggregate.task.WorksheetCreatorWorkerImpl;
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
public class WorksheetServlet extends ServletUtilBase {

  /**
   * Serial number for serialization
   */
  private static final long serialVersionUID = 3054003683995535651L;

  /**
   * URI from base
   */
  public static final String ADDR = "worksheet";

  /**
   * Handler for HTTP Get request to create xform upload page
   * 
   * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest,
   *      javax.servlet.http.HttpServletResponse)
   */
  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

    UserService userService = (UserService) ContextFactory.get().getBean(BeanDefs.USER_BEAN);
    User user = userService.getCurrentUser();

    // get parameter
    String formId = getParameter(req, ServletConsts.FORM_ID);
    if (formId == null) {
      errorMissingKeyParam(resp);
      return;
    }

    String spreadsheetName = getParameter(req, ExternalServiceConsts.EXT_SERV_ADDRESS);
    if (spreadsheetName == null) {
      sendErrorNotEnoughParams(resp);
      return;
    }

    String esTypeString = getParameter(req, ServletConsts.EXTERNAL_SERVICE_TYPE);
    if (esTypeString == null) {
      sendErrorNotEnoughParams(resp);
      return;
    }
    ExternalServiceOption esType = ExternalServiceOption.valueOf(esTypeString);
    if (esType == null) {
      resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid "
          + ServletConsts.EXTERNAL_SERVICE_TYPE);
      return;
    }

    Datastore ds = (Datastore) ContextFactory.get().getBean(BeanDefs.DATASTORE_BEAN);

    // get form
    Form form;
    try {
      form = Form.retrieveForm(formId, ds, user);
    } catch (ODKFormNotFoundException e) {
      odkIdNotFoundError(resp);
      return;
    }

    WorksheetCreatorWorkerImpl ws = new WorksheetCreatorWorkerImpl(getServerURL(req), spreadsheetName, esType, form, ds, user);

    try {
      ws.worksheetCreator();
    } catch (ODKExternalServiceException e) {
      e.printStackTrace();
      resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.toString());
      return;
    } catch (ODKDatastoreException e) {
      e.printStackTrace();
      resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.toString());
      return;
    }

  }

}

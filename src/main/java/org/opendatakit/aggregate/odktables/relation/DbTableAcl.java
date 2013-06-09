/*
 * Copyright (C) 2012-2013 University of Washington
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

package org.opendatakit.aggregate.odktables.relation;

import java.util.ArrayList;
import java.util.List;

import org.opendatakit.aggregate.odktables.entity.TableRole;
import org.opendatakit.common.ermodel.simple.Entity;
import org.opendatakit.common.ermodel.simple.Query;
import org.opendatakit.common.ermodel.simple.Relation;
import org.opendatakit.common.persistence.DataField;
import org.opendatakit.common.persistence.DataField.DataType;
import org.opendatakit.common.persistence.DataField.IndexType;
import org.opendatakit.common.persistence.exception.ODKDatastoreException;
import org.opendatakit.common.web.CallingContext;

public class DbTableAcl {

  public static final String TABLE_ID = "TABLE_ID";
  public static final String SCOPE_TYPE = "SCOPE_TYPE";
  public static final String SCOPE_VALUE = "SCOPE_VALUE";
  public static final String ROLE = "ROLE";

  private static final String RELATION_NAME = "TABLE_ACL";

  private static final List<DataField> dataFields;
  static {
    dataFields = new ArrayList<DataField>();
    dataFields.add(new DataField(TABLE_ID, DataType.STRING, false).setIndexable(IndexType.HASH));
    dataFields.add(new DataField(SCOPE_TYPE, DataType.STRING, false));
    dataFields.add(new DataField(SCOPE_VALUE, DataType.STRING, true));
    dataFields.add(new DataField(ROLE, DataType.STRING, false));
  }

  public static Relation getRelation(CallingContext cc) throws ODKDatastoreException {
    Relation relation = new Relation(RUtil.NAMESPACE, RELATION_NAME, dataFields, cc);
    return relation;
  }

  public static List<Entity> query(String tableId, CallingContext cc) throws ODKDatastoreException {
    Query query = getRelation(cc).query("DbTableAcl.query()", cc);
    query.equal(DbTableAcl.TABLE_ID, tableId);
    return query.execute();
  }

  public static List<Entity> query(String tableId, String scopeType, CallingContext cc)
      throws ODKDatastoreException {
    Query query = getRelation(cc).query("DbTableAcl.query()", cc);
    query.equal(DbTableAcl.TABLE_ID, tableId);
    query.equal(DbTableAcl.SCOPE_TYPE, scopeType);
    return query.execute();
  }

  public static List<Entity> queryNotEqual(TableRole role, CallingContext cc)
      throws ODKDatastoreException {
    Query query = getRelation(cc).query("DbTableAcl.query(TableRole)", cc);
    query.notEqual(ROLE, role.name());

    return query.execute();
  }

  /**
   * Retrieves the acl entity for a given table and scope.
   *
   * @param tableId
   * @param scopeType
   * @param scopeValue
   * @param cc
   * @return the acl entity, or null if none exists
   * @throws ODKDatastoreException
   */
  public static Entity getAcl(String tableId, String scopeType, String scopeValue, CallingContext cc)
      throws ODKDatastoreException {
    Query query = getRelation(cc).query("DbTableAcl.getAcl()", cc);
    query.equal(DbTableAcl.TABLE_ID, tableId);
    query.equal(DbTableAcl.SCOPE_TYPE, scopeType);
    query.equal(DbTableAcl.SCOPE_VALUE, scopeValue);
    Entity acl = query.get();

    return acl;
  }
}

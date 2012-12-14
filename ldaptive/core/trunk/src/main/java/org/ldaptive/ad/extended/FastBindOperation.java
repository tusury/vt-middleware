/*
  $Id$

  Copyright (C) 2003-2012 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware Services
  Email:   middleware@vt.edu
  Version: $Revision$
  Updated: $Date$
*/
package org.ldaptive.ad.extended;

import org.ldaptive.AbstractOperation;
import org.ldaptive.Connection;
import org.ldaptive.ConnectionInitializer;
import org.ldaptive.LdapException;
import org.ldaptive.Response;

/**
 * Executes an active directory fast bind operation.
 * See http://msdn.microsoft.com/en-us/library/cc223503(v=prot.20).aspx.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class FastBindOperation extends AbstractOperation<FastBindRequest, Void>
{


  /**
   * Creates a new fast bind operation.
   *
   * @param  conn  connection
   */
  public FastBindOperation(final Connection conn)
  {
    super(conn);
  }


  /** {@inheritDoc} */
  @Override
  @SuppressWarnings("unchecked")
  protected Response<Void> invoke(final FastBindRequest request)
    throws LdapException
  {
    return (Response<Void>)
      getConnection().getProviderConnection().extendedOperation(request);
  }


  /**
   * Connection initializer that executes the {@link FastBindOperation}.
   */
  public static class FastBindConnectionInitializer
    implements ConnectionInitializer
  {


    /** {@inheritDoc} */
    @Override
    public Response<Void> initialize(final Connection c)
      throws LdapException
    {
      final FastBindOperation op = new FastBindOperation(c);
      return op.execute(new FastBindRequest());
    }
  }
}

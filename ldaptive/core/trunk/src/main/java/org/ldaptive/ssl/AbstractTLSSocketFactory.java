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
package org.ldaptive.ssl;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.security.GeneralSecurityException;
import javax.net.ssl.HandshakeCompletedListener;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

/**
 * Provides common implementation for TLSSocketFactory.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public abstract class AbstractTLSSocketFactory extends SSLSocketFactory
{

  /** Default SSL protocol, value is {@value}. */
  public static final String DEFAULT_PROTOCOL = "TLS";

  /** SSLSocketFactory used for creating SSL sockets. */
  protected SSLSocketFactory factory;

  /** Hostname verifier for this socket factory. */
  private HostnameVerifier hostnameVerifier;

  /** Handshake completed listeners. */
  private HandshakeCompletedListener[] handshakeCompletedListeners;

  /** Enabled cipher suites. */
  private String[] cipherSuites;

  /** Enabled protocol versions. */
  private String[] enabledProtocols;


  /**
   * Prepares this socket factory for use. Must be called before factory can be
   * used.
   *
   * @throws  GeneralSecurityException  if the factory cannot be initialized
   */
  public abstract void initialize()
    throws GeneralSecurityException;


  /**
   * Returns the underlying SSL socket factory that this class uses for creating
   * SSL Sockets.
   *
   * @return  SSL socket factory
   */
  public SSLSocketFactory getFactory()
  {
    return factory;
  }


  /**
   * Returns the hostname verifier to invoke when sockets are created.
   *
   * @return  hostname verifier
   */
  public HostnameVerifier getHostnameVerifier()
  {
    return hostnameVerifier;
  }


  /**
   * Sets the hostname verifier to invoke when sockets are created.
   *
   * @param  verifier  for SSL hostnames
   */
  public void setHostnameVerifier(final HostnameVerifier verifier)
  {
    hostnameVerifier = verifier;
  }


  /**
   * Returns the handshake completed listeners to add when sockets are created.
   *
   * @return  handshake completed listeners
   */
  public HandshakeCompletedListener[] getHandshakeCompletedListeners()
  {
    return handshakeCompletedListeners;
  }


  /**
   * Sets the handshake completed listeners to add when sockets are created.
   *
   * @param  listeners  for SSL handshake events
   */
  public void setHandshakeCompletedListeners(
    final HandshakeCompletedListener ... listeners)
  {
    handshakeCompletedListeners = listeners;
  }


  /**
   * Returns the names of the SSL cipher suites which are currently enabled for
   * use on sockets created by this factory. A null value indicates that no
   * specific cipher suites have been enabled and that the default suites are in
   * use.
   *
   * @return  cipher suites
   */
  public String[] getEnabledCipherSuites()
  {
    return cipherSuites;
  }


  /**
   * Sets the cipher suites enabled for use on sockets created by this factory.
   * See {@link javax.net.ssl.SSLSocket#setEnabledCipherSuites(String[])}.
   *
   * @param  suites  cipher suites
   */
  public void setEnabledCipherSuites(final String[] suites)
  {
    cipherSuites = suites;
  }


  /**
   * Returns the names of the protocol versions which are currently enabled for
   * use on sockets created by this factory. A null value indicates that no
   * specific protocols have been enabled and that the default protocols are in
   * use.
   *
   * @return  enabled protocols
   */
  public String[] getEnabledProtocols()
  {
    return enabledProtocols;
  }


  /**
   * Sets the protocol versions enabled for use on sockets created by this
   * factory. See {@link javax.net.ssl.SSLSocket#setEnabledProtocols(String[])}.
   *
   * @param  protocols  enabled protocols
   */
  public void setEnabledProtocols(final String[] protocols)
  {
    enabledProtocols = protocols;
  }


  /**
   * Initializes the supplied socket for use.
   *
   * @param  socket  SSL socket to initialize
   *
   * @return  SSL socket
   *
   * @throws  IOException  if an I/O error occurs when initializing the socket
   */
  protected SSLSocket initSSLSocket(final SSLSocket socket)
    throws IOException
  {
    if (cipherSuites != null) {
      socket.setEnabledCipherSuites(cipherSuites);
    }
    if (enabledProtocols != null) {
      socket.setEnabledProtocols(enabledProtocols);
    }
    if (handshakeCompletedListeners != null) {
      for (HandshakeCompletedListener listener : handshakeCompletedListeners) {
        socket.addHandshakeCompletedListener(listener);
      }
    }
    if (hostnameVerifier != null) {
      // calling getSession() will initiate the handshake if necessary
      final String hostname = socket.getSession().getPeerHost();
      if (!hostnameVerifier.verify(hostname, socket.getSession())) {
        socket.close();
        socket.getSession().invalidate();
        throw new SSLPeerUnverifiedException(
          String.format(
            "Hostname '%s' does not match the hostname in the server's " +
            "certificate", hostname));
      }
    }
    return socket;
  }


  /**
   * Returns a socket layered over an existing socket connected to the named
   * host, at the given port.
   *
   * @param  socket  existing socket
   * @param  host  server hostname
   * @param  port  server port
   * @param  autoClose  close the underlying socket when this socket is closed
   *
   * @return  socket connected to the specified host and port
   *
   * @throws  IOException  if an I/O error occurs when creating the socket
   */
  public Socket createSocket(
    final Socket socket,
    final String host,
    final int port,
    final boolean autoClose)
    throws IOException
  {
    return initSSLSocket(
      (SSLSocket) factory.createSocket(socket, host, port, autoClose));
  }


  /**
   * Creates an unconnected socket.
   *
   * @return  unconnected socket
   *
   * @throws  IOException  if an I/O error occurs when creating the socket
   */
  public Socket createSocket()
    throws IOException
  {
    return initSSLSocket((SSLSocket) factory.createSocket());
  }


  /**
   * Creates a socket and connects it to the specified port number at the
   * specified address.
   *
   * @param  host  server hostname
   * @param  port  server port
   *
   * @return  socket connected to the specified host and port
   *
   * @throws  IOException  if an I/O error occurs when creating the socket
   */
  public Socket createSocket(final InetAddress host, final int port)
    throws IOException
  {
    return initSSLSocket((SSLSocket) factory.createSocket(host, port));
  }


  /**
   * Creates a socket and connect it to the specified port number at the
   * specified address. The socket will also be bound to the supplied local
   * address and port.
   *
   * @param  address  server hostname
   * @param  port  server port
   * @param  localAddress  client hostname
   * @param  localPort  client port
   *
   * @return  socket connected to the specified host and port
   *
   * @throws  IOException  if an I/O error occurs when creating the socket
   */
  public Socket createSocket(
    final InetAddress address,
    final int port,
    final InetAddress localAddress,
    final int localPort)
    throws IOException
  {
    return initSSLSocket(
      (SSLSocket) factory.createSocket(address, port, localAddress, localPort));
  }


  /**
   * Creates a socket and connects it to the specified port number at the
   * specified address.
   *
   * @param  host  server hostname
   * @param  port  server port
   *
   * @return  socket connected to the specified host and port
   *
   * @throws  IOException  if an I/O error occurs when creating the socket
   */
  public Socket createSocket(final String host, final int port)
    throws IOException
  {
    return initSSLSocket((SSLSocket) factory.createSocket(host, port));
  }


  /**
   * Creates a socket and connect it to the specified port number at the
   * specified address. The socket will also be bound to the supplied local
   * address and port.
   *
   * @param  host  server hostname
   * @param  port  server port
   * @param  localHost  client hostname
   * @param  localPort  client port
   *
   * @return  socket connected to the specified host and port
   *
   * @throws  IOException  if an I/O error occurs when creating the socket
   */
  public Socket createSocket(
    final String host,
    final int port,
    final InetAddress localHost,
    final int localPort)
    throws IOException
  {
    return initSSLSocket(
      (SSLSocket) factory.createSocket(host, port, localHost, localPort));
  }


  /**
   * Returns the list of cipher suites which are enabled by default.
   *
   * @return  cipher suites
   */
  public String[] getDefaultCipherSuites()
  {
    return factory.getDefaultCipherSuites();
  }


  /**
   * Returns the names of the cipher suites which could be enabled for use on an
   * SSL connection.
   *
   * @return  cipher suites
   */
  public String[] getSupportedCipherSuites()
  {
    return factory.getSupportedCipherSuites();
  }
}

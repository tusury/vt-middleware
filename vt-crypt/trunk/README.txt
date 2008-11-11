CRYPT 1.5 README

    This is the 1.5 release of the VT Crypt Java libraries.
    If you have questions or comments about this library contact
    Daniel Fisher (dfisher@vt.edu)

DESCRIPTION
    CRYPT is a set of Java libraries which provide easy access to
    cryptographic functions.
    See the JavaDocs for method specifics.
    This library has many command-line interfaces,
    look in the edu.vt.middleware.crypt.commandline package.
    In addition each algorithm can also be executed from the command-line:
    java edu.vt.middleware.crypt.symmetric.AES
    See the javadocs for how each algorithm is configured.

REQUIRED SOFTWARE
    JDK 1.2, JDK 1.3, JDK 1.4, or JDK 1.5
    If using JDK 1.2 or 1.3 you must provide the JCE libs
    This project provides three different JCE providers in the lib
      directory.  You only need the one that corresponds to your version
      of the JDK.
    Some algorithms require 'stronger' crypto than the default policy files
      allow.
    To fix this, download the unlimited strength policy files from Sun.
    http://java.sun.com/products/jce/index-14.html#UnlimitedDownload
    For more information about the Bouncy Castle provides, see:
    http://www.bouncycastle.org

BUILDING
    A build.xml file is provided for building with ant.
    Edit the build.properties file to set the build options.
    To build the distribution run:
      build tar OR build zip
    This will create a release tarball in the dist directory, which is
      set in the build.properties file.
    Copy the tarball to the location you which to install from
      and then extract it.

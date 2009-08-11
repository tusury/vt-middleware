/*
  $Id$

  Copyright (C) 2008-2009 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware
  Email:   middleware@vt.edu
  Version: $Revision$
  Updated: $Date$
*/
package edu.vt.middleware.crypt.x509;

import java.util.ArrayList;
import java.util.List;

import edu.vt.middleware.crypt.x509.types.AuthorityKeyIdentifier;
import edu.vt.middleware.crypt.x509.types.BasicConstraints;
import edu.vt.middleware.crypt.x509.types.GeneralName;
import edu.vt.middleware.crypt.x509.types.GeneralNameList;
import edu.vt.middleware.crypt.x509.types.GeneralNameType;
import edu.vt.middleware.crypt.x509.types.KeyIdentifier;
import edu.vt.middleware.crypt.x509.types.KeyPurposeId;
import edu.vt.middleware.crypt.x509.types.KeyPurposeIdList;
import edu.vt.middleware.crypt.x509.types.KeyUsage;
import edu.vt.middleware.crypt.x509.types.NoticeReference;
import edu.vt.middleware.crypt.x509.types.PolicyInformation;
import edu.vt.middleware.crypt.x509.types.PolicyInformationList;
import edu.vt.middleware.crypt.x509.types.PolicyQualifierInfo;
import edu.vt.middleware.crypt.x509.types.UserNotice;

import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.DEREncodable;
import org.bouncycastle.asn1.DERIA5String;
import org.bouncycastle.asn1.DERInteger;

/**
 * Creates X.509v3 extension types in the VT Crypt namespace from
 * BouncyCastle ASN1Encodable types.
 *
 * @author Middleware
 * @version $Revision$
 *
 */
public final class ExtensionFactory
{
  /** Private default constructor for utility class */
  private ExtensionFactory() {}


  /**
   * Creates an instance of a VT Crypt X.509v3 extension type from the
   * corresponding Bouncy Castle extension type.
   *
   * @param  type  Type of extension.
   * @param  encodedExtension  DER encoded data representing the extension
   * field data.
   *
   * @return  An extension type from the
   * <code>edu.vt.middleware.crypt.x509.types</code> package that is
   * semantically equivalent to the given Bouncy Castle object.
   *
   * @throws  IllegalArgumentException  If given ASN.1 encodable object is
   * not compatible with the given extension type.
   */
  public static Object createInstance(
      final DEREncodable encodedExtension,
      final ExtensionType type)
  {
    Object extension = null;
    try {
      switch (type) {
      case AuthorityInfoAccess:
        break;
      case AuthorityKeyIdentifier:
        extension = createAuthorityKeyIdentifier(encodedExtension);
        break;
      case BasicConstraints:
        extension = createBasicConstraints(encodedExtension);
        break;
      case CertificatePolicies:
        extension = createPolicyInformationList(encodedExtension);
        break;
      case CRLDistributionPoints:
        break;
      case ExtendedKeyUsage:
        extension = createKeyPurposeIdList(encodedExtension);
        break;
      case IssuerAlternativeName:
      case SubjectAlternativeName:
        extension = createGeneralNameList(encodedExtension);
        break;
      case KeyUsage:
        extension = createKeyUsage(encodedExtension);
        break;
      case PolicyConstraints:
        break;
      case PolicyMappings:
        break;
      case PrivateKeyUsagePeriod:
        break;
      case SubjectDirectoryAttributes:
        break;
      case SubjectKeyIdentifier:
        extension = createKeyIdentifier(encodedExtension);
        break;
      default:
        break;
      }
    } catch (Exception e) {
      throw new IllegalArgumentException(
          String.format("%s is not compatible with %s.",
              encodedExtension.getClass().getName(), type, e));
    }
    return extension;
  }


  /**
   * Creates a {@link GeneralNameList} object from DER data.
   *
   * @param  enc  DER encoded general names data.
   *
   * @return  Collection of general names.
   */
  public static GeneralNameList createGeneralNameList(final DEREncodable enc)
  {
    final List<GeneralName> nameList = new ArrayList<GeneralName>();
    for (org.bouncycastle.asn1.x509.GeneralName name :
      org.bouncycastle.asn1.x509.GeneralNames.getInstance(enc).getNames())
    {
      nameList.add(new GeneralName(
          name.getName().toString(),
          GeneralNameType.fromTagNumber(name.getTagNo())));
    }
    return new GeneralNameList(nameList);
  }


  /**
   * Creates a {@link BasicConstraints} object from DER data.
   *
   * @param  enc  DER encoded basic constraints data.
   *
   * @return  Basic constraints.
   */
  public static BasicConstraints createBasicConstraints(final DEREncodable enc)
  {
    final org.bouncycastle.asn1.x509.BasicConstraints constraints =
      org.bouncycastle.asn1.x509.BasicConstraints.getInstance(enc);
    if (constraints.getPathLenConstraint() != null) {
      return new BasicConstraints(
          constraints.isCA(),
          constraints.getPathLenConstraint().intValue());
    } else {
      return new BasicConstraints(constraints.isCA());
    }
  }


  /**
   * Creates a {@link PolicyInformationList} object from DER data.
   *
   * @param  enc  DER encoded policy information data;
   * must be <code>ASN1Sequence</code>.
   *
   * @return  Certificate policy information listing.
   */
  public static PolicyInformationList createPolicyInformationList(
    final DEREncodable enc)
  {
    if (!(enc instanceof ASN1Sequence)) {
      throw new IllegalArgumentException(
        "Expected ASN1Sequence but got " + enc);
    }
    final ASN1Sequence seq = (ASN1Sequence) enc;
    final List<PolicyInformation> policies =
      new ArrayList<PolicyInformation>(seq.size());
    for (int i = 0; i < seq.size(); i++) {
      policies.add(createPolicyInformation(seq.getObjectAt(i)));
    }
    return new PolicyInformationList(
        policies.toArray(new PolicyInformation[policies.size()]));
  }


  /**
   * Creates a {@link PolicyInformation} object from DER data.
   *
   * @param  enc  DER encoded policy information data.
   *
   * @return  Certificate policy information object.
   */
  public static PolicyInformation createPolicyInformation(
    final DEREncodable enc)
  {
    final org.bouncycastle.asn1.x509.PolicyInformation info =
      org.bouncycastle.asn1.x509.PolicyInformation.getInstance(enc);
    final ASN1Sequence encodedQualifiers = info.getPolicyQualifiers();
    if (encodedQualifiers != null) {
      final int size = encodedQualifiers.size();
      final List<PolicyQualifierInfo> qualifiers =
        new ArrayList<PolicyQualifierInfo>(size);
      for (int i = 0; i < size; i++) {
        final DEREncodable item = encodedQualifiers.getObjectAt(i);
        qualifiers.add(createPolicyQualifierInfo(item));
      }
      return new PolicyInformation(
        info.getPolicyIdentifier().toString(),
        qualifiers.toArray(new PolicyQualifierInfo[size]));
    } else {
      return new PolicyInformation(info.getPolicyIdentifier().toString());
    }
  }


  /**
   * Creates a {@link PolicyQualifierInfo} object from DER data.
   *
   * @param  enc  DER encoded policy information data.
   *
   * @return  Certificate policy qualifier.
   */
  public static PolicyQualifierInfo createPolicyQualifierInfo(
    final DEREncodable enc)
  {
    final org.bouncycastle.asn1.x509.PolicyQualifierInfo policyQualifier =
      org.bouncycastle.asn1.x509.PolicyQualifierInfo.getInstance(enc);
    final DEREncodable qualifier = policyQualifier.getQualifier();
    if (qualifier instanceof DERIA5String) {
      return new PolicyQualifierInfo(qualifier.toString());
    } else {
      return new PolicyQualifierInfo(createUserNotice(qualifier));
    }
  }


  /**
   * Creates a {@link UserNotice} object from DER data.
   *
   * @param  enc  DER encoded user notice; must be <code>ASN1Sequence</code>.
   *
   * @return  User notice.
   */
  public static UserNotice createUserNotice(final DEREncodable enc)
  {
    if (!(enc instanceof ASN1Sequence)) {
      throw new IllegalArgumentException(
        "Expected ASN1Sequence but got " + enc);
    }
    final ASN1Sequence seq = (ASN1Sequence) enc;
    UserNotice result = null;
    if (seq.size() == 0) {
      // Bouncy Castle will throw an exception if sequence size is 0
      // which is reasonable, since an empty user notice is nonsense.
      // However this is not strictly conformant to RFC 2459 section 4.2.1.5
      // where both UserNotice fields are optional, which would allow
      // for an empty notice.
      // We allow an empty UserNotice to be more strictly conformant.
      result = new UserNotice();
    } else {
      final org.bouncycastle.asn1.x509.UserNotice notice =
        new org.bouncycastle.asn1.x509.UserNotice(seq);
      if (notice.getExplicitText() != null) {
        if (notice.getNoticeRef() != null) {
          result = new UserNotice(
            createNoticeReference(notice.getNoticeRef()),
            notice.getExplicitText().getString());
        } else {
          result = new UserNotice(notice.getExplicitText().getString());
        }
      } else {
        // UserNotice must contain NoticeReference since
        // there is no explicitText yet seq has non-zero size
        result = new UserNotice(createNoticeReference(notice.getNoticeRef()));
      }
    }
    return result;
  }


  /**
   * Creates a {@link NoticeReference} object from DER data.
   *
   * @param  enc  DER encoded user notice; must be either
   * <code>ASN1Sequence</code> or
   * <code>org.bouncycastle.asn1.x509.NoticeReference</code> object.
   *
   * @return  Notice reference.
   */
  public static NoticeReference createNoticeReference(final DEREncodable enc)
  {
    final org.bouncycastle.asn1.x509.NoticeReference notRef =
      org.bouncycastle.asn1.x509.NoticeReference.getInstance(enc);
    // Build the array of notice numbers
    final int[] notNums = new int[notRef.getNoticeNumbers().size()];
    for (int i = 0; i < notNums.length; i++) {
      final DERInteger num =
        (DERInteger) notRef.getNoticeNumbers().getObjectAt(i);
      notNums[i] = num.getValue().intValue();
    }
    return new NoticeReference(notRef.getOrganization().toString(), notNums);
  }


  /**
   * Creates a {@link KeyIdentifier} object from DER data.
   *
   * @param  enc  DER encoded policy information data;
   * must be <code>ASN1OctetString</code>.
   *
   * @return  Key identifier.
   */
  public static KeyIdentifier createKeyIdentifier(final DEREncodable enc)
  {
    if (!(enc instanceof ASN1OctetString)) {
      throw new IllegalArgumentException(
        "Expected ASN1OctetString but got " + enc);
    }
    final ASN1OctetString os = (ASN1OctetString) enc;
    return new KeyIdentifier(os.getOctets());
  }


  /**
   * Creates a {@link AuthorityKeyIdentifier} object from DER data.
   *
   * @param  enc  DER encoded authority key identifier data.
   *
   * @return  Authority key identifier.
   */
  public static AuthorityKeyIdentifier createAuthorityKeyIdentifier(
    final DEREncodable enc)
  {
    final org.bouncycastle.asn1.x509.AuthorityKeyIdentifier aki =
      org.bouncycastle.asn1.x509.AuthorityKeyIdentifier.getInstance(enc);
    KeyIdentifier keyIdentifier = null;
    if (aki.getKeyIdentifier() != null) {
      keyIdentifier = new KeyIdentifier(aki.getKeyIdentifier());
    }
    GeneralNameList issuerNames = null;
    if (aki.getAuthorityCertIssuer() != null) {
      issuerNames = createGeneralNameList(aki.getAuthorityCertIssuer());
    }
    Integer issuerSerial = null;
    if (aki.getAuthorityCertSerialNumber() != null) {
      issuerSerial = aki.getAuthorityCertSerialNumber().intValue();
    }
    return new AuthorityKeyIdentifier(keyIdentifier, issuerNames, issuerSerial);
  }


  /**
   * Creates a {@link KeyUsage} object from DER data.
   *
   * @param  enc  DER encoded key usage data.
   *
   * @return  Key usage data.
   */
  public static KeyUsage createKeyUsage(final DEREncodable enc)
  {
    final DERBitString usage =
      org.bouncycastle.asn1.x509.KeyUsage.getInstance(enc);
    return new KeyUsage(usage.getBytes());
  }


  /**
   * Creates a {@link KeyPurposeIdList} object from DER data.
   *
   * @param  enc  DER encoded key purpose identifier data.
   *
   * @return  Key purpose ID list object.
   */
  public static KeyPurposeIdList createKeyPurposeIdList(final DEREncodable enc)
  {
    final org.bouncycastle.asn1.x509.ExtendedKeyUsage usages =
      org.bouncycastle.asn1.x509.ExtendedKeyUsage.getInstance(enc);
    final List<KeyPurposeId> idList = new ArrayList<KeyPurposeId>();
    for (Object usage : usages.getUsages()) {
      idList.add(KeyPurposeId.getByOid(usage.toString()));
    }
    return new KeyPurposeIdList(idList);
  }
}

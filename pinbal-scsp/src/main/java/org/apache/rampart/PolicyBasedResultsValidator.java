/*      */ package org.apache.rampart;
/*      */ 
/*      */ import java.math.BigInteger;
/*      */ import java.security.cert.X509Certificate;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Calendar;
/*      */ import java.util.Date;
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Vector;
/*      */ import javax.security.auth.x500.X500Principal;
/*      */ import javax.xml.namespace.QName;
/*      */ import org.apache.axiom.om.OMNamespace;
/*      */ import org.apache.axiom.om.xpath.AXIOMXPath;
/*      */ import org.apache.axiom.soap.SOAPEnvelope;
/*      */ import org.apache.axis2.context.MessageContext;
/*      */ import org.apache.commons.logging.Log;
/*      */ import org.apache.rampart.policy.RampartPolicyData;
/*      */ import org.apache.rampart.policy.SupportingPolicyData;
/*      */ import org.apache.rampart.util.RampartUtil;
/*      */ import org.apache.ws.secpolicy.model.IssuedToken;
/*      */ import org.apache.ws.secpolicy.model.SignedEncryptedParts;
/*      */ import org.apache.ws.secpolicy.model.SupportingToken;
/*      */ import org.apache.ws.secpolicy.model.Token;
/*      */ import org.apache.ws.secpolicy.model.UsernameToken;
/*      */ import org.apache.ws.secpolicy.model.X509Token;
/*      */ import org.apache.ws.security.SOAP11Constants;
/*      */ import org.apache.ws.security.SOAP12Constants;
/*      */ import org.apache.ws.security.WSDataRef;
/*      */ import org.apache.ws.security.WSEncryptionPart;
/*      */ import org.apache.ws.security.WSSecurityEngineResult;
/*      */ import org.apache.ws.security.WSSecurityException;
/*      */ import org.apache.ws.security.components.crypto.Crypto;
/*      */ import org.apache.ws.security.components.crypto.CryptoType;
/*      */ import org.apache.ws.security.components.crypto.CryptoType.TYPE;
/*      */ import org.apache.ws.security.message.token.Timestamp;
/*      */ import org.apache.ws.security.util.WSSecurityUtil;
/*      */ import org.jaxen.JaxenException;
/*      */ import org.jaxen.XPath;
/*      */ import org.w3c.dom.Document;
/*      */ import org.w3c.dom.Element;
/*      */ import org.w3c.dom.Node;
/*      */ import org.w3c.dom.NodeList;
/*      */ 
/*      */ public class PolicyBasedResultsValidator implements ExtendedPolicyValidatorCallbackHandler
/*      */ {
/*   48 */   private static Log log = org.apache.commons.logging.LogFactory.getLog(PolicyBasedResultsValidator.class);
/*      */   
/*      */   public void validate(ValidatorData data, Vector results) throws RampartException
/*      */   {
/*   52 */     List<WSSecurityEngineResult> resultsList = new ArrayList(results);
/*   53 */     validate(data, resultsList);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void validate(ValidatorData data, List<WSSecurityEngineResult> results)
/*      */     throws RampartException
/*      */   {
/*   62 */     RampartMessageData rmd = data.getRampartMessageData();
/*      */     
/*   64 */     RampartPolicyData rpd = rmd.getPolicyData();
/*      */     
/*      */ 
/*      */ 
/*   68 */     if ((rpd != null) && (results == null)) {
/*   69 */       throw new RampartException("noSecurityResults");
/*      */     }
/*      */     
/*      */ 
/*   73 */     WSSecurityEngineResult tsResult = null;
/*   74 */     if ((rpd != null) && (rpd.isIncludeTimestamp())) {
/*   75 */       tsResult = WSSecurityUtil.fetchActionResult(results, 32);
/*      */       
/*   77 */       if ((tsResult == null) && (!rpd.isIncludeTimestampOptional())) {
/*   78 */         throw new RampartException("timestampMissing");
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*   84 */     List<WSEncryptionPart> encryptedParts = RampartUtil.getEncryptedParts(rmd);
               System.out.println(">>>>>> PolicyBasedResultsValidator.validate 0 (" +
                       "encryptedParts=" + encryptedParts.size() + ")");
/*   85 */     if ((rpd != null) && (rpd.isSignatureProtection()) && (isSignatureRequired(rmd)))
/*      */     {
/*   87 */       String sigId = RampartUtil.getSigElementId(rmd);
/*      */       
/*   89 */       encryptedParts.add(RampartUtil.createEncryptionPart("Signature", sigId, "http://www.w3.org/2000/09/xmldsig#", "Element"));
                 System.out.println(">>>>>> PolicyBasedResultsValidator.validate 1 (" +
                         "encryptedParts=" + encryptedParts.size() + ")");
/*      */     }
/*      */     
/*      */ 
/*   93 */     List<WSEncryptionPart> signatureParts = RampartUtil.getSignedParts(rmd);
/*      */     
/*      */ 
/*   96 */     if ((rpd != null) && 
/*   97 */       ((tsResult != null) || (!rpd.isIncludeTimestampOptional())) && 
/*   98 */       (rpd.isIncludeTimestamp()) && (!rpd.isTransportBinding()))
/*      */     {
/*  100 */       signatureParts.add(RampartUtil.createEncryptionPart("Timestamp", "timestamp"));
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*  105 */     if (!rmd.isInitiator())
/*      */     {
/*      */ 
/*  108 */       SupportingToken endSupportingToken = null;
/*  109 */       if (rpd != null) {
/*  110 */         endSupportingToken = rpd.getEndorsingSupportingTokens();
/*      */       }
/*      */       
/*  113 */       if ((endSupportingToken != null) && (!endSupportingToken.isOptional())) {
/*  114 */         SignedEncryptedParts endSignedParts = endSupportingToken.getSignedParts();
/*  115 */         if (((endSignedParts != null) && (!endSignedParts.isOptional()) && ((endSignedParts.isBody()) || (endSignedParts.getHeaders().size() > 0))) || (rpd.isIncludeTimestamp()))
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/*  120 */           signatureParts.add(RampartUtil.createEncryptionPart("EndorsingSupportingTokens", "EndorsingSupportingTokens"));
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*  125 */       SupportingToken sgndEndSupportingToken = null;
/*  126 */       if (rpd != null) {
/*  127 */         sgndEndSupportingToken = rpd.getSignedEndorsingSupportingTokens();
/*      */       }
/*  129 */       if ((sgndEndSupportingToken != null) && (!sgndEndSupportingToken.isOptional())) {
/*  130 */         SignedEncryptedParts sgndEndSignedParts = sgndEndSupportingToken.getSignedParts();
/*  131 */         if (((sgndEndSignedParts != null) && (!sgndEndSignedParts.isOptional()) && ((sgndEndSignedParts.isBody()) || (sgndEndSignedParts.getHeaders().size() > 0))) || (rpd.isIncludeTimestamp()))
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/*  136 */           signatureParts.add(RampartUtil.createEncryptionPart("SignedEndorsingSupportingTokens", "SignedEndorsingSupportingTokens"));
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*  141 */       if (rpd != null) {
/*  142 */         List<SupportingToken> supportingToks = rpd.getSupportingTokensList();
/*  143 */         for (SupportingToken supportingToken : supportingToks) {
/*  144 */           if ((supportingToken != null) && (!supportingToken.isOptional())) {
/*  145 */             SupportingPolicyData policyData = new SupportingPolicyData();
/*  146 */             policyData.build(supportingToken);
/*  147 */             encryptedParts.addAll(RampartUtil.getSupportingEncryptedParts(rmd, policyData));
/*  148 */             signatureParts.addAll(RampartUtil.getSupportingSignedParts(rmd, policyData));
                       System.out.println(">>>>>> PolicyBasedResultsValidator.validate 2 (" +
                               "encryptedParts=" + encryptedParts.size() + ")");
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*  154 */     validateEncrSig(data, encryptedParts, signatureParts, results);
/*      */     
/*  156 */     if ((rpd != null) && (!rpd.isTransportBinding())) {
/*  157 */       validateProtectionOrder(data, results);
/*      */     }
/*      */     
/*  160 */     validateEncryptedParts(data, encryptedParts, results);
/*      */     
/*  162 */     validateSignedPartsHeaders(data, signatureParts, results);
/*      */     
/*  164 */     validateRequiredElements(data);
/*      */     
/*      */ 
/*  167 */     if (!rmd.isInitiator()) {
/*  168 */       validateSupportingTokens(data, results);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  181 */     WSSecurityEngineResult actionResult = WSSecurityUtil.fetchActionResult(results, 2);
/*      */     
/*      */ 
/*  184 */     if (actionResult != null) {
/*  185 */       X509Certificate returnCert = (X509Certificate)actionResult.get("x509-certificate");
/*      */       
/*      */ 
/*  188 */       if ((returnCert != null) && 
/*  189 */         (!verifyTrust(returnCert, rmd))) {
/*  190 */         throw new RampartException("trustVerificationError");
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  206 */     actionResult = WSSecurityUtil.fetchActionResult(results, 32);
/*      */     
/*  208 */     if (actionResult != null) {
/*  209 */       Timestamp timestamp = (Timestamp)actionResult.get("timestamp");
/*      */       
/*      */ 
/*  212 */       if ((timestamp != null) && 
/*  213 */         (!verifyTimestamp(timestamp, rmd))) {
/*  214 */         throw new RampartException("cannotValidateTimestamp");
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void validateEncrSig(ValidatorData data, List<WSEncryptionPart> encryptedParts, List<WSEncryptionPart> signatureParts, List<WSSecurityEngineResult> results)
/*      */     throws RampartException
/*      */   {
/*  227 */     List<Integer> actions = getSigEncrActions(results);
/*  228 */     boolean sig = false;
/*  229 */     boolean encr = false;
/*  230 */     for (Object action : actions) {
/*  231 */       Integer act = (Integer)action;
/*  232 */       if (act.intValue() == 2) {
/*  233 */         sig = true;
/*  234 */       } else if (act.intValue() == 4) {
/*  235 */         encr = true;
/*      */       }
/*      */     }
/*      */     System.out.println(">>>>>> PolicyBasedResultsValidator.validateEncrSig (" +
                   "sig=" + sig + ", " +
                   "encr=" + encr + ", " +
                   "signatureParts=" + signatureParts.size() + ", " +
                   "encryptedParts=" + encryptedParts.size() + ")");
/*  239 */     RampartPolicyData rpd = data.getRampartMessageData().getPolicyData();
/*      */     
/*  241 */     SupportingToken sgndSupTokens = rpd.getSignedSupportingTokens();
/*  242 */     SupportingToken sgndEndorSupTokens = rpd.getSignedEndorsingSupportingTokens();
/*      */     
/*  244 */     if ((sig) && (signatureParts.size() == 0) && ((sgndSupTokens == null) || (sgndSupTokens.getTokens().size() == 0)) && ((sgndEndorSupTokens == null) || (sgndEndorSupTokens.getTokens().size() == 0)))
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/*  249 */       throw new RampartException("unexprectedSignature"); }
/*  250 */     if ((!sig) && (signatureParts.size() > 0))
/*      */     {
/*      */ 
/*  253 */       throw new RampartException("signatureMissing");
/*      */     }
/*      */     
/*  256 */     if ((encr) && (encryptedParts.size() == 0))
/*      */     {
/*      */ 
/*  259 */       List<WSSecurityEngineResult> list = getResults(results, 4);
/*      */       
/*  261 */       boolean encrDataFound = false;
/*  262 */       for (WSSecurityEngineResult result : list) {
/*  263 */         ArrayList dataRefURIs = (ArrayList)result.get("data-ref-uris");
/*  264 */         if ((dataRefURIs != null) && (dataRefURIs.size() != 0)) {
/*  265 */           encrDataFound = true;
/*      */         }
/*      */       }
/*      */       
/*  269 */       if ((encrDataFound) && (!isUsernameTokenPresent(data)))
/*      */       {
/*  271 */         throw new RampartException("unexprectedEncryptedPart");
/*      */       }
/*  273 */     } else if ((!encr) && (encryptedParts.size() > 0))
/*      */     {
/*      */ 
/*  276 */       throw new RampartException("encryptionMissing");
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void validateSupportingTokens(ValidatorData data, List<WSSecurityEngineResult> results)
/*      */     throws RampartException
/*      */   {
/*  288 */     RampartPolicyData rpd = data.getRampartMessageData().getPolicyData();
/*  289 */     List<SupportingToken> supportingTokens = rpd.getSupportingTokensList();
/*  290 */     for (SupportingToken suppTok : supportingTokens) {
/*  291 */       handleSupportingTokens(results, suppTok);
/*      */     }
/*  293 */     SupportingToken signedSuppToken = rpd.getSignedSupportingTokens();
/*  294 */     handleSupportingTokens(results, signedSuppToken);
/*  295 */     SupportingToken signedEndSuppToken = rpd.getSignedEndorsingSupportingTokens();
/*  296 */     handleSupportingTokens(results, signedEndSuppToken);
/*  297 */     SupportingToken endSuppToken = rpd.getEndorsingSupportingTokens();
/*  298 */     handleSupportingTokens(results, endSuppToken);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void handleSupportingTokens(List<WSSecurityEngineResult> results, SupportingToken suppTok)
/*      */     throws RampartException
/*      */   {
/*  308 */     if (suppTok == null) {
/*  309 */       return;
/*      */     }
/*      */     
/*  312 */     ArrayList tokens = suppTok.getTokens();
/*  313 */     for (Object objectToken : tokens) {
/*  314 */       Token token = (Token)objectToken;
/*  315 */       if ((token instanceof UsernameToken)) {
/*  316 */         UsernameToken ut = (UsernameToken)token;
/*      */         
/*  318 */         WSSecurityEngineResult utResult = WSSecurityUtil.fetchActionResult(results, 1);
/*  319 */         if ((utResult == null) && (!ut.isOptional())) {
/*  320 */           throw new RampartException("usernameTokenMissing");
/*      */         }
/*      */       }
/*  323 */       else if ((token instanceof IssuedToken)) {
/*  324 */         WSSecurityEngineResult samlResult = WSSecurityUtil.fetchActionResult(results, 16);
/*      */         
/*  326 */         if (samlResult == null) {
/*  327 */           log.debug("No signed SAMLToken found. Looking for unsigned SAMLTokens");
/*  328 */           samlResult = WSSecurityUtil.fetchActionResult(results, 8);
/*      */         }
/*  330 */         if (samlResult == null) {
/*  331 */           throw new RampartException("samlTokenMissing");
/*      */         }
/*  333 */       } else if ((token instanceof X509Token)) {
/*  334 */         X509Token x509Token = (X509Token)token;
/*  335 */         WSSecurityEngineResult x509Result = WSSecurityUtil.fetchActionResult(results, 4096);
/*  336 */         if ((x509Result == null) && (!x509Token.isOptional())) {
/*  337 */           throw new RampartException("binaryTokenMissing");
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void validateProtectionOrder(ValidatorData data, List<WSSecurityEngineResult> results)
/*      */     throws RampartException
/*      */   {
/*  353 */     String protectionOrder = data.getRampartMessageData().getPolicyData().getProtectionOrder();
/*  354 */     List<Integer> sigEncrActions = getSigEncrActions(results);
/*      */     
/*  356 */     if (sigEncrActions.size() < 2)
/*      */     {
/*  358 */       return;
/*      */     }
/*      */     
/*  361 */     boolean sigNotPresent = true;
/*  362 */     boolean encrNotPresent = true;
/*      */     
/*  364 */     for (Object sigEncrAction : sigEncrActions) {
/*  365 */       Integer act = (Integer)sigEncrAction;
/*  366 */       if (act.intValue() == 2) {
/*  367 */         sigNotPresent = false;
/*  368 */       } else if (act.intValue() == 4) {
/*  369 */         encrNotPresent = false;
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*  374 */     if ((sigNotPresent) || (encrNotPresent)) {
/*  375 */       return;
/*      */     }
/*      */     
/*      */ 
/*  379 */     boolean done = false;
/*  380 */     boolean encrFound; if ("SignBeforeEncrypting".equals(protectionOrder))
/*      */     {
/*  382 */       boolean sigFound = false;
/*  383 */       Iterator iter = sigEncrActions.iterator();
/*  384 */       while ((iter.hasNext()) || (!done)) {
/*  385 */         Integer act = (Integer)iter.next();
/*  386 */         if ((act.intValue() == 4) && (!sigFound)) {
/*      */           break;
/*      */         }
/*      */         
/*  390 */         if (act.intValue() == 2) {
/*  391 */           sigFound = true;
/*  392 */         } else if (sigFound)
/*      */         {
/*  394 */           done = true;
/*      */         }
/*      */       }
/*      */     }
/*      */     else {
/*  399 */       encrFound = false;
/*  400 */       for (Object sigEncrAction : sigEncrActions) {
/*  401 */         Integer act = (Integer)sigEncrAction;
/*  402 */         if ((act.intValue() == 2) && (!encrFound)) {
/*      */           break;
/*      */         }
/*      */         
/*  406 */         if (act.intValue() == 4) {
/*  407 */           encrFound = true;
/*  408 */         } else if (encrFound)
/*      */         {
/*  410 */           done = true;
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*  415 */     if (!done) {
/*  416 */       throw new RampartException("protectionOrderMismatch");
/*      */     }
/*      */   }
/*      */   
/*      */   protected List<Integer> getSigEncrActions(List<WSSecurityEngineResult> results)
/*      */   {
/*  422 */     List<Integer> sigEncrActions = new ArrayList();
/*  423 */     for (WSSecurityEngineResult result : results) {
/*  424 */       Integer action = (Integer)result.get("action");
/*      */       
/*      */ 
/*  427 */       if ((2 == action.intValue()) || (4 == action.intValue())) {
/*  428 */         sigEncrActions.add(action);
/*      */       }
/*      */     }
/*      */     
/*  432 */     return sigEncrActions;
/*      */   }
/*      */   
/*      */ 
/*      */   protected void validateEncryptedParts(ValidatorData data, List<WSEncryptionPart> encryptedParts, List<WSSecurityEngineResult> results)
/*      */     throws RampartException
/*      */   {
/*  439 */     RampartMessageData rmd = data.getRampartMessageData();
/*      */     
/*  441 */     ArrayList encrRefs = getEncryptedReferences(results);
/*      */     
/*  443 */     RampartPolicyData rpd = rmd.getPolicyData();
/*      */     
/*      */ 
/*  446 */     SOAPEnvelope envelope = rmd.getMsgContext().getEnvelope();
/*  447 */     java.util.Set namespaces = RampartUtil.findAllPrefixNamespaces(envelope, rpd.getDeclaredNamespaces());
/*      */     
/*      */ 
/*  450 */     Map decryptedElements = new java.util.HashMap();
/*  451 */     for (Object encrRef : encrRefs) {
/*  452 */       WSDataRef dataRef = (WSDataRef)encrRef;
/*      */       
/*  454 */       if ((dataRef != null) && (dataRef.getXpath() != null))
/*      */       {
/*      */         try
/*      */         {
/*      */ 
/*  459 */           XPath xp = new AXIOMXPath(dataRef.getXpath());
/*      */           
/*  461 */           for (Object namespaceObject : namespaces) {
/*  462 */             OMNamespace tmpNs = (OMNamespace)namespaceObject;
/*  463 */             xp.addNamespace(tmpNs.getPrefix(), tmpNs.getNamespaceURI());
/*      */           }
/*      */           
/*  466 */           for (Object o : xp.selectNodes(envelope)) {
/*  467 */             decryptedElements.put(o, Boolean.valueOf(dataRef.isContent()));
/*      */           }
/*      */           
/*      */         }
/*      */         catch (JaxenException e)
/*      */         {
/*  473 */           throw new RampartException("An error occurred while searching for decrypted elements.", e);
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*  479 */     if ((rpd.isEncryptBody()) && (!rpd.isEncryptBodyOptional()))
/*      */     {
/*  481 */       if (!isRefIdPresent(encrRefs, data.getBodyEncrDataId())) {
/*  482 */         throw new RampartException("encryptedPartMissing", new String[] { data.getBodyEncrDataId() });
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*  487 */     for (WSEncryptionPart encryptedPart : encryptedParts)
/*      */     {
/*      */ 
/*  490 */       if (!encryptedPart.getName().equals("Body"))
/*      */       {
/*      */ 
/*      */ 
/*  494 */         if ((("Signature".equals(encryptedPart.getName())) && ("http://www.w3.org/2000/09/xmldsig#".equals(encryptedPart.getNamespace()))) || (encryptedPart.getEncModifier().equals("Header")))
/*      */         {
/*      */ 
/*  497 */           if (!isRefIdPresent(encrRefs, new QName(encryptedPart.getNamespace(), encryptedPart.getName()))) {
/*  498 */             throw new RampartException("encryptedPartMissing", new String[] { encryptedPart.getNamespace() + ":" + encryptedPart.getName() });
/*      */           }
/*      */           
/*      */ 
/*      */         }
/*      */         else
/*      */         {
/*  505 */           String xpath = encryptedPart.getXpath();
/*  506 */           boolean found = false;
/*      */           try {
/*  508 */             XPath xp = new AXIOMXPath(xpath);
/*      */             
/*  510 */             for (Object namespaceObject : namespaces) {
/*  511 */               OMNamespace tmpNs = (OMNamespace)namespaceObject;
/*  512 */               xp.addNamespace(tmpNs.getPrefix(), tmpNs.getNamespaceURI());
/*      */             }
/*      */             
/*  515 */             for (Object o : xp.selectNodes(envelope)) {
/*  516 */               Object result = decryptedElements.get(o);
/*  517 */               if ((result != null) && (("Element".equals(encryptedPart.getEncModifier()) ^ ((Boolean)result).booleanValue())))
/*      */               {
/*      */ 
/*  520 */                 found = true;
/*  521 */                 break;
/*      */               }
/*      */             }
/*      */             
/*  525 */             if (!found) {
/*  526 */               throw new RampartException("encryptedPartMissing", new String[] { xpath });
/*      */             }
/*      */             
/*      */ 
/*      */           }
/*      */           catch (JaxenException e)
/*      */           {
/*  533 */             throw new RampartException("An error occurred while searching for decrypted elements.", e);
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   public void validateRequiredElements(ValidatorData data) throws RampartException
/*      */   {
/*  542 */     RampartMessageData rmd = data.getRampartMessageData();
/*      */     
/*  544 */     RampartPolicyData rpd = rmd.getPolicyData();
/*      */     
/*  546 */     SOAPEnvelope envelope = rmd.getMsgContext().getEnvelope();
/*      */     
/*  548 */     for (String expression : rpd.getRequiredElements())
/*      */     {
/*  550 */       if (!RampartUtil.checkRequiredElements(envelope, rpd.getDeclaredNamespaces(), expression)) {
/*  551 */         throw new RampartException("requiredElementsMissing", new String[] { expression });
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void validateSignedPartsHeaders(ValidatorData data, List<WSEncryptionPart> signatureParts, List<WSSecurityEngineResult> results)
/*      */     throws RampartException
/*      */   {
/*  561 */     RampartMessageData rmd = data.getRampartMessageData();
/*      */     
/*  563 */     Node envelope = rmd.getDocument().getFirstChild();
/*      */     
/*  565 */     WSSecurityEngineResult[] actionResults = fetchActionResults(results, 2);
/*      */     
/*      */ 
/*  568 */     List<QName> actuallySigned = new ArrayList();
/*  569 */     if (actionResults != null) {
/*  570 */       for (WSSecurityEngineResult actionResult : actionResults)
/*      */       {
/*  572 */         List wsDataRefs = (List)actionResult.get("data-ref-uris");
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  578 */         for (Object objectDataReference : wsDataRefs) {
/*  579 */           WSDataRef wsDataRef = (WSDataRef)objectDataReference;
/*  580 */           Element protectedElement = wsDataRef.getProtectedElement();
/*  581 */           if (protectedElement.getLocalName().equals("EncryptedHeader")) {
/*  582 */             NodeList nodeList = protectedElement.getChildNodes();
/*  583 */             for (int x = 0; x < nodeList.getLength(); x++) {
/*  584 */               if (nodeList.item(x).getNodeType() == 1) {
/*  585 */                 String ns = nodeList.item(x).getNamespaceURI();
/*  586 */                 String ln = nodeList.item(x).getLocalName();
/*  587 */                 actuallySigned.add(new QName(ns, ln));
/*  588 */                 break;
/*      */               }
/*      */             }
/*      */           } else {
/*  592 */             String ns = protectedElement.getNamespaceURI();
/*  593 */             String ln = protectedElement.getLocalName();
/*  594 */             actuallySigned.add(new QName(ns, ln));
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*  601 */     for (WSEncryptionPart wsep : signatureParts) {
/*  602 */       if (wsep.getName().equals("Body"))
/*      */       {
/*      */         QName bodyQName;
/*  606 */         if ("http://schemas.xmlsoap.org/soap/envelope/".equals(envelope.getNamespaceURI())) {
/*  607 */           bodyQName = new SOAP11Constants().getBodyQName();
/*      */         } else {
/*  609 */           bodyQName = new SOAP12Constants().getBodyQName();
/*      */         }
/*      */         
/*  612 */         if ((!actuallySigned.contains(bodyQName)) && (!rmd.getPolicyData().isSignBodyOptional()))
/*      */         {
/*  614 */           throw new RampartException("bodyNotSigned");
/*      */         }
/*      */       }
/*  617 */       else if ((wsep.getName().equals("Header")) || (wsep.getXpath() != null))
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  623 */         Element element = WSSecurityUtil.findElement(envelope, wsep.getName(), wsep.getNamespace());
/*      */         
/*      */ 
/*  626 */         if ((element != null) && 
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  633 */           (!actuallySigned.contains(new QName(element.getNamespaceURI(), element.getLocalName()))))
/*      */         {
/*      */ 
/*      */ 
/*  637 */           String msg = wsep.getXpath() != null ? "signedPartHeaderNotSigned" : "signedElementNotSigned";
/*      */           
/*      */ 
/*      */ 
/*  641 */           throw new RampartException(msg, new String[] { wsep.getNamespace() + ":" + wsep.getName() });
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   protected boolean isSignatureRequired(RampartMessageData rmd)
/*      */   {
/*  649 */     RampartPolicyData rpd = rmd.getPolicyData();
/*  650 */     return ((rpd.isSymmetricBinding()) && (rpd.getSignatureToken() != null)) || ((!rpd.isSymmetricBinding()) && (!rpd.isTransportBinding()) && (((rpd.getInitiatorToken() != null) && (rmd.isInitiator())) || ((rpd.getRecipientToken() != null) && (!rmd.isInitiator()))));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected boolean verifyTimestamp(Timestamp timestamp, RampartMessageData rmd)
/*      */     throws RampartException
/*      */   {
/*  664 */     Date createdTime = timestamp.getCreated();
/*  665 */     if (createdTime != null) {
/*  666 */       long now = Calendar.getInstance().getTimeInMillis();
/*      */       
/*      */ 
/*  669 */       long maxSkew = RampartUtil.getTimestampMaxSkew(rmd);
/*  670 */       if (maxSkew > 0L) {
/*  671 */         now += maxSkew * 1000L;
/*      */       }
/*      */       
/*      */ 
/*  675 */       if (createdTime.getTime() > now) {
/*  676 */         return false;
/*      */       }
/*      */     }
/*      */     
/*  680 */     return true;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected boolean verifyTrust(X509Certificate cert, RampartMessageData rmd)
/*      */     throws RampartException
/*      */   {
/*  701 */     if (cert == null) {
/*  702 */       return false;
/*      */     }
/*      */     
/*  705 */     Crypto crypto = RampartUtil.getSignatureCrypto(rmd.getPolicyData().getRampartConfig(), rmd.getCustomClassLoader());
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  717 */     return isCertificateTrusted(cert, crypto);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected boolean isCertificateTrusted(X509Certificate cert, Crypto crypto)
/*      */     throws RampartException
/*      */   {
/*  744 */     String subjectString = cert.getSubjectX500Principal().getName();
/*  745 */     String issuerString = cert.getIssuerX500Principal().getName();
/*  746 */     BigInteger issuerSerial = cert.getSerialNumber();
/*      */     
/*  748 */     if (log.isDebugEnabled()) {
/*  749 */       log.debug("Transmitted certificate has subject " + subjectString);
/*  750 */       log.debug("Transmitted certificate has issuer " + issuerString + " (serial " + issuerSerial + ")");
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  759 */     if (isCertificateInKeyStore(crypto, cert)) {
/*  760 */       return true;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  767 */     CryptoType cryptoType = new CryptoType(CryptoType.TYPE.SUBJECT_DN);
/*  768 */     cryptoType.setSubjectDN(issuerString);
/*  769 */     X509Certificate[] foundCerts = new X509Certificate[0];
/*      */     try {
/*  771 */       foundCerts = crypto.getX509Certificates(cryptoType);
/*      */     } catch (WSSecurityException e) {
/*  773 */       throw new RampartException("noCertForSubject", e);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*  778 */     if ((foundCerts == null) || (foundCerts.length < 1)) {
/*  779 */       if (log.isDebugEnabled()) {
/*  780 */         log.debug("No certs found in keystore for issuer " + issuerString + " of certificate for " + subjectString);
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*  785 */       return false;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  792 */     if (log.isDebugEnabled()) {
/*  793 */       log.debug("Preparing to validate certificate path for issuer " + issuerString);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  801 */     X509Certificate[] x509certs = new X509Certificate[foundCerts.length + 1];
/*  802 */     x509certs[0] = cert;
/*  803 */     for (int j = 0; j < foundCerts.length; j++) {
/*  804 */       x509certs[(j + 1)] = foundCerts[j];
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     try
/*      */     {
/*  813 */       if (crypto.verifyTrust(x509certs, false)) {
/*  814 */         if (log.isDebugEnabled()) {
/*  815 */           log.debug("Certificate path has been verified for certificate with subject " + subjectString);
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*  820 */         return true;
/*      */       }
/*      */     } catch (WSSecurityException e) {
/*  823 */       throw new RampartException("certPathVerificationFailed", e);
/*      */     }
/*      */     
/*  826 */     if (log.isDebugEnabled()) {
/*  827 */       log.debug("Certificate path could not be verified for certificate with subject " + subjectString);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*  832 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected boolean isCertificateInKeyStore(Crypto crypto, X509Certificate cert)
/*      */     throws RampartException
/*      */   {
/*  847 */     String issuerString = cert.getIssuerX500Principal().getName();
/*  848 */     BigInteger issuerSerial = cert.getSerialNumber();
/*      */     
/*  850 */     CryptoType cryptoType = new CryptoType(CryptoType.TYPE.ISSUER_SERIAL);
/*  851 */     cryptoType.setIssuerSerial(issuerString, issuerSerial);
/*  852 */     X509Certificate[] foundCerts = new X509Certificate[0];
/*      */     try {
/*  854 */       foundCerts = crypto.getX509Certificates(cryptoType);
/*      */     } catch (WSSecurityException e) {
/*  856 */       throw new RampartException("noCertificatesForIssuer", new String[] { issuerString, issuerSerial.toString() }, e);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  864 */     if ((foundCerts != null) && (foundCerts[0] != null) && (foundCerts[0].equals(cert))) {
/*  865 */       if (log.isDebugEnabled()) {
/*  866 */         log.debug("Direct trust for certificate with " + cert.getSubjectX500Principal().getName());
/*      */       }
/*      */       
/*      */ 
/*  870 */       return true;
/*      */     }
/*  872 */     if (log.isDebugEnabled()) {
/*  873 */       log.debug("No certificate found for subject from issuer with " + issuerString + " (serial " + issuerSerial + ")");
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*  878 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected ArrayList getEncryptedReferences(List<WSSecurityEngineResult> results)
/*      */   {
/*  885 */     List<WSSecurityEngineResult> encrResults = getResults(results, 4);
/*      */     
/*  887 */     ArrayList refs = new ArrayList();
/*      */     
/*  889 */     for (WSSecurityEngineResult engineResult : encrResults) {
/*  890 */       ArrayList dataRefUris = (ArrayList)engineResult.get("data-ref-uris");
/*      */       
/*      */ 
/*      */ 
/*  894 */       if (dataRefUris != null) {
/*  895 */         Iterator iterator = dataRefUris.iterator();
/*  896 */         while (iterator.hasNext()) {
/*  897 */           WSDataRef uri = (WSDataRef)iterator.next();
/*  898 */           refs.add(uri);
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*  903 */     return refs;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected List<WSSecurityEngineResult> getResults(List<WSSecurityEngineResult> results, int action)
/*      */   {
/*  910 */     List<WSSecurityEngineResult> list = new ArrayList();
/*      */     
/*  912 */     for (WSSecurityEngineResult result : results)
/*      */     {
/*      */ 
/*  915 */       Integer actInt = (Integer)result.get("action");
/*  916 */       if (actInt.intValue() == action) {
/*  917 */         list.add(result);
/*      */       }
/*      */     }
/*      */     
/*  921 */     return list;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected boolean isUsernameTokenPresent(ValidatorData data)
/*      */   {
/*  929 */     RampartPolicyData rpd = data.getRampartMessageData().getPolicyData();
/*      */     
/*  931 */     List<SupportingToken> supportingToks = rpd.getSupportingTokensList();
/*  932 */     for (SupportingToken suppTok : supportingToks) {
/*  933 */       if (isUsernameTokenPresent(suppTok)) {
/*  934 */         return true;
/*      */       }
/*      */     }
/*      */     
/*  938 */     SupportingToken signedSuppToken = rpd.getSignedSupportingTokens();
/*  939 */     if (isUsernameTokenPresent(signedSuppToken)) {
/*  940 */       return true;
/*      */     }
/*      */     
/*  943 */     SupportingToken signedEndSuppToken = rpd.getSignedEndorsingSupportingTokens();
/*  944 */     if (isUsernameTokenPresent(signedEndSuppToken)) {
/*  945 */       return true;
/*      */     }
/*      */     
/*  948 */     SupportingToken endSuppToken = rpd.getEndorsingSupportingTokens();
/*  949 */     return isUsernameTokenPresent(endSuppToken);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected boolean isUsernameTokenPresent(SupportingToken suppTok)
/*      */   {
/*  956 */     if (suppTok == null) {
/*  957 */       return false;
/*      */     }
/*      */     
/*  960 */     ArrayList tokens = suppTok.getTokens();
/*  961 */     for (Iterator iter = tokens.iterator(); iter.hasNext();) {
/*  962 */       Token token = (Token)iter.next();
/*  963 */       if ((token instanceof UsernameToken)) {
/*  964 */         return true;
/*      */       }
/*      */     }
/*      */     
/*  968 */     return false;
/*      */   }
/*      */   
/*      */   private boolean isRefIdPresent(ArrayList refList, String id)
/*      */   {
/*  973 */     if ((id != null) && (id.charAt(0) == '#')) {
/*  974 */       id = id.substring(1);
/*      */     }
/*      */     
/*  977 */     for (Object aRefList : refList) {
/*  978 */       WSDataRef dataRef = (WSDataRef)aRefList;
/*      */       
/*      */ 
/*  981 */       if (dataRef != null)
/*      */       {
/*      */ 
/*      */ 
/*  985 */         String dataRefUri = dataRef.getWsuId();
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  993 */         if ((dataRefUri != null) && (dataRefUri.equals(id))) {
/*  994 */           return true;
/*      */         }
/*      */       }
/*      */     }
/*  998 */     return false;
/*      */   }
/*      */   
/*      */   public static WSSecurityEngineResult[] fetchActionResults(List<WSSecurityEngineResult> wsSecurityEngineResults, int action)
/*      */   {
/* 1003 */     List<WSSecurityEngineResult> wsResult = new ArrayList();
/*      */     
/*      */ 
/* 1006 */     for (WSSecurityEngineResult wsSecurityEngineResult : wsSecurityEngineResults)
/*      */     {
/* 1008 */       WSSecurityEngineResult result = wsSecurityEngineResult;
/* 1009 */       int resultAction = ((Integer)result.get("action")).intValue();
/* 1010 */       if (resultAction == action) {
/* 1011 */         wsResult.add(wsSecurityEngineResult);
/*      */       }
/*      */     }
/*      */     
/* 1015 */     return (WSSecurityEngineResult[])wsResult.toArray(new WSSecurityEngineResult[wsResult.size()]);
/*      */   }
/*      */   
/*      */ 
/*      */   private boolean isRefIdPresent(ArrayList refList, QName qname)
/*      */   {
/* 1021 */     for (Object aRefList : refList) {
/* 1022 */       WSDataRef dataRef = (WSDataRef)aRefList;
/*      */       
/*      */ 
/* 1025 */       if (dataRef != null)
/*      */       {
/*      */ 
/*      */ 
/* 1029 */         QName dataRefQName = dataRef.getName();
/*      */         
/* 1031 */         if ((dataRefQName != null) && (dataRefQName.equals(qname))) {
/* 1032 */           return true;
/*      */         }
/*      */       }
/*      */     }
/*      */     
/* 1037 */     return false;
/*      */   }
/*      */ }

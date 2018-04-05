/*      */ package org.apache.rampart.util;
/*      */ 
/*      */ import java.security.NoSuchAlgorithmException;
/*      */ import java.security.cert.X509Certificate;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Calendar;
/*      */ import java.util.HashMap;
/*      */ import java.util.HashSet;
/*      */ import java.util.Hashtable;
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Properties;
/*      */ import java.util.Set;
/*      */ import java.util.concurrent.ConcurrentHashMap;
/*      */ import javax.crypto.KeyGenerator;
/*      */ import javax.security.auth.callback.Callback;
/*      */ import javax.security.auth.callback.CallbackHandler;
/*      */ import javax.servlet.http.HttpServletRequest;
/*      */ import javax.xml.namespace.QName;
/*      */ import org.apache.axiom.om.OMAbstractFactory;
/*      */ import org.apache.axiom.om.OMAttribute;
/*      */ import org.apache.axiom.om.OMElement;
/*      */ import org.apache.axiom.om.OMFactory;
/*      */ import org.apache.axiom.om.OMNamespace;
/*      */ import org.apache.axiom.om.xpath.AXIOMXPath;
/*      */ import org.apache.axiom.soap.SOAPBody;
/*      */ import org.apache.axiom.soap.SOAPEnvelope;
/*      */ import org.apache.axiom.soap.SOAPHeader;
/*      */ import org.apache.axiom.soap.SOAPHeaderBlock;
/*      */ import org.apache.axis2.AxisFault;
/*      */ import org.apache.axis2.addressing.EndpointReference;
/*      */ import org.apache.axis2.client.Options;
/*      */ import org.apache.axis2.context.ConfigurationContext;
/*      */ import org.apache.axis2.context.MessageContext;
/*      */ import org.apache.axis2.dataretrieval.client.MexClient;
/*      */ import org.apache.axis2.description.AxisService;
/*      */ import org.apache.axis2.description.Parameter;
/*      */ import org.apache.axis2.mex.MexException;
/*      */ import org.apache.axis2.mex.om.Metadata;
/*      */ import org.apache.axis2.mex.om.MetadataReference;
/*      */ import org.apache.axis2.mex.om.MetadataSection;
/*      */ import org.apache.axis2.transport.http.HTTPConstants;
/*      */ import org.apache.commons.httpclient.protocol.Protocol;
/*      */ import org.apache.commons.logging.Log;
/*      */ import org.apache.commons.logging.LogFactory;
/*      */ import org.apache.neethi.Policy;
/*      */ import org.apache.neethi.PolicyEngine;
/*      */ import org.apache.rahas.TokenStorage;
/*      */ import org.apache.rahas.TrustException;
/*      */ import org.apache.rahas.TrustUtil;
/*      */ import org.apache.rahas.client.STSClient;
/*      */ import org.apache.rampart.PolicyBasedResultsValidator;
/*      */ import org.apache.rampart.PolicyValidatorCallbackHandler;
/*      */ import org.apache.rampart.RampartConfigCallbackHandler;
/*      */ import org.apache.rampart.RampartException;
/*      */ import org.apache.rampart.RampartMessageData;
/*      */ import org.apache.rampart.policy.RampartPolicyData;
/*      */ import org.apache.rampart.policy.SupportingPolicyData;
/*      */ import org.apache.rampart.policy.model.CryptoConfig;
/*      */ import org.apache.rampart.policy.model.RampartConfig;
/*      */ import org.apache.ws.secpolicy.model.HttpsToken;
/*      */ import org.apache.ws.secpolicy.model.IssuedToken;
/*      */ import org.apache.ws.secpolicy.model.SecureConversationToken;
/*      */ import org.apache.ws.secpolicy.model.SupportingToken;
/*      */ import org.apache.ws.secpolicy.model.Wss10;
/*      */ import org.apache.ws.secpolicy.model.Wss11;
/*      */ import org.apache.ws.secpolicy.model.X509Token;
/*      */ import org.apache.ws.security.WSEncryptionPart;
/*      */ import org.apache.ws.security.WSPasswordCallback;
/*      */ import org.apache.ws.security.WSSConfig;
/*      */ import org.apache.ws.security.WSSecurityEngineResult;
/*      */ import org.apache.ws.security.WSSecurityException;
/*      */ import org.apache.ws.security.WSUsernameTokenPrincipal;
/*      */ import org.apache.ws.security.components.crypto.Crypto;
/*      */ import org.apache.ws.security.components.crypto.CryptoFactory;
/*      */ import org.apache.ws.security.conversation.ConversationConstants;
/*      */ import org.apache.ws.security.conversation.ConversationException;
/*      */ import org.apache.ws.security.handler.WSHandlerResult;
/*      */ import org.apache.ws.security.message.WSSecBase;
/*      */ import org.apache.ws.security.message.WSSecEncryptedKey;
/*      */ import org.apache.ws.security.message.WSSecHeader;
/*      */ import org.apache.ws.security.util.Loader;
/*      */ import org.apache.ws.security.util.WSSecurityUtil;
/*      */ import org.jaxen.JaxenException;
/*      */ import org.jaxen.XPath;
/*      */ import org.w3c.dom.Document;
/*      */ import org.w3c.dom.Element;
/*      */ import org.w3c.dom.Node;
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
/*      */ 
/*      */ 
/*      */ 
/*      */ public class RampartUtil
/*      */ {
/*      */   private static final String CRYPTO_PROVIDER = "org.apache.ws.security.crypto.provider";
/*  118 */   private static Log log = LogFactory.getLog(RampartUtil.class);
/*      */   
/*  120 */   private static Map<String, CachedCrypto> cryptoStore = new ConcurrentHashMap();
/*      */   
/*      */   private static class CachedCrypto {
/*      */     private Crypto crypto;
/*      */     private long creationTime;
/*      */     
/*      */     public CachedCrypto(Crypto crypto, long creationTime) {
/*  127 */       this.crypto = crypto;
/*  128 */       this.creationTime = creationTime;
/*      */     }
/*      */   }
/*      */   
/*      */   public static CallbackHandler getPasswordCB(RampartMessageData rmd) throws RampartException
/*      */   {
/*  134 */     MessageContext msgContext = rmd.getMsgContext();
/*  135 */     RampartPolicyData rpd = rmd.getPolicyData();
/*      */     
/*  137 */     return getPasswordCB(msgContext, rpd);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public static CallbackHandler getPasswordCB(MessageContext msgContext, RampartPolicyData rpd)
/*      */     throws RampartException
/*      */   {
/*      */     CallbackHandler cbHandler;
/*      */     
/*      */ 
/*      */ 
/*  150 */     if ((rpd.getRampartConfig() != null) && (rpd.getRampartConfig().getPwCbClass() != null))
/*      */     {
/*  152 */       String cbHandlerClass = rpd.getRampartConfig().getPwCbClass();
/*  153 */       ClassLoader classLoader = msgContext.getAxisService().getClassLoader();
/*      */       
/*  155 */       if (log.isDebugEnabled()) {
/*  156 */         log.debug("loading class : " + cbHandlerClass);
/*      */       }
/*      */       Class cbClass;
/*      */       try
/*      */       {
/*  161 */         cbClass = Loader.loadClass(classLoader, cbHandlerClass);
/*      */       } catch (ClassNotFoundException e) {
/*  163 */         throw new RampartException("cannotLoadPWCBClass", new String[] { cbHandlerClass }, e);
/*      */       }
/*      */       try
/*      */       {
/*  167 */         cbHandler = (CallbackHandler)cbClass.newInstance();
/*      */       } catch (Exception e) {
/*  169 */         throw new RampartException("cannotCreatePWCBInstance", new String[] { cbHandlerClass }, e);
/*      */       }
/*      */     }
/*      */     else {
/*  173 */       cbHandler = (CallbackHandler)msgContext.getProperty("passwordCallbackRef");
/*      */       
/*  175 */       if (cbHandler == null) {
/*  176 */         Parameter param = msgContext.getParameter("passwordCallbackRef");
/*      */         
/*  178 */         if (param != null) {
/*  179 */           cbHandler = (CallbackHandler)param.getValue();
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*  184 */     return cbHandler;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static PolicyValidatorCallbackHandler getPolicyValidatorCB(MessageContext msgContext, RampartPolicyData rpd)
/*      */     throws RampartException
/*      */   {
/*      */     PolicyValidatorCallbackHandler cbHandler;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*  199 */     if ((rpd.getRampartConfig() != null) && (rpd.getRampartConfig().getPolicyValidatorCbClass() != null))
/*      */     {
/*  201 */       String cbHandlerClass = rpd.getRampartConfig().getPolicyValidatorCbClass();
/*  202 */       ClassLoader classLoader = msgContext.getAxisService().getClassLoader();
/*      */       
/*  204 */       if (log.isDebugEnabled()) {
/*  205 */         log.debug("loading class : " + cbHandlerClass);
/*      */       }
/*      */       Class cbClass;
/*      */       try
/*      */       {
/*  210 */         cbClass = Loader.loadClass(classLoader, cbHandlerClass);
/*      */       } catch (ClassNotFoundException e) {
/*  212 */         throw new RampartException("cannotLoadPolicyValidatorCbClass", new String[] { cbHandlerClass }, e);
/*      */       }
/*      */       try
/*      */       {
/*  216 */         cbHandler = (PolicyValidatorCallbackHandler)cbClass.newInstance();
/*      */       } catch (Exception e) {
/*  218 */         throw new RampartException("cannotCreatePolicyValidatorCallbackInstance", new String[] { cbHandlerClass }, e);
/*      */       }
/*      */     }
/*      */     else
/*      */     {
/*  223 */       cbHandler = new PolicyBasedResultsValidator();
/*      */     }
/*      */     
/*  226 */     return cbHandler;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public static RampartConfigCallbackHandler getRampartConfigCallbackHandler(MessageContext msgContext, RampartPolicyData rpd)
/*      */     throws RampartException
/*      */   {
/*  234 */     if ((rpd.getRampartConfig() != null) && (rpd.getRampartConfig().getRampartConfigCbClass() != null))
/*      */     {
/*  236 */       String cbHandlerClass = rpd.getRampartConfig().getRampartConfigCbClass();
/*  237 */       ClassLoader classLoader = msgContext.getAxisService().getClassLoader();
/*      */       
/*  239 */       if (log.isDebugEnabled()) {
/*  240 */         log.debug("loading class : " + cbHandlerClass);
/*      */       }
/*      */       Class cbClass;
/*      */       try
/*      */       {
/*  245 */         cbClass = Loader.loadClass(classLoader, cbHandlerClass);
/*      */       } catch (ClassNotFoundException e) {
/*  247 */         throw new RampartException("cannotLoadRampartConfigCallbackClass", new String[] { cbHandlerClass }, e);
/*      */       }
/*      */       RampartConfigCallbackHandler rampartConfigCB;
/*      */       try {
/*  251 */         rampartConfigCB = (RampartConfigCallbackHandler)cbClass.newInstance();
/*      */       } catch (Exception e) {
/*  253 */         throw new RampartException("cannotCreateRampartConfigCallbackInstance", new String[] { cbHandlerClass }, e);
/*      */       }
/*      */       
/*      */ 
/*  257 */       return rampartConfigCB;
/*      */     }
/*      */     
/*      */ 
/*  261 */     return null;
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
/*      */   public static WSPasswordCallback performCallback(CallbackHandler cbHandler, String username, int doAction)
/*      */     throws RampartException
/*      */   {
/*  277 */     int reason = 0;
/*      */     
/*  279 */     switch (doAction) {
/*      */     case 1: 
/*      */     case 64: 
/*  282 */       reason = 2;
/*  283 */       break;
/*      */     case 2: 
/*  285 */       reason = 3;
/*  286 */       break;
/*      */     case 4: 
/*  288 */       reason = 4;
/*      */     }
/*      */     
/*  291 */     WSPasswordCallback pwCb = new WSPasswordCallback(username, reason);
/*  292 */     Callback[] callbacks = new Callback[1];
/*  293 */     callbacks[0] = pwCb;
/*      */     
/*      */ 
/*      */     try
/*      */     {
/*  298 */       cbHandler.handle(callbacks);
/*      */     } catch (Exception e) {
/*  300 */       throw new RampartException("pwcbFailed", e);
/*      */     }
/*  302 */     return pwCb;
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
/*      */   public static Crypto getEncryptionCrypto(RampartConfig config, ClassLoader loader)
/*      */     throws RampartException
/*      */   {
/*  316 */     log.debug("Loading encryption crypto");
/*      */     
/*  318 */     Crypto crypto = null;
/*      */     
/*  320 */     if ((config != null) && (config.getEncrCryptoConfig() != null)) {
/*  321 */       CryptoConfig cryptoConfig = config.getEncrCryptoConfig();
/*  322 */       String provider = cryptoConfig.getProvider();
/*  323 */       if (log.isDebugEnabled()) {
/*  324 */         log.debug("Using provider: " + provider);
/*      */       }
/*  326 */       Properties prop = cryptoConfig.getProp();
/*  327 */       prop.put("org.apache.ws.security.crypto.provider", provider);
/*      */       
/*  329 */       String cryptoKey = null;
/*  330 */       String interval = null;
/*  331 */       if (cryptoConfig.isCacheEnabled()) {
/*  332 */         if (cryptoConfig.getCryptoKey() != null) {
/*  333 */           cryptoKey = prop.getProperty(cryptoConfig.getCryptoKey());
/*  334 */           interval = cryptoConfig.getCacheRefreshInterval();
/*      */         }
/*  336 */         else if (provider.equals("org.apache.ws.security.components.crypto.Merlin")) {
/*  337 */           cryptoKey = cryptoConfig.getProp().getProperty("org.apache.ws.security.crypto.merlin.file");
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*  342 */       if (cryptoKey != null)
/*      */       {
/*  344 */         crypto = retrieveCryptoFromCache(cryptoKey.trim() + "#" + provider.trim(), interval);
/*      */       }
/*      */       
/*  347 */       if (crypto == null)
/*      */       {
/*  349 */         crypto = createCrypto(prop, loader);
/*      */         
/*  351 */         if (cryptoKey != null)
/*      */         {
/*  353 */           cacheCrypto(cryptoKey.trim() + "#" + provider.trim(), crypto);
/*      */         }
/*      */       }
/*      */     } else {
/*  357 */       log.debug("Trying the signature crypto info");
/*  358 */       crypto = getSignatureCrypto(config, loader);
/*      */     }
/*  360 */     return crypto;
/*      */   }
/*      */   
/*      */   private static Crypto createCrypto(Properties properties, ClassLoader classLoader) throws RampartException
/*      */   {
/*      */     try {
/*  366 */       return CryptoFactory.getInstance(properties, classLoader);
/*      */     } catch (WSSecurityException e) {
/*  368 */       log.error("Error loading crypto properties.", e);
/*  369 */       throw new RampartException("cannotCrateCryptoInstance", e);
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
/*      */ 
/*      */   public static Crypto getSignatureCrypto(RampartConfig config, ClassLoader loader)
/*      */     throws RampartException
/*      */   {
/*  384 */     log.debug("Loading Signature crypto");
/*      */     
/*  386 */     Crypto crypto = null;
/*      */     
/*  388 */     if ((config != null) && (config.getSigCryptoConfig() != null)) {
/*  389 */       CryptoConfig cryptoConfig = config.getSigCryptoConfig();
/*  390 */       String provider = cryptoConfig.getProvider();
/*  391 */       if (log.isDebugEnabled()) {
/*  392 */         log.debug("Using provider: " + provider);
/*      */       }
/*  394 */       Properties prop = cryptoConfig.getProp();
/*  395 */       prop.put("org.apache.ws.security.crypto.provider", provider);
/*  396 */       String cryptoKey = null;
/*  397 */       String interval = null;
/*      */       
/*  399 */       if (cryptoConfig.isCacheEnabled()) {
/*  400 */         if (cryptoConfig.getCryptoKey() != null) {
/*  401 */           cryptoKey = prop.getProperty(cryptoConfig.getCryptoKey());
/*  402 */           interval = cryptoConfig.getCacheRefreshInterval();
/*      */         }
/*  404 */         else if (provider.equals("org.apache.ws.security.components.crypto.Merlin")) {
/*  405 */           cryptoKey = cryptoConfig.getProp().getProperty("org.apache.ws.security.crypto.merlin.file");
/*      */         }
/*      */       }
/*      */       
/*  409 */       if (cryptoKey != null)
/*      */       {
/*  411 */         crypto = retrieveCryptoFromCache(cryptoKey.trim() + "#" + provider.trim(), interval);
/*      */       }
/*      */       
/*  414 */       if (crypto == null)
/*      */       {
/*  416 */         crypto = createCrypto(prop, loader);
/*  417 */         if (cryptoKey != null)
/*      */         {
/*  419 */           cacheCrypto(cryptoKey.trim() + "#" + provider.trim(), crypto);
/*      */         }
/*      */       }
/*      */     }
/*  423 */     return crypto;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static int getKeyIdentifier(X509Token token)
/*      */     throws RampartException
/*      */   {
/*  434 */     if (token.isRequireIssuerSerialReference())
/*  435 */       return 2;
/*  436 */     if (token.isRequireThumbprintReference())
/*  437 */       return 8;
/*  438 */     if (token.isRequireEmbeddedTokenReference()) {
/*  439 */       return 1;
/*      */     }
/*  441 */     throw new RampartException("unknownKeyRefSpeficier");
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
/*      */   public static String processIssuerAddress(OMElement issuerAddress)
/*      */     throws RampartException
/*      */   {
/*  456 */     if (issuerAddress == null) {
/*  457 */       throw new RampartException("invalidIssuerAddress", new String[] { "Issuer address null" });
/*      */     }
/*      */     
/*      */ 
/*  461 */     if ((issuerAddress.getText() == null) || ("".equals(issuerAddress.getText()))) {
/*  462 */       throw new RampartException("invalidIssuerAddress", new String[] { issuerAddress.toString() });
/*      */     }
/*      */     
/*      */ 
/*  466 */     return issuerAddress.getText().trim();
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
/*      */   public static Policy getPolicyFromMetadataRef(OMElement mex)
/*      */     throws RampartException
/*      */   {
/*      */     try
/*      */     {
/*  488 */       Metadata metadata = new Metadata();
/*  489 */       metadata.fromOM(mex.getFirstElement());
/*      */       
/*  491 */       MetadataSection[] metadataSections = metadata.getMetadatSections();
/*  492 */       MetadataReference reference = metadataSections[0].getMetadataReference();
/*      */       
/*  494 */       MexClient serviceClient = new MexClient();
/*      */       
/*  496 */       Options options = serviceClient.getOptions();
/*  497 */       options.setTo(reference.getEPR());
/*  498 */       options.setAction("http://schemas.xmlsoap.org/ws/2004/09/mex/GetMetadata/Request");
/*      */       
/*  500 */       OMElement request = serviceClient.setupGetMetadataRequest("http://schemas.xmlsoap.org/ws/2004/09/policy", null);
/*      */       
/*  502 */       OMElement result = serviceClient.sendReceive(request);
/*      */       
/*  504 */       metadata.fromOM(result);
/*  505 */       MetadataSection[] mexSecs = metadata.getMetadataSection("http://schemas.xmlsoap.org/ws/2004/09/policy", null);
/*  506 */       OMElement policyElement = (OMElement)mexSecs[0].getInlineData();
/*      */       
/*  508 */       return PolicyEngine.getPolicy(policyElement);
/*      */     }
/*      */     catch (MexException e)
/*      */     {
/*  512 */       throw new RampartException("Error Retrieving the policy from mex", e);
/*      */     } catch (AxisFault e) {
/*  514 */       throw new RampartException("Error Retrieving the policy from mex", e);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public static Policy addRampartConfig(RampartMessageData rmd, Policy policy)
/*      */   {
/*  521 */     RampartConfig servicRampConf = rmd.getPolicyData().getRampartConfig();
/*  522 */     RampartConfig stsRampConf = new RampartConfig();
/*      */     
/*      */ 
/*  525 */     stsRampConf.setUser(servicRampConf.getUser());
/*  526 */     stsRampConf.setSigCryptoConfig(servicRampConf.getSigCryptoConfig());
/*  527 */     stsRampConf.setPwCbClass(servicRampConf.getPwCbClass());
/*      */     
/*  529 */     stsRampConf.setEncryptionUser(servicRampConf.getStsAlias());
/*  530 */     stsRampConf.setEncrCryptoConfig(servicRampConf.getStsCryptoConfig());
/*      */     
/*  532 */     policy.addAssertion(stsRampConf);
/*      */     
/*  534 */     return policy;
/*      */   }
/*      */   
/*      */   public static OMElement createRSTTempalteForSCT(int conversationVersion, int wstVersion)
/*      */     throws RampartException
/*      */   {
/*      */     try
/*      */     {
/*  542 */       log.debug("Creating RSTTemplate for an SCT request");
/*  543 */       OMFactory fac = OMAbstractFactory.getOMFactory();
/*      */       
/*  545 */       OMNamespace wspNs = fac.createOMNamespace("http://schemas.xmlsoap.org/ws/2004/09/policy", "wsp");
/*  546 */       OMElement rstTempl = fac.createOMElement("RequestSecurityTokenTemplate", wspNs);
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*  551 */       OMElement tokenTypeElem = TrustUtil.createTokenTypeElement(wstVersion, rstTempl);
/*      */       
/*  553 */       String tokenType = ConversationConstants.getWSCNs(conversationVersion) + "/sct";
/*      */       
/*      */ 
/*  556 */       tokenTypeElem.setText(tokenType);
/*      */       
/*  558 */       return rstTempl;
/*      */     } catch (TrustException e) {
/*  560 */       throw new RampartException("errorCreatingRSTTemplateForSCT", e);
/*      */     } catch (ConversationException e) {
/*  562 */       throw new RampartException("errorCreatingRSTTemplateForSCT", e);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public static int getTimeToLive(RampartMessageData messageData)
/*      */   {
/*  569 */     RampartConfig rampartConfig = messageData.getPolicyData().getRampartConfig();
/*  570 */     if (rampartConfig != null) {
/*  571 */       String ttl = rampartConfig.getTimestampTTL();
/*  572 */       int ttl_i = 0;
/*  573 */       if (ttl != null) {
/*      */         try {
/*  575 */           ttl_i = Integer.parseInt(ttl);
/*      */         } catch (NumberFormatException e) {
/*  577 */           ttl_i = messageData.getTimeToLive();
/*      */         }
/*      */       }
/*  580 */       if (ttl_i <= 0) {
/*  581 */         ttl_i = messageData.getTimeToLive();
/*      */       }
/*  583 */       return ttl_i;
/*      */     }
/*  585 */     return 300;
/*      */   }
/*      */   
/*      */ 
/*      */   public static int getTimestampMaxSkew(RampartMessageData messageData)
/*      */   {
/*  591 */     RampartConfig rampartConfig = messageData.getPolicyData().getRampartConfig();
/*  592 */     if (rampartConfig != null) {
/*  593 */       String maxSkew = rampartConfig.getTimestampMaxSkew();
/*  594 */       int maxSkew_i = 0;
/*  595 */       if (maxSkew != null) {
/*      */         try {
/*  597 */           maxSkew_i = Integer.parseInt(maxSkew);
/*      */         } catch (NumberFormatException e) {
/*  599 */           maxSkew_i = messageData.getTimestampMaxSkew();
/*      */         }
/*      */       }
/*  602 */       if (maxSkew_i < 0) {
/*  603 */         maxSkew_i = 0;
/*      */       }
/*  605 */       return maxSkew_i;
/*      */     }
/*  607 */     return 300;
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
/*      */   public static String getSecConvToken(RampartMessageData rmd, SecureConversationToken secConvTok)
/*      */     throws TrustException, RampartException
/*      */   {
/*  622 */     String action = TrustUtil.getActionValue(rmd.getWstVersion(), "/RST/SCT");
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*  627 */     OMElement issuerEpr = secConvTok.getIssuerEpr();
/*  628 */     String issuerEprAddress = rmd.getMsgContext().getTo().getAddress();
/*  629 */     if (issuerEpr != null) {
/*  630 */       issuerEprAddress = processIssuerAddress(issuerEpr);
/*      */     }
/*      */     
/*      */ 
/*  634 */     int conversationVersion = rmd.getSecConvVersion();
/*      */     
/*  636 */     OMElement rstTemplate = createRSTTempalteForSCT(conversationVersion, rmd.getWstVersion());
/*      */     
/*      */ 
/*      */ 
/*  640 */     Policy stsPolicy = null;
/*      */     
/*      */ 
/*  643 */     Policy bsPol = secConvTok.getBootstrapPolicy();
/*      */     
/*  645 */     if (bsPol != null) {
/*  646 */       log.debug("BootstrapPolicy found");
/*  647 */       bsPol.addAssertion(rmd.getPolicyData().getRampartConfig());
/*      */       
/*  649 */       if (rmd.getPolicyData().getMTOMAssertion() != null) {
/*  650 */         bsPol.addAssertion(rmd.getPolicyData().getMTOMAssertion());
/*      */       }
/*  652 */       stsPolicy = bsPol;
/*      */     }
/*      */     else {
/*  655 */       log.debug("No bootstrap policy, using issuer policy");
/*  656 */       stsPolicy = rmd.getPolicyData().getIssuerPolicy();
/*      */     }
/*      */     
/*  659 */     String id = getToken(rmd, rstTemplate, issuerEprAddress, action, stsPolicy);
/*      */     
/*      */ 
/*  662 */     if (log.isDebugEnabled()) {
/*  663 */       log.debug("SecureConversationToken obtained: id=" + id);
/*      */     }
/*  665 */     return id;
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
/*      */   public static String getIssuedToken(RampartMessageData rmd, IssuedToken issuedToken)
/*      */     throws RampartException
/*      */   {
/*      */     try
/*      */     {
/*  684 */       String action = TrustUtil.getActionValue(rmd.getWstVersion(), "/RST/Issue");
/*      */       
/*      */ 
/*      */ 
/*  688 */       String issuerEprAddress = processIssuerAddress(issuedToken.getIssuerEpr());
/*      */       
/*      */ 
/*  691 */       OMElement rstTemplate = issuedToken.getRstTemplate();
/*      */       
/*      */ 
/*  694 */       Policy stsPolicy = (Policy)rmd.getMsgContext().getProperty("rampartStsPolicy");
/*      */       
/*  696 */       if ((stsPolicy == null) && (issuedToken.getIssuerMex() != null)) {
/*  697 */         stsPolicy = getPolicyFromMetadataRef(issuedToken.getIssuerMex());
/*  698 */         addRampartConfig(rmd, stsPolicy);
/*      */       }
/*      */       
/*  701 */       String id = getToken(rmd, rstTemplate, issuerEprAddress, action, stsPolicy);
/*      */       
/*      */ 
/*  704 */       if (log.isDebugEnabled()) {
/*  705 */         log.debug("Issued token obtained: id=" + id);
/*      */       }
/*  707 */       return id;
/*      */     } catch (TrustException e) {
/*  709 */       throw new RampartException("errorInObtainingToken", e);
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
/*      */ 
/*      */ 
/*      */ 
/*      */   public static String getToken(RampartMessageData rmd, OMElement rstTemplate, String issuerEpr, String action, Policy issuerPolicy)
/*      */     throws RampartException
/*      */   {
/*      */     try
/*      */     {
/*  728 */       MessageContext msgContext = rmd.getMsgContext();
/*  729 */       String customTokeId = (String)msgContext.getProperty("customIssuedToken");
/*      */       
/*  731 */       if (customTokeId != null) {
/*  732 */         return customTokeId;
/*      */       }
/*      */       
/*  735 */       Axis2Util.useDOOM(false);
/*      */       
/*  737 */       STSClient client = new STSClient(rmd.getMsgContext().getConfigurationContext());
/*      */       
/*      */ 
/*  740 */       client.setAction(action);
/*      */       
/*  742 */       client.setVersion(rmd.getWstVersion());
/*      */       
/*  744 */       client.setRstTemplate(rstTemplate);
/*      */       
/*      */ 
/*  747 */       Crypto crypto = getSignatureCrypto(rmd.getPolicyData().getRampartConfig(), rmd.getMsgContext().getAxisService().getClassLoader());
/*      */       
/*  749 */       CallbackHandler cbh = getPasswordCB(rmd);
/*  750 */       client.setCryptoInfo(crypto, cbh);
/*      */       
/*      */ 
/*  753 */       Policy servicePolicy = rmd.getServicePolicy();
/*      */       
/*      */ 
/*  756 */       String servceEprAddress = rmd.getMsgContext().getOptions().getTo().getAddress();
/*      */       
/*      */ 
/*      */ 
/*  760 */       Object addrVersionNs = msgContext.getProperty("WSAddressingVersion");
/*  761 */       if (addrVersionNs != null) {
/*  762 */         client.setAddressingNs((String)addrVersionNs);
/*      */       }
/*      */       
/*  765 */       Options options = new Options();
/*      */       
/*  767 */       options.setUserName(rmd.getMsgContext().getOptions().getUserName());
/*  768 */       options.setPassword(rmd.getMsgContext().getOptions().getPassword());
/*      */       
/*  770 */       if (msgContext.getProperty("CUSTOM_PROTOCOL_HANDLER") != null) {
/*  771 */         Protocol protocolHandler = (Protocol)msgContext.getProperty("CUSTOM_PROTOCOL_HANDLER");
/*      */         
/*  773 */         options.setProperty("CUSTOM_PROTOCOL_HANDLER", protocolHandler);
/*      */       }
/*      */       
/*  776 */       if (msgContext.getParameter("passwordCallbackRef") != null) {
/*  777 */         Parameter pwCallback = msgContext.getParameter("passwordCallbackRef");
/*  778 */         client.addParameter(pwCallback);
/*      */       }
/*      */       
/*  781 */       client.setOptions(options);
/*      */       
/*      */ 
/*  784 */       if (msgContext.isSOAP11()) {
/*  785 */         client.setSoapVersion("http://schemas.xmlsoap.org/soap/envelope/");
/*      */       } else {
/*  787 */         client.setSoapVersion("http://www.w3.org/2003/05/soap-envelope");
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*  792 */       org.apache.rahas.Token rst = client.requestSecurityToken(servicePolicy, issuerEpr, issuerPolicy, servceEprAddress);
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  799 */       rst.setState(1);
/*  800 */       rmd.getTokenStorage().add(rst);
/*  801 */       Axis2Util.useDOOM(true);
/*  802 */       return rst.getId();
/*      */     }
/*      */     catch (Exception e) {
/*  805 */       throw new RampartException("errorInObtainingToken", e);
/*      */     }
/*      */   }
/*      */   
/*      */   public static String getSoapBodyId(SOAPEnvelope env) {
/*  810 */     return addWsuIdToElement(env.getBody());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public static String addWsuIdToElement(OMElement elem)
/*      */   {
/*  817 */     OMAttribute idAttr = elem.getAttribute(new QName("Id"));
/*  818 */     if (idAttr == null)
/*      */     {
/*  820 */       idAttr = elem.getAttribute(new QName("http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd", "Id")); }
/*      */     String id;
/*      */
/*  823 */     if (idAttr != null) {
/*  824 */       id = idAttr.getAttributeValue();
/*      */     }
/*      */     else {
/*  827 */       OMNamespace ns = elem.getOMFactory().createOMNamespace("http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd", "wsu");
/*      */       
/*  829 */       id = "Id-" + elem.hashCode();
/*  830 */       idAttr = elem.getOMFactory().createOMAttribute("Id", ns, id);
/*  831 */       elem.addAttribute(idAttr);
/*      */     }
/*      */     
/*  834 */     return id;
/*      */   }
/*      */   
/*      */   public static Element appendChildToSecHeader(RampartMessageData rmd, OMElement elem)
/*      */   {
/*  839 */     return appendChildToSecHeader(rmd, (Element)elem);
/*      */   }
/*      */   
/*      */   public static Element appendChildToSecHeader(RampartMessageData rmd, Element elem)
/*      */   {
/*  844 */     Element secHeaderElem = rmd.getSecHeader().getSecurityHeader();
/*  845 */     Node node = secHeaderElem.getOwnerDocument().importNode(elem, true);
/*      */     
/*  847 */     return (Element)secHeaderElem.appendChild(node);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static Element appendChildToElement(RampartMessageData rmd, Element elemchild, Element parent)
/*      */   {
/*  857 */     Node node = parent.getOwnerDocument().importNode(elemchild, true);
/*      */     
/*  859 */     return (Element)parent.appendChild(node);
/*      */   }
/*      */   
/*      */   public static Element insertSiblingAfter(RampartMessageData rmd, Element child, Element sibling) {
/*  863 */     if (child == null) {
/*  864 */       return appendChildToSecHeader(rmd, sibling);
/*      */     }
/*  866 */     if (child.getOwnerDocument().equals(sibling.getOwnerDocument()))
/*      */     {
/*  868 */       if ((child.getParentNode() == null) && (!child.getLocalName().equals("UsernameToken")))
/*      */       {
/*  870 */         rmd.getSecHeader().getSecurityHeader().appendChild(child);
/*      */       }
/*  872 */       ((OMElement)child).insertSiblingAfter((OMElement)sibling);
/*  873 */       return sibling;
/*      */     }
/*  875 */     Element newSib = (Element)child.getOwnerDocument().importNode(sibling, true);
/*      */     
/*  877 */     ((OMElement)child).insertSiblingAfter((OMElement)newSib);
/*  878 */     return newSib;
/*      */   }
/*      */   
/*      */ 
/*      */   public static Element insertSiblingBefore(RampartMessageData rmd, Element child, Element sibling)
/*      */   {
/*  884 */     if (child == null) {
/*  885 */       return appendChildToSecHeader(rmd, sibling);
/*      */     }
/*  887 */     if (child.getOwnerDocument().equals(sibling.getOwnerDocument())) {
/*  888 */       ((OMElement)child).insertSiblingBefore((OMElement)sibling);
/*  889 */       return sibling;
/*      */     }
/*  891 */     Element newSib = (Element)child.getOwnerDocument().importNode(sibling, true);
/*  892 */     ((OMElement)child).insertSiblingBefore((OMElement)newSib);
/*  893 */     return newSib;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public static List<WSEncryptionPart> getEncryptedParts(RampartMessageData rmd)
/*      */   {
/*  900 */     RampartPolicyData rpd = rmd.getPolicyData();
/*  901 */     SOAPEnvelope envelope = rmd.getMsgContext().getEnvelope();
/*  902 */     List<WSEncryptionPart> encryptedPartsElements = getPartsAndElements(false, envelope, (rpd.isEncryptBody()) && (!rpd.isEncryptBodyOptional()), rpd.getEncryptedParts(), rpd.getEncryptedElements(), rpd.getDeclaredNamespaces());
System.out.println(">>> RampartUtil.getEncryptedParts(isEncryptBody=" + rpd.isEncryptBody() + ", isEncryptBodyOptional=" + rpd.isEncryptBodyOptional() + ", envelope=" + envelope + ")");
System.out.println(">>> RampartUtil.getPartsAndElements(sign=" + false + ", " + "includeBody=" + ((rpd.isEncryptBody()) && (!rpd.isEncryptBodyOptional())) + ", " + "parts=" + rpd.getEncryptedParts().size()  + ", " + "elements=" + rpd.getEncryptedElements().size() + ", " + "decNamespaces=" + rpd.getDeclaredNamespaces().size() + ")");
System.out.println(">>> RampartUtil.getContentEncryptedElements(encryptedPartsElements=" + encryptedPartsElements.size() + ", " + "elements=" + rpd.getContentEncryptedElements().size() + ", " + "decNamespaces=" + rpd.getDeclaredNamespaces().size() + ")");
/*  906 */     return getContentEncryptedElements(encryptedPartsElements, envelope, rpd.getContentEncryptedElements(), rpd.getDeclaredNamespaces());
/*      */   }
/*      */   
/*      */   public static List<WSEncryptionPart> getSignedParts(RampartMessageData rmd)
/*      */   {
/*  911 */     RampartPolicyData rpd = rmd.getPolicyData();
/*  912 */     SOAPEnvelope envelope = rmd.getMsgContext().getEnvelope();
/*      */     
/*      */ 
/*  915 */     if (rpd.isSignAllHeaders()) {
/*  916 */       Iterator childHeaders = envelope.getHeader().getChildElements();
/*  917 */       while (childHeaders.hasNext()) {
/*  918 */         OMElement hb = (OMElement)childHeaders.next();
/*  919 */         if ((!hb.getLocalName().equals("Security")) || (!hb.getNamespace().getNamespaceURI().equals("http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd")))
/*      */         {
/*  921 */           rpd.addSignedPart(hb.getNamespace().getNamespaceURI(), hb.getLocalName());
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*  926 */     return getPartsAndElements(true, envelope, (rpd.isSignBody()) && (!rpd.isSignBodyOptional()), rpd.getSignedParts(), rpd.getSignedElements(), rpd.getDeclaredNamespaces());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public static List<WSEncryptionPart> getSupportingEncryptedParts(RampartMessageData rmd, SupportingPolicyData rpd)
/*      */   {
/*  933 */     SOAPEnvelope envelope = rmd.getMsgContext().getEnvelope();
/*  934 */     return getPartsAndElements(false, envelope, (rpd.isEncryptBody()) && (!rpd.isEncryptBodyOptional()), rpd.getEncryptedParts(), rpd.getEncryptedElements(), rpd.getDeclaredNamespaces());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public static List<WSEncryptionPart> getSupportingSignedParts(RampartMessageData rmd, SupportingPolicyData rpd)
/*      */   {
/*  941 */     SOAPEnvelope envelope = rmd.getMsgContext().getEnvelope();
/*  942 */     return getPartsAndElements(true, envelope, (rpd.isSignBody()) && (!rpd.isSignBodyOptional()), rpd.getSignedParts(), rpd.getSignedElements(), rpd.getDeclaredNamespaces());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public static Set findAllPrefixNamespaces(OMElement currentElement, HashMap decNamespacess)
/*      */   {
/*  949 */     Set<OMNamespace> results = new HashSet();
/*      */     
/*      */ 
/*  952 */     findPrefixNamespaces(currentElement, results);
/*      */     
/*      */ 
/*  955 */     List defaultNamespaces = getDefaultPrefixNamespaces(currentElement.getOMFactory());
/*  956 */     for (Object defaultNamespace : defaultNamespaces) {
/*  957 */       OMNamespace ns = (OMNamespace)defaultNamespace;
/*  958 */       results.add(ns);
/*      */     }
/*      */     
/*  961 */     for (Object o : decNamespacess.keySet()) {
/*  962 */       String prefix = (String)o;
/*  963 */       String ns = (String)decNamespacess.get(prefix);
/*  964 */       OMFactory omFactory = currentElement.getOMFactory();
/*  965 */       OMNamespace namespace = omFactory.createOMNamespace(ns, prefix);
/*  966 */       results.add(namespace);
/*      */     }
/*      */     
/*      */ 
/*  970 */     return results;
/*      */   }
/*      */   
/*      */   private static void findPrefixNamespaces(OMElement e, Set<OMNamespace> results)
/*      */   {
/*  975 */     Iterator iterator = e.getAllDeclaredNamespaces();
/*      */     
/*  977 */     if (iterator != null) {
/*  978 */       while (iterator.hasNext()) {
/*  979 */         results.add((OMNamespace)iterator.next());
/*      */       }
/*      */     }
/*  982 */     Iterator children = e.getChildElements();
/*      */     
/*  984 */     while (children.hasNext()) {
/*  985 */       findPrefixNamespaces((OMElement)children.next(), results);
/*      */     }
/*      */   }
/*      */   
/*      */   private static List getDefaultPrefixNamespaces(OMFactory factory)
/*      */   {
/*  991 */     List<OMNamespace> namespaces = new ArrayList();
/*      */     
/*      */ 
/*  994 */     namespaces.add(factory.createOMNamespace("http://www.w3.org/2001/04/xmlenc#", "xenc"));
/*  995 */     namespaces.add(factory.createOMNamespace("http://www.w3.org/2000/09/xmldsig#", "ds"));
/*  996 */     namespaces.add(factory.createOMNamespace("http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd", "wsse"));
/*  997 */     namespaces.add(factory.createOMNamespace("http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd", "wsu"));
/*      */     
/*  999 */     return namespaces;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public static List<WSEncryptionPart> getContentEncryptedElements(List<WSEncryptionPart> encryptedPartsElements, SOAPEnvelope envelope, List<String> elements, HashMap decNamespaces)
/*      */   {
/* 1006 */     Set namespaces = findAllPrefixNamespaces(envelope, decNamespaces);
/*      */     
/* 1008 */     for (String expression : elements) {
/*      */       try {
/* 1010 */         XPath xp = new AXIOMXPath(expression);
/*      */         
/* 1012 */         for (Object objectNamespace : namespaces) {
/* 1013 */           OMNamespace tmpNs = (OMNamespace)objectNamespace;
/* 1014 */           xp.addNamespace(tmpNs.getPrefix(), tmpNs.getNamespaceURI());
/*      */         }
/*      */         
/*      */         List selectedNodes;
/*      */         
/*      */         try
/*      */         {
/* 1021 */           selectedNodes = xp.selectNodes(envelope);
/*      */         }
/*      */         catch (JaxenException ex) {
/*      */           try {
/* 1025 */             XPath xpath = new AXIOMXPath(expression);
/* 1026 */             selectedNodes = (List)xpath.evaluate(envelope);
/*      */           } catch (Exception e) {
/* 1028 */             log.warn("Error al evaluar la expresion XPath " + expression + ". " + ex.getMessage());
/* 1029 */             throw ex;
/*      */           }
/*      */         }
/*      */         
/*      */ 
/*      */ 
/* 1035 */         for (Object selectedNode : selectedNodes) {
/* 1036 */           OMElement e = (OMElement)selectedNode;
/*      */           
/* 1038 */           String localName = e.getLocalName();
/* 1039 */           String namespace = e.getNamespace() != null ? e.getNamespace().getNamespaceURI() : null;
/*      */           
/* 1041 */           OMAttribute wsuIdAttribute = e.getAttribute(new QName("http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd", "Id"));
/*      */           
/* 1043 */           String wsuId = null;
/* 1044 */           if (wsuIdAttribute != null) {
/* 1045 */             wsuId = wsuIdAttribute.getAttributeValue();
/*      */           }
/*      */           
/* 1048 */           encryptedPartsElements.add(createEncryptionPart(localName, wsuId, namespace, "Content", expression));
/*      */         }
/*      */         
/*      */ 
/*      */       }
/*      */       catch (JaxenException e)
/*      */       {
/* 1055 */         throw new RuntimeException(e);
/*      */       }
/*      */     }
/*      */     
/*      */ 
/* 1060 */     return encryptedPartsElements;
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
/*      */   public static WSEncryptionPart createEncryptionPart(String name, String id)
/*      */   {
/* 1073 */     return createEncryptionPart(name, id, null, null, null);
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
/*      */   public static WSEncryptionPart createEncryptionPart(String name, String id, String namespace, String modifier)
/*      */   {
/* 1087 */     return createEncryptionPart(name, id, namespace, modifier, null);
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
/*      */   public static WSEncryptionPart createEncryptionPart(String name, String id, String namespace, String modifier, String xPath)
/*      */   {
/* 1103 */     assert (name != null);
/*      */     
/* 1105 */     WSEncryptionPart wsEncryptionPart = new WSEncryptionPart(name, namespace, modifier);
/* 1106 */     wsEncryptionPart.setId(id);
/* 1107 */     wsEncryptionPart.setXpath(xPath);
/*      */     
/* 1109 */     return wsEncryptionPart;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public static List<WSEncryptionPart> getPartsAndElements(boolean sign, SOAPEnvelope envelope, boolean includeBody, List<WSEncryptionPart> parts, List<String> elements, HashMap decNamespaces)
/*      */   {
/* 1116 */     List<OMElement> found = new ArrayList();
/* 1117 */     List<WSEncryptionPart> result = new ArrayList();
/*      */     
/*      */ 
/* 1120 */     if (includeBody)
/*      */     {
/* 1122 */       String wsuId = addWsuIdToElement(envelope.getBody());
/*      */       
/* 1124 */       if (sign) {
/* 1125 */         result.add(createEncryptionPart(envelope.getBody().getLocalName(), wsuId, null, null));
/*      */       }
/*      */       else {
/* 1128 */         result.add(createEncryptionPart(envelope.getBody().getLocalName(), wsuId, null, "Content"));
/*      */       }
/*      */       
/*      */ 
/* 1132 */       found.add(envelope.getBody());
/*      */     }
/*      */     
/*      */ 
/*      */ 
/* 1137 */     SOAPHeader header = envelope.getHeader();
/*      */     
/* 1139 */     for (WSEncryptionPart part : parts) {
/* 1140 */       if (part.getName() == null)
/*      */       {
/* 1142 */         ArrayList headerList = header.getHeaderBlocksWithNSURI(part.getNamespace());
/*      */         
/* 1144 */         for (Object aHeaderList : headerList) {
/* 1145 */           SOAPHeaderBlock shb = (SOAPHeaderBlock)aHeaderList;
/*      */           
/*      */ 
/* 1148 */           OMElement e = header.getFirstChildWithName(shb.getQName());
/*      */           
/* 1150 */           if (!found.contains(e))
/*      */           {
/* 1152 */             found.add(e);
/*      */             
/* 1154 */             if (sign) {
/* 1155 */               result.add(createEncryptionPart(e.getLocalName(), null, part.getNamespace(), "Content"));
/*      */             }
/*      */             else
/*      */             {
/* 1159 */               OMAttribute wsuIdAttribute = e.getAttribute(new QName("http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd", "Id"));
/*      */               
/* 1161 */               String wsuId = null;
/* 1162 */               if (wsuIdAttribute != null) {
/* 1163 */                 wsuId = wsuIdAttribute.getAttributeValue();
/*      */               }
/*      */               
/* 1166 */               result.add(createEncryptionPart(e.getLocalName(), wsuId, part.getNamespace(), "Element"));
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */       else
/*      */       {
/* 1173 */         OMElement e = header.getFirstChildWithName(new QName(part.getNamespace(), part.getName()));
/* 1174 */         if ((e != null) && 
/* 1175 */           (!found.contains(e)))
/*      */         {
/* 1177 */           found.add(e);
/* 1178 */           OMAttribute wsuId = e.getAttribute(new QName("http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd", "Id"));
/*      */           
/* 1180 */           if (wsuId != null) {
/* 1181 */             part.setEncId(wsuId.getAttributeValue());
/*      */           }
/*      */           
/* 1184 */           result.add(part);
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1193 */     Set namespaces = findAllPrefixNamespaces(envelope, decNamespaces);
/*      */     
/* 1195 */     for (String expression : elements) {
/*      */       try {
/* 1197 */         XPath xp = new AXIOMXPath(expression);
/*      */         
/* 1199 */         for (Object objectNamespace : namespaces) {
/* 1200 */           OMNamespace tmpNs = (OMNamespace)objectNamespace;
/* 1201 */           xp.addNamespace(tmpNs.getPrefix(), tmpNs.getNamespaceURI());
/*      */         }
/*      */         
/*      */         List selectedNodes;
/*      */         
/*      */         try
/*      */         {
/* 1208 */           selectedNodes = xp.selectNodes(envelope);
/*      */         }
/*      */         catch (JaxenException ex) {
/*      */           try {
/* 1212 */             XPath xpath = new AXIOMXPath(expression);
/* 1213 */             selectedNodes = (List)xpath.evaluate(envelope);
/*      */           } catch (Exception e) {
/* 1215 */             log.warn("Error al evaluar la expresion XPath " + expression + ". " + ex.getMessage());
/* 1216 */             throw ex;
/*      */           }
/*      */         }
/*      */         
/*      */ 
/* 1221 */         for (Object selectedNode : selectedNodes) {
/* 1222 */           OMElement e = (OMElement)selectedNode;
/* 1223 */           String localName = e.getLocalName();
/* 1224 */           String namespace = e.getNamespace() != null ? e.getNamespace().getNamespaceURI() : null;
/*      */           
/* 1226 */           if (sign)
/*      */           {
/* 1228 */             result.add(createEncryptionPart(localName, null, namespace, "Content", expression));
/*      */           }
/*      */           else
/*      */           {
/* 1232 */             OMAttribute wsuIdAttribute = e.getAttribute(new QName("http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd", "Id"));
/*      */             
/* 1234 */             String wsuId = null;
/* 1235 */             if (wsuIdAttribute != null) {
/* 1236 */               wsuId = wsuIdAttribute.getAttributeValue();
/*      */             }
/*      */             
/* 1239 */             result.add(createEncryptionPart(localName, wsuId, namespace, "Element", expression));
/*      */           }
/*      */         }
/*      */       }
/*      */       catch (JaxenException e)
/*      */       {
/* 1245 */         throw new RuntimeException(e);
/*      */       }
/*      */     }
/*      */     
/* 1249 */     return result;
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
/*      */   public static boolean checkRequiredElements(SOAPEnvelope envelope, HashMap decNamespaces, String expression)
/*      */   {
/* 1263 */     SOAPHeader header = envelope.getHeader();
/* 1264 */     Set namespaces = findAllPrefixNamespaces(header, decNamespaces);
/*      */     try
/*      */     {
/* 1267 */       XPath xp = new AXIOMXPath(expression);
/*      */       
/* 1269 */       for (Object namespace : namespaces) {
/* 1270 */         OMNamespace tmpNs = (OMNamespace)namespace;
/* 1271 */         xp.addNamespace(tmpNs.getPrefix(), tmpNs.getNamespaceURI());
/*      */       }
/*      */       
/* 1274 */       List selectedNodes = xp.selectNodes(header);
/*      */       
/* 1276 */       if (selectedNodes.size() == 0) {
/* 1277 */         return false;
/*      */       }
/*      */     }
/*      */     catch (JaxenException e)
/*      */     {
/* 1282 */       throw new RuntimeException(e);
/*      */     }
/*      */     
/* 1285 */     return true;
/*      */   }
/*      */   
/*      */ 
/*      */   public static KeyGenerator getEncryptionKeyGenerator(String symEncrAlgo)
/*      */     throws WSSecurityException
/*      */   {
/*      */     KeyGenerator keyGen;
/*      */     try
/*      */     {
/* 1295 */       keyGen = KeyGenerator.getInstance("AES");
/* 1296 */       if (symEncrAlgo.equalsIgnoreCase("http://www.w3.org/2001/04/xmlenc#tripledes-cbc")) {
/* 1297 */         keyGen = KeyGenerator.getInstance("DESede");
/* 1298 */       } else if (symEncrAlgo.equalsIgnoreCase("http://www.w3.org/2001/04/xmlenc#aes128-cbc")) {
/* 1299 */         keyGen.init(128);
/* 1300 */       } else if (symEncrAlgo.equalsIgnoreCase("http://www.w3.org/2001/04/xmlenc#aes192-cbc")) {
/* 1301 */         keyGen.init(192);
/* 1302 */       } else if (symEncrAlgo.equalsIgnoreCase("http://www.w3.org/2001/04/xmlenc#aes256-cbc")) {
/* 1303 */         keyGen.init(256);
/*      */       } else {
/* 1305 */         return null;
/*      */       }
/*      */     } catch (NoSuchAlgorithmException e) {
/* 1308 */       throw new WSSecurityException(2, null, null, e);
/*      */     }
/*      */     
/* 1311 */     return keyGen;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static String getContextIdentifierKey(MessageContext msgContext)
/*      */   {
/* 1320 */     return msgContext.getAxisService().getName();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static Hashtable getContextMap(MessageContext msgContext)
/*      */   {
/* 1330 */     Object map = msgContext.getConfigurationContext().getProperty("contextMap");
/*      */     
/*      */ 
/* 1333 */     if (map == null)
/*      */     {
/* 1335 */       map = new Hashtable();
/*      */       
/* 1337 */       msgContext.getConfigurationContext().setProperty("contextMap", map);
/*      */     }
/*      */     
/*      */ 
/* 1341 */     return (Hashtable)map;
/*      */   }
/*      */   
/*      */   public static boolean isTokenValid(RampartMessageData rmd, String id) throws RampartException {
/*      */     try {
/* 1346 */       org.apache.rahas.Token token = rmd.getTokenStorage().getToken(id);
/* 1347 */       return (token != null) && (token.getState() == 1);
/*      */     } catch (TrustException e) {
/* 1349 */       throw new RampartException("errorExtractingToken");
/*      */     }
/*      */   }
/*      */   
/*      */   public static void setEncryptionUser(RampartMessageData rmd, WSSecEncryptedKey encrKeyBuilder) throws RampartException
/*      */   {
/* 1355 */     RampartPolicyData rpd = rmd.getPolicyData();
/* 1356 */     String encrUser = rpd.getRampartConfig().getEncryptionUser();
/* 1357 */     setEncryptionUser(rmd, encrKeyBuilder, encrUser);
/*      */   }
/*      */   
/*      */   public static void setEncryptionUser(RampartMessageData rmd, WSSecEncryptedKey encrKeyBuilder, String encrUser) throws RampartException
/*      */   {
/* 1362 */     RampartPolicyData rpd = rmd.getPolicyData();
/*      */     
/* 1364 */     if (encrUser == null) {
/* 1365 */       encrUser = rpd.getRampartConfig().getEncryptionUser();
/*      */     }
/*      */     
/* 1368 */     if ((encrUser == null) || ("".equals(encrUser))) {
/* 1369 */       throw new RampartException("missingEncryptionUser");
/*      */     }
/* 1371 */     if (encrUser.equals("useReqSigCert")) {
/* 1372 */       List<WSHandlerResult> resultsObj = (List)rmd.getMsgContext().getProperty("RECV_RESULTS");
/*      */       
/* 1374 */       if (resultsObj != null) {
/* 1375 */         encrKeyBuilder.setUseThisCert(getReqSigCert(resultsObj));
/*      */         
/*      */ 
/* 1378 */         if (encrKeyBuilder.isCertSet()) {
/* 1379 */           encrKeyBuilder.setUserInfo(getUsername(resultsObj));
/*      */         }
/*      */       }
/*      */       else
/*      */       {
/* 1384 */         throw new RampartException("noSecurityResults");
/*      */       }
/*      */     } else {
/* 1387 */       encrKeyBuilder.setUserInfo(encrUser);
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
/*      */ 
/*      */ 
/*      */ 
/*      */   public static void setKeyIdentifierType(RampartMessageData rmd, WSSecBase secBase, org.apache.ws.secpolicy.model.Token token)
/*      */   {
/* 1403 */     boolean useReference = (token.getInclusion() == 1) || ((!rmd.isInitiator()) && (token.getInclusion() == 3)) || ((rmd.isInitiator()) && (token.getInclusion() == 4));
/*      */     
/*      */ 
/* 1406 */     if (useReference)
/*      */     {
/* 1408 */       boolean tokenTypeSet = false;
/*      */       
/* 1410 */       if ((token instanceof X509Token)) {
/* 1411 */         X509Token x509Token = (X509Token)token;
/*      */         
/* 1413 */         if (x509Token.isRequireIssuerSerialReference()) {
/* 1414 */           secBase.setKeyIdentifierType(2);
/* 1415 */           tokenTypeSet = true;
/* 1416 */         } else if (x509Token.isRequireKeyIdentifierReference()) {
/* 1417 */           secBase.setKeyIdentifierType(4);
/* 1418 */           tokenTypeSet = true;
/* 1419 */         } else if (x509Token.isRequireThumbprintReference()) {
/* 1420 */           secBase.setKeyIdentifierType(8);
/* 1421 */           tokenTypeSet = true;
/*      */         }
/*      */       }
/*      */       
/* 1425 */       if (!tokenTypeSet) {
/* 1426 */         RampartPolicyData rpd = rmd.getPolicyData();
/* 1427 */         Wss10 wss = rpd.getWss11();
/* 1428 */         if (wss == null) {
/* 1429 */           wss = rpd.getWss10();
/*      */         }
/*      */         
/* 1432 */         if (wss.isMustSupportRefKeyIdentifier()) {
/* 1433 */           secBase.setKeyIdentifierType(4);
/* 1434 */         } else if (wss.isMustSupportRefIssuerSerial()) {
/* 1435 */           secBase.setKeyIdentifierType(2);
/* 1436 */         } else if (((wss instanceof Wss11)) && (((Wss11)wss).isMustSupportRefThumbprint()))
/*      */         {
/* 1438 */           secBase.setKeyIdentifierType(8);
/*      */         }
/*      */       }
/*      */     }
/*      */     else {
/* 1443 */       secBase.setKeyIdentifierType(1);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private static X509Certificate getReqSigCert(List<WSHandlerResult> results)
/*      */   {
/* 1452 */     for (WSHandlerResult result : results)
/*      */     {
/* 1454 */       List<WSSecurityEngineResult> wsSecEngineResults = result.getResults();
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1460 */       for (WSSecurityEngineResult wsSecEngineResult : wsSecEngineResults) {
/* 1461 */         Integer actInt = (Integer)wsSecEngineResult.get("action");
/* 1462 */         if (actInt.intValue() == 2) {
/* 1463 */           return (X509Certificate)wsSecEngineResult.get("x509-certificate");
/*      */         }
/*      */       }
/*      */     }
/*      */     
/* 1468 */     return null;
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
/*      */   public static String getUsername(List<WSHandlerResult> results)
/*      */   {
/* 1483 */     for (WSHandlerResult result : results)
/*      */     {
/* 1485 */       List<WSSecurityEngineResult> wsSecEngineResults = result.getResults();
/*      */       
/*      */ 
/*      */ 
/*      */ 
/* 1490 */       for (WSSecurityEngineResult wsSecEngineResult : wsSecEngineResults) {
/* 1491 */         Integer actInt = (Integer)wsSecEngineResult.get("action");
/* 1492 */         if (actInt.intValue() == 1) {
/* 1493 */           WSUsernameTokenPrincipal principal = (WSUsernameTokenPrincipal)wsSecEngineResult.get("principal");
/*      */           
/* 1495 */           return principal.getName();
/*      */         }
/*      */       }
/*      */     }
/*      */     
/* 1500 */     return null;
/*      */   }
/*      */   
/*      */   public static String getRequestEncryptedKeyId(List<WSHandlerResult> results)
/*      */   {
/* 1505 */     for (WSHandlerResult result : results)
/*      */     {
/* 1507 */       List<WSSecurityEngineResult> wsSecEngineResults = result.getResults();
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1513 */       for (WSSecurityEngineResult wsSecEngineResult : wsSecEngineResults) {
/* 1514 */         Integer actInt = (Integer)wsSecEngineResult.get("action");
/* 1515 */         String encrKeyId = (String)wsSecEngineResult.get("id");
/* 1516 */         if ((actInt.intValue() == 4) && (encrKeyId != null))
/*      */         {
/* 1518 */           return encrKeyId;
/*      */         }
/*      */       }
/*      */     }
/*      */     
/* 1523 */     return null;
/*      */   }
/*      */   
/*      */   public static byte[] getRequestEncryptedKeyValue(List<WSHandlerResult> results)
/*      */   {
/* 1528 */     for (WSHandlerResult result : results)
/*      */     {
/* 1530 */       List<WSSecurityEngineResult> wsSecEngineResults = result.getResults();
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1536 */       for (WSSecurityEngineResult wsSecEngineResult : wsSecEngineResults) {
/* 1537 */         Integer actInt = (Integer)wsSecEngineResult.get("action");
/* 1538 */         byte[] decryptedKey = (byte[])wsSecEngineResult.get("secret");
/* 1539 */         if ((actInt.intValue() == 4) && (decryptedKey != null))
/*      */         {
/* 1541 */           return decryptedKey;
/*      */         }
/*      */       }
/*      */     }
/*      */     
/* 1546 */     return null;
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
/*      */   public static Element insertSiblingAfterOrPrepend(RampartMessageData rmd, Element child, Element elem)
/*      */   {
/* 1560 */     Element retElem = null;
/* 1561 */     if (child != null) {
/* 1562 */       retElem = insertSiblingAfter(rmd, child, elem);
/*      */     } else {
/* 1564 */       retElem = prependSecHeader(rmd, elem);
/*      */     }
/*      */     
/* 1567 */     return retElem;
/*      */   }
/*      */   
/*      */   public static Element insertSiblingBeforeOrPrepend(RampartMessageData rmd, Element child, Element elem) {
/* 1571 */     Element retElem = null;
/* 1572 */     if ((child != null) && (child.getPreviousSibling() != null)) {
/* 1573 */       retElem = insertSiblingBefore(rmd, child, elem);
/*      */     } else {
/* 1575 */       retElem = prependSecHeader(rmd, elem);
/*      */     }
/*      */     
/* 1578 */     return retElem;
/*      */   }
/*      */   
/*      */   private static Element prependSecHeader(RampartMessageData rmd, Element elem) {
/* 1582 */     Element retElem = null;
/*      */     
/* 1584 */     Element secHeaderElem = rmd.getSecHeader().getSecurityHeader();
/* 1585 */     Node node = secHeaderElem.getOwnerDocument().importNode(elem, true);
/*      */     
/* 1587 */     Element firstElem = (Element)secHeaderElem.getFirstChild();
/*      */     
/* 1589 */     if (firstElem == null) {
/* 1590 */       retElem = (Element)secHeaderElem.appendChild(node);
/*      */     }
/* 1592 */     else if (firstElem.getOwnerDocument().equals(elem.getOwnerDocument())) {
/* 1593 */       ((OMElement)firstElem).insertSiblingBefore((OMElement)elem);
/* 1594 */       retElem = elem;
/*      */     } else {
/* 1596 */       Element newSib = (Element)firstElem.getOwnerDocument().importNode(elem, true);
/* 1597 */       ((OMElement)firstElem).insertSiblingBefore((OMElement)newSib);
/* 1598 */       retElem = newSib;
/*      */     }
/*      */     
/*      */ 
/* 1602 */     return retElem;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static boolean isSecHeaderRequired(RampartPolicyData rpd, boolean initiator, boolean inflow)
/*      */   {
/* 1614 */     if (rpd.isIncludeTimestamp()) {
/* 1615 */       return true;
/*      */     }
/*      */     
/*      */ 
/* 1619 */     if ((rpd.isSignBody()) || (rpd.getSignedParts().size() != 0) || (rpd.getSignedElements().size() != 0))
/*      */     {
/* 1621 */       return true;
/*      */     }
/*      */     
/*      */ 
/* 1625 */     if ((rpd.isEncryptBody()) || (rpd.getEncryptedParts().size() != 0) || (rpd.getEncryptedElements().size() != 0))
/*      */     {
/* 1627 */       return true;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 1633 */     if (((!initiator) && (inflow)) || ((initiator) && (!inflow)))
/*      */     {
/* 1635 */       List<SupportingToken> supportingToks = rpd.getSupportingTokensList();
/* 1636 */       for (SupportingToken supportingTok : supportingToks) {
/* 1637 */         if ((supportingTok != null) && (supportingTok.getTokens().size() != 0)) {
/* 1638 */           return true;
/*      */         }
/*      */       }
/*      */       
/* 1642 */       SupportingToken supportingTokens = rpd.getSignedSupportingTokens();
/* 1643 */       if ((supportingTokens != null) && (supportingTokens.getTokens().size() != 0)) {
/* 1644 */         return true;
/*      */       }
/*      */       
/* 1647 */       supportingTokens = rpd.getEndorsingSupportingTokens();
/* 1648 */       if ((supportingTokens != null) && (supportingTokens.getTokens().size() != 0)) {
/* 1649 */         return true;
/*      */       }
/*      */       
/* 1652 */       supportingTokens = rpd.getSignedEndorsingSupportingTokens();
/* 1653 */       if ((supportingTokens != null) && (supportingTokens.getTokens().size() != 0)) {
/* 1654 */         return true;
/*      */       }
/*      */       
/* 1657 */       supportingTokens = rpd.getEncryptedSupportingTokens();
/* 1658 */       if ((supportingTokens != null) && (supportingTokens.getTokens().size() != 0)) {
/* 1659 */         return true;
/*      */       }
/*      */       
/* 1662 */       supportingTokens = rpd.getSignedEncryptedSupportingTokens();
/* 1663 */       if ((supportingTokens != null) && (supportingTokens.getTokens().size() != 0)) {
/* 1664 */         return true;
/*      */       }
/*      */       
/* 1667 */       supportingTokens = rpd.getEndorsingEncryptedSupportingTokens();
/* 1668 */       if ((supportingTokens != null) && (supportingTokens.getTokens().size() != 0)) {
/* 1669 */         return true;
/*      */       }
/*      */       
/* 1672 */       supportingTokens = rpd.getSignedEndorsingEncryptedSupportingTokens();
/* 1673 */       if ((supportingTokens != null) && (supportingTokens.getTokens().size() != 0)) {
/* 1674 */         return true;
/*      */       }
/*      */     }
/*      */     
/* 1678 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public static void handleEncryptedSignedHeaders(List<WSEncryptionPart> encryptedParts, List<WSEncryptionPart> signedParts, Document doc)
/*      */   {
/* 1686 */     for (Iterator i$ = signedParts.iterator(); i$.hasNext();) { WSEncryptionPart signedPart = (WSEncryptionPart)i$.next();
/*      */       
/* 1688 */       if ((signedPart.getNamespace() != null) && (signedPart.getName() != null))
/*      */       {
/*      */ 
/*      */ 
/* 1692 */         for (WSEncryptionPart encryptedPart : encryptedParts)
/*      */         {
/* 1694 */           if ((encryptedPart.getNamespace() != null) && (encryptedPart.getName() != null))
/*      */           {
/*      */ 
/*      */ 
/* 1698 */             if ((signedPart.getName().equals(encryptedPart.getName())) && (signedPart.getNamespace().equals(encryptedPart.getNamespace())))
/*      */             {
/*      */ 
/* 1701 */               String encDataID = encryptedPart.getEncId();
/*      */               
/*      */ 
/* 1704 */               Element encDataElem = WSSecurityUtil.findElementById(doc.getDocumentElement(), encDataID, false);
/*      */               
/* 1706 */               if (encDataElem != null) {
/* 1707 */                 Element encHeader = (Element)encDataElem.getParentNode();
/* 1708 */                 String encHeaderId = encHeader.getAttributeNS("http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd", "Id");
/*      */                 
/*      */ 
/*      */ 
/* 1712 */                 if ((encHeaderId != null) && (!"".equals(encHeaderId.trim()))) {
/* 1713 */                   signedParts.remove(signedPart);
/*      */                   
/* 1715 */                   signedParts.add(createEncryptionPart(signedPart.getName(), encHeaderId, signedPart.getNamespace(), signedPart.getEncModifier(), signedPart.getXpath()));
/*      */                 }
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */     WSEncryptionPart signedPart;
/*      */   }
/*      */   
/*      */ 
/*      */   public static String getSigElementId(RampartMessageData rmd)
/*      */   {
/* 1731 */     SOAPEnvelope envelope = rmd.getMsgContext().getEnvelope();
/*      */     
/* 1733 */     SOAPHeader header = envelope.getHeader();
/*      */     
/* 1735 */     if (header == null) {
/* 1736 */       return null;
/*      */     }
/*      */     
/* 1739 */     ArrayList secHeaders = header.getHeaderBlocksWithNSURI("http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd");
/*      */     
/* 1741 */     if ((secHeaders != null) && (secHeaders.size() > 0)) {
/* 1742 */       QName sigQName = new QName("http://www.w3.org/2000/09/xmldsig#", "Signature");
/* 1743 */       QName wsuIdQName = new QName("http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd", "Id");
/* 1744 */       OMElement sigElem = ((SOAPHeaderBlock)secHeaders.get(0)).getFirstChildWithName(sigQName);
/* 1745 */       OMAttribute wsuId = sigElem.getAttribute(wsuIdQName);
/*      */       
/* 1747 */       if (wsuId != null) {
/* 1748 */         return wsuId.getAttributeValue();
/*      */       }
/*      */       
/* 1751 */       wsuId = sigElem.getAttribute(new QName("Id"));
/*      */       
/* 1753 */       if (wsuId != null) {
/* 1754 */         return wsuId.getAttributeValue();
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/* 1760 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static WSSConfig getWSSConfigInstance()
/*      */   {
/* 1770 */     WSSConfig defaultWssConfig = WSSConfig.getNewInstance();
/* 1771 */     WSSConfig wssConfig = WSSConfig.getNewInstance();
/*      */     
/* 1773 */     wssConfig.setEnableSignatureConfirmation(defaultWssConfig.isEnableSignatureConfirmation());
/* 1774 */     wssConfig.setTimeStampStrict(defaultWssConfig.isTimeStampStrict());
/* 1775 */     wssConfig.setWsiBSPCompliant(defaultWssConfig.isWsiBSPCompliant());
/* 1776 */     return wssConfig;
/*      */   }
/*      */   
/*      */   public static void validateTransport(RampartMessageData rmd)
/*      */     throws RampartException
/*      */   {
/* 1782 */     RampartPolicyData rpd = rmd.getPolicyData();
/*      */     
/* 1784 */     if (rpd == null) {
/* 1785 */       return;
/*      */     }
/*      */     
/* 1788 */     if ((rpd.isTransportBinding()) && (!rmd.isInitiator()) && 
/* 1789 */       ((rpd.getTransportToken() instanceof HttpsToken))) {
/* 1790 */       String incomingTransport = rmd.getMsgContext().getIncomingTransportName();
/* 1791 */       if (!incomingTransport.equals("https")) {
/* 1792 */         throw new RampartException("invalidTransport", new String[] { incomingTransport });
/*      */       }
/*      */       
/* 1795 */       if (((HttpsToken)rpd.getTransportToken()).isRequireClientCertificate())
/*      */       {
/* 1797 */         MessageContext messageContext = rmd.getMsgContext();
/* 1798 */         HttpServletRequest request = (HttpServletRequest)messageContext.getProperty(HTTPConstants.MC_HTTP_SERVLETREQUEST);
/* 1799 */         if ((request == null) || (request.getAttribute("javax.servlet.request.X509Certificate") == null)) {
/* 1800 */           throw new RampartException("clientAuthRequired");
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private static Crypto retrieveCryptoFromCache(String cryptoKey, String refreshInterval)
/*      */   {
/* 1810 */     if (cryptoStore.containsKey(cryptoKey)) {
/* 1811 */       CachedCrypto cachedCrypto = (CachedCrypto)cryptoStore.get(cryptoKey);
/* 1812 */       if (refreshInterval != null) {
/* 1813 */         if (cachedCrypto.creationTime + new Long(refreshInterval).longValue() > Calendar.getInstance().getTimeInMillis())
/*      */         {
/* 1815 */           log.debug("Cache Hit : Crypto Object was found in cache.");
/* 1816 */           return cachedCrypto.crypto;
/*      */         }
/* 1818 */         log.debug("Cache Miss : Crypto Object found in cache is expired.");
/* 1819 */         return null;
/*      */       }
/*      */       
/* 1822 */       log.debug("Cache Hit : Crypto Object was found in cache.");
/* 1823 */       return cachedCrypto.crypto;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/* 1828 */     log.debug("Cache Miss : Crypto Object was not found in cache.");
/* 1829 */     return null;
/*      */   }
/*      */   
/*      */   private static void cacheCrypto(String cryptoKey, Crypto crypto)
/*      */   {
/* 1834 */     cryptoStore.put(cryptoKey, new CachedCrypto(crypto, Calendar.getInstance().getTimeInMillis()));
/*      */     
/* 1836 */     log.debug("Crypto object is inserted into the Cache.");
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static String getSAML10AssertionNamespace()
/*      */   {
/* 1847 */     StringBuilder stringBuilder = new StringBuilder("http://docs.oasis-open.org/wss/oasis-wss-saml-token-profile-1.0");
/* 1848 */     stringBuilder.append("#").append("SAMLAssertionID");
/*      */     
/* 1850 */     return stringBuilder.toString();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static void setEncryptionCrypto(MessageContext msgContext)
/*      */   {
/* 1861 */     setEncryptionCryptoFileProperty(msgContext);
/* 1862 */     setEncryptionCryptoReferenceProperty(msgContext);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static void setDecryptionCrypto(MessageContext msgContext)
/*      */   {
/* 1872 */     setDecryptionCryptoFileProperty(msgContext);
/* 1873 */     setDecryptionCryptoReferenceProperty(msgContext);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private static void setEncryptionCryptoReferenceProperty(MessageContext msgContext)
/*      */   {
/* 1881 */     setCryptoProperty(msgContext, "signaturePropRefId", "encryptionPropRefId");
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private static void setDecryptionCryptoReferenceProperty(MessageContext msgContext)
/*      */   {
/* 1889 */     setCryptoProperty(msgContext, "signaturePropRefId", "decryptionPropRefId");
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private static void setEncryptionCryptoFileProperty(MessageContext msgContext)
/*      */   {
/* 1897 */     setCryptoProperty(msgContext, "signaturePropFile", "encryptionPropFile");
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private static void setDecryptionCryptoFileProperty(MessageContext msgContext)
/*      */   {
/* 1905 */     setCryptoProperty(msgContext, "signaturePropFile", "decryptionPropFile");
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private static void setCryptoProperty(MessageContext msgContext, String signaturePropertyName, String cryptoPropertyName)
/*      */   {
/* 1915 */     if (msgContext.getProperty(cryptoPropertyName) == null)
/*      */     {
/*      */ 
/* 1918 */       String signaturePropertyFile = (String)msgContext.getProperty(signaturePropertyName);
/*      */       
/* 1920 */       if (signaturePropertyFile == null)
/*      */       {
/* 1922 */         if (log.isDebugEnabled()) {
/* 1923 */           log.debug("Signature crypto property file is not set. Property file key - signaturePropFile");
/*      */         }
/*      */       }
/*      */       else {
/* 1927 */         msgContext.setProperty(cryptoPropertyName, signaturePropertyFile);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static boolean encryptFirst(RampartPolicyData rpd)
/*      */   {
/* 1938 */     return "EncryptBeforeSigning".equals(rpd.getProtectionOrder());
/*      */   }
/*      */ }


/* Location:              /home/LIMIT_CECOMASA.LOCAL/josepg/Documents/Limit/tmp/JD-GUI/scsp-core-4.0.0.jar!/org/apache/rampart/util/RampartUtil.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */
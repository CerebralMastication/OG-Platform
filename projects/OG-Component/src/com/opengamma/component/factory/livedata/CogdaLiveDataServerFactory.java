/**
 * Copyright (C) 2012 - present by OpenGamma Inc. and the OpenGamma group of companies
 * 
 * Please see distribution for license.
 */
package com.opengamma.component.factory.livedata;

import java.util.LinkedHashMap;
import java.util.Map;

import org.joda.beans.BeanBuilder;
import org.joda.beans.BeanDefinition;
import org.joda.beans.JodaBeanUtils;
import org.joda.beans.MetaProperty;
import org.joda.beans.Property;
import org.joda.beans.PropertyDefinition;
import org.joda.beans.impl.direct.DirectBeanBuilder;
import org.joda.beans.impl.direct.DirectMetaProperty;
import org.joda.beans.impl.direct.DirectMetaPropertyMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.listener.DefaultMessageListenerContainer;

import com.opengamma.component.ComponentRepository;
import com.opengamma.component.factory.AbstractComponentFactory;
import com.opengamma.livedata.cogda.server.CogdaLiveDataServer;
import com.opengamma.livedata.cogda.server.CogdaLiveDataServerMBean;
import com.opengamma.livedata.cogda.server.CogdaLiveDataServerUpdateListener;
import com.opengamma.livedata.server.LastKnownValueStoreProvider;
import com.opengamma.transport.ByteArrayFudgeMessageReceiver;
import com.opengamma.transport.jms.JmsByteArrayMessageDispatcher;
import com.opengamma.util.jms.JmsConnector;

/**
 * 
 */
@BeanDefinition
public class CogdaLiveDataServerFactory extends AbstractComponentFactory {
  private static final Logger s_logger = LoggerFactory.getLogger(CogdaLiveDataServerFactory.class);

  @PropertyDefinition(validate = "notNull")
  private JmsConnector _listenJmsConnector;
  
  @PropertyDefinition(validate = "notNull")
  private String _listenTopicName;
  
  @PropertyDefinition
  private String _dataRedisServer;
  
  @PropertyDefinition
  private Integer _dataRedisPort;
  
  @PropertyDefinition
  private String _dataRedisPrefix;
  
  @PropertyDefinition
  private Integer _listenPort;
  
  @Override
  public void init(ComponentRepository repo, LinkedHashMap<String, String> configuration) throws Exception {
    LastKnownValueStoreProvider lkvStoreProvider =
        CogdaFactoryUtil.constructLastKnownValueStoreProvider(
            getDataRedisServer(),
            getDataRedisPort(),
            getDataRedisPrefix(),
            false,
            s_logger);
    CogdaLiveDataServer liveDataServer = new CogdaLiveDataServer(lkvStoreProvider);
    if (getListenPort() != null) {
      liveDataServer.setPortNumber(getListenPort());
    }
    
    CogdaLiveDataServerUpdateListener updateListener = new CogdaLiveDataServerUpdateListener(liveDataServer);
    
    DefaultMessageListenerContainer container = new DefaultMessageListenerContainer();
    container.setMessageListener(
        new JmsByteArrayMessageDispatcher(
            new ByteArrayFudgeMessageReceiver(updateListener)));
    container.setDestinationName(getListenTopicName());
    container.setPubSubDomain(true);
    container.setConnectionFactory(getListenJmsConnector().getConnectionFactory());
    container.afterPropertiesSet();
    
    CogdaLiveDataServerMBean mbean = new CogdaLiveDataServerMBean(liveDataServer);
    
    repo.registerLifecycle(liveDataServer);
    repo.registerLifecycle(container);
    repo.registerMBean(mbean);
  }

  //------------------------- AUTOGENERATED START -------------------------
  ///CLOVER:OFF
  /**
   * The meta-bean for {@code CogdaLiveDataServerFactory}.
   * @return the meta-bean, not null
   */
  public static CogdaLiveDataServerFactory.Meta meta() {
    return CogdaLiveDataServerFactory.Meta.INSTANCE;
  }
  static {
    JodaBeanUtils.registerMetaBean(CogdaLiveDataServerFactory.Meta.INSTANCE);
  }

  @Override
  public CogdaLiveDataServerFactory.Meta metaBean() {
    return CogdaLiveDataServerFactory.Meta.INSTANCE;
  }

  @Override
  protected Object propertyGet(String propertyName, boolean quiet) {
    switch (propertyName.hashCode()) {
      case 1486580228:  // listenJmsConnector
        return getListenJmsConnector();
      case 1916939859:  // listenTopicName
        return getListenTopicName();
      case -1684872556:  // dataRedisServer
        return getDataRedisServer();
      case -95687662:  // dataRedisPort
        return getDataRedisPort();
      case -1759156765:  // dataRedisPrefix
        return getDataRedisPrefix();
      case 873783016:  // listenPort
        return getListenPort();
    }
    return super.propertyGet(propertyName, quiet);
  }

  @Override
  protected void propertySet(String propertyName, Object newValue, boolean quiet) {
    switch (propertyName.hashCode()) {
      case 1486580228:  // listenJmsConnector
        setListenJmsConnector((JmsConnector) newValue);
        return;
      case 1916939859:  // listenTopicName
        setListenTopicName((String) newValue);
        return;
      case -1684872556:  // dataRedisServer
        setDataRedisServer((String) newValue);
        return;
      case -95687662:  // dataRedisPort
        setDataRedisPort((Integer) newValue);
        return;
      case -1759156765:  // dataRedisPrefix
        setDataRedisPrefix((String) newValue);
        return;
      case 873783016:  // listenPort
        setListenPort((Integer) newValue);
        return;
    }
    super.propertySet(propertyName, newValue, quiet);
  }

  @Override
  protected void validate() {
    JodaBeanUtils.notNull(_listenJmsConnector, "listenJmsConnector");
    JodaBeanUtils.notNull(_listenTopicName, "listenTopicName");
    super.validate();
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj != null && obj.getClass() == this.getClass()) {
      CogdaLiveDataServerFactory other = (CogdaLiveDataServerFactory) obj;
      return JodaBeanUtils.equal(getListenJmsConnector(), other.getListenJmsConnector()) &&
          JodaBeanUtils.equal(getListenTopicName(), other.getListenTopicName()) &&
          JodaBeanUtils.equal(getDataRedisServer(), other.getDataRedisServer()) &&
          JodaBeanUtils.equal(getDataRedisPort(), other.getDataRedisPort()) &&
          JodaBeanUtils.equal(getDataRedisPrefix(), other.getDataRedisPrefix()) &&
          JodaBeanUtils.equal(getListenPort(), other.getListenPort()) &&
          super.equals(obj);
    }
    return false;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash += hash * 31 + JodaBeanUtils.hashCode(getListenJmsConnector());
    hash += hash * 31 + JodaBeanUtils.hashCode(getListenTopicName());
    hash += hash * 31 + JodaBeanUtils.hashCode(getDataRedisServer());
    hash += hash * 31 + JodaBeanUtils.hashCode(getDataRedisPort());
    hash += hash * 31 + JodaBeanUtils.hashCode(getDataRedisPrefix());
    hash += hash * 31 + JodaBeanUtils.hashCode(getListenPort());
    return hash ^ super.hashCode();
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the listenJmsConnector.
   * @return the value of the property, not null
   */
  public JmsConnector getListenJmsConnector() {
    return _listenJmsConnector;
  }

  /**
   * Sets the listenJmsConnector.
   * @param listenJmsConnector  the new value of the property, not null
   */
  public void setListenJmsConnector(JmsConnector listenJmsConnector) {
    JodaBeanUtils.notNull(listenJmsConnector, "listenJmsConnector");
    this._listenJmsConnector = listenJmsConnector;
  }

  /**
   * Gets the the {@code listenJmsConnector} property.
   * @return the property, not null
   */
  public final Property<JmsConnector> listenJmsConnector() {
    return metaBean().listenJmsConnector().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the listenTopicName.
   * @return the value of the property, not null
   */
  public String getListenTopicName() {
    return _listenTopicName;
  }

  /**
   * Sets the listenTopicName.
   * @param listenTopicName  the new value of the property, not null
   */
  public void setListenTopicName(String listenTopicName) {
    JodaBeanUtils.notNull(listenTopicName, "listenTopicName");
    this._listenTopicName = listenTopicName;
  }

  /**
   * Gets the the {@code listenTopicName} property.
   * @return the property, not null
   */
  public final Property<String> listenTopicName() {
    return metaBean().listenTopicName().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the dataRedisServer.
   * @return the value of the property
   */
  public String getDataRedisServer() {
    return _dataRedisServer;
  }

  /**
   * Sets the dataRedisServer.
   * @param dataRedisServer  the new value of the property
   */
  public void setDataRedisServer(String dataRedisServer) {
    this._dataRedisServer = dataRedisServer;
  }

  /**
   * Gets the the {@code dataRedisServer} property.
   * @return the property, not null
   */
  public final Property<String> dataRedisServer() {
    return metaBean().dataRedisServer().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the dataRedisPort.
   * @return the value of the property
   */
  public Integer getDataRedisPort() {
    return _dataRedisPort;
  }

  /**
   * Sets the dataRedisPort.
   * @param dataRedisPort  the new value of the property
   */
  public void setDataRedisPort(Integer dataRedisPort) {
    this._dataRedisPort = dataRedisPort;
  }

  /**
   * Gets the the {@code dataRedisPort} property.
   * @return the property, not null
   */
  public final Property<Integer> dataRedisPort() {
    return metaBean().dataRedisPort().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the dataRedisPrefix.
   * @return the value of the property
   */
  public String getDataRedisPrefix() {
    return _dataRedisPrefix;
  }

  /**
   * Sets the dataRedisPrefix.
   * @param dataRedisPrefix  the new value of the property
   */
  public void setDataRedisPrefix(String dataRedisPrefix) {
    this._dataRedisPrefix = dataRedisPrefix;
  }

  /**
   * Gets the the {@code dataRedisPrefix} property.
   * @return the property, not null
   */
  public final Property<String> dataRedisPrefix() {
    return metaBean().dataRedisPrefix().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the listenPort.
   * @return the value of the property
   */
  public Integer getListenPort() {
    return _listenPort;
  }

  /**
   * Sets the listenPort.
   * @param listenPort  the new value of the property
   */
  public void setListenPort(Integer listenPort) {
    this._listenPort = listenPort;
  }

  /**
   * Gets the the {@code listenPort} property.
   * @return the property, not null
   */
  public final Property<Integer> listenPort() {
    return metaBean().listenPort().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * The meta-bean for {@code CogdaLiveDataServerFactory}.
   */
  public static class Meta extends AbstractComponentFactory.Meta {
    /**
     * The singleton instance of the meta-bean.
     */
    static final Meta INSTANCE = new Meta();

    /**
     * The meta-property for the {@code listenJmsConnector} property.
     */
    private final MetaProperty<JmsConnector> _listenJmsConnector = DirectMetaProperty.ofReadWrite(
        this, "listenJmsConnector", CogdaLiveDataServerFactory.class, JmsConnector.class);
    /**
     * The meta-property for the {@code listenTopicName} property.
     */
    private final MetaProperty<String> _listenTopicName = DirectMetaProperty.ofReadWrite(
        this, "listenTopicName", CogdaLiveDataServerFactory.class, String.class);
    /**
     * The meta-property for the {@code dataRedisServer} property.
     */
    private final MetaProperty<String> _dataRedisServer = DirectMetaProperty.ofReadWrite(
        this, "dataRedisServer", CogdaLiveDataServerFactory.class, String.class);
    /**
     * The meta-property for the {@code dataRedisPort} property.
     */
    private final MetaProperty<Integer> _dataRedisPort = DirectMetaProperty.ofReadWrite(
        this, "dataRedisPort", CogdaLiveDataServerFactory.class, Integer.class);
    /**
     * The meta-property for the {@code dataRedisPrefix} property.
     */
    private final MetaProperty<String> _dataRedisPrefix = DirectMetaProperty.ofReadWrite(
        this, "dataRedisPrefix", CogdaLiveDataServerFactory.class, String.class);
    /**
     * The meta-property for the {@code listenPort} property.
     */
    private final MetaProperty<Integer> _listenPort = DirectMetaProperty.ofReadWrite(
        this, "listenPort", CogdaLiveDataServerFactory.class, Integer.class);
    /**
     * The meta-properties.
     */
    private final Map<String, MetaProperty<?>> _metaPropertyMap$ = new DirectMetaPropertyMap(
      this, (DirectMetaPropertyMap) super.metaPropertyMap(),
        "listenJmsConnector",
        "listenTopicName",
        "dataRedisServer",
        "dataRedisPort",
        "dataRedisPrefix",
        "listenPort");

    /**
     * Restricted constructor.
     */
    protected Meta() {
    }

    @Override
    protected MetaProperty<?> metaPropertyGet(String propertyName) {
      switch (propertyName.hashCode()) {
        case 1486580228:  // listenJmsConnector
          return _listenJmsConnector;
        case 1916939859:  // listenTopicName
          return _listenTopicName;
        case -1684872556:  // dataRedisServer
          return _dataRedisServer;
        case -95687662:  // dataRedisPort
          return _dataRedisPort;
        case -1759156765:  // dataRedisPrefix
          return _dataRedisPrefix;
        case 873783016:  // listenPort
          return _listenPort;
      }
      return super.metaPropertyGet(propertyName);
    }

    @Override
    public BeanBuilder<? extends CogdaLiveDataServerFactory> builder() {
      return new DirectBeanBuilder<CogdaLiveDataServerFactory>(new CogdaLiveDataServerFactory());
    }

    @Override
    public Class<? extends CogdaLiveDataServerFactory> beanType() {
      return CogdaLiveDataServerFactory.class;
    }

    @Override
    public Map<String, MetaProperty<?>> metaPropertyMap() {
      return _metaPropertyMap$;
    }

    //-----------------------------------------------------------------------
    /**
     * The meta-property for the {@code listenJmsConnector} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<JmsConnector> listenJmsConnector() {
      return _listenJmsConnector;
    }

    /**
     * The meta-property for the {@code listenTopicName} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<String> listenTopicName() {
      return _listenTopicName;
    }

    /**
     * The meta-property for the {@code dataRedisServer} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<String> dataRedisServer() {
      return _dataRedisServer;
    }

    /**
     * The meta-property for the {@code dataRedisPort} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<Integer> dataRedisPort() {
      return _dataRedisPort;
    }

    /**
     * The meta-property for the {@code dataRedisPrefix} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<String> dataRedisPrefix() {
      return _dataRedisPrefix;
    }

    /**
     * The meta-property for the {@code listenPort} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<Integer> listenPort() {
      return _listenPort;
    }

  }

  ///CLOVER:ON
  //-------------------------- AUTOGENERATED END --------------------------
}

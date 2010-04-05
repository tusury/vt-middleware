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
package edu.vt.middleware.gator.validation;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.constraints.NotNull;

import edu.vt.middleware.gator.AppenderConfig;
import edu.vt.middleware.gator.CategoryConfig;
import edu.vt.middleware.gator.ClientConfig;
import edu.vt.middleware.gator.Config;
import edu.vt.middleware.gator.ConfigManager;
import edu.vt.middleware.gator.PermissionConfig;
import edu.vt.middleware.gator.ProjectConfig;

import org.springframework.beans.factory.annotation.Autowired;

/**
 * Validates the {@link UniqueName} constraint on a {@link Config} object.
 *
 * @author Middleware
 * @version $Revision$
 *
 */
public class UniqueNameValidator
  implements ConstraintValidator<UniqueName, Config>
{
  /** Classes on which this validator supports the UniqueName attribute */
  public static final Set<Class<? extends Config>> SUPPORTED_CLASSES;

  @Autowired
  @NotNull
  private ConfigManager configManager;
  
  private String message;

  
  /** Class initializer */
  static
  {
    SUPPORTED_CLASSES = new HashSet<Class<? extends Config>>();
    SUPPORTED_CLASSES.add(ProjectConfig.class);
    SUPPORTED_CLASSES.add(AppenderConfig.class);
    SUPPORTED_CLASSES.add(CategoryConfig.class);
    SUPPORTED_CLASSES.add(ClientConfig.class);
    SUPPORTED_CLASSES.add(PermissionConfig.class);
  }


  /**
   * Sets the configuration manager.
   * @param helper Configuration manager;
   */
  public void setConfigManager(final ConfigManager helper)
  {
    this.configManager = helper;
  }


  /** {@inheritDoc} */
  public void initialize(UniqueName annotation)
  {
    message = annotation.message();
  }


  /** {@inheritDoc} */
  public boolean isValid(
      final Config value,
      final ConstraintValidatorContext context)
  {
    if (!SUPPORTED_CLASSES.contains(value.getClass())) {
      throw new IllegalArgumentException("Cannot validate " + value);
    }
    if (!configManager.exists(value) || nameChanged(value)) {
      // Name must be unique among peers
      for (Config c : getPeers(value)) {
        if (c.getName().equals(value.getName())) {
          if (context != null) {
	          context.buildConstraintViolationWithTemplate(
		          message).addNode("name").addConstraintViolation();
          }
          return false;
        }
      }
    }
    return true;
  }


  /**
   * Determines whether the name of the given config object has changed from
   * what is recorded in the DB.
   * @param value Config object to evaluate.
   * @return True if name of given object is different from that in the DB,
   * false otherwise.  Returns false if object does not exist in DB.
   */
  private boolean nameChanged(final Config value)
  {
    final Config dbConfig = configManager.find(value.getClass(), value.getId());
    if (dbConfig != null) {
      return !dbConfig.getName().equals(value.getName());
    } else {
      return false;
    }
  }


  /**
   * Gets a collection of peers among which the candidate's name must be unique.
   *
   * @param value Config object under validation.
   * @return List of peers of the given config object.
   */
  private Collection<? extends Config> getPeers(final Config value)
  {
    Collection<? extends Config> peers = Collections.emptyList();
    if (value instanceof ProjectConfig) {
      peers = configManager.findAll(ProjectConfig.class);
    } else if (value instanceof AppenderConfig) {
      peers = ((AppenderConfig) value).getProject().getAppenders();
    } else if (value instanceof CategoryConfig) {
      peers = ((CategoryConfig) value).getProject().getCategories();
    } else if (value instanceof ClientConfig) {
      peers = ((ClientConfig) value).getProject().getClients();
    } else if (value instanceof PermissionConfig) {
      peers = ((PermissionConfig) value).getProject().getPermissions();
    }
    return peers;
  }
}

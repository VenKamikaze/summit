package org.awiki.kamikaze.summit.exception;

public class ResourceNotFoundException extends RuntimeException
{
  /**
   * 
   */
  private static final long serialVersionUID = 5701229614238402875L;

  public ResourceNotFoundException(String resourceMessage) {
    super(resourceMessage);
  }
  
  public ResourceNotFoundException() {
    super();
  }
}

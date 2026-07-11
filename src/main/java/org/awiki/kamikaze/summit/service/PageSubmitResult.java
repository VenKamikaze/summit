package org.awiki.kamikaze.summit.service;

import java.util.ArrayList;
import java.util.List;

/**
 * Outcome of processing a page POST.
 * - validationErrors non-empty: no processing ran; the page should re-render
 *   inline with the errors in the notification area.
 * - otherwise: branchTarget (nullable, null = redirect back to the same page)
 *   and the success messages of the POST1 processings that ran, to be shown
 *   once after the redirect.
 */
public class PageSubmitResult
{
  private String branchTarget;
  private final List<String> validationErrors = new ArrayList<>();
  private final List<String> successMessages = new ArrayList<>();

  public String getBranchTarget()
  {
    return branchTarget;
  }

  public void setBranchTarget(String branchTarget)
  {
    this.branchTarget = branchTarget;
  }

  public List<String> getValidationErrors()
  {
    return validationErrors;
  }

  public boolean hasValidationErrors()
  {
    return !validationErrors.isEmpty();
  }

  public List<String> getSuccessMessages()
  {
    return successMessages;
  }
}

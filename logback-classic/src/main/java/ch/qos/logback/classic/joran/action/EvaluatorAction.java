package ch.qos.logback.classic.joran.action;

import ch.qos.logback.classic.boolex.JaninoEventEvaluator;
import ch.qos.logback.core.boolex.EventEvaluator;
import ch.qos.logback.core.joran.action.AbstractEventEvaluatorAction;


public class EvaluatorAction extends AbstractEventEvaluatorAction {

  protected boolean isOfCorrectType(EventEvaluator ee) {
    return (ee instanceof JaninoEventEvaluator);
  }


  protected String defaultClassName() {
    return JaninoEventEvaluator.class.getName();
  }



}
package com.alerts;

/**
 * Adds a [REPEATED] tag to the alert's condition.
 */
public class RepeatedAlertDecorator extends AlertDecorator {

    public RepeatedAlertDecorator(Alert decoratedAlert) {
        super(decoratedAlert);
    }

    @Override
    public String getCondition() {
        return "[REPEATED] " + decoratedAlert.getCondition();
    }
}
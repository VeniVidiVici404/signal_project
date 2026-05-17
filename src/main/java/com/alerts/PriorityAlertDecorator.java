package com.alerts;

/**
 * Adds a [PRIORITY] tag to the alert's condition.
 */
public class PriorityAlertDecorator extends AlertDecorator {

    public PriorityAlertDecorator(Alert decoratedAlert) {
        super(decoratedAlert);
    }

    @Override
    public String getCondition() {
        // We intercept the normal condition and slap our tag on the front!
        return "[PRIORITY] " + decoratedAlert.getCondition();
    }
}
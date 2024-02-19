package org.tinkoff.task.two.service;

import java.time.Duration;

public interface Handler {
    Duration timeout();
    void performOperation();
}
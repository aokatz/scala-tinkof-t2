package org.tinkoff.task.two.model;

import java.util.List;

public record Event(List<Address> recipients, Payload payload) {}
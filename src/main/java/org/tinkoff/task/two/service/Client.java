package org.tinkoff.task.two.service;

import org.tinkoff.task.two.model.Address;
import org.tinkoff.task.two.model.Event;
import org.tinkoff.task.two.model.Payload;
import org.tinkoff.task.two.model.Result;

public interface Client {
    //блокирующий метод для чтения данных
    Event readData();

    //блокирующий метод отправки данных
    Result sendData(Address dest, Payload payload);
}